package com.shatteredpixel.shatteredpixeldungeon.items.food.fusion;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Nut extends Food {

	{
		image = ItemSpriteSheet.PASTY;
		energy = Hunger.HUNGRY / 6f;
		hornValue = 1;
	}

	@Override
	protected void satisfy(Hero hero) {
		super.satisfy(hero);
		hero.HP = Math.min(hero.HT, hero.HP + 1);
		if (Random.Int(10) == 0) {
			Buff.affect(hero, Barkskin.class).set(2 + hero.lvl / 4, 1);
		}
	}

	@Override
	public int value() {
		return 2 * quantity;
	}
}
