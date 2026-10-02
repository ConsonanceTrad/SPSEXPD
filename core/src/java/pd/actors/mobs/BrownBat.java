/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.sprites.BrownBatSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the small sewer bat. */
public class BrownBat extends SpsSewerMobs.BrownBat {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BrownBat.class)
			.t("name", "小蝙蝠")
			.t("desc", "小蝙蝠呈棕色，是一种没什么威胁的生物。当然如果你杀死了它的话，它有几率发出很大的响声。")
			.t("die", "凄惨的叫声惊醒了附近的敌人！");
	}




	{
		spriteClass = BrownBatSprite.class;
		properties.add(Property.BEAST);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.SEED4;
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(specialLootCategory());
	}
}
