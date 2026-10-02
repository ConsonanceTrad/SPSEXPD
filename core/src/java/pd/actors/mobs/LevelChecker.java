/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.ExpOre;
import pd.items.Item;
import pd.sprites.LevelCheckerSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the adjudicator. */
public class LevelChecker extends SpsCityMobs.LevelChecker {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LevelChecker.class)
			.t("name", "裁决者")
			.t("desc", "外形如同天平的巨型机械裁决者，会依据受害者的阅历作出裁决。");
	}


	{
		spriteClass = LevelCheckerSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new ExpOre();
	}

	public static Class<?> specialLootType() {
		return ExpOre.class;
	}
}
