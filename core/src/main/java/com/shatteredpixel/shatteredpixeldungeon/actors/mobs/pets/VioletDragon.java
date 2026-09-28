package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.VioletDragonSprite;

public class VioletDragon extends PET {
	{ spriteClass = VioletDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.VIOLET_DRAGON; }
}
