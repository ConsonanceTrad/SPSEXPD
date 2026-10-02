/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.weapon.melee.special.SJRBMusic;
import pd.sprites.VagrantSprite;

/** Original SPS-PD runtime and save identity for the sewer vagrant. */
public class Vagrant extends SpsSewerMobs.Vagrant {

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
