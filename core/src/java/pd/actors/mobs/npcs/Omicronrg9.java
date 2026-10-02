/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Omicronrg9 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Omicronrg9.class)
			.t("name", "Ømicrónrg9")
			.t("desc", "这个看上去像十字军战士的长发男子，是一个研究平行世界的学者。他正在收集这个世界的信息的时候受到了来自高塔的邀请，在蜜蜂罐罐的引导下来的这个世界，并和这个世界的其他人分享关于其他世界的信息。由于看上去有些失眠，所以他可能有攻击性。")
			.t("yell1", "别挡道，伙计!我可没时间弄些杂七杂八的事情，mod表可是不会自己生成的。")
			.t("yell2", "食人鱼这种这种生物是在是太棒了，它们能轻易撕碎各种动物，特别是没有外壳的。")
			.t("yell3", "你知道吗，在所有像素地牢的衍生mod中，超过24种是以p字母打头的。")
			.t("yell4", "为了更好地寻找和收集mod，我把我的一只眼睛换成了钛合金电子眼。这很酷，也很值得。")
			.t("yell5", "希望这东西...无论在哪，依然是个幻象。")
			.t("yell6", "这个世界包容了超过9个不同世界的不同产物，真出乎我的意料!");
	}



	public Omicronrg9() {
		configure(Spec.OMICRONRG9);
		spriteClass = pd.sprites.Omicronrg9Sprite.class;
	}
}
