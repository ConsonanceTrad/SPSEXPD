/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.ParalyticGas;
import pd.actors.blobs.StenchGas;
import pd.actors.blobs.TarGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.weather.WeatherOfDead;
import pd.actors.blobs.weather.WeatherOfRain;
import pd.actors.blobs.weather.WeatherOfSand;
import pd.actors.blobs.weather.WeatherOfSnow;
import pd.actors.blobs.weather.WeatherOfSun;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class GasesImmunity extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GasesImmunity.class)
			.t("name", "气体免疫")
			.t("desc", "一种奇特的力量正在过滤你周边的空气。效果持续时，你免疫有害气体和天气。\n\n剩余时间：%s回合。");
	}



	public static final float DURATION = 20f;
	{
		type = buffType.POSITIVE;
		immunities.add(ParalyticGas.class);
		immunities.add(ToxicGas.class);
		immunities.add(ConfusionGas.class);
		immunities.add(StenchGas.class);
		immunities.add(DarkGas.class);
		immunities.add(TarGas.class);
		immunities.add(Locked.class);
		immunities.add(WeatherOfDead.class);
		immunities.add(WeatherOfRain.class);
		immunities.add(WeatherOfSun.class);
		immunities.add(WeatherOfSnow.class);
		immunities.add(WeatherOfSand.class);
	}
	@Override public int icon() { return BuffIndicator.IMMUNITY; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
