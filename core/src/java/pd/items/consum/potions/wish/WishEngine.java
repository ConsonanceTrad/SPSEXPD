/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
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
 * 使用许愿魔药后由 UI 传入愿望文本，判定顺序：
 * <ol>
 *   <li>死亡愿望；</li>
 *   <li>特质愿望（名称必须分毫不差）；</li>
 *   <li>具体物品愿望（按名称词匹配，按幸运值决定奖励等级）；</li>
 *   <li>怪物愿望（当前层普通怪）；</li>
 *   <li>类型词愿望（武器/神器/秘药等，从对应池随机抽）；</li>
 *   <li>负面效果 / 增益效果愿望；</li>
 *   <li>模糊兜底。</li>
 * </ol>
 */
public final class WishEngine {

	private static final String KEY_PREFIX = "items.consum.potions.elixirs.wishpotion.";

	public enum Kind { NOTHING, ITEM, MOB, TRAIT, BUFF, NEGATIVE, DEATH }

	/** 一次许愿的结果。 */
	public static final class Result {
		public final Kind kind;
		public final Item item;
		/** MOB 愿望：召出的怪物类；其它情况为 null。 */
		public final Class<? extends Mob> summoned;
		public final int tier;
		public final double accuracy;
		public final boolean weakened;
		public final boolean wishOnly;
		/** 附加说明：MOB 的怪物名、TRAIT 的特质名，其它情况为 null。 */
		public final String detail;

		Result(Kind kind, Item item, int tier, double accuracy, boolean weakened, boolean wishOnly) {
			this(kind, item, null, tier, accuracy, weakened, wishOnly, null);
		}

		Result(Kind kind, Item item, Class<? extends Mob> summoned, int tier, double accuracy,
			   boolean weakened, boolean wishOnly, String detail) {
			this.kind = kind;
			this.item = item;
			this.summoned = summoned;
			this.tier = tier;
			this.accuracy = accuracy;
			this.weakened = weakened;
			this.wishOnly = wishOnly;
			this.detail = detail;
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

		//2. 特质愿望：名称必须分毫不差
		WishTraitGrant.Outcome trait = WishTraitGrant.grantByName(hero, raw);
		if (trait == WishTraitGrant.Outcome.GRANTED) {
			logFormatted("result_trait", raw);
			return new Result(Kind.TRAIT, null, null, allowed, 1d, false, false, raw);
		}
		if (trait == WishTraitGrant.Outcome.UNAVAILABLE) {
			logFormatted("result_trait_unavailable", raw);
			return new Result(Kind.TRAIT, null, null, allowed, 1d, false, false, raw);
		}

		//3. 怪物愿望：只从当前层的普通怪轮转表里取（泛指"怪物"或点名某一怪物）
		ArrayList<Class<? extends Mob>> rotation = WishSummon.rotation();
		if (!rotation.isEmpty()) {
			Class<? extends Mob> pick = WishSummon.isGeneric(raw)
					? Random.element(rotation)
					: WishSummon.matchNamed(raw, rotation);
			if (pick != null) {
				String mobName = WishSummon.nameOf(pick);
				Mob mob = WishSummon.summon(pick);
				logFormatted(mob != null ? "result_mob" : "result_mob_failed", mobName);
				return new Result(Kind.MOB, null, pick, allowed, 1d, false, false, mobName);
			}
		}

		//4. 具体物品愿望（按名称词匹配）
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

		//5. 类型词愿望：从该类型的条目池里随机抽一件
		String kind = WishType.match(raw);
		if (kind != null) {
			WishCatalog.Entry pick = WishCatalog.randomOfKind(kind);
			if (pick != null) {
				int tier = WishRewardTable.grantTier(pick.tier, luck);
				Item item = grantItem(hero, pick, tier);
				logItem("result_kind", item);
				return new Result(Kind.ITEM, item, tier, 1d, false, pick.wishOnly);
			}
		}

		//6. 负面效果愿望
		String negative = WishEffects.negativeKeyword(raw);
		if (negative != null) {
			WishEffects.applyNegative(hero, negative, allowed);
			log(message("result_negative"), true);
			return new Result(Kind.NEGATIVE, null, allowed, 0d, false, false);
		}

		//7. 增益效果愿望
		String positive = WishEffects.positiveKeyword(raw);
		if (positive != null) {
			WishEffects.applyPositive(hero, positive, allowed);
			log(message("result_buff"), false);
			return new Result(Kind.BUFF, null, allowed, 0d, false, false);
		}

		//8. 什么都没对上的模糊愿望：按可达等级随机给一件
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

		try {
			item.identify();
		} catch (Throwable ignored) {
			//测试等未初始化物品状态容器（如 Potion.handler）的环境下跳过鉴定
		}
		int quantity = WishRewardTable.rollQuantity(hero, tier);
		if (quantity > 1) item.quantity(quantity);
		if (item.isUpgradable()) {
			int upgrades = WishRewardTable.rollUpgrades(hero, tier);
			if (upgrades > 0) item.upgrade(upgrades);
		}
		if (Random.Float() < WishRewardTable.traitChance(tier, LuckyBadge.luckBonus(hero))) {
			WishTraitGrant.grantRandom(hero, tier);
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
		logFormatted(key, item == null ? null : item.name());
	}

	private static void logFormatted(String key, String value) {
		if (value == null) {
			log(message(key), true);
			return;
		}
		try {
			log(String.format(message(key), value), true);
		} catch (Throwable ignored) {
			//名称或文案缺失时跳过提示
		}
	}
}
