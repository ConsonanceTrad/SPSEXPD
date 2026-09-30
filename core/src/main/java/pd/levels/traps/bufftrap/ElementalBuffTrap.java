package pd.levels.traps.bufftrap;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

abstract class ElementalBuffTrap extends Trap {
	private final Class<? extends Blob> blobClass;
	private final int radius;
	private final int duration;
	private final boolean waterShortens;

	ElementalBuffTrap(int color, int shape, Class<? extends Blob> blobClass,
			int radius, int duration, boolean waterShortens) {
		this.color = color;
		this.shape = shape;
		this.blobClass = blobClass;
		this.radius = radius;
		this.duration = duration;
		this.waterShortens = waterShortens;
	}

	@Override
	public void activate() {
		PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), radius);
		for (int cell = 0; cell < PathFinder.distance.length; cell++) {
			if (PathFinder.distance[cell] <= radius && Dungeon.level.insideMap(cell)
					&& !Dungeon.level.solid[cell]) {
				int volume = Dungeon.level.pit[cell]
						|| (waterShortens && Dungeon.level.water[cell]) ? 1 : duration;
				GameScene.add(Blob.seed(cell, volume, blobClass));
			}
		}
	}
}
