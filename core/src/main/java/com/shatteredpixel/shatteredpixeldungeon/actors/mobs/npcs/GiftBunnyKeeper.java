/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.EasterEgg;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.shatteredpixel.shatteredpixeldungeon.items.sellitem.MiniBunny;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public class GiftBunnyKeeper extends GiftNpc {
	{ properties.add(Property.MECH); }
	@Override public Visual visual() { return Visual.BUNNY_KEEPER; }
	@Override public boolean acceptsGift(Item item) { return item instanceof Egg || item instanceof Weapon; }
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new EasterEgg());
		if (friendship() % 40 == 0) return result("reward1", new MiniBunny());
		return result("thank1");
	}
}
