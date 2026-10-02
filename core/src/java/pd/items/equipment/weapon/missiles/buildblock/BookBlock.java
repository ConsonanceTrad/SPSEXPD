package pd.items.equipment.weapon.missiles.buildblock;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.levels.Terrain;
import pd.messages.InlineText;
public class BookBlock extends LegacyBuildBlock {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BookBlock.class)
			.t("name", "书架方块")
			.t("desc", "投掷后会筑起书架的回收方块。");
	}


 public BookBlock(){super(Terrain.BOOKSHELF,SpecificPlaceHolderDict.SCROLL_HOLDER_0);} }
