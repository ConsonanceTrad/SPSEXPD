/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeFireFruit extends FireFruit {
	static {
		InlineText.of(LargeFireFruit.class)
			.t("name", "火焰大型果实")
			.t("desc", "精心种植的烈焰花结出的巨型果实，命中与食用效果都是普通果实的 3 倍。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_FIREBLOOM; }
}
