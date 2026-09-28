package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpionSprite;

public class Scorpion extends PET {
	{ spriteClass = ScorpionSprite.class; properties.add(Property.BEAST); updateStats(true); }
	@Override protected Kind kind() { return Kind.SCORPION; }
}
