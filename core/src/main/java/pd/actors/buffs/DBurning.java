/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.BuffIndicator;
import watabou.utils.Bundle;
import watabou.utils.Random;

/** SPS percentage-burning effect used by the senior exit guard. */
public class DBurning extends Buff implements Hero.Doom, Buff.DOTbuff {
	private static final String LEFT = "left";
	private float left;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	public DBurning set(float duration) {
		left = Math.max(left, duration);
		if (target != null) target.needsIncomingDOTUpdate = true;
		return this;
	}

	@Override public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		if (target instanceof Hero) Buff.prolong(target, Light.class, TICK * 1.01f);
		int upper = Math.max(1, Math.min(1000, target.HT / 10));
		target.damage(Random.IntRange(1, upper), this);
		Buff.detach(target, Chill.class);
		spend(TICK);
		left -= TICK;
		target.needsIncomingDOTUpdate = true;
		if (left <= 0) detach();
		return true;
	}

	@Override public int totalIncomingDMG() {
		return Math.max(0, (int)Math.ceil(left)) * Math.max(1, target.HT / 20);
	}
	@Override public int icon() { return BuffIndicator.FIRE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(left)); }
	@Override public void fx(boolean on) {
		if (target.sprite == null) return;
		if (on) target.sprite.add(CharSprite.State.BURNING);
		else target.sprite.remove(CharSprite.State.BURNING);
	}
	@Override public void onDeath() { Dungeon.fail(this); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(LEFT, left); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); left = bundle.getFloat(LEFT); }
}
