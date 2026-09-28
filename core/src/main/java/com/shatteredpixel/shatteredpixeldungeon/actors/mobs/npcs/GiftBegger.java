/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.YellowDewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.VelociroosterEgg;

public class GiftBegger extends GiftNpc {
	{ properties.add(Property.HUMAN); }
	@Override public Visual visual() { return Visual.BEGGER; }
	@Override public boolean acceptsGift(Item item) { return item != null && !item.unique; }
	@Override protected GiftResult reward(Hero hero) {
		return friendship() == 100 ? result("reward1", new VelociroosterEgg()) : result("thank1", new YellowDewdrop());
	}
}
