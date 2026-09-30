/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Gold;
import pd.items.Item;
import pd.items.artifacts.MasterThievesArmband;
import pd.items.sellitem.VIPcard;
import pd.sprites.GoldCollectorSprite;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the goblin tax collector. */
public class GoldCollector extends SpsPrisonMobs.GoldCollector {

	{
		spriteClass = GoldCollectorSprite.class;
		properties.add(Property.GOBLIN);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new Gold(100), new VIPcard(), new MasterThievesArmband());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{Gold.class, VIPcard.class, MasterThievesArmband.class};
	}
}
