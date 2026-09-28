/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** SPS-PD's stacking-slow stun. Taking actual damage ends it early. */
public class StandDown extends FlavourBuff {

	public static final float DURATION = 5f;

	{
		type = buffType.NEGATIVE;
		immunities.add(SpeedSlow.class);
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		target.paralysed++;
		Buff.detach(target, SpeedSlow.class);
		return true;
	}

	@Override
	public void detach() {
		super.detach();
		if (target.paralysed > 0) target.paralysed--;
		Buff.prolong(target, SpeedSlow.class, 3f);
	}

	@Override
	public int icon() {
		return BuffIndicator.PARALYSIS;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.FROZEN);
		else target.sprite.remove(CharSprite.State.FROZEN);
	}

	public static float duration(Char ch) {
		return DURATION;
	}
}
