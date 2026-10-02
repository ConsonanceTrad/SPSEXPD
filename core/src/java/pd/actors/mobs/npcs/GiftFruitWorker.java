/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.vegetable.Vegetable;
import pd.plants.ReNepenth;
import pd.plants.Seedpod;
import pd.plants.StarEater;

public class GiftFruitWorker extends GiftNpc {
	{ properties.add(Property.MECH); }
	@Override public Visual visual() { return Visual.FRUIT_WORKER; }
	@Override public boolean acceptsGift(Item item) { return item instanceof Vegetable || item instanceof Fruit; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new ReNepenth.Seed(), new StarEater.Seed(), new Seedpod.Seed());
		if (friendship() % 40 == 0) return result("reward1", Generator.random(Generator.Category.HIGHFOOD));
		return result("thank1");
	}
}
