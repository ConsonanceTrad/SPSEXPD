/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class StormAndRain extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(StormAndRain.class)
			.t("desc", "这是一个对发芽改有重大贡献的玩家。")
			.t("name", "雷雨交加")
			.t("yell1", "嗨，需要买些密宝吗。这些可是高级货。")
			.t("yell2", "严格意义上我并不属于人类...但没人规定只有人类才能当财宝猎人。");
	}

	public StormAndRain() {
		configure(Spec.STORM_AND_RAIN);
		spriteClass = pd.sprites.StormAndRainSprite.class;
	}
}
