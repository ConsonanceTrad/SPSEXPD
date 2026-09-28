/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.eggs.Egg;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.BlueNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.GreenNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.OrangeNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.PurpleNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.nornstone.YellowNornStone;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;

public class GiftTorch extends GiftNpc {
	{ properties.add(Property.HUMAN); }
	@Override public Visual visual() { return Visual.TORCH; }
	@Override public boolean acceptsGift(Item item) {
		return item instanceof Scroll || item instanceof Egg || named(item, "Gsword", "AresSword");
	}
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward2", new BlueNornStone(), new GreenNornStone(),
				new OrangeNornStone(), new PurpleNornStone(), new YellowNornStone());
		if (friendship() % 30 == 0) return result("reward1", new StoneOre());
		return result("thank1", new Torch());
	}
}
