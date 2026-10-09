/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.damagetype;

/**
 * SPSEXPD: 元素伤害类型的统一定义 —— 七系元素枚举。
 *
 * <p>原先元素伤害只有「来源令牌」两套并存（{@link DamageType} 单例对象与
 * {@link SpsMagicDamage} 枚举），没有数值化的元素加成/抗性查询入口。
 * 本枚举把两套令牌收口到一处：元素伤害加成、元素抗性一律按 {@code Element}
 * 查询（见 pd.actors.hero.HeroStats），再由 {@link #damageType()} 映射回
 * 既有的 Char.resist/weak/isImmune 匹配用 class，因此不改动任何旧调用点。</p>
 */
public enum Element {

	ENERGY(DamageType.ENERGY_DAMAGE),
	FIRE(DamageType.FIRE_DAMAGE),
	ICE(DamageType.ICE_DAMAGE),
	EARTH(DamageType.EARTH_DAMAGE),
	SHOCK(DamageType.SHOCK_DAMAGE),
	LIGHT(DamageType.LIGHT_DAMAGE),
	DARK(DamageType.DARK_DAMAGE);

	private final DamageType damageType;

	Element(DamageType damageType) {
		this.damageType = damageType;
	}

	/** 对应的伤害来源令牌（Char.resist/weak/isImmune 按 class 匹配用） */
	public DamageType damageType() {
		return damageType;
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
}
