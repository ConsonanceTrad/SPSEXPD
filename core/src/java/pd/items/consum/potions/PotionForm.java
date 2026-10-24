/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions;

/**
 * SPSEXPD: 药水形态（docs/alchemy-rework.md 裁决 #3 / §6.1）。
 *
 * <p>粗制/精制、缓释片/注射剂/试剂都是「Potion 的形态维度」，效果结算按
 * {@link #scale} 折算，而不是为每种药水×每种形态建子类。
 * 形态随物品存档，旧档缺省 {@link #NORMAL}。
 */
public enum PotionForm {

	/** 普通（野外/商店/旧配方产出的原药水）。 */
	NORMAL(1f, null, false),
	/** 粗制：效果为标准药水的 1/3（2026-10 二次改版）。前置词。 */
	CRUDE(1f / 3f, "form_crude", false),
	/** 标准：大型果实 + 盐 + 水 的完全效果产物（原「精制」）。前置词。 */
	STANDARD(1f, "form_standard", false),
	/** 缓释片：免回合吞服、持续更久、不可投掷。后缀（「治疗缓释片」）。 */
	TABLET(1f, "form_tablet", true),
	/** 注射剂：免回合、瞬间化、数值更激烈。后缀（「治疗注射剂」）。 */
	INJECTOR(1.25f, "form_injector", true),
	/** 试剂：分装弱化版，效果为正常的 1/3。后缀（「治疗试剂」）。 */
	REAGENT(1f / 3f, "form_reagent", true),
	/** 药片：蒸馏得到的中间形态，加盐后压成缓释片。后缀（「治疗药片」）。 */
	PILL(1f, "form_pill", true),
	/** 安培：分装灌液机产物，需配合投掷注射针使用。后缀（「治疗安培」）。 */
	AMPOULE(1f, "form_ampoule", true);

	public final float scale;
	public final String nameKey;
	/** SPSEXPD: 形态词缀放在名字**后面**（药片/缓释片/安培…），false = 放在前面（粗制/标准）。 */
	public final boolean suffix;

	PotionForm(float scale, String nameKey, boolean suffix) {
		this.scale = scale;
		this.nameKey = nameKey;
		this.suffix = suffix;
	}

	/** 是否带形态词缀。 */
	public boolean labeled() {
		return nameKey != null;
	}
}
