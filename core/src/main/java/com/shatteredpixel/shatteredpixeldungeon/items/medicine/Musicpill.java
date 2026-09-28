package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Rhythm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Rhythm2;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WarGroove;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Musicpill extends Pill {
	{ image = ItemSpriteSheet.GREAT_PILL; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, Rhythm.class, 800f);
		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.PERFORMER) {
			Buff.affect(hero, WarGroove.class);
		}
		if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.SUPERSTAR) {
			Buff.affect(hero, Rhythm2.class, 800f);
		}
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
