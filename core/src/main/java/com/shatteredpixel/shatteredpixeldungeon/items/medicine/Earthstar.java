package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Earthstar extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_EARTHSTAR; }
	public Earthstar() { this(1); }
	public Earthstar(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			int min = mob.HP / 8;
			int max = mob.HP / 4;
			Buff.affect(mob, Bleeding.class).set(max > min ? Random.Int(min, max) : Math.max(1, max));
		}
		hero.damage(Math.max(1, hero.HP / 4), this);
		Buff.prolong(hero, Blindness.class, Random.IntRange(5, 7));
	}
}
