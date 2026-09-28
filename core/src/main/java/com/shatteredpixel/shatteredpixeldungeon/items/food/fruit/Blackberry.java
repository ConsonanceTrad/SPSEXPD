package com.shatteredpixel.shatteredpixeldungeon.items.food.fruit;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Blackberry extends Fruit {
	{ image = ItemSpriteSheet.BLACKBERRY; }
	@Override protected void onEat(Hero hero) {
		int healing = Math.max(hero.HT / (Random.Int(5) == 0 ? 8 : 10), 15);
		Buff.affect(hero, Healing.class).setHeal(healing, 0.25f, 0);
		if (Random.Int(5) == 0) {
			Buff.prolong(hero, MindVision.class, MindVision.DURATION);
			Dungeon.observe();
		}
	}
}
