package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.LightDragonSprite;

public class LightDragon extends PET {
	{ spriteClass = LightDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.LIGHT_DRAGON; }
}
