/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class RainTrainer extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RainTrainer.class)
			.t("desc", "这是一个早期的计划npc，但是他并没有加入到游戏中。现在，他准备好了。")
			.t("name", "训练家Rain")
			.t("yell1", "训练家只要眼神对上了就要战...抱歉说错了。")
			.t("yell2", "你可以用那个稻草人练手,不用担心,它很结实。");
	}



	public RainTrainer() {
		configure(Spec.RAIN_TRAINER);
		spriteClass = pd.sprites.RainSprite.class;
	}
}
