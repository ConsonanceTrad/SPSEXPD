/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.Torch;
import pd.items.consum.eggs.Egg;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.items.consum.scrolls.Scroll;
import pd.messages.InlineText;

public class GiftTorch extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftTorch.class)
			.t("desc", "普通的火堆。")
			.t("name", "燃烧的火堆")
			.t("normal", "（火星炸裂的声音）")
			.t("yell1", "（火苗普通地燃烧）")
			.t("yell2", "（火苗猛烈地燃烧）")
			.t("yell3", "（火堆普通地燃烧）")
			.t("yell4", "（火苗猛烈地燃烧）")
			.t("thank1", "（火焰稍微旺了点，你获得了火把）")
			.t("reward1", "（火苗猛烈地燃烧，一块碳块掉了出来）")
			.t("reward2", "（火苗猛烈地燃烧，一堆宝石掉了出来）");
	}

	{ properties.add(Property.HUMAN); }
	@Override public Visual visual() { return Visual.TORCH; }
	@Override public boolean acceptsGift(Item item) {
		return item instanceof Scroll || item instanceof Egg || named(item, "Gsword", "AresSword");
	}
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new BlueNornStone(), new GreenNornStone(),
				new OrangeNornStone(), new PurpleNornStone(), new YellowNornStone());
		if (friendship() % 30 == 0) return result("reward1", new StoneOre());
		return result("thank1", new Torch());
	}
}
