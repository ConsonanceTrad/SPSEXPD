/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class HBB extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HBB.class)
			.t("name", "工会主席-巨无霸卡比")
			.t("desc", "破碎地牢的翻译者之一，同时也是像素地牢吧的吧主，小马的爱好者。")
			.t("yell1", "\"The-world's-still-the-same, there's-just...less-in-it.\"")
			.t("yell2", "\"Friendship-is-magic!\"")
			.t("yell3", "\"I-am-a-magical-princess-from-another-dimension.\"");
	}



	public HBB() {
		configure(Spec.HBB);
		spriteClass = pd.sprites.HBBSprite.class;
	}
}
