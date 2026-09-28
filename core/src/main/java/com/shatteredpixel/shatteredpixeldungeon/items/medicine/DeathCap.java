package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BeCorrupt;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BeOld;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class DeathCap extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_DEATHCAP; }
	public DeathCap() { this(1); }
	public DeathCap(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, BeOld.class).set(50f);
			Buff.affect(mob, BeCorrupt.class).level(50);
		}
		hero.damage(Math.max(1, hero.HP / 2), this);
		Buff.prolong(hero, Cripple.class, Cripple.DURATION);
	}
}
