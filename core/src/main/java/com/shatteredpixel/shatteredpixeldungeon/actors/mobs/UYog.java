/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.items.PowerHand;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ErrorSprite;

public class UYog extends BossRushBoss {
	{
		spriteClass = ErrorSprite.class;
		baseSpeed = 0.75f;
		loot = new PowerHand();
		lootChance = 1f;
		properties.add(Property.UNKNOW);
	}
	@Override protected Class<? extends BossRushBoss> nextBoss() { return UAmulet.class; }
}
