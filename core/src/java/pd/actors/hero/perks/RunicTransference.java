/*
 * 刻印转移 —— 战士的破碎纹章可以携带刻印（特定等级必然获得，不随机）。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class RunicTransference extends Perk {

	static {
		InlineText.of(RunicTransference.class)
				.t("title", "刻印转移")
				.t("desc", "战士的破碎纹章可以像携带一层升级一样携带常见刻印；等级更高时可携带常见、强力或诅咒刻印。");
	}

	public RunicTransference() {
		super(2);
	}

	@Override
	public int image() {
		return PerkImageSheet.RUNIC_TRANSFERENCE;
	}

	/** 是否允许携带诅咒刻印 */
	public boolean allowCursed() {
		return level() >= 2;
	}
}
