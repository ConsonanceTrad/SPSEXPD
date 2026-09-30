/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.YellowDewdrop;
import pd.items.eggs.VelociroosterEgg;

public class GiftBegger extends GiftNpc {
	{ properties.add(Property.HUMAN); }
	@Override public Visual visual() { return Visual.BEGGER; }
	@Override public boolean acceptsGift(Item item) { return item != null && !item.unique; }
	@Override protected GiftResult reward(Hero hero) {
		return friendship() == 100 ? result("reward1", new VelociroosterEgg()) : result("thank1", new YellowDewdrop());
	}
}
