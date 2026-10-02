package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cold;
import pd.actors.buffs.Hot;
import pd.effects.particles.ShaftParticle;
import render.noosa.particles.Emitter;
import pd.messages.InlineText;
public class WeatherOfSun extends SpsWeather {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WeatherOfSun.class)
			.t("desc", "这里阳光灼热，会使生物陷入炎热状态。");
	}



	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Hot.class, Hot.DURATION); Buff.detach(Dungeon.hero, Cold.class); }
	@Override protected Emitter.Factory particle(){ return ShaftParticle.FACTORY; }
	@Override protected float interval(){ return 0.9f; }
}
