/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.eggs.EasterEgg;
import pd.items.consum.eggs.Egg;
import pd.items.specific.sellitem.MiniBunny;
import pd.items.equipment.weapon.Weapon;
import pd.messages.InlineText;

public class GiftBunnyKeeper extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftBunnyKeeper.class)
			.t("desc", "她正在把兔子叠起来。为啥呢，不知道。")
			.t("name", "叠兔子的养兔人")
			.t("normal", "你好啊，愿意帮我收集兔子吗？")
			.t("yell1", "这些兔子会乱跑，你需要智慧来抓住它们。")
			.t("yell2", "如果让它们跑到洞里的话，也没啥关系。")
			.t("yell3", "兔子毕竟是有限的，但如果你把它们赶到一起的话……嘿嘿。")
			.t("yell4", "请支持Paquerette-Down-the-Bunburrows，谢谢。")
			.t("thank1", "兔~兔~")
			.t("reward1", "看，新的兔兔。")
			.t("reward2", "看，大兔兔。");
	}



	{ properties.add(Property.MECH); }
	@Override public Visual visual() { return Visual.BUNNY_KEEPER; }
	@Override public boolean acceptsGift(Item item) { return item instanceof Egg || item instanceof Weapon; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new EasterEgg());
		if (friendship() % 40 == 0) return result("reward1", new MiniBunny());
		return result("thank1");
	}
}
