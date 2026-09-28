package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.GoldDragonSprite;

public class GoldDragon extends PET {
	{ spriteClass = GoldDragonSprite.class; properties.add(Property.DRAGON); updateStats(true); }
	@Override protected Kind kind() { return Kind.GOLD_DRAGON; }
}
