/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrollWarriorSprite;

/** Original SPS-PD runtime and save identity for the troll warrior. */
public class TrollWarrior extends SpsPrisonMobs.TrollWarrior {

	{
		spriteClass = TrollWarriorSprite.class;
		properties.add(Property.TROLL);
		resistances.add(EnchantmentDark.class);
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.MUSICWEAPON);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.MUSICWEAPON;
	}
}
