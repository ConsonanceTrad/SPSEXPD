package pd.items.equipment.weapon.missiles.buildblock;

import pd.atlas.items.SpecificTaskDict;
import pd.levels.Terrain;
import pd.messages.InlineText;
public class WoodenBlock extends LegacyBuildBlock {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WoodenBlock.class)
			.t("name", "木制方块")
			.t("desc", "投掷后会筑起路障的回收方块。");
	}


 public WoodenBlock(){super(Terrain.BARRICADE,SpecificTaskDict.ORE_0);} }
