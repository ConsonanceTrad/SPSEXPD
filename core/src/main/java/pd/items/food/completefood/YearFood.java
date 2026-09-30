/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.mobs.YearBeast;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class YearFood extends CompleteFood {
	{
		image = ItemSpriteSheet.SPS_YEAR_FOOD;
		energy = 150f;
	}
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 6);
		if (Dungeon.legacyDepth() != 25 || Dungeon.level == null) return;
		increaseMaxHealth(hero, 3, 6);
		ArrayList<Integer> cells = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.passable[cell] && !Dungeon.level.adjacent(cell, hero.pos)
					&& Actor.findChar(cell) == null) cells.add(cell);
		}
		if (!cells.isEmpty()) {
			YearBeast.spawnAt(Random.element(cells));
		}
	}
	@Override public int value() { return 400 * quantity; }
}
