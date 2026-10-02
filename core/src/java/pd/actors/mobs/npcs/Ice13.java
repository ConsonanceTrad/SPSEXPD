/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Ice13 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Ice13.class)
			.t("name", "佣兵-寒雾十三")
			.t("desc", "一个看上去有点神经质的术士，他依靠着一张损坏的多利亚小镇的地图和蜂蜜罐罐的指引来到了这里，他待在这里的目的是为了让这里更加——按他的说法——混乱。")
			.t("yell1", "你好，我叫寒雾十三，是一位普普通通的佣兵，如果我没记错的话，我们不是第一次见面，对吧？")
			.t("yell2", "我的配色很奇怪吗？呵，这只是三原色而已。")
			.t("yell3", "记住，拥抱过去，创造未来。")
			.t("yell4", "为这个世界的造物主——坚果欢呼吧，坚果是至高无上的神!")
			.t("yell5", "元素没有正邪之分，但它的使用者有。")
			.t("yell6", "这个世界依旧存在一些差错，但它至少不会搞出一只黑色的野兽。");
	}

	public Ice13() {
		configure(Spec.ICE13);
		spriteClass = pd.sprites.Ice13Sprite.class;
	}
}
