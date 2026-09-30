/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.eggs.PigpetEgg;
import pd.items.food.vegetable.Truffles;
import watabou.utils.Random;

public class GiftAshWolf extends GiftNpc {
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
