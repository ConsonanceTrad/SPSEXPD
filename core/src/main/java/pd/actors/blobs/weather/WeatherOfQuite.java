package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.effects.particles.ShaftParticle;
import watabou.noosa.particles.Emitter;
public class WeatherOfQuite extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Bless.class, 5f); }
	@Override protected Emitter.Factory particle(){ return ShaftParticle.FACTORY; }
	@Override protected float interval(){ return 0.8f; }
}
