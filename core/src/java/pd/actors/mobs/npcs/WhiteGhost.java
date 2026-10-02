/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class WhiteGhost extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WhiteGhost.class)
			.t("name", "白幽妹")
			.t("yell1", "我很高兴还有人记得我。")
			.t("desc", "由于高塔的实验，她已经失去了原有的形体。");
	}



	public WhiteGhost() {
		configure(Spec.WHITE_GHOST);
		spriteClass = pd.sprites.WhiteGhostSprite.class;
	}
}
