/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Keeps an SPS exit guard asleep until its first incoming hit is absorbed. */
public class ExProtect extends Buff {

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		if (target instanceof Mob) ((Mob)target).state = ((Mob)target).SLEEPING;
		target.paralysed++;
		return true;
	}

	@Override
	public void detach() {
		if (target.paralysed > 0) target.paralysed--;
		super.detach();
	}

	@Override public int icon() { return BuffIndicator.MAGIC_SLEEP; }
	@Override public String desc() { return Messages.get(this, "desc"); }

	@Override
	public void fx(boolean on) {
		if (target.sprite == null) return;
		if (on) target.sprite.add(CharSprite.State.PARALYSED);
		else if (target.paralysed <= 1) target.sprite.remove(CharSprite.State.PARALYSED);
	}
}
