/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.misc.LuckyBadge;
import pd.messages.Messages;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.List;

/**
 * SPSEXPD: 许愿引擎。
 *
 * 使用许愿魔药后由 UI 传入愿望文本，这里完成：
 * <ol>
 *   <li>死亡愿望 / 物品愿望 / 负面或增益效果愿望的判定；</li>
 *   <li>物品按名称词匹配（描述准确度）命中，再按幸运值决定奖励等级；</li>
 *   <li>等级越高数量越多、装备越可能带强化、越可能触发许愿特质。</li>
 * </ol>
 */
public final class WishEngine {

	private static final String KEY_PREFIX = "items.consum.potions.elixirs.wishpotion.";

	public enum Kind { NOTHING, ITEM, BUFF, NEGATIVE, DEATH }

	/** 一次许愿的结果。 */
	public static final class Result {
		public final Kind kind;
		public final Item item;
		public final int tier;
		public final double accuracy;
		public final boolean weakened;
		public final boolean wishOnly;

		Result(Kind kind, Item item, int tier, double accuracy, boolean weakened, boolean wishOnly) {
			this.kind = kind;
			this.item = item;
			this.tier = tier;
			this.accuracy = accuracy;
			this.weakened = weakened;
			this.wishOnly = wishOnly;
		}
	}

	private WishEngine() {
	}

	public static Result wish(Hero hero, String text) {
		if (hero == null) return new Result(Kind.NOTHING, null, 0, 0d, false, false);

		String raw = text == null ? "" : text.trim();
		if (raw.isEmpty()) {
			log(message("result_nothing"), true);
			return new Result(Kind.NOTHING, null, 0, 0d, false, false);
		}

		int luck = LuckyBadge.luckBonus(hero);
		int allowed = WishRewardTable.maxTierForLuck(luck);

		//1. 死亡愿望
		if (WishEffects.isDeath(raw)) {
			log(message("result_death"), true);
			WishEffects.kill(hero, message("death_cause"));
			return new Result(Kind.DEATH, null, allowed, 1d, false, false);
		}

		//2. 物品愿望（按名称词匹配）
		List<WishCatalog.Entry> pool = WishCatalog.entries();
		WishCatalog.Entry best = WishMatcher.best(raw, pool);
		double accuracy = WishMatcher.accuracy(raw, best);
		if (best != null && accuracy >= WishMatcher.MIN_ACCURACY) {
			boolean weakened = best.tier > allowed;
			WishCatalog.Entry target = best;
			if (weakened) {
				WishCatalog.Entry fallback = WishMatcher.bestOfTier(raw, pool, allowed);
				target = fallback != null ? fallback : randomOfTier(pool, allowed);
			}
			if (target == null) target = best;
			int tier = WishRewardTable.grantTier(target.tier, luck);
			Item item = grantItem(hero, target, tier);
			logItem(weakened ? "result_weakened" : "result_item", item);
			return new Result(Kind.ITEM, item, tier, accuracy, weakened, target.wishOnly);
		}

		//3. 负面效果愿望
		String negative = WishEffects.negativeKeyword(raw);
		if (negative != null) {
			WishEffects.applyNegative(hero, negative, allowed);
			log(message("result_negative"), true);
			return new Result(Kind.NEGATIVE, null, allowed, 0d, false, false);
		}

		//4. 增益效果愿望
		String positive = WishEffects.positiveKeyword(raw);
		if (positive != null) {
			WishEffects.applyPositive(hero, positive, allowed);
			log(message("result_buff"), false);
			return new Result(Kind.BUFF, null, allowed, 0d, false, false);
		}

		//5. 什么都没对上的模糊愿望：按可达等级随机给一件
		WishCatalog.Entry vague = randomOfTier(pool, allowed);
		if (vague == null) {
			log(message("result_nothing"), true);
			return new Result(Kind.NOTHING, null, 0, 0d, false, false);
		}
		int tier = WishRewardTable.grantTier(vague.tier, luck);
		Item item = grantItem(hero, vague, tier);
		logItem("result_vague", item);
		return new Result(Kind.ITEM, item, tier, 0d, true, vague.wishOnly);
	}

	/** 生成奖励物品并交给英雄（背包放不下就掉在地上）。 */
	private static Item grantItem(Hero hero, WishCatalog.Entry entry, int tier) {
		Item item;
		try {
			item = Reflection.newInstance(entry.type);
		} catch (Throwable ignored) {
			return null;
		}
		if (item == null) return null;

		item.identify();
		int quantity = WishRewardTable.rollQuantity(hero, tier);
		if (quantity > 1) item.quantity(quantity);
		if (item.isUpgradable()) {
			int upgrades = WishRewardTable.rollUpgrades(hero, tier);
			if (upgrades > 0) item.upgrade(upgrades);
		}
		if (Random.Float() < WishRewardTable.traitChance(tier, LuckyBadge.luckBonus(hero))) {
			WishTraitGrant.grant(hero, tier);
		}

		boolean collected = false;
		try {
			collected = item.collect(hero.belongings.backpack);
		} catch (Throwable ignored) {
			collected = false;
		}
		if (!collected) {
			try {
				if (Dungeon.level != null) Dungeon.level.drop(item, hero.pos).sprite.drop(hero.pos);
			} catch (Throwable ignored) {
				//无关卡环境（测试）下不处理掉落
			}
		}
		return item;
	}

	private static WishCatalog.Entry randomOfTier(List<WishCatalog.Entry> pool, int tier) {
		List<WishCatalog.Entry> tiered = new ArrayList<>();
		for (WishCatalog.Entry entry : pool) {
			if (entry.tier == tier) tiered.add(entry);
		}
		if (tiered.isEmpty()) {
			int lowest = Integer.MAX_VALUE;
			for (WishCatalog.Entry entry : pool) lowest = Math.min(lowest, entry.tier);
			for (WishCatalog.Entry entry : pool) {
				if (entry.tier == lowest) tiered.add(entry);
			}
		}
		if (tiered.isEmpty()) return null;
		return Random.element(tiered);
	}

	private static String message(String key) {
		try {
			String value = Messages.get(KEY_PREFIX + key);
			if (value == null || value.equals(Messages.NO_TEXT_FOUND)) return null;
			return value;
		} catch (Throwable ignored) {
			return null;
		}
	}

	private static void log(String message, boolean warn) {
		if (message == null) return;
		try {
			if (warn) GLog.w(message);
			else GLog.i(message);
		} catch (Throwable ignored) {
			//无 UI 环境（测试）下不输出日志
		}
	}

	private static void logItem(String key, Item item) {
		if (item == null) {
			log(message(key), true);
			return;
		}
		try {
			log(String.format(message(key), item.name()), true);
		} catch (Throwable ignored) {
			//名称或文案缺失时跳过提示
		}
	}
}
