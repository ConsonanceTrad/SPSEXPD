/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.mobs.YearBeast;
import pd.scenes.GameScene;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class YearFood extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(YearFood.class)
			.t("name", "年糕")
			.t("desc", "能永久提高生命力的节庆食物。在最终层食用还会召来年兽。");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
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
