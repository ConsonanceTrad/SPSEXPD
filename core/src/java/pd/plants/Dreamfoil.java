package pd.plants;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicalSleep;
import pd.actors.hero.Hero;
import pd.items.food.vegetable.DreamLeaf;
import pd.items.potions.PotionOfHealing;
import pd.items.weapon.missiles.arrows.CharmFruit;
import pd.sprites.ItemSpriteSheet;

public class Dreamfoil extends Plant {
	{ image = 10; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(new DreamLeaf(), pos).sprite.drop();
		if (ch instanceof Hero) PotionOfHealing.cure(ch);
		else if (ch != null) Buff.affect(ch, MagicalSleep.class);
	}
	public static class Seed extends Plant.Seed {
		{ image = ItemSpriteSheet.SPS_SEED_DREAMFOIL; plantClass = Dreamfoil.class; explantClass = ExDreamfoil.class; }
	}
	public static class ExDreamfoil extends SpsFruitBush {
		{ image = 10; harvestCount = 3; harvestClass = CharmFruit.class; }
	}
}
