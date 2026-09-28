/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.MachineArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.food.WaterItem;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.OverpricedRation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunE;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

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
