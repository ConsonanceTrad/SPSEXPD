/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.actors.hero.Hero;
import pd.items.misc.LuckyBadge;
import render.utils.math.Random;

/**
 * SPSEXPD: 许愿奖励等级表。
 *
 * 奖励分为 4 级，每级有幸运门槛：幸运未达门槛时，描述再准确也拿不到该级奖励。
 * 幸运越高，物品数量越多、装备越容易带强化、许愿特质越容易触发。
 */
public final class WishRewardTable {

	public static final int MAX_TIER = 4;

	/** 下标即等级，LUCK_THRESHOLD[1] 为 1 级门槛，依此类推。 */
	private static final int[] LUCK_THRESHOLD = { 0, 0, 5, 10, 15 };

	private WishRewardTable() {
	}

	/** 当前幸运值能拿到的最高奖励等级。 */
	public static int maxTierForLuck(int luck) {
		int tier = 1;
		for (int candidate = 1; candidate <= MAX_TIER; candidate++) {
			if (luck >= LUCK_THRESHOLD[candidate]) tier = candidate;
		}
		return tier;
	}

	/** 幸运不足时对目标等级做削弱，最低 1 级。 */
	public static int grantTier(int itemTier, int luck) {
		return Math.max(1, Math.min(itemTier, maxTierForLuck(luck)));
	}

	/** 奖励数量：等级加成 + 幸运带来的额外份数。 */
	public static int rollQuantity(Hero hero, int tier) {
		int quantity = 1 + Math.max(0, tier - 1);
		if (hero != null) quantity += LuckyBadge.rollExtraItems(hero);
		return quantity;
	}

	/** 装备强化次数：每级奖励各给一次机会，幸运越高概率越大。 */
	public static int rollUpgrades(Hero hero, int tier) {
		int luck = hero == null ? 0 : LuckyBadge.luckBonus(hero);
		float chance = LuckyBadge.rareRewardChance(luck);
		int upgrades = 0;
		for (int i = 0; i < tier; i++) {
			if (Random.Float() < chance) upgrades++;
		}
		return upgrades;
	}

	/** 许愿特质触发概率：随等级与幸运提升，单调不降。 */
	public static float traitChance(int tier, int luck) {
		float chance = 0.05f * Math.max(1, tier) + 0.02f * Math.max(0, luck);
		return Math.min(1f, chance);
	}
}
