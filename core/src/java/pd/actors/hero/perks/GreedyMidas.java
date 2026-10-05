/*
 * 迈达斯之手 —— 拾取金币时概率获得额外收益。
 * 依赖特色物品 GoldenClaw（见 M5 引入）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class GreedyMidas extends Perk {

	static {
		InlineText.of(GreedyMidas.class)
				.t("title", "迈达斯之手")
				.t("desc", "拾取金币时有 %d%% 的几率获得三倍多的金币。");
	}

	public GreedyMidas() {
		super(1);
		addTags(Tag.Viability);
	}

	@Override
	public int image() {
		return PerkImageSheet.GREEDY_MIDAS;
	}

	public float chance() {
		return 0.33f;
	}

	public float multiplier() {
		return 3.33f;
	}

	@Override
	public String description() {
		return pd.messages.Messages.get(this, "desc", Math.round(chance() * 100));
	}
}
