/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.sprites.BurningFistSprite;
import pd.sprites.InfectingFistSprite;
import pd.sprites.PinningFistSprite;
import pd.sprites.RottingFistSprite;
import pd.messages.InlineText;

/** The active SPS-PD 0.9.8 final boss identity. */
public class Yog extends SpsYog {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Yog.class)
			.t("name", "Yog-Dzewa")
			.t("desc", "Yog-Dzewa是一位上古之神，来自混沌的强大存在。一个世纪前，古代矮人在与恶魔军队的战争中惨胜，却无法将古神杀死。于是他们把它封印在城市下的大堂里，认为过于虚弱的它永远都不会复苏。")
			.t("notice", "希望的存在只是一种幻觉…")
			.t("die", "我...永生...")
			.t("blink", "Yog消失了！")
			.t("burningfist.name", "火焰之拳")
			.t("burningfist.desc", "火焰之拳")
			.t("infectingfist.name", "酸蚀之拳")
			.t("infectingfist.desc", "酸蚀之拳")
			.t("larva.name", "古神幼虫")
			.t("larva.desc", "古神幼虫")
			.t("pinningfist.name", "剧毒之拳")
			.t("pinningfist.desc", "剧毒之拳")
			.t("rottingfist.name", "大地之拳")
			.t("rottingfist.desc", "大地之拳");
	}


	@Override
	protected Fist[] createFists() {
		return new Fist[]{new RottingFist(), new BurningFist(), new PinningFist(), new InfectingFist()};
	}

	@Override
	protected SpsYog.Larva createLarva() {
		return new Larva();
	}

	public static class RottingFist extends SpsYog.RottingFist {
		{ spriteClass = RottingFistSprite.class; }
	}
	public static class BurningFist extends SpsYog.BurningFist {
		{ spriteClass = BurningFistSprite.class; }
	}
	public static class InfectingFist extends SpsYog.InfectingFist {
		{ spriteClass = InfectingFistSprite.class; }
	}
	public static class PinningFist extends SpsYog.PinningFist {
		{ spriteClass = PinningFistSprite.class; }
	}
	public static class Larva extends SpsYog.Larva { }
}
