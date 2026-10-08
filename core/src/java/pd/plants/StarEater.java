package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.items.consum.food.vegetable.StarEaterFlower;
import pd.messages.InlineText;

public class StarEater extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(StarEater.class)
			.t("name", "吞星花")
			.t("desc", "形如巨口的植物。它的花能吞噬物品并提炼其中的精华。")
			.t("warden_desc", "_守望者_可以安全收取其中保存的装备精华。")
			.t("$seed.name", "吞星花之种")
			.t("$exstareater.name", "吞星花丛")
			.t("$exstareater.desc", "生长吞星花的果丛。");
	}



	{ image = 15; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(new StarEaterFlower(), pos).sprite.drop();
		Dungeon.level.drop(Generator.random(Generator.Category.BERRY), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = ConsumPotionSeedSeedDict.SEED_STAREATER; plantClass = StarEater.class; explantClass = ExStarEater.class; }
	}
	public static class ExStarEater extends SpsFruitBush {
		{ image = 15; }
	}
}
