/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class AshWolf extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(AshWolf.class)
			.t("name", "阿萨男爵")
			.t("desc", "地牢游戏和像素画爱好者，RM制作者，兽控，正在制作异域冒险传。")
			.t("yell1", "看，我给你种个宝贝。")
			.t("yell2", "16*16的范围里也可以做出很萌的家伙，另外，为什么豺狼人不能成为伙伴呢？！")
			.t("yell3", "万圣节强制彩蛋，南瓜灯换糖!!!")
			.t("yell4", "看看新的房屋，转换一下心态。");
	}



	public AshWolf() {
		configure(Spec.ASH_WOLF);
		spriteClass = pd.sprites.AshWolfSprite.class;
	}
}
