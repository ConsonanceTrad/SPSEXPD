/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.Torch;
import pd.items.eggs.Egg;
import pd.items.nornstone.BlueNornStone;
import pd.items.nornstone.GreenNornStone;
import pd.items.nornstone.OrangeNornStone;
import pd.items.nornstone.PurpleNornStone;
import pd.items.nornstone.YellowNornStone;
import pd.items.scrolls.Scroll;

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
