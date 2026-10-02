/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.weapon.guns.GunD;
import pd.sprites.DemonRabbitSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the demon-blood shooter. */
public class DemonRabbit extends SpsHallsMobs.DemonRabbit {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DemonRabbit.class)
			.t("name", "血兔射手")
			.t("desc", "被恶魔血转化的兔人射手，会使用流血弹射击，并在命中处留下腐化气体。");
	}




	{
		spriteClass = DemonRabbitSprite.class;
		properties.add(Property.ORC);
	}

	@Override
	public Item SupercreateLoot() {
		return new GunD();
	}

	public static Class<?> specialLootType() {
		return GunD.class;
	}
}
