/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs.mindbuff;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfRain;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSand;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSnow;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather.WeatherOfSun;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cold;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dry;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hot;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wet;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Persistent positive mental state which blocks weather and web ailments. */
public class KeepMind extends Buff {
	{
		type = buffType.POSITIVE;
		announced = true;
		immunities.add(Web.class);
		immunities.add(Hot.class);
		immunities.add(Cold.class);
		immunities.add(Wet.class);
		immunities.add(Dry.class);
		immunities.add(WeatherOfRain.class);
		immunities.add(WeatherOfSand.class);
		immunities.add(WeatherOfSnow.class);
		immunities.add(WeatherOfSun.class);
	}
	@Override public boolean act() { if (target != null && target.isAlive()) spend(TICK); return true; }
	@Override public int icon() { return BuffIndicator.MIND_VISION; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}
