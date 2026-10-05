package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Dewdrop;
import pd.items.RedDewdrop;
import pd.items.VioletDewdrop;
import pd.items.YellowDewdrop;
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
			.t("$exdewcatcher.desc", "生长露珠菌孢的果丛。");
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
		{ image = ConsumPotionSeedSeedDict.SEED_DEWCATCHER; plantClass = Dewcatcher.class; explantClass = ExDewcatcher.class; }
	}
	//SPSEXPD: 果丛收获 = 露珠菌孢（原地 1 个）+ 2~3 枚集露果实（见 PlantHarvest 表），不再产露珠能量瓶
	public static class ExDewcatcher extends SpsFruitBush {
		{ image = 12; }
	}
}
