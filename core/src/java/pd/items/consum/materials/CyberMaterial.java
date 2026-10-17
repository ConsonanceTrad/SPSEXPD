/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.materials;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.messages.InlineText;

/**
 * SPSEXPD: 义体材料 —— 由有机食物转换来的原料，用来在义体工具箱里制造临时义体。
 */
public class CyberMaterial extends Item {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(CyberMaterial.class)
			.t("name", "义体材料")
			.t("desc", "一小块被重组过的有机原料，摸起来像冷却下来的蜡。\n\n义体工具箱用它来制造临时义体。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}
