/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.armor.normalarmor.MachineArmor;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.equipment.weapon.guns.GunE;
import pd.plants.Plant;
import pd.messages.InlineText;

public class GiftBaMech extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftBaMech.class)
			.t("desc", "壁垒用于开拓不明地区的装置。")
			.t("name", "壁垒专属开拓探机")
			.t("normal", "滴……滴……滴……滴……")
			.t("yell1", "土.壤.环.境.调.查")
			.t("yell2", "生.物.环.境.调.查")
			.t("yell3", "气.候.环.境.调.查")
			.t("yell4", "调.查.完.成.分.析.中")
			.t("thank1", "指.标.上.传.已.完.成")
			.t("reward1", "补.给.已.配.送")
			.t("reward2", "开.拓.完.成.点.数.传.输.中");
	}



	{ properties.add(Property.MECH); }
	@Override public Visual visual() { return Visual.BA_MECH; }
	@Override public boolean acceptsGift(Item item) { return item instanceof Plant.Seed || item instanceof WaterItem; }
	@Override protected int friendshipAdjustment() { return -5; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new MachineArmor(), new GunE());
		if (friendship() % 30 == 0) return result("reward1", new OverpricedRation(), new OverpricedRation());
		return result("thank1");
	}
}
