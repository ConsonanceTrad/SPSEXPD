package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.DreamLeaf;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows.CharmFruit;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

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
