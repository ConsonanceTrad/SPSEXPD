package pd.levels.traps.damagetrap;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SpsElementalDamage;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import render.utils.BArray;
import render.utils.PathFinder;

abstract class ElementalDamageTrap extends Trap {
	private final Class<? extends SpsElementalDamage> blobClass;
	private final int radius;
	private final int duration;

	ElementalDamageTrap(int color, int shape, Class<? extends SpsElementalDamage> blobClass,
			int radius, int duration) {
		this.color = color;
		this.shape = shape;
		this.blobClass = blobClass;
		this.radius = radius;
		this.duration = duration;
	}

	@Override
	public void activate() {
		PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), radius);
		for (int cell = 0; cell < PathFinder.distance.length; cell++) {
			if (PathFinder.distance[cell] <= radius && Dungeon.level.insideMap(cell)
					&& !Dungeon.level.solid[cell]) {
				GameScene.add(Blob.seed(cell, duration, blobClass));
			}
		}
	}
}
