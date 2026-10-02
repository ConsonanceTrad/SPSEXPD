package pd.items.equipment.weapon.missiles.buildblock;

import pd.atlas.items.SpecificTaskDict;
import pd.levels.Terrain;
import pd.messages.InlineText;
public class WallBlock extends LegacyBuildBlock {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WallBlock.class)
			.t("name", "墙壁方块")
			.t("desc", "投掷后会筑起墙壁的回收方块。");
	}


 public WallBlock(){super(Terrain.WALL,SpecificTaskDict.ORE_0);} }
