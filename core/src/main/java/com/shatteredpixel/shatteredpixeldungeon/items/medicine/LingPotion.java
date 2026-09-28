package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LingBless;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LingPotion extends Pill {
	{ image = ItemSpriteSheet.SPS_LING_POTION; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, LingBless.class, 200f);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.STAR), 0.2f, 3);
	}
	@Override public int value() { return 50 * quantity; }
}
