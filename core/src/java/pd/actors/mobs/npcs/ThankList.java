/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class ThankList extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ThankList.class)
			.t("name", "感谢名单")
			.t("yell1", "以及所有游玩这个游戏的玩家。")
			.t("desc", "朴实无华的感谢列表。");
	}



	public ThankList() {
		configure(Spec.THANK_LIST);
		spriteClass = pd.sprites.ThankListSprite.class;
	}
}
