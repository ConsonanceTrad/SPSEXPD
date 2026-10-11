/* Special Surprise Pixel Dungeon, GPLv3 or later. */

package pd.actors.hero;

import java.util.Collections;

import pd.actors.damagetype.Element;
import pd.actors.hero.perks.Perk;
import pd.items.KindOfWeapon;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.enchantments.*;

/**
 * SPSEXPD: 角色属性统一定义层（暗黑式）—— 暴击、暴击倍率、物理/元素/纯粹伤害加成、
 * 元素抗性、魔法抗性、回血加成的唯一计算与查询入口。
 *
 * <p>此前属性散落在 Hero 字段、Perk 特质、戒指乘区里，且部分属性（魔法抗性、回血加成）
 * 只写不读。本类统一收口：</p>
 * <ul>
 *   <li>数值来源 = 基础字段（Hero.criticalChance / regenerationBonus）+ 特质钩子
 *       （{@link Perk#physicalDamageBonus()} 等，默认 0，子类覆写即贡献加值）+ 装备钩子（预留）；</li>
 *   <li>计算一律带 null 守卫，供无头校验与运行期临时对象调用；</li>
 *   <li>显示（WndHero 属性页）与伤害管线（Char.attack / Char.damage）都只读本类。</li>
 * </ul>
 *
 * <p>加成语义：返回百分比小数，0.1 表示 +10%。伤害加成作用于输出端（英雄造成的伤害），
 * 抗性作用于受击端（英雄承受的伤害）。</p>
 */
public class HeroStats {

	private HeroStats() {
	}

	// ---- 暴击 ----------------------------------------------------------

	/** 暴击几率（0-1，已夹取） */
	public static float critChance(Hero hero) {
		if (hero == null) return 0f;
		return Math.max(0f, Math.min(1f, hero.criticalChance));
	}

	/** 暴击倍率 = 1.5 基数 + 重击（HardCrit）加成 + 特质/装备加成 */
	public static float critMultiplier(Hero hero) {
		if (hero == null) return Critical.BASE_MULTIPLIER;
		return Critical.BASE_MULTIPLIER + Critical.hardCritBonus(hero) + sumCritMultiplierBonus(hero);
	}

	/** 特质/装备提供的额外暴击倍率（加法，默认 0，预留扩展点） */
	public static float critMultiplierBonus(Hero hero) {
		return sumCritMultiplierBonus(hero);
	}

	private static float sumCritMultiplierBonus(Hero hero) {
		float sum = 0f;
		for (Perk p : perks(hero)) sum += p.critMultiplierBonus();
		return sum + equipmentCritMultiplierBonus(hero);
	}

	// ---- 伤害加成（输出端） --------------------------------------------

	/** 物理伤害加成（近战/投掷等武器伤害） */
	public static float physicalDamageBonus(Hero hero) {
		float sum = 0f;
		for (Perk p : perks(hero)) sum += p.physicalDamageBonus();
		return sum + equipmentPhysicalDamageBonus(hero);
	}

	/** 元素伤害加成（按元素系独立计算） */
	public static float elementBonus(Hero hero, Element element) {
		if (element == null) return 0f;
		float sum = 0f;
		for (Perk p : perks(hero)) sum += p.elementBonus(element);
		return sum + equipmentElementBonus(hero, element);
	}

	/** 纯粹伤害加成（纯粹伤害无视一切防御，见 PureDamage） */
	public static float pureDamageBonus(Hero hero) {
		float sum = 0f;
		for (Perk p : perks(hero)) sum += p.pureDamageBonus();
		return sum + equipmentPureDamageBonus(hero);
	}

	// ---- 减免（受击端） ------------------------------------------------

	/** 元素抗性（0~0.9），对该系元素伤害按比例减免 */
	public static float elementResistance(Hero hero, Element element) {
		if (hero == null || element == null) return 0f;
		float sum = 0f;
		for (Perk p : perks(hero)) sum += p.elementResistance(element);
		return Math.max(0f, Math.min(0.9f, sum));
	}

	/** 魔法抗性（0~0.9），对魔法伤害按比例减免（原 Hero.magicalResistance 收口至此） */
	public static float magicResistance(Hero hero) {
		if (hero == null) return 0f;
		float sum = 0f;
		for (Perk p : perks(hero)) sum += p.magicResistance();
		return Math.max(0f, Math.min(0.9f, sum));
	}

