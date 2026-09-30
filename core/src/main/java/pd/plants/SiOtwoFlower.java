package pd.plants;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.items.food.vegetable.NutVegetable;
import pd.items.weapon.missiles.arrows.GlassFruit;
import pd.sprites.ItemSpriteSheet;

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
