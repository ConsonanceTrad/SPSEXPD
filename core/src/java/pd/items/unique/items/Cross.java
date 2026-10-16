/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.equipment.trinkets.Trinket;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 十字架 —— 信徒一档（面具 B 路线）的徽章（饰物）。
 *
 * <p>带着它就生效：_有害魔力_——武器诅咒、恶性陷阱、邪恶法术——的出现概率与伤害都会被压低。</p>
 */
public class Cross extends Trinket implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.FOLLOWER_CUSHION_T1_2_CROSS;

	/** 有害魔力伤害的减免比例。 */
	public static final float HARM_REDUCTION = 0.5f;
	/** 武器诅咒出现概率的折扣。 */
	public static final float CURSE_FACTOR = 0.5f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Cross.class)
			.t("name", "十字架")
			.t("stats_desc", "有害魔力的出现率与伤害降低_%1$d%%_（武器诅咒、恶性陷阱、邪恶法术）。")
			.t("desc", "一枚随身带着的小十字架，握久了会有点温。\n\n只要_带在身上_就生效：_有害魔力_的出现概率与伤害都会被压低——包括_武器诅咒_、_恶性陷阱_与_邪恶法术_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = pd.atlas.items.SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	@Override
	protected int upgradeEnergyCost() {
		return 2;
	}

	@Override
	public String statsDesc() {
		return Messages.get(this, "stats_desc", Math.round(HARM_REDUCTION * 100f));
	}

	/** SPSEXPD: 带在身上（背包或装备位）就生效。 */
	public static boolean active(Hero hero) {
		return hero != null && hero.belongings.getItem(Cross.class) != null;
	}

	/**
	 * SPSEXPD: 在 Hero.damage 里调用——把「有害魔力」造成的伤害压下来。
	 * 来自角色的物理攻击不算；饥饿等自然损耗也不算。
	 */
	public static float reduceHarm(Hero hero, Object src, float damage) {
		if (damage <= 0f || !active(hero)) return damage;
		if (src instanceof Char) return damage;
		if (src instanceof Hunger) return damage;
		return damage * HARM_REDUCTION;
	}

	/** SPSEXPD: 武器诅咒出现概率的折扣（1 = 不变）。 */
	public static float curseChanceFactor(Hero hero) {
		return active(hero) ? CURSE_FACTOR : 1f;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
