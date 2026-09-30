package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cold;
import pd.actors.buffs.Hot;
import pd.effects.particles.SnowParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfSnow extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Cold.class, Cold.DURATION); Buff.detach(Dungeon.hero, Hot.class); }
	@Override protected Emitter.Factory particle(){ return SnowParticle.FACTORY; }
	@Override protected float interval(){ return 0.5f; }
}
