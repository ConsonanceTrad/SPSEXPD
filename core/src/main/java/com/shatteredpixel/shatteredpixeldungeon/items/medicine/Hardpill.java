package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DefenceUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Hardpill extends Pill {
	{ image = ItemSpriteSheet.GREAT_PILL; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, DefenceUp.class, 800f).level(50);
	}
	@Override public int value() { return 50 * quantity; }
}
