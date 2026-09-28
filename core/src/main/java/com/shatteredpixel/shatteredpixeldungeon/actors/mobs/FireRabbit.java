/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.FireEffectDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFirebolt;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FireRabbitSprite;

/** Original SPS-PD runtime and save identity for the prison fire trooper. */
public class FireRabbit extends SpsPrisonMobs.FireRabbit {

	{
		spriteClass = FireRabbitSprite.class;
		properties.remove(Property.FIERY);
		properties.add(Property.ORC);
		immunities.remove(FireEffectDamage.class);
		immunities.add(DamageType.Fire.class);
		immunities.add(Fire.class);
		immunities.add(WandOfFirebolt.class);
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.FOOD);
	}

	public static Generator.Category specialLootCategory() {
		return Generator.Category.FOOD;
	}
}
