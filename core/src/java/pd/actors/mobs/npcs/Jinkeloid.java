/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Jinkeloid extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Jinkeloid.class)
			.t("name", "公会会长Jinkeloid")
			.t("desc", "这是mispd作者所创建的镜像。mispd作者同时也是破碎地牢的中文审核者。")
			.t("yell1", "现在工会不对外营业，当然欢迎你们前来参观。")
			.t("yell2", "你知道冰冻效果吗，它能控制住目标并使其受到更多的伤害。")
			.t("yell3", "听说你完成了所有挑战目标，那么这个你有资格前往这个地方。");
	}



	public Jinkeloid() {
		configure(Spec.JINKELOID);
		spriteClass = pd.sprites.JinkeloidSprite.class;
	}
}
