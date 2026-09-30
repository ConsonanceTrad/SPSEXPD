package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DeadRaise;
import pd.actors.buffs.Hot;
import pd.effects.particles.DeadParticle;
import render.noosa.particles.Emitter;
public class WeatherOfDead extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, DeadRaise.class, 2f); Buff.detach(Dungeon.hero, Hot.class); }
	@Override protected Emitter.Factory particle(){ return DeadParticle.FACTORY; }
	@Override protected float interval(){ return 0.3f; }
}
