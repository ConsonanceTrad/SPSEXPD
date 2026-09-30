/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.weapon.melee.special.SJRBMusic;
import pd.sprites.ExVagrantSprite;

/** Original SPS-PD runtime and save identity for the infected vagrant. */
public class ExVagrant extends SpsSewerMobs.ExVagrant {

	{
		spriteClass = ExVagrantSprite.class;
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
