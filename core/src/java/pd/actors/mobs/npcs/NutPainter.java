/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class NutPainter extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(NutPainter.class)
			.t("name", "坚果教教主")
			.t("desc", "一个测试画家，十分喜欢画坚果。")
			.t("yell1", "坚果nb!!!")
			.t("yell2", "坚果万岁!!");
	}

	public NutPainter() {
		configure(Spec.NUT_PAINTER);
		spriteClass = pd.sprites.PainterSprite.class;
	}
}
