/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.weapon.enchantments.EnchantmentDark;
import pd.sprites.TrollWarriorSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the troll warrior. */
public class TrollWarrior extends SpsPrisonMobs.TrollWarrior {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TrollWarrior.class)
			.t("name", "巨魔战士")
			.t("desc", "监狱沦陷前，巨魔是其中最强大的势力之一，就连守卫也不愿招惹他们。")
			.t("angry", "你竟敢攻击我？！");
	}


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
