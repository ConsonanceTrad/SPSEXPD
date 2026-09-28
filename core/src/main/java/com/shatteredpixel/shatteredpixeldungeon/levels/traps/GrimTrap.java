/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class GrimTrap extends Trap {
	{
		color = GREY;
		shape = LARGE_DOT;
		canBeHidden = false;
	}

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play(Assets.Sounds.HIT);
			Wound.hit(pos);
		}
		Char target = Actor.findChar(pos);
		if (target == null) return;
		int damage = Random.NormalIntRange(target.HP / 2, target.HP);
		damage -= Random.IntRange(target.drRoll() / 2, target.drRoll());
		target.damage(Math.max(damage, 0), this);
		if (!target.isAlive() && target == Dungeon.hero) Dungeon.fail(this);
	}
}
