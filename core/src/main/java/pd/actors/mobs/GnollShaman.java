/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.blobs.Electricity;
import pd.items.Item;
import pd.items.artifacts.SandalsOfNature;
import pd.items.potions.PotionOfLevitation;
import pd.items.scrolls.ScrollOfRegrowth;
import pd.sprites.GnollShamanSprite;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the cave shaman. */
public class GnollShaman extends SpsCaveMobs.GnollShaman {

	{
		spriteClass = GnollShamanSprite.class;
		properties.add(Property.ORC);
		properties.add(Property.MAGICER);
		resistances.add(Electricity.class);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new ScrollOfRegrowth(), new PotionOfLevitation(), new SandalsOfNature());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{ScrollOfRegrowth.class, PotionOfLevitation.class, SandalsOfNature.class};
	}
}
