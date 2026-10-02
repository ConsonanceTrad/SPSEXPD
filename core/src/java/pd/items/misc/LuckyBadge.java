/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import pd.actors.buffs.AflyBless;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.items.Item;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The shared implementation of the three legacy SPS luck bonuses. */
public class LuckyBadge extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LuckyBadge.class)
			.t("name", "幸运胸章")
			.t("desc", "购买房产后商人老板送你的纪念品之一，据说可以小幅提升佩戴者的运气，并且可以被强化。");
	}


	public static final int MAX_ITEM_LUCK = 10;
	public static final int MAX_EXTRA_ITEMS = 64;

	{
		image = EquipmentJewelleryArtifactDict.LUCKY_BADGE;
		unique = true;
	}

	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return true; }

	public static int luckBonus(Hero hero) {
		if (hero == null) return 0;
		int bonus = 0;
		LuckyBadge badge = hero.belongings.getItem(LuckyBadge.class);
		if (badge != null) bonus += badge.level();
		if (hero.heroClass == HeroClass.SOLDIER) bonus += 5;
		if (hero.subClass == HeroSubClass.SUPERSTAR) bonus += 3;
		bonus += 3 * hero.buffs(AflyBless.class).size();
		return bonus;
	}

	public static float rareRewardChance(int bonus) {
		return (float)(1d - Math.pow(0.95d, bonus));
	}

	public static float additionalItemChance(int bonus) {
		return 0.3f + Math.min(bonus, MAX_ITEM_LUCK) * 0.05f;
	}

	public static int rollExtraItems(Hero hero) {
		float chance = additionalItemChance(luckBonus(hero));
		int extra = 0;
		while (extra < MAX_EXTRA_ITEMS && Random.Float() < chance) extra++;
		return extra;
	}
}
