/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class G2159687 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(G2159687.class)
			.t("name", "G2159687")
			.t("desc", "简单发芽的作者及汉化者，国人，现在处于弃坑状态。")
			.t("yell1", "我是G2159687，喜欢我的简单发芽吗?我可没有其他台词。")
			.t("yell2", "我想应该有一些玩家会认为地牢类游戏比较难，所以我弄了几个简单版本。");
	}

	public G2159687() {
		configure(Spec.G2159687);
		spriteClass = pd.sprites.G2159687Sprite.class;
	}
}
