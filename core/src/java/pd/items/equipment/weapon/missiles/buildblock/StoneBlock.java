package pd.items.equipment.weapon.missiles.buildblock;

import pd.atlas.items.SpecificTaskDict;
import pd.levels.Terrain;
import pd.messages.InlineText;
import pd.atlas.items.ConsumThrowsDict;
public class StoneBlock extends LegacyBuildBlock {
	{
		image = ConsumThrowsDict.STATUE_PLACER;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StoneBlock.class)
			.t("name", "石制方块")
			.t("desc", "投掷后会筑起雕像的回收方块。");
	}


 public StoneBlock(){super(Terrain.STATUE,SpecificTaskDict.ORE_0);} }
