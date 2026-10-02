/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class OtilukeNPC extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(OtilukeNPC.class)
			.t("desc", "Otiluke，一位极具天赋的巫师。 他做了许多事情。其中之一就是带走了Amulet护符。")
			.t("name", "Otiluke")
			.t("yell1", "我们终于相见了，同时我重新看到了热闹的家乡，就和我小时候一样。")
			.t("yell2", "我十分感谢你的所作所为。我会把你引荐到高塔的。")
			.t("yell3", "好吧，我知道你要什么。Amulet护符现在被封印在高塔里面，没法拿出来了。但是我可以给你一个仿制品，它和Amulet护符功能一样。");
	}



	public OtilukeNPC() {
		configure(Spec.OTILUKE_NPC);
		spriteClass = pd.sprites.OtilukeNPCSprite.class;
	}
}
