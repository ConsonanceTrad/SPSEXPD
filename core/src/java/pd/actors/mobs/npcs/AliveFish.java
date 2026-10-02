/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class AliveFish extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(AliveFish.class)
			.t("desc", "这是一个“淹死”的玩家。")
			.t("name", "淹死的食人鱼")
			.t("yell1", "（水泡声）（水泡声）（水泡声）")
			.t("yell2", "涌流法杖...水泡声...水泡声...");
	}

	public AliveFish() {
		configure(Spec.ALIVE_FISH);
		spriteClass = pd.sprites.PiranhaSprite.class;
	}
}
