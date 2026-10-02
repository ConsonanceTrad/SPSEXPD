/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.sprites.LiveMossSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for living moss. */
public class LiveMoss extends SpsSewerMobs.LiveMoss {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LiveMoss.class)
			.t("name", "寄生苔藓")
			.t("desc", "由于黑暗力量的侵蚀，下水道诡异的生物逐渐增多。这个生物就是其中的一种:扭曲的植物占据了死去的老鼠躯体，并向外抛洒寄生孢子。");
	}




	{
		spriteClass = LiveMossSprite.class;
		loot = Generator.Category.MUSHROOM;
		properties.add(Property.PLANT);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(Generator.random(Generator.Category.POTION),
				Generator.random(Generator.Category.SUMMONED));
	}

	public static Generator.Category[] specialLootCategories() {
		return new Generator.Category[]{Generator.Category.POTION, Generator.Category.SUMMONED};
	}
}
