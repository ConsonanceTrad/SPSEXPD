package com.shatteredpixel.shatteredpixeldungeon.items.food.fruit;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Cherry extends Fruit {
	{ image = ItemSpriteSheet.CHERRY; energy = Hunger.HUNGRY / 10f; }
	@Override protected void onEat(Hero hero) {
		Dungeon.level.drop(new Bomb(), hero.pos).sprite.drop();
	}
	@Override public int value() { return 5 * quantity; }
}
