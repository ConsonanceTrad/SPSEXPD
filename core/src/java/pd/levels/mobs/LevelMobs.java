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

package pd.levels.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ChampionEnemy;
import pd.actors.mobs.Mob;
import pd.actors.mobs.MobSpawner;
import pd.items.trinkets.DimensionalSundial;
import pd.levels.Level;
import pd.messages.Messages;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.data.BArray;
import render.utils.math.GameMath;
import render.utils.serialize.Bundle;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

//关卡在场怪物的集合与补充机制：谁在场、怎么找、何时补充、补到哪。
//各关卡不同的刷怪规则（Level.mobLimit / Level.randomRespawnCell / Level.randomDestination /
//Level.markSpsOriginalMobs）仍由关卡子类覆写，本类只持有数据并驱动它们。
public class LevelMobs implements Bundlable, Iterable<Mob> {

	private static final String MOBS			= "mobs";
	private static final String MOBS_TO_SPAWN	= "mobs_to_spawn";
	private static final String RESPAWNER		= "respawner";

	//随机落点与补充尝试的上限，避免无解地形下空转
	private static final int MAX_CELL_TRIES		= 30;
	private static final int MAX_SPAWN_TRIES	= 30;

	private final Level level;

	private final HashSet<Mob> mobs = new HashSet<>();
	private ArrayList<Class<? extends Mob>> mobsToSpawn = new ArrayList<>();
	private MobSpawner respawner;

	public LevelMobs( Level level ) {
		this.level = level;
	}

	// ***** 集合访问面：怪物的增删查只经这里 *****

	public void add( Mob mob ) {
		mobs.add( mob );
	}

	public boolean remove( Object mob ) {
		return mobs.remove( mob );
	}

	public void clear() {
		mobs.clear();
	}

	public boolean contains( Object mob ) {
		return mobs.contains( mob );
	}

	public int size() {
		return mobs.size();
	}

	public boolean isEmpty() {
		return mobs.isEmpty();
	}

	public Mob[] toArray() {
		return mobs.toArray( new Mob[0] );
	}

	public Mob[] toArray( Mob[] array ) {
		return mobs.toArray( array );
	}

	//需要 Collection 视图的调用方（例如 new ArrayList<>(...)）用这个；返回只读视图
	public Collection<Mob> all() {
		return Collections.unmodifiableSet( mobs );
	}

	@Override
	public Iterator<Mob> iterator() {
		return mobs.iterator();
	}

	//遍历过程中增删怪物是常态（死亡、召唤），需要快照而不是活集合
	public HashSet<Mob> snapshot() {
		return new HashSet<>( mobs );
	}

	// ***** 刷怪与驻留机制 *****

	//按当前深度取下一个轮换怪物；队列耗尽时重新抽取本层牌组
	public Mob createMob() {
		if (mobsToSpawn == null || mobsToSpawn.isEmpty()) {
			mobsToSpawn = MobSpawner.getMobRotation( Dungeon.depth );
		}

		Mob m = Reflection.newInstance( mobsToSpawn.remove( 0 ) );
		ChampionEnemy.rollForChampion( m );
		return m;
	}

	//关卡上"占位"怪物的总权重：小怪按权重计，迷你首领与中立不计
	public int mobCount(){
		float count = 0;
		for (Mob mob : mobs){
			if (mob.alignment == Char.Alignment.ENEMY && !mob.properties().contains(Char.Property.MINIBOSS)) {
				count += mob.spawningWeight();
			}
		}
		return Math.round(count);
	}

	public Mob findMob( int pos ){
		for (Mob mob : mobs){
			if (mob.pos == pos){
				return mob;
			}
		}
		return null;
	}

	//本层是否还有 SPS 原始生成的敌人（露珠路线判定用）
	public boolean hasSpsOriginalMobs() {
		for (Mob mob : mobs) {
			if (mob.alignment == Char.Alignment.ENEMY && mob.spsOriginalGeneration) return true;
		}
		return false;
	}

	public Actor addRespawner() {
		if (respawner == null){
			respawner = new MobSpawner();
			Actor.addDelayed( respawner, respawnCooldown() );
		} else {
			Actor.add( respawner );
			if (respawner.cooldown() > respawnCooldown()){
				respawner.resetCooldown();
			}
		}
		return respawner;
	}

	public float respawnCooldown(){
		float base = Level.timeToRespawn();
		float cooldown;
		if (Statistics.amuletObtained){
			if (Dungeon.depth == 1){
				//very fast spawns on floor 1! 0/2/4/6/8/10/12, etc.
				cooldown = mobCount() * (base / 25f);
			} else {
				//respawn time is 5/5/10/15/20/25/25, etc.
				cooldown = Math.round( GameMath.gate( base / 10f, mobCount() * (base / 10f), base / 2f ) );
			}
		} else if (level.feeling == Level.Feeling.DARK){
			cooldown = 2 * base / 3f;
		} else {
			cooldown = base;
		}
		return cooldown / DimensionalSundial.spawnMultiplierAtCurrentTime();
	}

	//在离英雄足够远的位置投放一只新怪；落点规则由关卡子类决定
	public boolean spawnMob( int disLimit ){
		PathFinder.buildDistanceMap( Dungeon.hero.pos, BArray.or( level.passable, level.avoid, null ) );

		Mob mob = level.createMob();
		if (mob.state != mob.PASSIVE) {
			mob.state = mob.WANDERING;
		}
		int tries = MAX_SPAWN_TRIES;
		do {
			mob.pos = level.randomRespawnCell( mob );
			tries--;
		} while ((mob.pos == -1 || PathFinder.distance[mob.pos] < disLimit) && tries > 0);

		if (Dungeon.hero.isAlive() && mob.pos != -1 && PathFinder.distance[mob.pos] >= disLimit) {
			GameScene.add( mob );
			if (!mob.buffs(ChampionEnemy.class).isEmpty()){
				GLog.w( Messages.get(ChampionEnemy.class, "warn") );
			}
			return true;
		} else {
			return false;
		}
	}

	// ***** 序列化：怪物与补充队列随关卡一起存读 *****

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( MOBS, mobs );
		bundle.put( MOBS_TO_SPAWN, mobsToSpawn.toArray( new Class[0] ) );
		bundle.put( RESPAWNER, respawner );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		mobs.clear();
		for (Bundlable m : bundle.getCollection( MOBS )) {
			Mob mob = (Mob)m;
			if (mob != null) {
				mobs.add( mob );
			}
		}

		if (bundle.contains( MOBS_TO_SPAWN )) {
			for (Class<? extends Mob> mob : bundle.getClassArray( MOBS_TO_SPAWN )) {
				if (mob != null) mobsToSpawn.add( mob );
			}
		}

		if (bundle.contains( RESPAWNER )){
			respawner = (MobSpawner) bundle.get( RESPAWNER );
		}
	}
}
