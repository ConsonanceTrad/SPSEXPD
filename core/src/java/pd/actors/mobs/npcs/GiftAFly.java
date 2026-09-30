/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.food.AflyFood;
import pd.items.food.completefood.CompleteFood;
import pd.items.potions.PotionOfMindVision;
import pd.items.scrolls.ScrollOfPsionicBlast;

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
