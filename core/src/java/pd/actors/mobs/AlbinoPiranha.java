/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Roots;
import pd.items.consum.food.meatfood.Meat;

import pd.items.Item;
import pd.items.equipment.bombs.FishingBomb;
import pd.items.consum.food.vegetable.NutVegetable;
import pd.messages.Messages;
import pd.sprites.AlbinoPiranhaSprite;
import pd.utils.GLog;
import render.utils.data.BArray;
import render.utils.math.Random;

public class AlbinoPiranha extends Mob {

	{
		spriteClass = AlbinoPiranhaSprite.class;
		baseSpeed = 1f;
		EXP = 5;
		loot = Meat.class;
		lootChance = 0.1f;
		properties.add(Property.FISHER);
		immunities.add(Burning.class);
		immunities.add(Paralysis.class);
		immunities.add(ToxicGas.class);
		immunities.add(Roots.class);
		immunities.add(Frost.class);
	}

	public AlbinoPiranha() {
		HP = HT = 20 + Statistics.albinoPiranhasKilled * 2;
		defenseSkill = 40 + Statistics.albinoPiranhasKilled / 5;
	}

	@Override protected boolean act() {
		if (!Dungeon.level.water[pos]) {
			die(null);
			return true;
		}
		return super.act();
	}

	@Override public int damageRoll() {
		return Random.NormalIntRange(Statistics.albinoPiranhasKilled / 2,
				4 + Statistics.albinoPiranhasKilled);
	}

	@Override public int attackSkill(Char target) { return 20 + Statistics.albinoPiranhasKilled; }
	@Override public int drRoll() { return Statistics.albinoPiranhasKilled; }
	@Override public Item SupercreateLoot() { return new FishingBomb(); }

	@Override public void die(Object cause) {
		super.die(cause);
		Statistics.albinoPiranhasKilled++;
		GLog.w(Messages.get(this, "killcount", Statistics.albinoPiranhasKilled));
		dropLegacyDew(pos);
		if (Random.Int(105 - Math.min(Statistics.albinoPiranhasKilled, 100)) == 0) {
			Dungeon.level.drop(new NutVegetable(), pos).sprite.drop();
		}
		SpsChallengeKillRewards.albinoPiranha(pos);
	}

	@Override public boolean reset() { return true; }

	@Override protected boolean getCloser(int target) {
		if (rooted) return false;
		int step = Dungeon.findStep(this, target,
				BArray.and(Dungeon.level.water, Dungeon.level.passable, null), fieldOfView, true);
		if (step == -1) return false;
		move(step);
		return true;
	}

	@Override protected boolean getFurther(int target) {
		int step = Dungeon.flee(this, target,
				BArray.and(Dungeon.level.water, Dungeon.level.passable, null), fieldOfView, true);
		if (step == -1) return false;
		move(step);
		return true;
	}
}
