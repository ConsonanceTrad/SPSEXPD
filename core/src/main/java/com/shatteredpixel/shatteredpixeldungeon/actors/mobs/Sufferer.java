/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.RedDewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SuffererSprite;
import com.watabou.utils.Random;

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
