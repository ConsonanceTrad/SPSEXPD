package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.LerySprite;

public class LeryFire extends PET {
	{ spriteClass = LerySprite.class; properties.add(Property.ELEMENT); updateStats(true); }
	@Override protected Kind kind() { return Kind.LERY_FIRE; }
}
