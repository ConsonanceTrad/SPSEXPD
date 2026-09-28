package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GrowSeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GoldenJelly extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_GOLDENJELLY; }
	public GoldenJelly() { this(1); }
	public GoldenJelly(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) Buff.affect(mob, GrowSeed.class).set(10f);
		Buff.affect(hero, Vertigo.class, 10f);
	}
}
