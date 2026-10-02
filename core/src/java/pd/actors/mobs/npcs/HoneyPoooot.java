/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class HoneyPoooot extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(HoneyPoooot.class)
			.t("name", "蜜蜂罐罐")
			.t("desc", "一个老练的冒险者，是工会主席的助手，负责帮忙处理各种工会任务，包括各类新道具的测试。")
			.t("yell1", "看见那边的那只黑猫了吗，它是我的头儿。")
			.t("yell2", "这件蜂蜜袍子很好看?拜托这是兜帽唉。");
	}

	public HoneyPoooot() {
		configure(Spec.HONEY_POOOOT);
		spriteClass = pd.sprites.HoneyPooootSprite.class;
	}
}
