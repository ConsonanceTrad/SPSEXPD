package com.shatteredpixel.shatteredpixeldungeon.items.food.fruit;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Moonberry extends Fruit {
	{ image = ItemSpriteSheet.MOONBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, AdrenalineSurge.class).reset(1, 40f);
		if (Random.Int(2) == 0) Buff.affect(hero, Barkskin.class).set(hero.lvl, 30);
	}
}
