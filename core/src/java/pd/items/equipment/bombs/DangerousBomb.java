/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.items.equipment.bombs;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.particles.SmokeParticle;
import pd.items.Heap;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.data.BArray;
import render.utils.math.Random;

/** Hybrid's phase-change bomb from SPS-PD 0.9.8. */
public class DangerousBomb extends Bomb {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public void explode(int cell) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)) return;

		// The original first performs an ordinary bomb explosion, then adds its
		// two-tile terrain blast and hero-only percentage damage.
		super.explode(cell);

		boolean[] affected = secondaryBlastCells(cell);
		boolean terrainAffected = false;
		for (int target = 0; target < affected.length; target++) {
			if (!affected[target]) continue;

			if (Dungeon.level.heroFOV[target]) {
				CellEmitter.get(target).burst(SmokeParticle.FACTORY, 4);
			}

			if (Dungeon.level.flamable[target]) {
				Level.set(target, Terrain.EMBERS, Dungeon.level);
				GameScene.updateMap(target);
				terrainAffected = true;
			} else if (isBreakableWall(Dungeon.level.map[target])) {
				Level.set(target, Terrain.EMPTY, Dungeon.level);
				GameScene.updateMap(target);
				terrainAffected = true;
			}

			Heap heap = Dungeon.level.heaps.get(target);
			if (heap != null) heap.explode();

			Char ch = Actor.findChar(target);
			if (ch == Dungeon.hero && ch.isAlive()) {
				int minDamage = ch.HT / 8;
				int maxDamage = ch.HT / 4;
				int damage = Random.NormalIntRange(minDamage, maxDamage) - Math.max(ch.drRoll(), 0);
				if (damage > 0) ch.damage(damage, this);
				if (!ch.isAlive()) {
					GLog.n(Messages.get(this, "ondeath"));
					Dungeon.fail(this);
				}
			}
		}

		if (terrainAffected) Dungeon.observe();
	}

	boolean[] secondaryBlastCells(int cell) {
		boolean[] affected = new boolean[Dungeon.level.length()];
		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) affected[i] = true;
		}

		// Solid walls are excluded from the old distance map, which made its wall
		// destruction branch unreachable. Include walls bordering the blast while
		// retaining the old two-tile range and preventing row-wrap at map edges.
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] >= 2) continue;
			for (int offset : PathFinder.NEIGHBOURS8) {
				int target = i + offset;
				if (target >= 0 && target < affected.length
						&& Dungeon.level.insideMap(target)
						&& Dungeon.level.distance(i, target) <= 1.5f
						&& isBreakableWall(Dungeon.level.map[target])) {
					affected[target] = true;
				}
			}
		}
		return affected;
	}

	private static boolean isBreakableWall(int terrain) {
		return terrain == Terrain.WALL || terrain == Terrain.GLASS_WALL;
	}

	@Override
	public int value() {
		return 20 * quantity();
	}

	@Override
	public DangerousBomb random() {
		return this;
	}
}
