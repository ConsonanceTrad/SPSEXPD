/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollShamanSprite;
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
