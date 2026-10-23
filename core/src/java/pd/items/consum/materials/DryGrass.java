/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 晾干的鲜草——火源链的中间材料（docs/alchemy-rework.md §3）。
 * 鲜草 ×2 → 干草；干草 → 火种。干草本身也可作烹饪辅料。
 *
 * <p>贴图暂用枯枝的帧占位（待美术绘制独立帧，见裁决 #9 的后续美术待办）。
 */
public class DryGrass extends Item {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(DryGrass.class)
			.t("name", "干草")
			.t("desc", "晾干的鲜草，蓬松易着。可以搓成火种，也是烹饪时的引火辅料。");
	}

	{
		image = ConsumGoodsMaterialsMaterialsExtraDict.DRY_TWIG;
		stackable = true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 4 * quantity; }
}
