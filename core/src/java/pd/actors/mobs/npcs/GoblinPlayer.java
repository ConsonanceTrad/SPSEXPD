/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class GoblinPlayer extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GoblinPlayer.class)
			.t("name", "神盾哥布林")
			.t("desc", "一个参加测试的哥布林，他手里拿着一块炫彩盾牌。")
			.t("yell1", "这个世界蛮不错，但比起哥布林族的试炼来说还是太简单了。")
			.t("yell2", "看见这块盾牌了吗，这是神盾。我们教派的支柱。");
	}



	public GoblinPlayer() {
		configure(Spec.GOBLIN_PLAYER);
		spriteClass = pd.sprites.GoblinPlayerSprite.class;
	}
}
