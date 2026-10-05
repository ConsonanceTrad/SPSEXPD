/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeSwiftFruit extends SwiftFruit {
	static {
		InlineText.of(LargeSwiftFruit.class)
			.t("name", "速行大型果实")
			.t("desc", "精心种植的速行蓟结出的巨型果实，加速效果更强。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_SWIFTTHISTLE; }
}
