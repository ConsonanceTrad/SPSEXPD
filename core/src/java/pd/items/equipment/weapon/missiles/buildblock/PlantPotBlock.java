/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.buildblock;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import render.utils.math.Random;

/** A thrown construction block which creates a plantable flower pot. */
public class PlantPotBlock extends BuildBlock {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	public PlantPotBlock() { this(1); }

	public PlantPotBlock(int quantity) { quantity(quantity); }

	@Override
	protected void onThrow(int cell) {
		if (canBuildAt(cell)) {
			Level.set(cell, Terrain.FLOWER_POT, Dungeon.level);
			GameScene.updateMap(cell);
			Dungeon.observe();
		} else {
			super.onThrow(cell);
		}
	}

	private static boolean canBuildAt(int cell) {
		if (Dungeon.level == null || cell < 0 || cell >= Dungeon.level.length()
				|| Actor.findChar(cell) != null) return false;
		int terrain = Dungeon.level.map[cell];
		return terrain != Terrain.WELL && terrain != Terrain.EMPTY_WELL
				&& terrain != Terrain.ENTRANCE && terrain != Terrain.EXIT
				&& terrain != Terrain.ALCHEMY && terrain != Terrain.IRON_MAKER;
	}

	@Override public Item random() { return quantity(Random.IntRange(1, 2)); }
	@Override public int value() { return 50 * quantity; }
}
