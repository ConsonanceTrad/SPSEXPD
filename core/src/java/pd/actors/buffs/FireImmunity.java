/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/**
 * SPSEXPD: 辣椒的火焰免疫——持续期间免疫火焰伤害（Burning 无法造成伤害）。
 */
public class FireImmunity extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FireImmunity.class)
			.t("name", "火焰免疫")
			.t("desc", "你的身体暂时适应了高温，火焰无法伤到你。\n\n剩余回合：%s");
	}

	public static final float DURATION = 2f;

	{
		type = buffType.POSITIVE;
		announced = true;
		immunities.add(Burning.class);
	}

	@Override
	public int icon() {
		return BuffIndicator.IMMUNITY;
	}
}
