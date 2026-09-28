/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShitSprite;

/** Original SPS-PD runtime and save identity for the toilet elf. */
public class Shit extends SpsSewerMobs.Shit {

	{
		spriteClass = ShitSprite.class;
		properties.add(Property.ELF);
	}

	@Override
	public int attackSkill(Char target) {
		return 10 + legacyDepthAdjustment(0);
	}

	@Override
	public Item SupercreateLoot() {
		return new PotionOfToxicGas();
	}

	public static Class<?> specialLootType() {
		return PotionOfToxicGas.class;
	}
}
