package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.NutVegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.GlassFruit;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SiOtwoFlower extends Plant {
	{ image = 18; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		if (ch != null) Buff.affect(ch, Barrier.class).setShield(Math.max(4, ch.HT / 3));
		Dungeon.level.drop(new NutVegetable(), pos).sprite.drop();
		Dungeon.level.drop(new GlassFruit(), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = ItemSpriteSheet.SPS_SEED_SIOFLOWER; plantClass = SiOtwoFlower.class; explantClass = ExSiOtwoFlower.class; }
	}
	public static class ExSiOtwoFlower extends SpsFruitBush {
		{ image = 18; harvestCount = 2; harvestClass = GlassFruit.class; }
	}
}
