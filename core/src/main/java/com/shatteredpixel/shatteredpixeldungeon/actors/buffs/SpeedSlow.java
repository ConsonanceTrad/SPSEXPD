/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** A stacking slow which becomes weaker as its remaining duration falls. */
public class SpeedSlow extends FlavourBuff {

	{
		type = buffType.NEGATIVE;
	}

	@Override
	public boolean attachTo(Char target) {
		return target.buff(StandDown.class) == null && super.attachTo(target);
	}

	public float speedFactor() {
		return speedFactor(cooldown());
	}

	public static float speedFactor(float cooldown) {
		return Math.max(0.5f, 1f - cooldown * 0.1f);
	}

	@Override
	public int icon() {
		return BuffIndicator.CRIPPLE;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.CHILLED);
		else target.sprite.remove(CharSprite.State.CHILLED);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns(),
				Messages.decimalFormat("#.##", (1f - speedFactor()) * 100f));
	}
}
