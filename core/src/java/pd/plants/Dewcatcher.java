package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Dewdrop;
import pd.items.RedDewdrop;
import pd.items.VioletDewdrop;
import pd.items.YellowDewdrop;
import pd.items.consum.medicine.GreenSpore;
import pd.mechanics.pathfind.PathFinder;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Dewcatcher extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Dewcatcher.class)
			.t("name", "集露草")
			.t("desc", "集露草伪装成普通青草，但叶片间鼓起的露珠暴露了它。触碰后会将露珠洒向周围。")
			.t("warden_desc", "_守望者_能把集露草当作格外丰厚的露水来源。")
			.t("$seed.name", "集露草之种")
			.t("$exdewcatcher.name", "集露草果丛")
			.t("$exdewcatcher.desc", "生长绿菌孢的果丛。");
	}



	{ image = 12; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = pos + offset;
			if (cell >= 0 && cell < Dungeon.level.length() && Dungeon.level.passable[cell]) {
				Dewdrop dew;
				if (Random.Int(10) == 1) dew = new VioletDewdrop();
				else if (Random.Int(5) == 1) dew = new RedDewdrop();
				else if (Random.Int(3) == 1) dew = new YellowDewdrop();
				else dew = new Dewdrop();
				Dungeon.level.drop(dew, cell).sprite.drop(pos);
			}
		}
	}
	public static class Seed extends Plant.Seed {
		{ image = SpecificPlaceHolderDict.SOMETHING_0; plantClass = Dewcatcher.class; explantClass = ExDewcatcher.class; }
	}
	public static class ExDewcatcher extends SpsFruitBush {
		{ image = 12; harvestCount = 3; harvestClass = GreenSpore.class; }
	}
}
