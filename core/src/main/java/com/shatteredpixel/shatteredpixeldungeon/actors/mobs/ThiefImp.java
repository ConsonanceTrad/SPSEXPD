/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ThiefImpSprite;
import com.watabou.utils.Random;

/** Original SPS-PD runtime and save identity for the thief imp. */
public class ThiefImp extends SpsHallsMobs.ThiefImp {

	{
		spriteClass = ThiefImpSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new PotionOfInvisibility(), new ScrollOfRage(), new ChaliceOfBlood());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{PotionOfInvisibility.class, ScrollOfRage.class, ChaliceOfBlood.class};
	}
}
