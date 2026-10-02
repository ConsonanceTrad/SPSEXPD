/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Ravenwolf extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Ravenwolf.class)
			.t("name", "ravenwolf")
			.t("yell1", "......")
			.t("yell2", "......")
			.t("desc", "无名地牢的作者");
	}



	public Ravenwolf() {
		configure(Spec.RAVENWOLF);
		spriteClass = pd.sprites.RavenwolfSprite.class;
	}
}
