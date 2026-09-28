/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.DarkGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.TarGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfDead;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfRain;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSand;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSnow;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSun;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

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
