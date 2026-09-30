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
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.effects.particles.ShadowParticle;
import pd.items.scrolls.ScrollOfMagicalInfusion;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.items.scrolls.ScrollOfPsionicBlast;
import pd.scenes.GameScene;
import pd.sprites.WraithSprite;
import render.noosa.tweeners.AlphaTweener;
import render.utils.Bundle;
import render.utils.PathFinder;
import render.utils.Random;
import render.utils.Reflection;

import java.util.ArrayList;

public class Wraith extends LegacyDualLootMob {

	private static final float SPAWN_DELAY	= 2f;
	
	protected int level;
	
	{
		spriteClass = WraithSprite.class;
		
		HP = HT = 1 + legacyDepthAdjustment(0);
		EXP = 1;
		
		flying = true;
		setupLegacyDualLoot(ScrollOfMagicalInfusion.class, 0.06f,
				ScrollOfUpgrade.class, 0.09f);

		properties.add(Property.UNDEAD);
	}

	static Class<?>[] legacyLootTypes() {
		return new Class<?>[]{ScrollOfMagicalInfusion.class, ScrollOfUpgrade.class};
	}

	static float[] legacyLootChances() {
		return new float[]{0.06f, 0.09f};
	}
	
	private static final String LEVEL = "level";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getInt( LEVEL );
		adjustStats( level );
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 3 + level );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 10 + level;
	}
	
	public void adjustStats( int level ) {
		this.level = level;
		defenseSkill = attackSkill( null ) * 5;
		enemySeen = true;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return Dungeon.level != null && enemy != null
				&& Dungeon.level.distance(pos, enemy.pos) <= 4;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(10) == 0) {
			Buff.affect(enemy, Vertigo.class, 5f);
			Buff.affect(enemy, Terror.class, Terror.DURATION).object = id();
		}
		return damage;
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public boolean reset() {
		state = WANDERING;
		return true;
	}

	public static void spawnAround( int pos ) {
		spawnAround( pos, null );
	}
	
	public static void spawnAround( int pos, Class<? extends Wraith> wraithClass ) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(pos)) return;
		for (int n : PathFinder.NEIGHBOURS4) {
			spawnAt( pos + n, wraithClass, false );
		}
	}

	public static Wraith spawnAt( int pos ) {
		return spawnAt( pos, null );
	}

	public static Wraith spawnAt( int pos, Class<? extends Wraith> wraithClass ) {
		return spawnAt( pos, wraithClass, true );
	}

	private static Wraith spawnAt( int pos, Class<? extends Wraith> wraithClass, boolean allowAdjacent ) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(pos)) return null;

		//if the position itself is blocked, try to place in an adjacent cell if allowed
		if (!validSpawnCell(pos)){
			ArrayList<Integer> candidates = new ArrayList<>();

			for (int i : PathFinder.NEIGHBOURS8){
				if (validSpawnCell(pos + i)){
					candidates.add(pos+i);
				}
			}

			if (allowAdjacent && !candidates.isEmpty()){
				pos = Random.element(candidates);
			} else {
				pos = -1;
			}

		}

		if (pos != -1) {

			Wraith w;
			if (wraithClass == null){
				w = new Wraith();
			} else {
				w = Reflection.newInstance(wraithClass);
			}
			w.adjustStats( legacyDungeonDepth() );
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add( w, SPAWN_DELAY );
			Dungeon.level.occupyCell(w);

			if (w.sprite != null) {
				w.sprite.alpha( 0 );
				if (w.sprite.parent != null) {
					w.sprite.parent.add( new AlphaTweener( w.sprite, 1, 0.5f ) );
				}
				w.sprite.emitter().burst(ShadowParticle.CURSE, 5);
			}

			return w;
		} else {
			return null;
		}
	}

	private static boolean validSpawnCell(int pos) {
		return Dungeon.level.insideMap(pos) && Dungeon.level.passable[pos]
				&& Actor.findChar(pos) == null;
	}

	{
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(ToxicGas.class);
		immunities.add(ScrollOfPsionicBlast.class);
		immunities.add(Vertigo.class);
		immunities.add(Burning.class);
		immunities.add(Paralysis.class);
		immunities.add(Roots.class);
		immunities.add(Frost.class);
	}

}
