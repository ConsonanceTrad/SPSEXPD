/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeDewFruit extends DewFruit {
	static {
		InlineText.of(LargeDewFruit.class)
			.t("name", "集露大型果实")
			.t("desc", "精心种植的集露草结出的巨型果实，落地时溢出的露珠更多。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_DEWCATCHER; }
}
