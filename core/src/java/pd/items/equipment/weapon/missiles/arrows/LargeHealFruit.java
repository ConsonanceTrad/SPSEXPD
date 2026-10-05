/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeHealFruit extends HealFruit {
	static {
		InlineText.of(LargeHealFruit.class)
			.t("name", "阳春大型果实")
			.t("desc", "精心种植的阳春草结出的巨型果实，治疗效果更强。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_SUNGRASS; }
}
