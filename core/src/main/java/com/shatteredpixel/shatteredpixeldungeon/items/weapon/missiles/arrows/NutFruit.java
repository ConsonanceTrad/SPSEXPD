/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Durian;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

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
