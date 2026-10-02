/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Lery extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Lery.class)
			.t("desc", "破碎地牢的翻译者之一，同时也是最早期像素地牢更新日志的翻译者之一。")
			.t("name", "驯兽师-论坛修齐")
			.t("yell1", "你足够冷酷吗?")
			.t("yell2", "我这里出售各种蛋。如果你是一个出色的训练家的话，你也可以训练出像我那么强大的怪物的。");
	}

	public Lery() {
		configure(Spec.LERY);
		spriteClass = pd.sprites.LerySprite.class;
	}
}
