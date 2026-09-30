package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Dry;
import pd.actors.buffs.Wet;
import pd.effects.particles.RainParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfRain extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Wet.class, Wet.DURATION); Buff.detach(Dungeon.hero, Dry.class); }
	@Override protected Emitter.Factory particle(){ return RainParticle.FACTORY; }
	@Override protected float interval(){ return 0.8f; }
}
