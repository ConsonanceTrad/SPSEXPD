package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cold;
import pd.actors.buffs.Hot;
import pd.effects.particles.SnowParticle;
import render.noosa.particles.Emitter;
import pd.messages.InlineText;
public class WeatherOfSnow extends SpsWeather {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WeatherOfSnow.class)
			.t("desc", "这里降着小雪，会使生物寒冷并减缓移动。");
	}

	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Cold.class, Cold.DURATION); Buff.detach(Dungeon.hero, Hot.class); }
	@Override protected Emitter.Factory particle(){ return SnowParticle.FACTORY; }
	@Override protected float interval(){ return 0.5f; }
}
