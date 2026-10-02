/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class NewPlayer extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(NewPlayer.class)
			.t("name", "你")
			.t("desc", "？？？")
			.t("yell1", "这是你。")
			.t("yell2", "这就是你。");
	}



	public NewPlayer() {
		configure(Spec.NEW_PLAYER);
		spriteClass = pd.sprites.NewPlayerSprite.class;
	}
}
