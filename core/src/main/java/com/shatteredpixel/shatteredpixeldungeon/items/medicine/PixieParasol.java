package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class PixieParasol extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_PIXIEPARASOL; }
	public PixieParasol() { this(1); }
	public PixieParasol(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, Drowsy.class);
			Buff.prolong(mob, Paralysis.class, Random.IntRange(10, 16));
			Buff.affect(mob, ArmorBreak.class, 50f).level(30);
			if (mob.sprite != null) mob.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
		}
		Buff.affect(hero, Bless.class, 20f);
	}
}
