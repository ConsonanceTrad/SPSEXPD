/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Tinkerer5 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Tinkerer5.class)
			.t("name", "地质勘探员Xavier251998")
			.t("desc", "一名勘探小镇地质的家伙，他人很好。")
			.t("tell1", "我正在做一个课题，研究地壳变动和人为开矿的关系。可惜的是矿洞遗址里面有条龙再那，我没法进入矿洞查看。")
			.t("tell2", "据说小镇曾经有过辉煌的时候，大量的魔法矿石出口并给小镇带来可观的收入。但这一切已经结束了。")
			.t("tell3", "你该不会饿了吧，拿上这块肉，伙计。");
	}



	public Tinkerer5() {
		configure(Spec.GEOLOGIST);
		spriteClass = pd.sprites.Xavier251998Sprite.class;
	}
}
