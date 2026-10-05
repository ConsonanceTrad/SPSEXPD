/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.messages.InlineText;

/** SPSEXPD: 精心种植（花盆）产出的强化果实，效果强度翻倍。 */
public class LargeTransmuteFruit extends TransmuteFruit {
	static {
		InlineText.of(LargeTransmuteFruit.class)
			.t("name", "转换大型果实")
			.t("desc", "精心种植的转换笼结出的巨型果实，落地效果更强。");
	}
	{ large = true; image = pd.atlas.items.ConsumPotionSeedSeedDict.LARGE_FRUIT_TRANSMUTE_CAGE; }
}
