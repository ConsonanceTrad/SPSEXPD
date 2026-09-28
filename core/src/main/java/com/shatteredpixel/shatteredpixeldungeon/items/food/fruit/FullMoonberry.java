package com.shatteredpixel.shatteredpixeldungeon.items.food.fruit;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FullMoonStrength;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MoonFury;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class FullMoonberry extends Fruit {
	{ image = ItemSpriteSheet.FULLMOONBERRY; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, MoonFury.class);
		Buff.affect(hero, FullMoonStrength.class);
		Buff.prolong(hero, Light.class, Light.DURATION);
		if (Random.Int(2) == 1) Buff.affect(hero, Barkskin.class).set(hero.lvl, 1);
	}
	@Override public int value() { return 5 * quantity; }
}
