/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SandmobSprite;

/** Original SPS-PD runtime and save identity for the sand creature. */
public class SandMob extends SpsCaveMobs.SandMob {

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
