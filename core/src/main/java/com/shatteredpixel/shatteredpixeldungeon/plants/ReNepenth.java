package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.TransmutationBall;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ReNepenth extends Plant {
	{ image = 14; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(new TransmutationBall(), pos).sprite.drop();
		Dungeon.level.drop(Generator.random(Generator.Category.SPS_BERRY), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = ItemSpriteSheet.SPS_SEED_RENEPENTH; plantClass = ReNepenth.class; explantClass = ExReNepenth.class; }
	}
	public static class ExReNepenth extends SpsFruitBush {
		{ image = 14; harvestCount = 2; harvestClass = TransmutationBall.class; }
	}
}
