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
	NORMAL(1f, null),
	/** 粗制：效果为标准药水的 1/3（2026-10 二次改版）。 */
	CRUDE(1f / 3f, "form_crude"),
	/** 标准：大型果实 + 盐 + 水 的完全效果产物（原「精制」，数值同普通）。 */
	STANDARD(1f, "form_standard"),
	/** 缓释片：免回合吞服、持续更久、不可投掷（批4）。 */
	TABLET(1f, "form_tablet"),
	/** 注射剂：免回合、瞬间化、数值更激烈（批4）。 */
	INJECTOR(1.25f, "form_injector"),
	/** 试剂：分装弱化版，效果为正常的 1/3（批4）。 */
	REAGENT(1f / 3f, "form_reagent");

	public final float scale;
	public final String nameKey;

	PotionForm(float scale, String nameKey) {
		this.scale = scale;
		this.nameKey = nameKey;
	}

	/** 是否有名字前缀（粗制/精制等需要「粗糙的 X」这类显示）。 */
	public boolean labeled() {
		return nameKey != null;
	}
}
