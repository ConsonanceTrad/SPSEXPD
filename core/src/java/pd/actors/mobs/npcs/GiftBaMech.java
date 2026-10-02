/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.armor.normalarmor.MachineArmor;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.staplefood.OverpricedRation;
import pd.items.equipment.weapon.guns.GunE;
import pd.plants.Plant;

public class GiftBaMech extends GiftNpc {
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
