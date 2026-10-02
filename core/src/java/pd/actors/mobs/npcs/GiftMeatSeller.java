/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.completefood.Honeymeat;
import pd.items.consum.food.meatfood.Meat;
import pd.messages.InlineText;

public class GiftMeatSeller extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftMeatSeller.class)
			.t("desc", "他卖肉，不卖身。喜欢有价值的东西。")
			.t("name", "卖肉的游商")
			.t("normal", "瞧一瞧看一看，新鲜的肉，拿东西来换吧。")
			.t("yell1", "走过路过不要错过啊。")
			.t("yell2", "过了这个村就没这个店了啊。")
			.t("yell3", "有钱的捧个钱场，没钱的捧个人场啊。")
			.t("yell4", "看你我有缘，以后我会罩着你的啊。")
			.t("thank1", "谢谢惠顾。")
			.t("reward1", "拿好这个小券，欢迎下次再来。")
			.t("reward2", "肉的制作方法很多，我最喜欢的是蜂蜜肉。这些蜜蜂肉就免费给你了。");
	}

	{ properties.add(Property.MECH); }
	@Override public Visual visual() { return Visual.MEAT_SELLER; }
	@Override public boolean acceptsGift(Item item) { return item != null && !item.unique && item.value() > 100; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new Honeymeat(), new Honeymeat(), new Honeymeat());
		if (friendship() % 30 == 0) return result("reward1", Generator.random(Generator.Category.SCROLL), new Meat());
		return result("thank1", new Meat());
	}
}
