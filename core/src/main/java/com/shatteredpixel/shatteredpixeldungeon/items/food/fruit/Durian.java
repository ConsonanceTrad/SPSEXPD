package com.shatteredpixel.shatteredpixeldungeon.items.food.fruit;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Durian extends Fruit {
	{ image = ItemSpriteSheet.DURIAN; energy = Hunger.HUNGRY / 3f; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Barkskin.class).set(4 + hero.lvl / 3, 30);
	}
	@Override public int value() { return 5 * quantity; }
}
