/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.InlineText;

/** The legacy SPS all-action speed boost. */
public class SpeedUp extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpeedUp.class)
			.t("name", "加速")
			.t("desc", "你的移动和行动速度提升了50%%。\n\n加速效果剩余时长：%s回合");
	}




	public static final float DURATION = 10f;
	public static final float SPEED_FACTOR = 1.5f;

	{
		type = buffType.POSITIVE;
	}
}
