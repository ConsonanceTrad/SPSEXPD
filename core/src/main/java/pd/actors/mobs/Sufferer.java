/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.RedDewdrop;
import pd.items.artifacts.UnstableSpellbook;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.sprites.SuffererSprite;
import watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the corrupted warlock. */
public class Sufferer extends SpsHallsMobs.Sufferer {

	{
		spriteClass = SuffererSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new ScrollOfUpgrade(), new RedDewdrop(), new UnstableSpellbook());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{ScrollOfUpgrade.class, RedDewdrop.class, UnstableSpellbook.class};
	}
}
