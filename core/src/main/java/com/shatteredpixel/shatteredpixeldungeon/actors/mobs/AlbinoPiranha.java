/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood.Meat;

import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.NutVegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FishingBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AlbinoPiranhaSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.BArray;
import com.watabou.utils.Random;

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
