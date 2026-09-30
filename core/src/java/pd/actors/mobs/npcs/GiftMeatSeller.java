/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.items.food.completefood.Honeymeat;
import pd.items.food.meatfood.Meat;

public class GiftMeatSeller extends GiftNpc {
	{ properties.add(Property.MECH); }
	@Override public Visual visual() { return Visual.MEAT_SELLER; }
	@Override public boolean acceptsGift(Item item) { return item != null && !item.unique && item.value() > 100; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new Honeymeat(), new Honeymeat(), new Honeymeat());
		if (friendship() % 30 == 0) return result("reward1", Generator.random(Generator.Category.SCROLL), new Meat());
		return result("thank1", new Meat());
	}
}
