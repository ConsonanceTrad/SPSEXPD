package pd.plants;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.sprites.ItemSpriteSheet;
import render.utils.PathFinder;
import render.utils.Random;

import java.util.ArrayList;

public class Seedpod extends Plant {
	{ image = 13; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length() && Dungeon.level.passable[cell]) cells.add(cell);
		}
		int count = Random.NormalIntRange(1, 5);
		while (count-- > 0 && !cells.isEmpty()) {
			int cell = Random.element(cells);
			cells.remove((Integer)cell);
			Dungeon.level.drop(Generator.random(Generator.Category.SEED), cell).sprite.drop(pos);
		}
	}
	public static class Seed extends Plant.Seed {
		{ image = ItemSpriteSheet.SPS_SEED_SEEDPOD; plantClass = Seedpod.class; explantClass = ExSeedpod.class; }
	}
	public static class ExSeedpod extends SpsFruitBush {
		{ image = 13; harvestCount = 3; harvestCategory = Generator.Category.SPS_BERRY; }
	}
}
