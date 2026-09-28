package com.shatteredpixel.shatteredpixeldungeon.items.food.fruit;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Blueberry extends Fruit {
	{ image = ItemSpriteSheet.BLUEBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Foresight.class, Foresight.DURATION);
	}
}
