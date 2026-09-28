package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cold;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hot;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfSnow extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Cold.class, Cold.DURATION); Buff.detach(Dungeon.hero, Hot.class); }
	@Override protected Emitter.Factory particle(){ return SnowParticle.FACTORY; }
	@Override protected float interval(){ return 0.5f; }
}
