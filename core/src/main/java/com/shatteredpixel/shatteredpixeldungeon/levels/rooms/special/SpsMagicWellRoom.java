/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.ReNepenth;
import com.shatteredpixel.shatteredpixeldungeon.plants.StarEater;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

/** Hidden magic well and special plant from SPS-PD 0.9.8. */
public class SpsMagicWellRoom extends SpecialRoom {

	private static final Class<?>[] WATERS = {
			WaterOfAwareness.class, WaterOfHealth.class, WaterOfTransmutation.class
	};

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);

		Point center = center();
		Painter.set(level, center, Terrain.WELL);
		@SuppressWarnings("unchecked")
		Class<? extends WellWater> water = (Class<? extends WellWater>) Random.element(WATERS);
		WellWater.seed(level.pointToCell(center), 1, water, level);

		Plant.Seed seed;
		switch (Random.Int(3)) {
			case 0: seed = new StarEater.Seed(); break;
			case 1: seed = new BlandfruitBush.Seed(); break;
			default: seed = new ReNepenth.Seed(); break;
		}
		level.plant(seed, level.pointToCell(random()));
		entrance().set(Door.Type.HIDDEN);
	}
}
