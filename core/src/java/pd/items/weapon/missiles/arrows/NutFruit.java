/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.arrows;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Heap;
import pd.items.food.fruit.Durian;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

public class NutFruit extends SpsFruit {
	public NutFruit() { this(1); }
	public NutFruit(int number) { super(ItemSpriteSheet.SPS_SEED_DUNGEONNUT, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			if (Dungeon.level != null && Dungeon.level.insideMap(cell)) {
				Level.set(cell, Terrain.HIGH_GRASS);
				GameScene.updateMap(cell);
				if (Random.Int(10) == 0) {
					Heap heap = Dungeon.level.drop(new Durian(), cell);
					if (heap.sprite != null) heap.sprite.drop(cell);
				}
			}
		} else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		return super.proc(attacker, defender, damage);
	}
}
