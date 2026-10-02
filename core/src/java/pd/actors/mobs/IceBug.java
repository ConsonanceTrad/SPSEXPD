/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.FrostIce;
import pd.actors.damagetype.DamageType;
import pd.items.Item;
import pd.items.equipment.wands.WandOfFreeze;
import pd.items.equipment.wands.fusion.WandOfFlow;
import pd.items.equipment.weapon.enchantments.EnchantmentIce2;
import pd.items.equipment.weapon.enchantments.EnchantmentIce;
import pd.plants.Icecap;
import pd.scenes.GameScene;
import pd.sprites.IceBugSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the ice climber. */
public class IceBug extends SpsCaveMobs.IceBug {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(IceBug.class)
			.t("name", "冰足虫")
			.t("desc", "冰冷环境下生存的虫子。");
	}




	{
		spriteClass = IceBugSprite.class;
		properties.remove(Property.ICY);
		properties.add(Property.BEAST);
		resistances.add(DamageType.Ice.class);
		resistances.add(WandOfFlow.class);
		resistances.add(WandOfFreeze.class);
		immunities.add(FrostIce.class);
		immunities.add(EnchantmentIce.class);
		immunities.add(EnchantmentIce2.class);
	}

	@Override
	public Item SupercreateLoot() {
		return new Icecap.Seed();
	}

	public static Class<?> specialLootType() {
		return Icecap.Seed.class;
	}

	public static IceBug spawnAt(int cell) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(cell)
				|| !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null;
		IceBug mob = new IceBug();
		mob.pos = cell;
		mob.state = mob.HUNTING;
		GameScene.add(mob, 1f);
		return mob;
	}
}
