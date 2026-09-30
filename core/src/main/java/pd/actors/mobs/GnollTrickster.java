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
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Poison;
import pd.actors.mobs.npcs.Ghost;
import pd.items.Generator;
import pd.items.weapon.missiles.darts.PoisonDart;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import pd.sprites.GnollTricksterSprite;
import watabou.utils.Bundle;
import watabou.utils.Random;

public class GnollTrickster extends Gnoll {

	{
		spriteClass = GnollTricksterSprite.class;

		HP = HT = 60;
		defenseSkill = 5;

		EXP = 5;

		state = WANDERING;

		loot = Generator.random(PoisonDart.class);
		lootChance = 1f;

		properties.add(Property.ORC);
		properties.add(Property.MINIBOSS);
	}

	private int combo = 0;

	@Override
	public int attackSkill( Char target ) {
		return 16;
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
		if (buff(Locked.class) != null) {
			return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
		}
		return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		//The gnoll's attacks get more severe the more the player lets it hit them
		combo++;
		int effect = Random.Int(4)+combo;

		if (effect > 2) {

			if (effect >=6 && enemy.buff(Burning.class) == null){

				if (Dungeon.level.flamable[enemy.pos]) {
					GameScene.add(Blob.seed(enemy.pos, 4, Fire.class));
				}
				Buff.affect(enemy, Burning.class).reignite(enemy, 3f);

			} else {
				Buff.affect(enemy, Poison.class).set((effect - 2));
			}

		}
		return damage;
	}

	@Override
	protected boolean getCloser( int target ) {
		combo = 0; //if he's moving, he isn't attacking, reset combo.
		if (enemy != null && Dungeon.level.adjacent(pos, enemy.pos)) {
			return getFurther(target);
		} else {
			return super.getCloser( target );
		}
	}
	
	@Override
	public void die( Object cause ) {
		super.die( cause );

		Ghost.Quest.process();
	}

	private static final String COMBO = "combo";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(COMBO, combo);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		combo = bundle.getInt( COMBO );
	}

}
