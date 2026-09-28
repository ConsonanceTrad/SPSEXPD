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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Swarm extends Mob {

	{
		spriteClass = SwarmSprite.class;
		
		HP = HT = 60;
		defenseSkill = 5;

		EXP = 1;
		maxLvl = 10;
		
		flying = true;

		loot = Generator.Category.SEED;
		lootChance = 0.1f; // raised to the source 30% immediately before death
		properties.add(Property.BEAST);
	}
	
	private static final float SPLIT_DELAY	= 1f;
	
	int generation	= 0;
	
	private static final String GENERATION	= "generation";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( GENERATION, generation );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		generation = bundle.getInt( GENERATION );
	}

	@Override
	public void die(Object cause) {
		lootChance = 0.3f;
		super.die(cause);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 4, 7 );
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {

		if (HP >= damage + 2) {
			ArrayList<Integer> candidates = new ArrayList<>();
			
			int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
			for (int n : neighbours) {
				if (Dungeon.level.insideMap(n)
						&& Dungeon.level.adjacent(pos, n)
						&& Dungeon.level.passable[n]
						&& Actor.findChar( n ) == null) {
					candidates.add( n );
				}
			}
	
			if (candidates.size() > 0) {
				
				Swarm clone = split();
				clone.pos = Random.element( candidates );
				clone.state = clone.HUNTING;
				GameScene.add( clone, SPLIT_DELAY ); //we add before assigning HP due to ascension

				clone.HP = (HP - damage) / 2;
				Actor.add( new Pushing( clone, pos, clone.pos ) );

				Dungeon.level.occupyCell(clone);
				
				HP -= clone.HP;
			}
		}
		
		return damage;
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10;
	}
	
	Swarm split() {
		Swarm clone = new Swarm();
		clone.generation = generation + 1;
		if (buff( Burning.class ) != null) {
			Buff.affect( clone, Burning.class ).reignite( clone, 3f );
		}
		if (buff( Poison.class ) != null) {
			Buff.affect( clone, Poison.class ).set(2);
		}
		return clone;
	}

	@Override
	public Item SupercreateLoot() {
		return new ScrollOfMagicalInfusion();
	}

	public static Class<?> specialLootType() {
		return ScrollOfMagicalInfusion.class;
	}

	@Override
	protected boolean spawnsNightmareVirusOnDeath() {
		return generation == 0;
	}
	
}
