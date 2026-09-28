package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PotionOfMixing extends SpsPotion {
	{ image = ItemSpriteSheet.SPS_POTION_MIXING; }
	@Override public void apply(Hero hero) {
		hero.improveCombatSkills(1);
		Buff.prolong(hero, Recharging.class, 30f);
		hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
		GLog.p(Messages.get(this, "skillup"));
	}
	@Override public int value() { return 100 * quantity; }
}
