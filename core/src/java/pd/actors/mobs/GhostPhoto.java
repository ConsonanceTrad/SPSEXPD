/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.sprites.LivePhotoSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the mouldy painting. */
public class GhostPhoto extends SpsPrisonMobs.GhostPhoto {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GhostPhoto.class)
			.t("name", "霉画")
			.t("desc", "用于装饰监狱办公室的油画，被黑魔法污染后开始四处游荡。");
	}


	{
		spriteClass = LivePhotoSprite.class;
		properties.add(Property.UNKNOW);
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.SEED);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.SEED;
	}
}
