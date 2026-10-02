/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class AFly extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(AFly.class)
			.t("name", "阿飞，阿比和阿比斯")
			.t("yell1", "你好啊，我是阿飞，旁边两个分别是阿比和阿比斯。")
			.t("desc", "不思议地牢的制作者");
	}

	public AFly() {
		configure(Spec.A_FLY);
		spriteClass = pd.sprites.AFlySprite.class;
	}
}
