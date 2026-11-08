/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.damagetype;

/**
 * Distinct SPS elemental damage sources used by resistances and immunities.
 *
 * SPSEXPD: 伤害类型细分体系定稿 ——
 * <ul>
 * <li>物理细分：刺击/钝击/切割/冲击/挤压（常规近战与枪械伤害落点，由
 * {@link PhysicalKind} 归类；常规武器默认由其类型映射出细分）。</li>
 * <li>法术：能量（无属性）/ 元素六系（火/冰/雷/光/毒/酸，旧 地/风 归并到 钝/冲，
 * 旧 暗/混乱 归并到 能量）/ 纯粹（由 {@link pd.actors.PureDamage} 独立管线，
 * 不设令牌）/ 诅咒（仪式类伤害）。</li>
 * <li>特殊：治疗/干扰/饥饿/疾病/反噬 —— 这些不参与常规减免，走各自路径。</li>
 * </ul>
 * 旧令牌（{@link Earth}/{@link Wind}/{@link Dark}/{@link Chaos}）保留兼容，
 * 语义由 {@link Element#canonical()} / {@link PhysicalKind#of(DamageType)} 统一归并。
 */
public abstract class DamageType {
	public static final Energy ENERGY_DAMAGE = new Energy();
	public static final Fire FIRE_DAMAGE = new Fire();
	public static final Ice ICE_DAMAGE = new Ice();
	public static final Shock SHOCK_DAMAGE = new Shock();
	public static final Earth EARTH_DAMAGE = new Earth();
	public static final Light LIGHT_DAMAGE = new Light();
	public static final Dark DARK_DAMAGE = new Dark();
	//SPSEXPD: 新增两系属性类型（风、混乱）——已归并，保留兼容
	public static final Wind WIND_DAMAGE = new Wind();
	public static final Chaos CHAOS_DAMAGE = new Chaos();
	//SPSEXPD: 元素六系增补（毒、酸）
	public static final Poison POISON_DAMAGE = new Poison();
	public static final Acid ACID_DAMAGE = new Acid();

	//SPSEXPD: 物理细分五档令牌（刺/钝/切/冲/挤）
	public static final Puncture PUNCTURE_DAMAGE = new Puncture();
	public static final Blunt BLUNT_DAMAGE = new Blunt();
	public static final Slash SLASH_DAMAGE = new Slash();
	public static final Impact IMPACT_DAMAGE = new Impact();
	public static final Crush CRUSH_DAMAGE = new Crush();

	//SPSEXPD: 法术类新增令牌（诅咒）
	public static final Curse CURSE_DAMAGE = new Curse();

	//SPSEXPD: 特殊类五档令牌（治疗/干扰/饥饿/疾病/反噬）
	public static final Heal HEAL_DAMAGE = new Heal();
	public static final Interfere INTERFERE_DAMAGE = new Interfere();
	public static final Hunger HUNGER_DAMAGE = new Hunger();
	public static final Disease DISEASE_DAMAGE = new Disease();
	public static final Backlash BACKLASH_DAMAGE = new Backlash();

	public static final class Energy extends DamageType { }
	public static final class Fire extends DamageType { }
	public static final class Ice extends DamageType { }
	public static final class Shock extends DamageType { }
	public static final class Earth extends DamageType { }
	public static final class Light extends DamageType { }
	public static final class Dark extends DamageType { }
	//SPSEXPD: 新增两系属性类型（风、混乱）——已归并，保留兼容
	public static final class Wind extends DamageType { }
	public static final class Chaos extends DamageType { }
	//SPSEXPD: 元素六系增补
	public static final class Poison extends DamageType { }
	public static final class Acid extends DamageType { }
	//SPSEXPD: 物理细分五档
	public static final class Puncture extends DamageType { }
	public static final class Blunt extends DamageType { }
	public static final class Slash extends DamageType { }
	public static final class Impact extends DamageType { }
	public static final class Crush extends DamageType { }
	//SPSEXPD: 诅咒
	public static final class Curse extends DamageType { }
	//SPSEXPD: 特殊类五档
	public static final class Heal extends DamageType { }
	public static final class Interfere extends DamageType { }
	public static final class Hunger extends DamageType { }
	public static final class Disease extends DamageType { }
	public static final class Backlash extends DamageType { }
}
