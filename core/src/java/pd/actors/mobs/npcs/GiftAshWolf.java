/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.eggs.PigpetEgg;
import pd.items.consum.food.vegetable.Truffles;
import render.utils.math.Random;
import pd.messages.InlineText;

public class GiftAshWolf extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftAshWolf.class)
			.t("desc", "阿萨正在拍摄地牢求生这一节目，一同拍摄的还有他领地内部各种生物。")
			.t("name", "地牢求生的阿萨男爵")
			.t("normal", "来到地下城冒险可没法带上能吃一辈子的粮食，合理利用就地取材吧。")
			.t("yell1", "再怎么饥不择食，也尽量别吃生肉，合理利用陷阱或魔法吧。")
			.t("yell2", "打干粮包虽然不错，但你会发现把它们煮一下再吃更美味。")
			.t("yell3", "用蜂蜜制作的料理营养丰富，可以有效地提高你的耐力。")
			.t("yell4", "有时你会发现，在孤独的旅程中，一个伙伴是多么的重要。")
			.t("thank1", "呵呵呵，你怎么会知道我刚好需要这个。")
			.t("thank2", "闻起来味道很不错，那我就不客气了。")
			.t("reward1", "刚好采到了不少这个，应该挺值钱的，送你一些。")
			.t("reward2", "这个小家伙很粘人，相信它更愿意陪你出去冒险。");
	}



	{ properties.add(Property.ORC); }
	@Override public Visual visual() { return Visual.ASH_WOLF; }
	@Override public boolean acceptsGift(Item item) {
		return named(item, "Meatroll", "Vegetablekebab", "Vegetableroll", "Kebab",
				"Porksoup", "Vegetablesoup", "Fruitsalad");
	}
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new PigpetEgg());
		if (friendship() % 30 == 0) return result("reward1", new Truffles());
		return result("thank" + Random.IntRange(1, 2));
	}
}
