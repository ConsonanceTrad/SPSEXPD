package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.consum.food.fruit.Cherry;
import pd.items.consum.food.fruit.Strawberry;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.food.vegetable.NutVegetable;
import pd.items.equipment.weapon.missiles.arrows.NutFruit;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;

public class NutPlant extends Plant {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(NutPlant.class)
			.t("name", "坚果藤")
			.t("desc", "一种可食用的藤蔓，总会结出地牢坚果，偶尔还会带有鲜莓。")
			.t("warden_desc", "_守望者_踩踏藤蔓时也能取得完整收获。")
			.t("$seed.name", "坚果藤之种")
			.t("$exnutplant.name", "坚果藤果丛")
			.t("$exnutplant.desc", "生长硬壳果的果丛。");
	}



	{ image = 17; seedClass = Seed.class; }
	@Override public void activate(Char ch) {
		if (Random.Int(5) == 1) {
			if (Random.Int(2) == 1) Dungeon.level.drop(new Strawberry(), pos).sprite.drop();
			else Dungeon.level.drop(new Cherry(), pos).sprite.drop();
		} else {
			Dungeon.level.drop(new Nut(), pos).sprite.drop();
		}
		Dungeon.level.drop(new NutVegetable(), pos).sprite.drop();
	}
	public static class Seed extends Plant.Seed {
		{ image = ConsumPotionSeedSeedDict.SEED_NUTVINE; plantClass = NutPlant.class; explantClass = ExNutPlant.class; }
	}
	public static class ExNutPlant extends SpsFruitBush {
		{ image = 17; }
	}
}
