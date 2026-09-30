package pd.actors.blobs.weather;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cold;
import pd.actors.buffs.Hot;
import pd.effects.particles.ShaftParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfSun extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Hot.class, Hot.DURATION); Buff.detach(Dungeon.hero, Cold.class); }
	@Override protected Emitter.Factory particle(){ return ShaftParticle.FACTORY; }
	@Override protected float interval(){ return 0.9f; }
}
