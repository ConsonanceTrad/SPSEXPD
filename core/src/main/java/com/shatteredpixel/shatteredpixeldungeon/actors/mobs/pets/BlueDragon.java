package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.BlueDragonSprite;

public class BlueDragon extends PET {
	{ spriteClass = BlueDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.BLUE_DRAGON; }
}
