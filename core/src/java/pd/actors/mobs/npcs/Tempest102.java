/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class Tempest102 extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Tempest102.class)
			.t("desc", "破碎地牢的翻译者之一，同时也是搞音乐的。")
			.t("name", "音乐家tempest102")
			.t("yell1", "原来我只搭了个帐篷，但现在我有了自己的商店。")
			.t("yell2", "欢迎光临我的乐器工作室。想买点什么吗?");
	}



	public Tempest102() {
		configure(Spec.TEMPEST102);
		spriteClass = pd.sprites.Tempest102Sprite.class;
	}
}
