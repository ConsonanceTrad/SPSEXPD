/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class ATV9 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ATV9.class)
			.t("name", "AekaTsrnVeskyinja999")
			.t("desc", "这个是9，也可以称之为9[带圈的那个9] 参与绘制明日方舟地牢。彻头彻尾的酒鬼。")
			.t("yell1", "喵喵喵")
			.t("yell2", "如果你觉得这个绘制在哪里看过请想办法与我联系")
			.t("yell3", "蜂蜜罐罐是我妈！");
	}



	public ATV9() {
		configure(Spec.ATV9);
		spriteClass = pd.sprites.ATV9Sprite.class;
	}
}
