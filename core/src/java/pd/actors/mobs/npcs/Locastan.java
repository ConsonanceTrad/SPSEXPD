/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Locastan extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Locastan.class)
			.t("name", "Locastan")
			.t("desc", "哥布林地牢的制作者。他相当喜欢哥布林这部漫画。神灯是释放地牢的标志。")
			.t("yell1", "这个门被锁住了...这个房间肯定不止一个门...没准我可以打开它...")
			.t("yell2", "嘿，这可不是什么废铁，这是一件非常有用的工具。")
			.t("yell3", "只要我能释放这个神灯的力量，我的世界就会更加精彩纷呈。");
	}



	public Locastan() {
		configure(Spec.LOCASTAN);
		spriteClass = pd.sprites.LocastanSprite.class;
	}
}
