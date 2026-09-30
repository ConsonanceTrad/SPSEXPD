/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.actors.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Roots;
import pd.items.food.meatfood.Meat;
import pd.items.challengelists.CaveChallenge;
import pd.items.challengelists.ChallengePageDrops;
import pd.items.Item;
import pd.items.weapon.missiles.meleethrow.HugeShuriken;
import pd.sprites.PiranhaSprite;
import render.utils.BArray;
import render.utils.Random;

public class Piranha extends Mob {
	@Override public Item SupercreateLoot() { return new HugeShuriken(); }
	
	{
		spriteClass = PiranhaSprite.class;

		baseSpeed = 1.5f;
		
		EXP = 5;
		
		loot = Meat.class;
		lootChance = 1f;
		
		properties.add(Property.FISHER);

	}
	
	public Piranha() {
		super();
		
		HP = HT = 40 + legacyDepthAdjustment(0) * 5;
		defenseSkill = 10 + legacyDepthAdjustment(0) * 2;
	}
	
	@Override
	protected boolean act() {
		
		if (Dungeon.level == null || pos < 0 || pos >= Dungeon.level.length()
				|| !Dungeon.level.water[pos]) {
			dieOnLand();
			return true;
		}

		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView(this, fieldOfView);
		enemy = chooseEnemy();
		if (state == HUNTING && (enemy == null || !enemy.isAlive()
				|| !Dungeon.level.insideMap(enemy.pos) || !fieldOfView[enemy.pos]
				|| enemy.invisible > 0)) {
			state = WANDERING;
			for (int attempts = 0; attempts < 100; attempts++) {
				target = Dungeon.level.randomDestination(this);
				int oldPos = pos;
				if (getCloser(target)) {
					if (sprite != null) moveSprite(oldPos, pos);
					return true;
				}
			}
			spend(TICK);
			return true;
		}

		return super.act();
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( legacyDepthAdjustment(0), 4 + legacyDepthAdjustment(0) * 2 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20 + legacyDepthAdjustment(0) * 2;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, legacyDepthAdjustment(0));
	}

	public void dieOnLand(){
		die( null );
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );
		if (Dungeon.branch == 0 && Statistics.deepestFloor > 10) {
			ChallengePageDrops.offer(new CaveChallenge(), pos);
		}
		
	}

	@Override
	public float spawningWeight() {
		return 0;
	}

	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	protected boolean getCloser( int target ) {
		
		if (rooted) {
			return false;
		}
		
		int step = Dungeon.findStep( this, target, BArray.and(Dungeon.level.water, Dungeon.level.passable, null), fieldOfView, true );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean getFurther( int target ) {
		int step = Dungeon.flee( this, target, BArray.and(Dungeon.level.water, Dungeon.level.passable, null), fieldOfView, true );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}
	
	{
		immunities.add(Burning.class);
		immunities.add(Paralysis.class);
		immunities.add(ToxicGas.class);
		immunities.add(Roots.class);
		immunities.add(Frost.class);
	}

	public static Piranha random(){
		return new Piranha();
	}
}
