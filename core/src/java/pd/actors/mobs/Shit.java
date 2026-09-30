/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Item;
import pd.items.potions.PotionOfToxicGas;
import pd.sprites.ShitSprite;

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
