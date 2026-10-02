/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs.mindbuff;

import pd.actors.blobs.Web;
import pd.actors.blobs.weather.WeatherOfRain;
import pd.actors.blobs.weather.WeatherOfSand;
import pd.actors.blobs.weather.WeatherOfSnow;
import pd.actors.blobs.weather.WeatherOfSun;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cold;
import pd.actors.buffs.Dry;
import pd.actors.buffs.Hot;
import pd.actors.buffs.Wet;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Persistent positive mental state which blocks weather and web ailments. */
public class KeepMind extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(KeepMind.class)
			.t("name", "疯狂-坚定")
			.t("desc", "免疫蛛网和有害天气效果。");
	}



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