	/** 生命回复速度加成（0.25 = 回复速度 +25%） */
	public static float regenBonus(Hero hero) {
		if (hero == null) return 0f;
		return Math.max(0f, hero.regenerationBonus);
	}

	// ---- 应用（伤害管线调用） ------------------------------------------

	/** 输出端：按物理伤害加成放大 */
	public static int applyPhysical(Hero hero, int dmg) {
		if (hero == null || dmg <= 0) return dmg;
		return Math.max(1, Math.round(dmg * (1f + physicalDamageBonus(hero))));
	}

	/** 输出端：按元素伤害加成放大 */
	public static int applyElement(Hero hero, int dmg, Element element) {
		if (hero == null || dmg <= 0) return dmg;
		return Math.max(1, Math.round(dmg * (1f + elementBonus(hero, element))));
	}

	/** 输出端：按纯粹伤害加成放大 */
	public static int applyPure(Hero hero, int dmg) {
		if (hero == null || dmg <= 0) return dmg;
		return Math.max(1, Math.round(dmg * (1f + pureDamageBonus(hero))));
	}

	/** 受击端：元素抗性后的剩余比例（1.0 = 无减免） */
	public static float elementTakenMultiplier(Hero hero, Element element) {
		return 1f - elementResistance(hero, element);
	}

	/** 受击端：魔法抗性后的剩余比例（1.0 = 无减免） */
	public static float magicTakenMultiplier(Hero hero) {
		return 1f - magicResistance(hero);
	}

	// ---- 钩子来源 ------------------------------------------------------

	/** 已获得的特质（hero/heroPerk 为空时返回空集合，供无头校验安全遍历） */
	private static Iterable<Perk> perks(Hero hero) {
		if (hero == null || hero.heroPerk == null) return Collections.emptyList();
		Iterable<Perk> ps = hero.heroPerk.getPerks();
		return ps == null ? Collections.<Perk>emptyList() : ps;
	}

	//SPSEXPD: 装备侧钩子 —— 附魔直接贡献英雄属性，让附魔从「触发型效果」变成可选的专精玩法。
	/** 元素附魔（Enchantment* 系列）→ 同系元素伤害加成 */
	private static final float ELEMENT_ENCHANT_BONUS = 0.2f;
	/** 致命附魔（Vorpal）→ 暴击倍率加成 */
	private static final float CRIT_ENCHANT_BONUS = 0.25f;

	private static final Object[][] ELEMENT_ENCHANTS = {
			{Element.FIRE, EnchantmentFire.class, EnchantmentFire2.class},
			{Element.ICE, EnchantmentIce.class, EnchantmentIce2.class},
			{Element.ENERGY, EnchantmentEnergy.class, EnchantmentEnergy2.class},
			{Element.EARTH, EnchantmentEarth.class, EnchantmentEarth2.class},
			{Element.SHOCK, EnchantmentShock.class, EnchantmentShock2.class},
			{Element.LIGHT, EnchantmentLight.class, EnchantmentLight2.class},
			{Element.DARK, EnchantmentDark.class, EnchantmentDark2.class},
	};

	private static float equipmentCritMultiplierBonus(Hero hero) {
		return equippedEnchantment(hero) instanceof Vorpal ? CRIT_ENCHANT_BONUS : 0f;
	}

	//SPSEXPD: 装备侧的物理加成不在这里重复计算——武器改造石（Augment）与狂怒戒指已在各自管线生效；
	// 纯粹伤害的来源是神器与技能，同样没有装备侧来源。
	private static float equipmentPhysicalDamageBonus(Hero hero) {
		return 0f;
	}

	private static float equipmentElementBonus(Hero hero, Element element) {
		if (element == null) return 0f;
		Weapon.Enchantment ench = equippedEnchantment(hero);
		if (ench == null) return 0f;
		for (Object[] entry : ELEMENT_ENCHANTS) {
			for (int i = 1; i < entry.length; i++) {
				if (entry[i] == ench.getClass()) return entry[0] == element ? ELEMENT_ENCHANT_BONUS : 0f;
			}
		}
		return 0f;
	}

	private static float equipmentPureDamageBonus(Hero hero) {
		return 0f;
	}

	/** 当前主手武器的附魔（无武器 / 非 Weapon 时返回 null） */
	private static Weapon.Enchantment equippedEnchantment(Hero hero) {
		if (hero == null || hero.belongings == null) return null;
		KindOfWeapon weapon = hero.belongings.weapon();
		return weapon instanceof Weapon ? ((Weapon) weapon).enchantment : null;
	}
}
