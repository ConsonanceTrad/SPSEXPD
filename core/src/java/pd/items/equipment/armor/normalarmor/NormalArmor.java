/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.normalarmor;

import pd.atlas.IconEntry;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.specialarmor.AsceticArmor;
import pd.items.equipment.armor.specialarmor.FollowerArmor;
import pd.items.equipment.armor.specialarmor.HuntressArmor;
import pd.items.equipment.armor.specialarmor.MageArmor;
import pd.items.equipment.armor.specialarmor.PerformerArmor;
import pd.items.equipment.armor.specialarmor.RogueArmor;
import pd.items.equipment.armor.specialarmor.SoldierArmor;
import pd.items.equipment.armor.specialarmor.WarriorArmor;
import pd.items.equipment.rings.RingOfEvasion;
import pd.messages.Messages;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

/** Shared implementation of SPS-PD's defense, dexterity, stealth and energy armor stats. */
public class NormalArmor extends Armor {
	{
		image = SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NormalArmor.class)
			.t("sps_stats", "这件防具的闪避倍率为_%1$s_，潜行为_%2$s_，技能能量为_%3$d_。");
	}




	public final float DEX;
	public final float STE;
	public final int ENG;

	//SPSEXPD: baseMin/baseMax/strengthOffset/minGrowth/maxGrowth 是旧版 DR 与力量需求数据，
	// 数值曲线统一到破碎式（Armor）后已不再参与计算；参数保留以免改动全部子类构造。
	protected NormalArmor(int tier, float dex, float stealth, int energy,
			int baseMin, int baseMax, int strengthOffset, int minGrowth, int maxGrowth, IconEntry image) {
		super(tier);
		DEX = dex;
		STE = stealth;
		ENG = energy;
		this.image = image;
	}

	//SPSEXPD: DR 与力量需求统一到破碎式（Armor）——DRMax = tier*(2+lvl)、DRMin = lvl（均取非负），
	// STRReq 随等级下降，力量不足才扣减伤（超额力量不再倒加 DR）。
	// baseMin/baseMax/minGrowth/maxGrowth/strengthOffset 作为旧版原始数据保留在构造参数里。
	@Override
	public int DRMin(int level) {
		//tier 0 的护甲（BaseArmor/ErrorArmor）恒 0 防御，不走破碎曲线
		if (tier <= 0) return 0;
		return Math.max(0, Math.min(super.DRMax(level), super.DRMin(level)));
	}

	@Override
	public int DRMax(int level) {
		//tier 0 的护甲在破碎曲线的 lvl>max 翻转分支下会算出非 0 防御，这里直接归零
		if (tier <= 0) return 0;
		return Math.max(0, super.DRMax(level));
	}

	@Override
	public float evasionFactor(Char owner, float evasion) {
		return super.evasionFactor(owner, evasion) * dexterityFactor(owner);
	}

	/** SPSEXPD: DEX 由「闪避倍率」收敛为有界加项（1.0 为中性，上下各按 50% 折算）。 */
	private float dexterityFactor(Char owner) {
		return 1f + (DEX + RingOfEvasion.dexterityBonus(owner) - 1f) * 0.5f;
	}

	@Override public float speedFactor(Char owner, float speed) { return speed; }

	@Override
	public float stealthFactor(Hero owner) {
		int encumbrance = STRReq() - owner.STR();
		float stealth = STE + RingOfEvasion.stealthBonus(owner);
		return encumbrance > 0 ? (float)(stealth / Math.pow(1.5, encumbrance)) : stealth;
	}

	@Override public int energyFactor(Hero owner) { return ENG; }

	@Override
	public String info() {
		return super.info() + "\n\n" + Messages.get(NormalArmor.class, "sps_stats",
				Messages.decimalFormat("#.##", DEX), Messages.decimalFormat("#.##", STE), ENG);
	}

	public Item safeUpgrade() { return upgrade(glyph != null); }

	@Override
	public int value() {
		int price = 100;
		if (glyph != null) price = (int)(price * 1.5f);
		if (cursed && cursedKnown) price /= 2;
		if (levelKnown && level() > 0) price *= level() + 1;
		else if (levelKnown && level() < 0) price /= 1 - level();
		return Math.max(1, price);
	}

	/** SPSXPD: 该英雄职业对应的英雄护甲外观图标（护甲工具包换皮用）。 */
	public static pd.atlas.IconEntry heroicIcon(Hero owner) {
		NormalArmor armor = upgrade(owner);
		return armor == null ? null : armor.image;
	}

	public static NormalArmor upgrade(Hero owner) {
		if (owner == null || owner.heroClass == null) return null;
		switch (owner.heroClass) {
			case WARRIOR: return new WarriorArmor();
			case MAGE: return new MageArmor();
			case ROGUE: return new RogueArmor();
			case HUNTRESS: return new HuntressArmor();
			case PERFORMER: return new PerformerArmor();
			case SOLDIER: return new SoldierArmor();
			case FOLLOWER: return new FollowerArmor();
			case ASCETIC: return new AsceticArmor();
			default: return null;
		}
	}
}
