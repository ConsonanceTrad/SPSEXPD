/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.medicine.LingPotion;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.specific.sellitem.LingHeart;
import pd.plants.Plant;
import render.utils.math.Random;
import pd.messages.InlineText;

public class GiftFlyLing extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftFlyLing.class)
			.t("desc", "春季野营是一个不错的选择，前提是带足可口食物。")
			.t("name", "发呆的澪")
			.t("normal", "在我那个世界里，也有和你一样不怕困难、舍身前往黄金之城实验室的人。所以我认为，每个冒险者都值得尊重。")
			.t("yell1", "嗯，不用感谢我。只是帮助你一下。")
			.t("yell2", "嗯，不用感谢我。只是帮助你一下。")
			.t("yell3", "嗯，不用感谢我。只是帮助你一下。")
			.t("yell4", "不用谢我。另外，路上小心，祝你一路顺风。")
			.t("thank1", "谢谢你的礼物，我会好好珍惜的。")
			.t("thank2", "这……这礼物是送给我的吗？！非常谢谢你，我非常喜欢这礼物！")
			.t("thank3", "你不仅身手了得，而且也如此可爱。这礼物我非常喜欢，我也会送你一个礼物。")
			.t("reward1", "觉得你很有战斗的潜力，我想这瓶药剂可以帮到你。")
			.t("reward2", "你很可爱，我想送你一件礼物。这个法杖会帮助到你。")
			.t("reward3", "我将为你祷告并祝福你，这个水晶项链你也拿去。");
	}



	{ properties.add(Property.ELF); }
	@Override public Visual visual() { return Visual.FLY_LING; }
	@Override public boolean acceptsGift(Item item) {
		return item instanceof PotionOfHealing || item instanceof Plant.Seed || item instanceof Fruit;
	}
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward3", new LingHeart());
		if (friendship() % 100 == 0) return result("reward2", Generator.random(Generator.Category.WAND));
		if (friendship() % 40 == 0) return result("reward1", new LingPotion());
		return result("thank" + Random.IntRange(1, 3));
	}
}
