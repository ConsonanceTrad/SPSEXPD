package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DeadRaise;
import pd.actors.buffs.Hot;
import pd.effects.particles.DeadParticle;
import render.noosa.particles.Emitter;
import pd.messages.InlineText;
public class WeatherOfDead extends SpsWeather {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WeatherOfDead.class)
			.t("desc", "这里怨念很重，小心亡灵出现！");
	}

	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, DeadRaise.class, 2f); Buff.detach(Dungeon.hero, Hot.class); }
	@Override protected Emitter.Factory particle(){ return DeadParticle.FACTORY; }
	@Override protected float interval(){ return 0.3f; }
}
