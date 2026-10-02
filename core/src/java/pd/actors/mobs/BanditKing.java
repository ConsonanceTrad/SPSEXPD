/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.sprites.BanditKingSprite;
import pd.messages.InlineText;

/** Original SPS-PD runtime and save identity for the life bandit. */
public class BanditKing extends SpsPrisonMobs.BanditKing {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BanditKing.class)
			.t("name", "蓝衣神偷")
			.t("desc", "传言，盗贼中有一群另类，他们对金银珠宝没有兴趣，倒是对生物的生命情有独钟。并且，他们可以在神不知鬼不觉的情况下偷走敌人的生命。他们也接受各种委托，但代价一般都是...生命。")
			.t("die", "算了，这次先放过你。")
			.t("dis", "蓝衣神偷消失了。")
			.t("spork", "啊！我偷到的叉勺呢！");
	}




	{
		spriteClass = BanditKingSprite.class;
		properties.add(Property.ELF);
		if (SpsPrisonMobs.BanditKing.grantsSpork()) Dungeon.sporkAvailable = false;
	}
}
