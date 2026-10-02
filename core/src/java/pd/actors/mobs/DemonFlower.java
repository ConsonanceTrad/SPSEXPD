/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.sprites.DemonflowerSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the infernal flower. */
public class DemonFlower extends SpsHallsMobs.DemonFlower {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(DemonFlower.class)
			.t("name", "恶魔花")
			.t("desc", "生长在地狱中的花。它不仅能够移动，还能连续攻击并削弱目标的攻击与护甲。")
			.t("debuff", "恶魔花释放了虚弱孢子。");
	}




	{
		spriteClass = DemonflowerSprite.class;
		properties.add(Property.PLANT);
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.PILL);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.PILL;
	}
}
