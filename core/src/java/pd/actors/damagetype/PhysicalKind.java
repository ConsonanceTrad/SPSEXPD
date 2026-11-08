/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.damagetype;

/**
 * SPSEXPD: 物理伤害细分收口层 —— 刺击/钝击/切割/冲击/挤压五档。
 *
 * <p>相性关系（受击端减免按此表的语义实现）：</p>
 * <ul>
 * <li>刺击：轻便近战/破片穿甲枪械，伤害偏低但对重甲足额；</li>
 * <li>钝击：冲击枪械/重锤，攻速慢但对所有物理防护足额；</li>
 * <li>切割：轻巧单手/特殊枪械，对无甲大量伤害与流血，对有甲略差；</li>
 * <li>冲击：炸弹/扇/乐器/音波枪械，伤害低且随距离衰减，无视部分护甲；</li>
 * <li>挤压：超重型武器，攻速极慢，无视大部分护甲。</li>
 * </ul>
 *
 * <p>归并口径（伤害类型体系定稿）：旧 EARTH（地）→ {@link #BLUNT}，
 * 旧 WIND（风）→ {@link #IMPACT}。</p>
 */
public enum PhysicalKind {
	//SPSEXPD: drFactor = 攻击端对目标 drRoll 的护甲相性系数（伤害类型体系定稿，可调参）
	//刺击对重甲足额、钝击对所有物理防护足额、切割对有甲略差、冲击/挤压无视部分护甲
	PUNCTURE( 1.00f ),
	BLUNT   ( 1.00f ),
	SLASH   ( 0.85f ),
	IMPACT  ( 0.65f ),
	CRUSH   ( 0.30f );

	private final float drFactor;

	PhysicalKind( float drFactor ) {
		this.drFactor = drFactor;
	}

	/** 攻击端护甲相性：目标 drRoll 的生效系数 */
	public float drFactor() {
		return drFactor;
	}

	/** SPSEXPD: 对无甲目标的伤害加成（切割——对无甲大量伤害） */
	public float unarmoredDamageBonus() {
		return this == SLASH ? 1.25f : 1f;
	}

	/** 对应的来源令牌（打点用） */
	public DamageType damageType() {
		switch (this) {
			case PUNCTURE: return DamageType.PUNCTURE_DAMAGE;
			case BLUNT:    return DamageType.BLUNT_DAMAGE;
			case SLASH:    return DamageType.SLASH_DAMAGE;
			case IMPACT:   return DamageType.IMPACT_DAMAGE;
			case CRUSH:    return DamageType.CRUSH_DAMAGE;
			default:       return null;
		}
	}

	/** 从来源令牌解析物理细分；非物理来源返回 null */
	public static PhysicalKind of(DamageType src) {
		if (src == null) return null;
		Class<?> srcClass = src.getClass();
		if (srcClass == DamageType.Puncture.class) return PUNCTURE;
		if (srcClass == DamageType.Blunt.class)    return BLUNT;
		if (srcClass == DamageType.Slash.class)    return SLASH;
		if (srcClass == DamageType.Impact.class)   return IMPACT;
		if (srcClass == DamageType.Crush.class)    return CRUSH;
		//归并：旧地系 → 钝击、旧风系 → 冲击
		if (srcClass == DamageType.Earth.class)    return BLUNT;
		if (srcClass == DamageType.Wind.class)     return IMPACT;
		return null;
	}

	/** 从任意伤害来源对象解析；非物理来源返回 null */
	public static PhysicalKind of(Object src) {
		if (src == null) return null;
		if (src instanceof DamageType) return of((DamageType) src);
		for (PhysicalKind k : values()) {
			if (k.damageType().getClass() == src.getClass()) return k;
		}
		if (DamageType.EARTH_DAMAGE.getClass() == src.getClass()) return BLUNT;
		if (DamageType.WIND_DAMAGE.getClass() == src.getClass())  return IMPACT;
		return null;
	}
}
