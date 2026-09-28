package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimeOclock;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Timepill2 extends Pill {
	{ image = ItemSpriteSheet.SANDBAG; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, HasteBuff.class, 400f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
		if (Dungeon.level != null) {
			Heap heap = Dungeon.level.drop(new TimeOclock.Clock(), hero.pos);
			if (heap.sprite != null) heap.sprite.drop();
		}
	}
	@Override public int value() { return 50 * quantity; }
}
