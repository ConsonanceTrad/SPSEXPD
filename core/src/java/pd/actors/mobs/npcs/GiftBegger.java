/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.YellowDewdrop;
import pd.items.consum.eggs.VelociroosterEgg;
import pd.messages.InlineText;

public class GiftBegger extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftBegger.class)
			.t("desc", "四处流浪的乞讨者，来者不拒。")
			.t("name", "无名的乞丐")
			.t("normal", "行行好吧。")
			.t("yell1", "行行好吧。")
			.t("yell2", "行行好吧。")
			.t("yell3", "行行好吧。")
			.t("yell4", "愿神保佑你。")
			.t("thank1", "太感谢了，我水都出来了。")
			.t("reward1", "我不知道该怎么谢你。这只鸡本来是午饭，现在就作为回礼给你吧。");
	}



	{ properties.add(Property.HUMAN); }
	@Override public Visual visual() { return Visual.BEGGER; }
	@Override public boolean acceptsGift(Item item) { return item != null && !item.unique; }
	@Override protected GiftResult reward(Hero hero) {
		return friendship() == 100 ? result("reward1", new VelociroosterEgg()) : result("thank1", new YellowDewdrop());
	}
}
