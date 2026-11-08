/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.damagetype;

import pd.actors.Char;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Chill;
import pd.actors.buffs.Corrosion;
import pd.actors.buffs.Corruption;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Electricity;
import pd.actors.blobs.StormCloud;
import pd.actors.blobs.ToxicGas;
import pd.items.KindOfWeapon;
import pd.items.equipment.armor.glyphs.AntiMagic;
import pd.items.equipment.armor.glyphs.Viscosity;
import pd.items.equipment.wands.Wand;
import pd.items.quest.Pickaxe;
import pd.levels.traps.GeyserTrap;

/**
 * SPSEXPD: 伤害类型族收口 —— 由任意伤害来源解析「类型族」与「物理细分」。
 *
 * <p>伤害类型体系定稿（2026-11 用户裁决）：</p>
 * <ul>
 * <li>物理族：物理细分五档令牌、旧地/风令牌（归并 钝/冲），以及 {@link Char}
 *     普攻（英雄普攻细分按其当前武器 {@code damageKind()} 解析，未分类武器视为
 *     无细分、不改变任何既有减免行为；怪物普攻暂无细分）；</li>
 * <li>法术族：元素令牌（火/冰/雷/光/毒/酸）、能量、诅咒、旧暗/混乱（归并能量），
 *     以及旧式魔法来源（法杖/Buff/Blob/职业技能令牌/魔法字体减免名单）；</li>
 * <li>特殊族：治疗/干扰/饥饿/疾病/反噬 —— 走各自路径，不参与格挡/法抗减免。</li>
 * </ul>
 *
 * <p>纯粹伤害走 {@link pd.actors.PureDamage} 独立管线，不经 {@code Char.damage}，
 * 不在本判定内。</p>
 */
public final class DamageKind {

	private DamageKind() { }

	/** 物理族判定（格挡值减免与护甲相性的适用范围） */
	public static boolean isPhysical( Object src ) {
		if (src == null) return false;
		if (src instanceof Char) return true;
		return PhysicalKind.of(src) != null;
	}

	/** 法术族判定（法抗值减免的适用范围）——先按令牌归一，再落旧式魔法判定 */
	public static boolean isMagic( Object src ) {
		if (src == null || isSpecial(src)) return false;
		return isMagicToken( canonicalToken(src) );
	}

	/** 类型令牌 → 是否法术族（法术=元素/能量/诅咒，含旧暗/混乱归并） */
	public static boolean isMagicToken( DamageType token ) {
		if (token == null) return false;
		return token instanceof DamageType.Energy || token instanceof DamageType.Curse
				|| token == DamageType.FIRE_DAMAGE || token == DamageType.ICE_DAMAGE
				|| token == DamageType.SHOCK_DAMAGE || token == DamageType.LIGHT_DAMAGE
				|| token == DamageType.POISON_DAMAGE || token == DamageType.ACID_DAMAGE
				|| token instanceof DamageType.Dark || token instanceof DamageType.Chaos;
	}

	/** 特殊族判定（治疗/干扰/饥饿/疾病/反噬）——含来源归一后的令牌形态 */
	public static boolean isSpecial( Object src ) {
		if (src == null) return false;
		if (src instanceof DamageType.Heal || src instanceof DamageType.Interfere
				|| src instanceof DamageType.Hunger || src instanceof DamageType.Disease
				|| src instanceof DamageType.Backlash) {
			return true;
		}
		return isSpecialToken( canonicalToken(src) );
	}

	/** 类型令牌 → 是否特殊族 */
	public static boolean isSpecialToken( DamageType token ) {
		if (token == null) return false;
		return token instanceof DamageType.Heal || token instanceof DamageType.Interfere
				|| token instanceof DamageType.Hunger || token instanceof DamageType.Disease
				|| token instanceof DamageType.Backlash;
	}

