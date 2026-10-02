package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dry;
import pd.actors.buffs.Wet;
import pd.effects.particles.RainParticle;
import render.noosa.particles.Emitter;
import pd.messages.InlineText;
public class WeatherOfRain extends SpsWeather {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WeatherOfRain.class)
			.t("desc", "这里下着小雨，会降低生物的命中和闪避。");
	}

	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Wet.class, Wet.DURATION); Buff.detach(Dungeon.hero, Dry.class); }
	@Override protected Emitter.Factory particle(){ return RainParticle.FACTORY; }
	@Override protected float interval(){ return 0.8f; }
}
