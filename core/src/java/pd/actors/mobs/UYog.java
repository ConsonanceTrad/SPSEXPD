/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.PowerHand;
import pd.sprites.ErrorSprite;

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
