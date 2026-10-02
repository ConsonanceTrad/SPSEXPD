/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.vegetable.Vegetable;
import pd.plants.ReNepenth;
import pd.plants.Seedpod;
import pd.plants.StarEater;
import pd.messages.InlineText;

public class GiftFruitWorker extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftFruitWorker.class)
			.t("desc", "水果和蔬菜，他都喜欢。")
			.t("name", "物色田地的果农")
			.t("normal", "抡起镐子开干吧，不管是贫瘠的大地还是富养的花盆。")
			.t("yell1", "哦，有事吗。")
			.t("yell2", "你的背包飘出了肥料的气息，你一定也是个开拓者。")
			.t("yell3", "花盆里面总能种出好东西，可能和花盆里的土壤有关系。")
			.t("yell4", "出色的孩子啊，愿你旅途坦荡。")
			.t("thank1", "这成色，这口感，我喜欢。")
			.t("reward1", "孩子，要多吃东西。")
			.t("reward2", "算下来你也送了我好多东西了。这些是稀有的种子，都给你了。");
	}

	{ properties.add(Property.MECH); }
	@Override public Visual visual() { return Visual.FRUIT_WORKER; }
	@Override public boolean acceptsGift(Item item) { return item instanceof Vegetable || item instanceof Fruit; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new ReNepenth.Seed(), new StarEater.Seed(), new Seedpod.Seed());
		if (friendship() % 40 == 0) return result("reward1", Generator.random(Generator.Category.HIGHFOOD));
		return result("thank1");
	}
}
