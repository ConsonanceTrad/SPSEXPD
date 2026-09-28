package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.BugDragonSprite;

public class BugDragon extends PET {
	{ spriteClass = BugDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.BUG_DRAGON; }
}
