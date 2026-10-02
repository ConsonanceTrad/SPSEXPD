/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.food.AflyFood;
import pd.items.consum.food.completefood.CompleteFood;
import pd.items.consum.potions.PotionOfMindVision;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.messages.InlineText;

public class GiftAFly extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftAFly.class)
			.t("desc", "春季野营是一个不错的选择，前提是带足可口食物。")
			.t("name", "野营的阿飞、阿比和阿比斯")
			.t("normal", "天气不错，一起来吃点什么吧。")
			.t("yell1", "怎么那么多灵视药水啊，食物呢？")
			.t("yell2", "怎么那么多灵能汲取卷轴啊，纸巾呢？")
			.t("yell3", "怎么连袜子都混进去了？")
			.t("yell4", "嘛，至少野营是顺利的，不是吗。")
			.t("thank1", "是……是食物，太好了。")
			.t("reward1", "白拿这么多吃的太不好意思了，这药水和卷轴就给你好了。")
			.t("reward2", "多亏了你，野营顺利结束了。这3个饭团带有属于我们的专属祝福。");
	}



	{ properties.add(Property.ELF); }
	@Override public Visual visual() { return Visual.A_FLY; }
	@Override public boolean acceptsGift(Item item) { return item instanceof CompleteFood; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new AflyFood(), new AflyFood(), new AflyFood());
		if (friendship() % 30 == 0) return result("reward1", new ScrollOfPsionicBlast(), new PotionOfMindVision());
		return result("thank1");
	}
}
