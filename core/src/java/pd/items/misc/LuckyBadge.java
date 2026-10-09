/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import pd.actors.buffs.AflyBless;
import pd.actors.buffs.LuckyMoment;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.perks.BornLucky;
import pd.items.Item;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.rings.RingOfWealth;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The shared implementation of the three legacy SPS luck bonuses. */
public class LuckyBadge extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LuckyBadge.class)
			.t("name", "幸运胸章")
			.t("desc", "购买房产后商人老板送你的纪念品之一。固定提供 3 点幸运，无法被强化。\n\n幸运还会提高种植收获的果实数量（每 7 点幸运，额外多出 0~1 枚果实），并让小概率产出大型果实（基础 2%，每 7 点幸运 +1%）。");
	}




	/** SPSEXPD: 幸运胸章固定提供的幸运点数（不再随等级成长）。 */
	public static final int BADGE_LUCK = 3;
	public static final int MAX_ITEM_LUCK = 10;
	public static final int MAX_EXTRA_ITEMS = 64;

	{
		image = EquipmentJewelleryArtifactDict.LUCKY_BADGE;
		unique = true;
	}

	@Override public boolean isIdentified() { return true; }
	//SPSEXPD: 幸运胸章不再可以强化——固定提供 3 点幸运
	@Override public boolean isUpgradable() { return false; }

	public static int luckBonus(Hero hero) {
		if (hero == null) return 0;
		int bonus = 0;
		//SPSEXPD: 持有幸运胸章即固定 +3（原按徽章等级计入）
		if (hero.belongings != null && hero.belongings.getItem(LuckyBadge.class) != null) bonus += BADGE_LUCK;
		//SPSEXPD: 财富之戒每 +1 级 = +1 点幸运
		bonus += Ring.getBuffedBonus(hero, RingOfWealth.Wealth.class);
		//SPSEXPD: 「幸运儿」特质
		bonus += BornLucky.luckOf(hero);
		if (hero.heroClass == HeroClass.SOLDIER) bonus += 5;
		if (hero.subClass == HeroSubClass.SUPERSTAR) bonus += 3;
		bonus += 3 * hero.buffs(AflyBless.class).size();
		//SPSEXPD: 彩虹三色堇的临时幸运
		bonus += LuckyMoment.BONUS * hero.buffs(LuckyMoment.class).size();
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

	/** SPSEXPD: 幸运带来的种植额外果实——均匀随机 0 ~ floor(幸运 / 7)。 */
	public static int plantExtraFruit(Hero hero) {
		int tiers = luckBonus(hero) / 7;
		return tiers <= 0 ? 0 : Random.Int(tiers + 1);
	}

	/** SPSEXPD: 种植掉的果实升为大型果实的概率：基础 2% + 每 7 点幸运 +1%。 */
	public static float largeFruitChance(Hero hero) {
		return 0.02f + 0.01f * (luckBonus(hero) / 7);
	}
}
