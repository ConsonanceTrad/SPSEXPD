/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

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