	/**
	 * SPSEXPD: 来源 → 类型令牌的统一归一（伤害类型体系定稿）。
	 * Buff/Blob/陷阱等来源按映射收口到类型令牌；元素/诅咒/能量令牌与
	 * 旧暗/混乱（归并能量）原样保留或按 canonical 归并；未覆盖来源返回 null
	 * （物理普攻/武器细分另走 Weapon.damageKind；旧式魔法来源保持既有判定）。
	 */
	public static DamageType canonicalToken( Object src ) {
		if (src == null) return null;
		//DOT/状态 → 类型令牌
		if (src instanceof Burning)                                return DamageType.FIRE_DAMAGE;
		if (src instanceof Chill || src instanceof Frost)          return DamageType.ICE_DAMAGE;
		if (src instanceof Electricity)                            return DamageType.SHOCK_DAMAGE;
		if (src instanceof ToxicGas || src instanceof Poison || src instanceof Ooze)
		                                                           return DamageType.POISON_DAMAGE;
		if (src instanceof Corrosion)                              return DamageType.ACID_DAMAGE;
		if (src instanceof Bleeding)                               return DamageType.DISEASE_DAMAGE;
		if (src instanceof Corruption)                             return DamageType.CURSE_DAMAGE;
		if (src instanceof Hunger)                                 return DamageType.HUNGER_DAMAGE;
		if (src instanceof GeyserTrap || src instanceof StormCloud) return DamageType.IMPACT_DAMAGE;
		if (src instanceof AscensionChallenge)                     return DamageType.BACKLASH_DAMAGE;
		if (src instanceof Pickaxe)                                return DamageType.PUNCTURE_DAMAGE;
		//旧地/风令牌归并到物理细分（钝/冲）
		if (src.getClass() == DamageType.EARTH_DAMAGE.getClass())  return DamageType.BLUNT_DAMAGE;
		if (src.getClass() == DamageType.WIND_DAMAGE.getClass())   return DamageType.IMPACT_DAMAGE;
		//旧暗/混乱令牌归并到能量
		if (src.getClass() == DamageType.DARK_DAMAGE.getClass() || src.getClass() == DamageType.CHAOS_DAMAGE.getClass())
		                                                           return DamageType.ENERGY_DAMAGE;
		//元素/能量/诅咒令牌（含 SpsMagicDamage 按名映射）由 Element 收口层解析
		if (Element.canonicalOf(src) != null)                      return Element.canonicalOf(src).damageType();
		//法杖按杖类映射元素（未映射者默认能量）
		if (src instanceof Wand && !(src instanceof pd.items.equipment.wands.SilverHand)) {
			return wandOf((Wand) src);
		}
		return null;
	}

	/** SPSEXPD: 法杖 → 元素/类型映射表（伤害类型体系定稿，未列入者默认能量） */
	public static DamageType wandOf( Wand wand ) {
		String n = wand.getClass().getSimpleName();
		switch (n) {
			//火
			case "WandOfFirebolt":
			case "WandOfFireblast":
			case "WandOfShatteredFireblast":
			case "WandOfMeteorite":       return DamageType.FIRE_DAMAGE;
			//冰
			case "WandOfFreeze":
			case "WandOfFrost":           return DamageType.ICE_DAMAGE;
			//雷
			case "WandOfLightning":       return DamageType.SHOCK_DAMAGE;
			//光
			case "WandOfLight":
			case "WandOfPrismaticLight":
			case "WandOfDisintegration":  return DamageType.LIGHT_DAMAGE;
			//毒
			case "WandOfSwamp":           return DamageType.POISON_DAMAGE;
			//酸
			case "WandOfAcid":
			case "WandOfCorrosion":       return DamageType.ACID_DAMAGE;
			//物理细分（归并旧地/风）
			case "WandOfLivingEarth":     return DamageType.BLUNT_DAMAGE;
			case "WandOfBlastWave":
			case "WandOfShockwave":       return DamageType.IMPACT_DAMAGE;
			//诅咒
			case "WandOfCorruption":      return DamageType.CURSE_DAMAGE;
			//物理中间变量（用于金属/刺轨等渗入）避免遗漏
			default:                      return DamageType.ENERGY_DAMAGE;
		}
	}

	/**
	 * SPSEXPD: 旧式「魔法来源」统一判定（原 Char.isLegacyMagicSource 清单收口至此，
	 * 两处判定不会漂移）。旧暗/混乱令牌已由 Element.canonicalOf 归并为能量，不在此列。
	 */
	public static boolean isLegacyMagic( Object src ) {
		if (src == null) return false;
		return src instanceof Wand || src instanceof Buff || src instanceof Blob
				|| src instanceof SpsMagicDamage
				|| AntiMagic.RESISTS.contains(src.getClass());
	}

	/**
	 * SPSEXPD: 由攻击者普攻解析物理细分 —— 英雄取其当前武器，未分类武器返回 null；
	 * 非英雄普攻（怪物、镜像等）暂无细分信息，返回 null。
	 */
	public static PhysicalKind physicalOf( Object attacker ) {
		if (attacker instanceof pd.actors.hero.Hero) {
			pd.actors.hero.Belongings b = ((pd.actors.hero.Hero) attacker).belongings;
			return ofWeapon(b == null ? null : b.attackingWeapon());
		}
		return null;
	}

	/** 由武器解析物理细分（未分类武器返回 null） */
	public static PhysicalKind ofWeapon( KindOfWeapon w ) {
		if (w instanceof pd.items.equipment.weapon.Weapon) {
			return ((pd.items.equipment.weapon.Weapon) w).damageKind();
		}
		return null;
	}
}
