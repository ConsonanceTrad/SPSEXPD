/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.actors.blobs.effectblobs.Fire;
import pd.actors.damagetype.DamageType;
import pd.items.Generator;
import pd.items.Item;
import pd.items.wands.WandOfFirebolt;
import pd.sprites.FireRabbitSprite;

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
