/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class FruitCat extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FruitCat.class)
			.t("desc", "这是一个很可爱的玩家。")
			.t("name", "工会荣誉商人-菜猫")
			.t("yell1", "自从椰子走后，这家店就由我来管理。放心好了，我可不会涨价。")
			.t("yell2", "信不信由你，在椰子离开之前，他没有带任何东西，只是把它们堆起来让它们吔尘！无论如何，你可能比我更需要它们。");
	}

	public FruitCat() {
		configure(Spec.FRUIT_CAT);
		spriteClass = pd.sprites.FruitCatSprite.class;
	}
}
