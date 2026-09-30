/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.Wound;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;

public class SpearTrap extends Trap {
	{
		color = GREY;
		shape = DOTS;
	}
	@Override public void activate() {
		Char ch = Actor.findChar(pos);
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play(Assets.Sounds.HIT);
			Wound.hit(pos);
		}
		if (ch != null) {
			int legacyDepth = Dungeon.legacyDepth();
			int damage = Random.NormalIntRange(legacyDepth * 2, legacyDepth * 4)
					- Random.IntRange(0, ch.drRoll());
			ch.damage(Math.max(0, damage), this);
			if (ch == Dungeon.hero && !ch.isAlive()) {
				Dungeon.fail(this);
			}
		}
	}
}
