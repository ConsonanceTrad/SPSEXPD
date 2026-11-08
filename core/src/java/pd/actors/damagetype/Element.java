/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.damagetype;

/**
 * SPSEXPD: 元素伤害类型的统一定义 —— 元素六系收口。
 *
 * <p>伤害类型体系定稿（2026-11 用户裁决）：元素只保留
 * <b>火/冰/雷/光/毒/酸</b> 六系；旧 地/风 归并到物理（钝击/冲击，见
 * {@link PhysicalKind}），旧 暗/混乱 归并到 {@link #ENERGY}（无属性法术）。
 * ENERGY 表示法术大类「无属性能量」，不属元素，但同住本枚举收口。</p>
 *
 * <p>原先元素伤害只有「来源令牌」两套并存（{@link DamageType} 单例对象与
 * {@link SpsMagicDamage} 枚举），没有数值化的元素加成/抗性查询入口。
 * 本枚举把两套令牌收口到一处：元素伤害加成、元素抗性一律按 {@code Element}
 * 查询（见 pd.actors.hero.HeroStats），再由 {@link #damageType()} 映射回
 * 既有的 Char.resist/weak/isImmune 匹配用 class，因此不改动任何旧调用点。</p>
 *
 * <p>旧成员 {@link #EARTH}/{@link #WIND}/{@link #DARK}/{@link #CHAOS}
 * 保留兼容（既有 ordinal 与附魔表引用不变），归并语义用
 * {@link #canonical()} 解析：canonical 为 null 表示该元素已并入
 * 物理细分（钝/冲，按 {@link PhysicalKind} 走物理管线）。</p>
 */
public enum Element {

	ENERGY(DamageType.ENERGY_DAMAGE),
	FIRE(DamageType.FIRE_DAMAGE),
	ICE(DamageType.ICE_DAMAGE),
	//SPSEXPD: 归并成员（兼容保留，canonical 无独立元素语义）
	EARTH(DamageType.EARTH_DAMAGE),
	SHOCK(DamageType.SHOCK_DAMAGE),
	LIGHT(DamageType.LIGHT_DAMAGE),
	DARK(DamageType.DARK_DAMAGE),
	//SPSEXPD: 归并成员（兼容保留）
	WIND(DamageType.WIND_DAMAGE),
	CHAOS(DamageType.CHAOS_DAMAGE),
	//SPSEXPD: 元素六系增补（毒、酸）
	POISON(DamageType.POISON_DAMAGE),
	ACID(DamageType.ACID_DAMAGE);

	private final DamageType damageType;

	Element(DamageType damageType) {
		this.damageType = damageType;
	}

	/** 对应的伤害来源令牌（Char.resist/weak/isImmune 按 class 匹配用） */
	public DamageType damageType() {
		return damageType;
	}

	/**
	 * SPSEXPD: 归并后的规范元素；返回 null 表示已并入物理细分（用
	 * {@link PhysicalKind#of(DamageType)} 解析 钝/冲）。
	 */
	public Element canonical() {
		switch (this) {
			case EARTH:
			case WIND:    return null;
			case DARK:
			case CHAOS:   return ENERGY;
			default:      return this;
		}
	}

	/** 从伤害来源对象解析元素类型；非元素来源返回 null */
	public static Element of(Object src) {
		if (src == null) return null;
		if (src instanceof SpsMagicDamage) return of((SpsMagicDamage) src);
		Class<?> srcClass = src.getClass();
		for (Element e : values()) {
			if (e.damageType.getClass() == srcClass) return e;
		}
		return null;
	}

	/** 从职业技能元素令牌解析（两套枚举同名，直接按名映射） */
	public static Element of(SpsMagicDamage src) {
		return src == null ? null : valueOf(src.name());
	}

	/**
	 * SPSEXPD: 从任意伤害来源解析「归并后的规范元素」：旧 地/风 令牌归并进物理后
	 * 返回 null（物理细分另查 {@link PhysicalKind}），旧 暗/混乱 归并为 ENERGY。
	 */
	public static Element canonicalOf(Object src) {
		Element e = of(src);
		return e == null ? null : e.canonical();
	}
}
