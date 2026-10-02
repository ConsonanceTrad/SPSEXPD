/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.consum.eggs.randomone.RandomMonthEgg;
import pd.sprites.DragonRiderSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the dragon rider. */
public class DragonRider extends SpsCityMobs.DragonRider {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DragonRider.class)
			.t("name", "龙骑兵")
			.t("desc", "与龙一起战斗的士兵。龙死亡后，骑兵仍会留下继续作战。");
	}




	{
		spriteClass = DragonRiderSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new RandomMonthEgg();
	}

	public static Class<?> specialLootType() {
		return RandomMonthEgg.class;
	}
}
