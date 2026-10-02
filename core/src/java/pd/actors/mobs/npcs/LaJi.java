/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class LaJi extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LaJi.class)
			.t("name", "原罪学者-司徒")
			.t("desc", "这是一个祭师，同时也是小镇的维修者。")
			.t("yell1", "这位客人，能不能让我耽误你一点时间讲一讲我们的天父克苏鲁。")
			.t("yell2", "梦与白昼者所知晓之诸多事物常为梦于黑夜者所忘却。")
			.t("yell3", "另外...维护这个小镇很累的......");
	}

	public LaJi() {
		configure(Spec.LAJI);
		spriteClass = pd.sprites.LaJiSprite.class;
	}
}
