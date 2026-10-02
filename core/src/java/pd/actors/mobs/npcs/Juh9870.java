/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Juh9870 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Juh9870.class)
			.t("name", "Juh9870")
			.t("desc", "月光地牢的制作者。")
			.t("yell1", "我记得我的祖父曾在月光下对我说过，严禁空想，脚踏实地。")
			.t("yell2", "所以...我希望我能在这找到更多有意义的东西。");
	}



	public Juh9870() {
		configure(Spec.JUH9870);
		spriteClass = pd.sprites.Juh9870Sprite.class;
	}
}
