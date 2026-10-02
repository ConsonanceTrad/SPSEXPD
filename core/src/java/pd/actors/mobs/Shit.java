/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.Item;
import pd.items.consum.potions.PotionOfToxicGas;
import pd.sprites.ShitSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the toilet elf. */
public class Shit extends SpsSewerMobs.Shit {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Shit.class)
			.t("name", "马桶精灵")
			.t("desc", "住在下水道的精灵，与腐坏一起生活。");
	}




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
