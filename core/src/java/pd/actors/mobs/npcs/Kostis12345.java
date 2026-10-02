/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Kostis12345 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Kostis12345.class)
			.t("name", "秘书kostis12345")
			.t("desc", "小镇的秘书，负责小镇宣传以及其他杂物。偶尔高层也会让她出去考察实习。")
			.t("yell1", "你看见过我们的镇长了吗?我们还有许多事情要做。")
			.t("yell2", "如果你对这一切有疑问的话，你可以在pixeldungeon.wikia.com寻找SpeciaSurprisePixelDungeon.还等什么，赶紧上船吧!");
	}



	public Kostis12345() {
		configure(Spec.KOSTIS12345);
		spriteClass = pd.sprites.Kostis12345Sprite.class;
	}
}
