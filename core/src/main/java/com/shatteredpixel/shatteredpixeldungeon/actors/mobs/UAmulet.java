/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.Playericon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.EnchantmentDark;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ErrorSprite;

/** Final shadow of the Amulet. Its death completes and unlocks the arena. */
public class UAmulet extends BossRushBoss {
	{
		spriteClass = ErrorSprite.class;
		baseSpeed = 0.75f;
		loot = new Playericon();
		lootChance = 1f;
		properties.add(Property.UNKNOW);
		resistances.add(EnchantmentDark.class);
		immunities.add(EnchantmentDark.class);
	}
	@Override protected Class<? extends BossRushBoss> nextBoss() { return null; }
}
