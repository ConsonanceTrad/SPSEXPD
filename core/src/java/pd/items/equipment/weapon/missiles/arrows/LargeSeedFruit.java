/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeSeedFruit extends SeedFruit {
	static {
		InlineText.of(LargeSeedFruit.class)
			.t("name", "种子大型果实")
			.t("desc", "精心种植的种子荚结出的巨型果实，落地时会撒出更多种子。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_POD; }
}
