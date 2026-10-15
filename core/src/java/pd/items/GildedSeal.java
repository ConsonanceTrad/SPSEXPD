/* Special Surprise Pixel Dungeon, GPLv3 or later. */

package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 镀金纹章 —— 完整纹章经矮人国王皇冠二次强化后的形态。
 *
 * <p>比完整纹章更强：携带升级上限 _7 级_；贴附后同样解锁连击技，并且
 * <b>连击被打断的时间翻倍</b>、<b>打断后保留一半层数</b>，下次连击开始时继承。</p>
 */
public class GildedSeal extends SealComplete {

	/** SPSEXPD: 特殊物品编号（见 UniqueItemId）。 */
	public static final UniqueItemId UNIQUE_ID = UniqueItemId.WARRIOR_SEAL_T2_1_GILDED;

	@Override public UniqueItemId uniqueId() { return UNIQUE_ID; }

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(GildedSeal.class)
			.t("name", "镀金纹章")
			.t("ac_affix", "贴附")
			.t("prompt", "选择一件护甲。")
			.t("unknown_armor", "你需要先鉴定那件护甲有无诅咒。")
			.t("already_affixed", "这件护甲上已经贴附了一枚纹章，不能再贴附第二枚。")
			.t("cursed_armor", "纹章不能贴附于被诅咒的盔甲。")
			.t("affix", "你将镀金纹章佩挂在了护甲上！")
			.t("inscribed", "纹章刻有_%s_。")
			.t("choose_title", "选择一个刻印")
			.t("choose_desc", "这件护甲与镀金纹章均刻有刻印。请选择一个想保留的刻印。\n\n护甲刻印：%1$s\n镀金纹章刻印：%2$s\n\n注意，如果选择保留护甲的刻印，纹章将无法转移该刻印。")
			.t("desc", "矮人国王的金光把完整纹章重新熔铸，裂痕处如今填满了明亮的金子。\n\n它比完整纹章更强：护盾随时间累积（_每 7 回合_获得上限 _20%%_ 的护盾，至少 1 点），上限为 _护甲等级 + 阶位×2 + 8_；极度饥饿时暂停累积。\n\n它可以携带最多 _7 级_升级（升级时纹章需已贴附在护甲上），并能携带_任意刻印_。\n\n贴附在护甲上时会_解锁连击技_，且连击被打断的时间_翻倍_；即使连击被打断，也会_保留一半层数_，在你下次开始连击时继承。");
	}

	{
		//SPSEXPD: 暂与完整纹章共用图标（待专属美术）
		image = EquipmentNonEquipDict.SEAL_COMPLETE;
	}

	/** SPSEXPD: 携带上限 7 级。 */
	@Override
	public int maxCarriedLevel() {
		return 7;
	}

	/** SPSEXPD: 护盾上限比完整纹章再多 4 点。 */
	@Override
	public int maxShield( int armTier, int armLvl ) {
		return armLvl + armTier * 2 + 8;
	}

	/** SPSEXPD: 连击被打断的时间翻倍（comboTime 衰减速度减半）。 */
	@Override
	public float comboBreakFactor() {
		return 2f;
	}

	/** SPSEXPD: 打断后保留一半层数，下次连击继承。 */
	@Override
	public boolean retainsHalfCombo() {
		return true;
	}
}
