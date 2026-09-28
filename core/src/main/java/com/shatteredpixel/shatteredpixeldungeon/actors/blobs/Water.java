/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

/** Legacy SPS dew-water, which turns suitable floor tiles into grass. */
public class Water extends Blob {

	@Override
	protected void evolve() {
		super.evolve();
		if (volume <= 0) return;

		boolean mapUpdated = false;
		for (int x = area.left; x < area.right; x++) {
			for (int y = area.top; y < area.bottom; y++) {
				int cell = x + y * Dungeon.level.width();
				if (off[cell] <= 0) continue;
				int terrain = Dungeon.level.map[cell];
				if (terrain == Terrain.EMPTY || terrain == Terrain.EMBERS
						|| terrain == Terrain.EMPTY_DECO || terrain == Terrain.OLD_HIGH_GRASS) {
					Level.set(cell, cur[cell] > 9 ? Terrain.HIGH_GRASS : Terrain.GRASS);
					GameScene.updateMap(cell);
					mapUpdated = true;
				} else if ((terrain == Terrain.GRASS || terrain == Terrain.FURROWED_GRASS)
						&& cur[cell] > 9) {
					Level.set(cell, Terrain.HIGH_GRASS);
					GameScene.updateMap(cell);
					mapUpdated = true;
				}
			}
		}
		if (mapUpdated) Dungeon.observe();
	}

	@Override public void use(BlobEmitter emitter) {
		super.use(emitter);
		emitter.start(LeafParticle.LEVEL_SPECIFIC, 0.2f, 0);
	}
}
