/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.items.Heap;
import pd.items.weapon.missiles.arrows.NutFruit;

import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.GnollArcherSprite;
import pd.utils.GLog;
import watabou.utils.Random;

public class GnollArcher extends Mob {
	{
		spriteClass = GnollArcherSprite.class;
		HP = HT = 25 + Statistics.gnollArchersKilled;
		defenseSkill = 5;
		EXP = 1;
		baseSpeed = 0.9f;
		state = WANDERING;
		properties.add(Property.ORC);
	}

	@Override public int attackSkill(Char target) { return 30; }
	@Override public int damageRoll() {
		return Random.NormalIntRange(1 + Statistics.gnollArchersKilled / 10,
				8 + Statistics.gnollArchersKilled / 5);
	}
	@Override public int drRoll() { return 0; }

	@Override
	protected boolean canAttack(Char enemy) {
		return !Dungeon.level.adjacent(pos, enemy.pos)
				&& new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}

	@Override
	protected boolean getCloser(int target) {
		if (enemy != null && Dungeon.level.adjacent(pos, enemy.pos)) return getFurther(target);
		return super.getCloser(target);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		if (legacyDungeonDepth() > 25) {
			Heap heap = Dungeon.level.drop(new NutFruit(3), pos);
			if (heap.sprite != null) heap.sprite.drop();
		}
		Statistics.gnollArchersKilled++;
		GLog.w(Messages.get(this, "killcount", Statistics.gnollArchersKilled));
		SpsChallengeKillRewards.gnollArcher(pos);
		dropLegacyDew(pos);
	}
}
