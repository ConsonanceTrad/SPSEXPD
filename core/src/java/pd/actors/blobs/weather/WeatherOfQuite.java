package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.effects.particles.ShaftParticle;
import render.noosa.particles.Emitter;
import pd.messages.InlineText;
public class WeatherOfQuite extends SpsWeather {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WeatherOfQuite.class)
			.t("desc", "这里异常安静，让人更容易集中精神。");
	}

	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Bless.class, 5f); }
	@Override protected Emitter.Factory particle(){ return ShaftParticle.FACTORY; }
	@Override protected float interval(){ return 0.8f; }
}
