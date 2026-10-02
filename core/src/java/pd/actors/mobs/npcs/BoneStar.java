/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.messages.InlineText;

/** Original SPS runtime and save identity for this town resident. */
public class BoneStar extends TownNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BoneStar.class)
			.t("desc", "星辰地牢的开发者，不过因为开源包问题已经弃坑。这个家伙简直就是全副武装，然而他却没有打算探索地牢的冲动，真是可惜了这身装备。然而有趣的是，当人们询问他关于这个地牢的故事时，他们只得到了一个回复“我已经通关了”，然而没有任何人见过他除了站在那里还有什么其他的动作。除了对在他面前停留已久的冒险者一个白眼除外……不过值得一提的是，星辰对人还算不错。每当有新的冒险者来交谈的时候，他总会从背包里摸出两块石头给你，当然，两个石子的可能性也并不是没有……看起来从前的他是个热情的冒险者，然而现在的他只想和远道而来的冒险者们唠唠家常话，消磨一下时光，这就够了。")
			.t("name", "星辰")
			.t("yell1", "嘿，又来了一个新家伙！看起来你准备的非常充分，祝你一路顺风！");
	}



	public BoneStar() {
		configure(Spec.BONE_STAR);
		spriteClass = pd.sprites.BoneStarSprite.class;
	}
}
