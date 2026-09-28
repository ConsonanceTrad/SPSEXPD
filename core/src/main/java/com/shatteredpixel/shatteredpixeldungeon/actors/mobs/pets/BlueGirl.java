package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.pets;

import com.shatteredpixel.shatteredpixeldungeon.sprites.BlueGirlSprite;

public class BlueGirl extends PET {
	{ spriteClass = BlueGirlSprite.class; properties.add(Property.ELF); updateStats(true); }
	@Override protected Kind kind() { return Kind.BLUE_GIRL; }
}
