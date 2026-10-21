/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 废料——药水酿造失败时的产物（粗制 25%、合剂 25%）。
 * 没有任何用途，只能卖给商人（或当作「又浪费了一批材料」的纪念品）。
 *
 * <p>贴图暂用枯枝的帧占位（独立帧待美术）。
 */
public class Waste extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Waste.class)
			.t("name", "废料")
			.t("desc", "一坨既不能吃也不能再炼的残渣。唯一的价值是还有商人愿意收。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsExtraDict.DRY_TWIG;
		stackable = true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 2 * quantity; }
}
