package com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Vegetable extends Food {
	{
		image = ItemSpriteSheet.RATION;
		energy = Hunger.HUNGRY / 15f;
		hornValue = 1;
		bones = false;
	}
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		onEat(hero);
	}
	protected void onEat(Hero hero) {
	}
	@Override protected float eatingTime() { return 1f; }
	@Override public int value() { return quantity; }
}
