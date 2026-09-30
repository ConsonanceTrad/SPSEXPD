/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.weapon.missiles.throwing.Skull;
import pd.sprites.DemonGooSprite;

/** Original SPS-PD runtime and save identity for demon goo. */
public class DemonGoo extends SpsHallsMobs.DemonGoo {

	{
		spriteClass = DemonGooSprite.class;
		properties.add(Property.ELEMENT);
	}

	@Override
	protected SpsHallsMobs.DemonGoo newSplit() {
		return new DemonGoo();
	}

	@Override
	public Item SupercreateLoot() {
		return new Skull(3);
	}

	public static Class<?> specialLootType() {
		return Skull.class;
	}
}
