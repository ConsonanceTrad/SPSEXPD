/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Vegetable;
import com.shatteredpixel.shatteredpixeldungeon.plants.ReNepenth;
import com.shatteredpixel.shatteredpixeldungeon.plants.Seedpod;
import com.shatteredpixel.shatteredpixeldungeon.plants.StarEater;

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
