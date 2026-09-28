package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class PotionOfShield extends SpsPotion {
	{ image = ItemSpriteSheet.SPS_POTION_SHIELD; }
	@Override public void apply(Hero hero) {
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.HT / 3));
		Buff.affect(hero, ArcaneArmor.class).set(Math.max(1, hero.HT / 3), 30);
		Sample.INSTANCE.play(Assets.Sounds.MELD);
	}
	@Override public void shatter(int cell) {
		Char ch = Actor.findChar(cell);
		if (ch != null) Buff.prolong(ch, Paralysis.class, 5f);
		super.shatter(cell);
	}
	@Override public int value() { return 40 * quantity; }
}
