/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.weapon.enchantments.EnchantmentDark;
import pd.sprites.TrollWarriorSprite;

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
