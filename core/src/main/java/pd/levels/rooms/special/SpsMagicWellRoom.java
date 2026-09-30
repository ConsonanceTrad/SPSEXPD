/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels.rooms.special;

import pd.actors.blobs.WaterOfAwareness;
import pd.actors.blobs.WaterOfHealth;
import pd.actors.blobs.WaterOfTransmutation;
import pd.actors.blobs.WellWater;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.painters.Painter;
import pd.plants.BlandfruitBush;
import pd.plants.Plant;
import pd.plants.ReNepenth;
import pd.plants.StarEater;
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
