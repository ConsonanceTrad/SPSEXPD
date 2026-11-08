/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.damagetype;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.blobs.Blob;
import pd.items.KindOfWeapon;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.armor.glyphs.AntiMagic;

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

	/** 法术族判定（法抗值减免的适用范围） */
	public static boolean isMagic( Object src ) {
		if (src == null || isSpecial(src)) return false;
		if (Element.canonicalOf(src) != null) return true;
		return isLegacyMagic(src);
	}

	/** 特殊族判定（治疗/干扰/饥饿/疾病/反噬令牌） */
	public static boolean isSpecial( Object src ) {
		return src instanceof DamageType.Heal || src instanceof DamageType.Interfere
				|| src instanceof DamageType.Hunger || src instanceof DamageType.Disease
				|| src instanceof DamageType.Backlash;
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
