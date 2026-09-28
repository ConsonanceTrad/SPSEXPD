package com.shatteredpixel.shatteredpixeldungeon.levels.traps.damagetrap;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SpsElementalDamage;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

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
