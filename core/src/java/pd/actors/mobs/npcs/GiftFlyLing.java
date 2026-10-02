/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.medicine.LingPotion;
import pd.items.consum.potions.PotionOfMending;
import pd.items.specific.sellitem.LingHeart;
import pd.plants.Plant;
import render.utils.math.Random;

public class GiftFlyLing extends GiftNpc {
	{ properties.add(Property.ELF); }
	@Override public Visual visual() { return Visual.FLY_LING; }
	@Override public boolean acceptsGift(Item item) {
		return item instanceof PotionOfMending || item instanceof Plant.Seed || item instanceof Fruit;
	}
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward3", new LingHeart());
		if (friendship() % 100 == 0) return result("reward2", Generator.random(Generator.Category.WAND));
		if (friendship() % 40 == 0) return result("reward1", new LingPotion());
		return result("thank" + Random.IntRange(1, 3));
	}
}
