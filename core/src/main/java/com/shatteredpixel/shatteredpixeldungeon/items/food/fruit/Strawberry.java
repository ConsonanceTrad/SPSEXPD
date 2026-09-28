package com.shatteredpixel.shatteredpixeldungeon.items.food.fruit;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Strawberry extends Fruit {
	{ image = ItemSpriteSheet.STRAWBERRY; energy = Hunger.HUNGRY / 10f; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Levitation.class, 20f);
	}
	@Override public int value() { return 5 * quantity; }
}
