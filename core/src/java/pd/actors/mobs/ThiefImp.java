/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.items.Item;
import pd.items.equipment.artifacts.ChaliceOfBlood;
import pd.items.consum.potions.PotionOfInvisibility;
import pd.items.consum.scrolls.ScrollOfRage;
import pd.sprites.ThiefImpSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the thief imp. */
public class ThiefImp extends SpsHallsMobs.ThiefImp {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ThiefImp.class)
			.t("name", "强盗小鬼")
			.t("desc", "小鬼是地狱中最底层的居民。它们没有固定收入，所以经常到其他地方偷窃。")
			.t("stole", "小鬼偷走了%s！")
			.t("carries", "\n\n这个小鬼携带着_%s_。明显是偷来的。");
	}




	{
		spriteClass = ThiefImpSprite.class;
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new PotionOfInvisibility(), new ScrollOfRage(), new ChaliceOfBlood());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{PotionOfInvisibility.class, ScrollOfRage.class, ChaliceOfBlood.class};
	}
}
