package com.shatteredpixel.shatteredpixeldungeon.items.medicine;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RealgarWine extends Pill {
	{ image = ItemSpriteSheet.WINE; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
		Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}
