package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfQuite extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Bless.class, 5f); }
	@Override protected Emitter.Factory particle(){ return ShaftParticle.FACTORY; }
	@Override protected float interval(){ return 0.8f; }
}
