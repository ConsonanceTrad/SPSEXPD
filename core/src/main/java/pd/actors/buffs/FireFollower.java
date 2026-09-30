/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SteamWarn;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import render.utils.Bundle;

/** Leaves delayed fire beneath the affected character for thirty turns. */
public class FireFollower extends Buff {

	public static final float DURATION = 30f;
	private float left;

	public FireFollower set(float duration) {
		left = duration;
		return this;
	}

	public float left() { return left; }

	@Override
	public boolean act() {
		if (Dungeon.level != null && target != null && Dungeon.level.insideMap(target.pos)) {
			GameScene.add(Blob.seed(target.pos, 4, SteamWarn.class));
		}
		left -= TICK;
		spend(TICK);
		if (left <= 0) detach();
		return true;
	}

	@Override public int icon() { return BuffIndicator.FIRE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns(left)); }

	private static final String LEFT = "left";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEFT, left);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		left = bundle.getFloat(LEFT);
	}
}
