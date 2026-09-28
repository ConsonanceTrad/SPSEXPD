package com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class NutVegetable extends Vegetable {
	{ image = ItemSpriteSheet.NUT_VEGETABLE; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.HT / 5));
	}
}
