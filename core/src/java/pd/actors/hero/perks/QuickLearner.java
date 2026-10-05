/*
 * 快速学习 —— 获得额外经验。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;
import render.utils.serialize.Bundle;

public class QuickLearner extends Perk {

	static {
		InlineText.of(QuickLearner.class)
				.t("title", "快速学习")
				.t("desc", "获得的经验值提升 %d%%。");
	}

	//小数部分累计，避免被取整吃掉
	private float dexp = 0f;

	public QuickLearner() {
		super(3);
	}

	@Override
	public int image() {
		return PerkImageSheet.EXP_EXTRA;
	}

	public float ratio() {
		return level() * 0.1f + 0.05f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(ratio() * 100));
	}

	/** 返回本次应额外获得的经验 */
	public int extraExp(int exp) {
		float total = dexp + exp * ratio();
		int whole = (int) total;
		dexp = total - whole;
		return whole;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("dexp", dexp);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		dexp = bundle.getFloat("dexp");
	}

	public static int extraExp(pd.actors.hero.Hero hero, int exp) {
		if (hero == null || hero.heroPerk == null) return 0;
		QuickLearner q = hero.heroPerk.get(QuickLearner.class);
		return q == null ? 0 : q.extraExp(exp);
	}
}
