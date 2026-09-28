package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfMending extends SpsPotion {
	{ image = ItemSpriteSheet.SPS_POTION_MENDING; }
	@Override public void apply(Hero hero) {
		PotionOfHealing.cure(hero);
		Buff.affect(hero, Healing.class).setHeal(Math.max(hero.HT / 4, 30), 0.25f, 0, true);
		hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}
	@Override public int value() { return 20 * quantity; }
}
