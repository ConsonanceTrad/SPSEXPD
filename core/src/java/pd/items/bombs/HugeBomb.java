/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.items.bombs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.particles.BlastParticle;
import pd.effects.particles.SmokeParticle;
import pd.items.Heap;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.utils.data.BArray;
import render.utils.math.Random;

/** SPS two-tile blast bomb, including its intended wall-breaking behavior. */
public class HugeBomb extends Bomb {

	{
		image = ItemSpriteSheet.HUGE_BOMB;
	}

	@Override
	public boolean explodesDestructively() {
		return false;
	}

	@Override
	protected int explosionRange() {
		return 2;
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);
		if (Dungeon.level.heroFOV[cell]) {
			CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
		}

		boolean[] affected = new boolean[Dungeon.level.length()];
		PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), explosionRange());
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) affected[i] = true;
		}
		// The old source attempted to destroy walls but excluded them from its path map.
		// Include walls bordering distance 0-1 cells so the documented effect works.
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] >= explosionRange()) continue;
			for (int offset : PathFinder.NEIGHBOURS8) {
				int target = i + offset;
				if (target >= 0 && target < affected.length && Dungeon.level.insideMap(target)
						&& Dungeon.level.distance(i, target) <= 1.5f
						&& (Dungeon.level.map[target] == Terrain.WALL
						|| Dungeon.level.map[target] == Terrain.WALL_DECO)) {
					affected[target] = true;
				}
			}
		}

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
			} else if ((Dungeon.level.map[target] == Terrain.WALL
					|| Dungeon.level.map[target] == Terrain.WALL_DECO)
					&& Dungeon.level.insideMap(target)) {
				Level.set(target, Terrain.EMPTY, Dungeon.level);
				GameScene.updateMap(target);
				terrainAffected = true;
			}

			Heap heap = Dungeon.level.heaps.get(target);
			if (heap != null) heap.explode();
			Char ch = Actor.findChar(target);
			if (ch != null && ch.isAlive()) {
				int damage = Random.NormalIntRange(ch.HT / 8, ch.HT / 4) - Math.max(ch.drRoll(), 0);
				if (damage > 0) ch.damage(damage, this);
			}
		}
		if (terrainAffected) Dungeon.observe();
	}

	@Override
	public int value() {
		return 20 * quantity();
	}

	@Override public HugeBomb random() { return this; }
}
