/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeFlavorlessFruit extends FlavorlessFruit {
	static {
		InlineText.of(LargeFlavorlessFruit.class)
			.t("name", "无味大型果实")
			.t("desc", "精心种植的无味果丛结出的巨型果实，落地时溅出的清水更多。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_BLANDFRUIT; }
}
