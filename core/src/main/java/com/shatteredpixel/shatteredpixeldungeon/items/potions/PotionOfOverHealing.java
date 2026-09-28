package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfOverHealing extends SpsPotion {
	{ image = ItemSpriteSheet.SPS_POTION_OVERHEALING; }
	@Override public void apply(Hero hero) {
		PotionOfHealing.cure(hero);
		hero.HP = hero.HT;
		Buff.affect(hero, Healing.class).setHeal(hero.HT, 0.25f, 0, true);
		Buff.affect(hero, Barrier.class).incShield(Math.max(1, hero.lvl * 2));
		hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
	}
	@Override public int value() { return 30 * quantity; }
}
