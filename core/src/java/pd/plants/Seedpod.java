package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.mechanics.pathfind.PathFinder;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Seedpod extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Seedpod.class)
			.t("name", "种子荚")
			.t("desc", "种子荚囤积从其他植物处夺来的种子，受到扰动时会把数颗种子撒向四周。")
			.t("warden_desc", "_守望者_能充分利用种子荚偷藏的种子。")
			.t("$seed.name", "种子荚之种")
			.t("$exseedpod.name", "种子荚果丛")
			.t("$exseedpod.desc", "生长随机果实的果丛。");
	}



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
		{ image = SpecificPlaceHolderDict.SOMETHING_0; plantClass = Seedpod.class; explantClass = ExSeedpod.class; }
	}
	public static class ExSeedpod extends SpsFruitBush {
		{ image = 13; harvestCount = 3; harvestCategory = Generator.Category.SPS_BERRY; }
	}
}
