package pd.items.equipment.weapon.missiles.buildblock;

import pd.atlas.items.ConsumThrowsDict;
import pd.levels.Terrain;
import pd.messages.InlineText;
public class WaterBlock extends LegacyBuildBlock {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WaterBlock.class)
			.t("name", "水块")
			.t("desc", "一种回收方块，投掷后会在目标位置生成水面。");
	}



	public WaterBlock() { super(Terrain.WATER, ConsumThrowsDict.WATER_BLOCK_PLACER); }
}
