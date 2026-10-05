/*
 * 蛮力 —— 力量溢出转换为近战伤害。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;
import render.utils.math.Random;

public class ExtraStrengthPower extends Perk {

	static {
		InlineText.of(ExtraStrengthPower.class)
				.t("title", "蛮力")
				.t("desc", "挥舞近战武器时更有效地从额外力量中获得加成。");
	}

	public ExtraStrengthPower() {
		super(3);
		addTags(Tag.Melee);
	}

	@Override
	public int image() {
		return PerkImageSheet.STRENGTH_POWER;
	}

	/** 每级追加一次 1..exStr 的随机伤害（照暗黑） */
	public int extraDamage(int exStr) {
		int extra = 0;
		for (int i = 0; i < level(); i++) {
			extra += Random.Int(1, Math.max(2, exStr));
		}
		return extra;
	}

	public static int extraDamageOf(pd.actors.hero.Hero hero, int exStr) {
		if (hero == null || hero.heroPerk == null) return 0;
		ExtraStrengthPower p = hero.heroPerk.get(ExtraStrengthPower.class);
		return p == null ? 0 : p.extraDamage(exStr);
	}
}
