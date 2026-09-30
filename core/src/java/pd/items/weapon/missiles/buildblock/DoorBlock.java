/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.buildblock;

import pd.Dungeon;
import pd.actors.Actor;
import pd.items.Item;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

/** A thrown construction block which turns its landing tile into a door. */
public class DoorBlock extends BuildBlock {

	{
		image = ItemSpriteSheet.DOOR_BLOCK;
	}

	public DoorBlock() { this(1); }

	public DoorBlock(int quantity) { quantity(quantity); }

	@Override
	protected void onThrow(int cell) {
		if (canBuildAt(cell)) {
			Level.set(cell, Terrain.DOOR, Dungeon.level);
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
	@Override public int value() { return 0; }
}
