/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;

/** Invisibility which is not removed by ordinary attacks. */
public class ForeverShadow extends FlavourBuff {

	public static final float DURATION = 30f;

	{ type = buffType.POSITIVE; }

	@Override
	public boolean attachTo(Char target) {
		if (!super.attachTo(target)) return false;
		target.invisible++;
		return true;
	}

	@Override
	public void detach() {
		if (target != null && target.invisible > 0) target.invisible--;
		super.detach();
	}

	@Override public int icon() { return BuffIndicator.BLESS; }
	@Override public String toString() { return Messages.get(this, "name"); }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
	@Override public void fx(boolean on) {
		if (target == null || target.sprite == null) return;
		if (on) target.sprite.add(CharSprite.State.INVISIBLE);
		else if (target.invisible == 0) target.sprite.remove(CharSprite.State.INVISIBLE);
	}
}
