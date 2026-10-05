/*
 * 定向传送 —— 传送卷轴可以自选目的地。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class IntendedTransportation extends Perk {

	static {
		InlineText.of(IntendedTransportation.class)
				.t("title", "定向传送")
				.t("desc", "使用传送卷轴时，你可以自行选择传送的目的地。");
	}

	public IntendedTransportation() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.TRANSPORTATION;
	}

	public boolean allowTargetChoice() {
		return true;
	}
}
