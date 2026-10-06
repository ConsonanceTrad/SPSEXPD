/*
 * 深渊巨口 —— 消耗吞星花啃咬附近的敌人；以此击杀生物会永久提高生命上限（每种生物只生效一次）。
 */

package pd.actors.hero.perks;

import java.util.HashSet;
import java.util.Set;

import pd.messages.InlineText;
import render.utils.serialize.Bundle;

public class AbyssalMaw extends Perk {

	/** 已铭记（已加过生命上限）的生物种类，存类名 */
	private final Set<String> devoured = new HashSet<>();

	static {
		InlineText.of(AbyssalMaw.class)
			.t("title", "深渊巨口")
			.t("desc", "你可以消耗吞星花，啃咬附近的一个敌人，造成相当于当前攻击力 40%% 的纯粹伤害。"
					+ "以此击杀生物会永久提高 5 点生命上限，每种生物只生效一次。\n\n已铭记的生物：%d 种");
	}

	public AbyssalMaw() {
		super(1);
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", devoured.size());
	}

	/** 记录一种生物；返回 true 表示首次（应当加生命上限） */
	public boolean markDevoured(String key) {
		return key != null && devoured.add(key);
	}

	public int devouredCount() {
		return devoured.size();
	}

	public boolean hasDevoured(String key) {
		return devoured.contains(key);
	}

	private static final String DEVOURED = "devoured";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DEVOURED, devoured.toArray(new String[0]));
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		devoured.clear();
		String[] arr = bundle.getStringArray(DEVOURED);
		if (arr != null) {
			for (String s : arr) {
				if (s != null) devoured.add(s);
			}
		}
	}
}