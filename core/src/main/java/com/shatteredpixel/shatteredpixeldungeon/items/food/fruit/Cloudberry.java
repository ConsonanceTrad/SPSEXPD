package com.shatteredpixel.shatteredpixeldungeon.items.food.fruit;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Cloudberry extends Fruit {
	{ image = ItemSpriteSheet.CLOUDBERRY; }
	@Override protected void onEat(Hero hero) {
		int roll = Random.Int(10);
		Buff.prolong(hero, HasteBuff.class, HasteBuff.DURATION);
		if (roll >= 6 && Dungeon.legacyDepth() < 51) {
			Buff.prolong(hero, Levitation.class, roll == 9 ? 20f : 10f);
		}
		if (roll == 9) {
			Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 10, 15));
		}
	}
}
