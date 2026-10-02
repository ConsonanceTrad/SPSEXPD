/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.weapon.guns.ToyGun;
import pd.sprites.MusketeerSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the dwarf musketeer. */
public class Musketeer extends SpsCityMobs.Musketeer {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Musketeer.class)
			.t("name", "矮人射手")
			.t("desc", "与矮人国王不同，矮人将军认为火器才是正确的研究方向，于是这些射手加入了矮人王国的巡逻队。");
	}




	{
		spriteClass = MusketeerSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return new ToyGun();
	}

	public static Class<?> specialLootType() {
		return ToyGun.class;
	}
}
