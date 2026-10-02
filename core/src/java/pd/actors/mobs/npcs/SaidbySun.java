/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class SaidbySun extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SaidbySun.class)
			.t("name", "阳说")
			.t("yell1", "我想下一步应该这么做...等等，我搞错了...")
			.t("yell2", "他们叫我帮忙测试，我就来这儿了...哦我什么都没说...")
			.t("desc", "一只猫，用不安的眼神盯着旁边的炼金设备。没准那里有它讨厌的食物。");
	}



	public SaidbySun() {
		configure(Spec.SAID_BY_SUN);
		spriteClass = pd.sprites.SaidbySunSprite.class;
	}
}
