/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeBlindFruit extends BlindFruit {
	static {
		InlineText.of(LargeBlindFruit.class)
			.t("name", "致盲大型果实")
			.t("desc", "精心种植的致盲草结出的巨型果实，命中与食用效果都是普通果实的 3 倍。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_BLINDWEED; }
}
