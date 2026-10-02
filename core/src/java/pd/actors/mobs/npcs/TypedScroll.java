/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class TypedScroll extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TypedScroll.class)
			.t("name", "TypedScroll")
			.t("desc", "LCPD的制作者。虽然他的确有不错的编程能力，但是他的没有仔细考虑程序总会有许多bug。")
			.t("yell1", "嘿，为什么我长成这样，为什么和我说话，我不知道该和你说什么。")
			.t("yell2", "LCPD已经上架谷歌商店啦，快去下载吧！");
	}



	public TypedScroll() {
		configure(Spec.TYPED_SCROLL);
		spriteClass = pd.sprites.TypedScrollSprite.class;
	}
}
