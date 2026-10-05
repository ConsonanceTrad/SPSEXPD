/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeFreshFruit extends FreshFruit {
	static {
		InlineText.of(LargeFreshFruit.class)
			.t("name", "鲜莓大型果实")
			.t("desc", "精心种植的鲜莓丛结出的巨型果实，治疗与落地产出都更强。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_ROT_BERRY; }
}
