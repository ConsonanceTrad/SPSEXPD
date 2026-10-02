/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class NYRDS extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(NYRDS.class)
			.t("name", "Nyrdie")
			.t("desc", "混合地牢的制作者???一个带墨镜的白牙肌肉块。")
			.t("yell1", "嘿，我是nyrdie")
			.t("yell2", "美牙建议:不要忘了每天早上刷牙哦!");
	}

	public NYRDS() {
		configure(Spec.NYRDS);
		spriteClass = pd.sprites.NYRDSSprite.class;
	}
}
