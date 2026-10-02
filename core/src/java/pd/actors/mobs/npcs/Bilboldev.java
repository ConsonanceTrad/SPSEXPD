/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Bilboldev extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Bilboldev.class)
			.t("name", "Bilboldev")
			.t("desc", "技巧地牢的制作者。他人非常好，因为据说他曾帮助typedscroll修复bug。")
			.t("yell1", "对我的技巧地牢多点耐心，伙计xD。")
			.t("yell2", "我计划未来对技能系统和故事系统做一次巨大的更新。")
			.t("yell3", "Hatsune的牺牲会被人所铭记!");
	}



	public Bilboldev() {
		configure(Spec.BILBOLDEV);
		spriteClass = pd.sprites.BilboldevSprite.class;
	}
}
