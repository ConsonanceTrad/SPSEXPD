package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dry;
import pd.actors.buffs.Wet;
import pd.effects.particles.SandParticle;
import render.noosa.particles.Emitter;
import pd.messages.InlineText;
public class WeatherOfSand extends SpsWeather {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WeatherOfSand.class)
			.t("desc", "这里飞扬着沙尘，会使生物干燥并降低伤害。");
	}

	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Dry.class, Dry.DURATION); Buff.detach(Dungeon.hero, Wet.class); }
	@Override protected Emitter.Factory particle(){ return SandParticle.FACTORY; }
	@Override protected float interval(){ return 0.5f; }
}
