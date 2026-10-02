/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.weapon.melee.special.SJRBMusic;
import pd.sprites.VagrantSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the sewer vagrant. */
public class Vagrant extends SpsSewerMobs.Vagrant {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Vagrant.class)
			.t("name", "流浪者")
			.t("desc", "住在下水道的流浪者，有着极高的恢复能力。");
	}


	{
		spriteClass = VagrantSprite.class;
		properties.add(Property.HUMAN);
	}

	@Override
	public Item SupercreateLoot() {
		return new SJRBMusic();
	}

	public static Class<?> specialLootType() {
		return SJRBMusic.class;
	}
}
