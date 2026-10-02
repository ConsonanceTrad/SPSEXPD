/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class OldNewStwist extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(OldNewStwist.class)
			.t("name", "Oldnewstwist")
			.t("desc", "一个豺狼流浪者，看来豺狼一族里面也是有纷争的。")
			.t("yell1", "啊!别伤害我，我和那群邪恶的豺狼人不一样。")
			.t("yell2", "我试图在我所在的部落宣扬和平，虽然有些居民支持我，但是掌权者却把我赶出了部落，还剃光了我的体毛。")
			.t("yell3", "嗯呐...我感觉有点冷，你能帮我找件衣服吗?")
			.t("desc_gnollmission", "既然他已经有衣服穿了，他已经可以做一些其他事了。")
			.t("yell4", "我听说你击败了豺狼王，你真的太厉害了!你有没有找到什么东西 可以穿上保暖的那种?")
			.t("yell5", "我对你非常感谢。你的强大，善良，智慧，令我钦佩。")
			.t("yell6", "哦，我这里卖各种特殊武器。它们来自于其他时间。");
	}

	public OldNewStwist() {
		configure(Spec.OLD_NEW_STWIST);
		spriteClass = pd.sprites.OldNewStwistSprite.class;
	}
}
