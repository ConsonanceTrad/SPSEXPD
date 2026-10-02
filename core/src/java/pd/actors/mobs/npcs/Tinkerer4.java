/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Tinkerer4 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Tinkerer4.class)
			.t("name", "镇长Noodlemire")
			.t("desc", "小镇的镇长，著有chancel一书。因为经济萧条所以工作十分轻松。")
			.t("tell1", "哦，欢迎来到这个小镇。容我介绍一下，我是这个小镇的镇长。这里以前还是一个著名景点，但现在就完全不行了。")
			.t("tell2", "东边的那幢屋子是一个私人工会，东南方是鱼塘和墓地，西南方是矿洞遗址，西方是武器商店，西北方是杂货店，西方是酒馆，东北方是一间在建旅馆，正中心是教堂。如果你想问居民睡哪里的话，我只能说无可奉告。");
	}



	public Tinkerer4() {
		configure(Spec.MAYOR);
		spriteClass = pd.sprites.NoodlemireSprite.class;
	}
}
