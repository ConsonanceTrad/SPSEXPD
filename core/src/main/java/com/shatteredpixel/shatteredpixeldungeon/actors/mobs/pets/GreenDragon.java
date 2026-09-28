package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.GreenDragonSprite;

public class GreenDragon extends PET {
	{ spriteClass = GreenDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.GREEN_DRAGON; }
}
