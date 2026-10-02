/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.Electricity;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.StenchGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.mobs.BrokenRobot;
import pd.actors.mobs.Eye;
import pd.actors.mobs.Warlock;
import pd.actors.mobs.YogFist;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class MagicImmunity extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MagicImmunity.class)
			.t("name", "奥术护盾")
			.t("desc", "奥术护盾可以为目标提供一定抗性。\n\n剩余的护盾效果时长：%s回合");
	}




	public static final float DURATION = 10f;

	{
		type = buffType.POSITIVE;
		announced = true;
		immunities.add(ParalyticGas.class);
		immunities.add(ToxicGas.class);
		immunities.add(ConfusionGas.class);
		immunities.add(StenchGas.class);
		immunities.add(Burning.class);
		immunities.add(Poison.class);
		immunities.add(Electricity.class);
		immunities.add(Warlock.class);
		immunities.add(Eye.class);
		immunities.add(YogFist.BurningFist.class);
		immunities.add(BrokenRobot.class);
		immunities.add(CorruptGas.class);
	}

	@Override
	public int icon() {
		return BuffIndicator.IMMUNITY;
	}
}
