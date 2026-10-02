/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.consum.eggs.EasterEgg;
import pd.items.consum.eggs.Egg;
import pd.items.specific.sellitem.MiniBunny;
import pd.items.equipment.weapon.Weapon;

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
