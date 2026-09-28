/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

/** The legacy SPS shield which caps a limited number of substantial hits at 10 damage. */
public class GlassShield extends Buff {

	private static final String TURNS = "turns";
	private int turns;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	public GlassShield turns(int value) {
		turns = Math.max(turns, value);
		return this;
	}

	public int turns() {
		return turns;
	}

	public int capDamage(int damage) {
		if (damage >= 10 && turns > 0) {
			damage = 10;
			turns--;
			if (turns <= 0) detach();
		}
		return damage;
	}

	@Override public int icon() { return BuffIndicator.ARMOR; }
	@Override public String iconTextDisplay() { return Integer.toString(turns); }
	@Override public String desc() { return Messages.get(this, "desc", turns); }

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.SHIELDED);
		else target.sprite.remove(CharSprite.State.SHIELDED);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TURNS, turns);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		turns = bundle.getInt(TURNS);
	}
}
