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

public class GasesImmunity extends FlavourBuff {
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
