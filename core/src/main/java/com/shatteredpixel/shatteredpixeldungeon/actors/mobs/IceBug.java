/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostIce;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFreeze;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.fusion.WandOfFlow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentIce;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentIce2;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.IceBugSprite;

/** Original SPS-PD runtime and save identity for the ice climber. */
public class IceBug extends SpsCaveMobs.IceBug {

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
