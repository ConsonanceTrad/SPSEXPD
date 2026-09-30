/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.items.RedDewdrop;
import pd.items.YellowDewdrop;
import pd.messages.Messages;
import pd.sprites.MossySkeletonSprite;
import pd.utils.GLog;
import render.utils.Random;

public class MossySkeleton extends LegacyDualLootMob {

	{
		spriteClass = MossySkeletonSprite.class;
		HP = HT = 90 + 10 * Random.NormalIntRange(7, 10);
		defenseSkill = 20;
		EXP = 1;
		baseSpeed = 0.5f + Math.min(1f, Statistics.mossySkeletonsKilled / 50);
		setupLegacyDualLoot(YellowDewdrop.class, 0.5f, RedDewdrop.class, 0.1f);
		properties.add(Property.UNDEAD);
	}

	@Override public int damageRoll() {
		return Random.NormalIntRange(20 + Statistics.mossySkeletonsKilled / 10,
				45 + Statistics.mossySkeletonsKilled / 5);
	}

	@Override public float attackDelay() {
		return 2f - Math.min(1.5f, Statistics.mossySkeletonsKilled / 50);
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(3) == 0) Buff.affect(enemy, BeOld.class).set(20f);
		return damage;
	}

	@Override public int attackSkill(Char target) { return 28; }
	@Override public int drRoll() { return 10 + Statistics.mossySkeletonsKilled / 5; }

	@Override public void die(Object cause) {
		super.die(cause);
		Statistics.mossySkeletonsKilled++;
		GLog.w(Messages.get(this, "killcount", Statistics.mossySkeletonsKilled));
		dropLegacyDew(pos);
		SpsChallengeKillRewards.mossySkeleton(pos);
	}
}
