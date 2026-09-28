/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.AflyFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.CompleteFood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;

public class GiftAFly extends GiftNpc {
	{ properties.add(Property.ELF); }
	@Override public Visual visual() { return Visual.A_FLY; }
	@Override public boolean acceptsGift(Item item) { return item instanceof CompleteFood; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new AflyFood(), new AflyFood(), new AflyFood());
		if (friendship() % 30 == 0) return result("reward1", new ScrollOfPsionicBlast(), new PotionOfMindVision());
		return result("thank1");
	}
}
