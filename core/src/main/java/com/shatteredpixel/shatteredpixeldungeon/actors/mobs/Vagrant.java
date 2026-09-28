/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.SJRBMusic;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VagrantSprite;

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
