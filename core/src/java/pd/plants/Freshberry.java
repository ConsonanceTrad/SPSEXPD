package pd.plants;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Generator;
import pd.messages.InlineText;

public class Freshberry extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Freshberry.class)
			.t("name", "鲜莓丛")
			.t("desc", "腐莓丛新鲜可口的近亲。它会结出鲜莓，并保留另一株植物的一颗种子。")
			.t("warden_desc", "_守望者_可以毫无额外风险地收获两种产物。")
			.t("$seed.name", "鲜莓果之种")
			.t("$exfreshberry.name", "鲜莓果丛")
			.t("$exfreshberry.desc", "生长鲜莓的果丛。");
	}



	{ image = 7; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		Dungeon.level.drop(Generator.random(Generator.Category.SEED), pos).sprite.drop();
		Dungeon.level.drop(Generator.random(Generator.Category.BERRY), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = ConsumPotionSeedSeedDict.SEED_ROT_BERRY; plantClass = Freshberry.class; explantClass = ExFreshberry.class; }
	}
	public static class ExFreshberry extends SpsFruitBush {
		{ image = 7; harvestCount = 3; harvestCategory = Generator.Category.SPS_BERRY; }
	}
}
