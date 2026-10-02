/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.damagetype.DamageType;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.wands.WandOfLightning;
import pd.sprites.SandmobSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the sand creature. */
public class SandMob extends SpsCaveMobs.SandMob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SandMob.class)
			.t("name", "沙怪")
			.t("desc", "一种由沙子组成的怪物，死亡后会分裂。\n元素")
			.t("minisand.name", "迷你沙怪")
			.t("minisand.desc", "迷你沙怪");
	}


	{
		spriteClass = SandmobSprite.class;
		properties.add(Property.ELEMENT);
		resistances.add(DamageType.Shock.class);
		resistances.add(WandOfLightning.class);
	}

	@Override
	protected SpsCaveMobs.SandMob.MiniSand newMiniSand() {
		return new MiniSand();
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.GOLD);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.GOLD;
	}

	public static class MiniSand extends SpsCaveMobs.SandMob.MiniSand {
		{
			spriteClass = SandmobSprite.class;
		}
	}
}
