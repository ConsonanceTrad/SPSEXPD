/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.PowerHand;
import pd.sprites.ErrorSprite;
import pd.messages.InlineText;

public class UYog extends BossRushBoss {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(UYog.class)
			.t("name", "始祖之眼")
			.t("desc", "一只叫作始祖的眼睛……它看上去很眼熟。");
	}



	{
		spriteClass = ErrorSprite.class;
		baseSpeed = 0.75f;
		loot = new PowerHand();
		lootChance = 1f;
		properties.add(Property.UNKNOW);
	}
	@Override protected Class<? extends BossRushBoss> nextBoss() { return UAmulet.class; }
}
