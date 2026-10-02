/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Watabou extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Watabou.class)
			.t("desc", "像素地牢的创造者。")
			.t("name", "Watabou")
			.t("yell1", "为什么不试试我的其他游戏呢?")
			.t("yell2", "像素地牢现在停止更新了......");
	}



	public Watabou() {
		configure(Spec.WATABOU);
		spriteClass = pd.sprites.WatabouSprite.class;
	}
}
