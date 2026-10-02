/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.artifacts.ChaliceOfBlood;
import pd.items.consum.potions.PotionOfInvisibility;
import pd.items.consum.scrolls.ScrollOfRage;
import pd.sprites.ThiefImpSprite;
import render.utils.math.Random;

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
