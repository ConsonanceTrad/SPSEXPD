package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cold;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hot;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfSun extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Hot.class, Hot.DURATION); Buff.detach(Dungeon.hero, Cold.class); }
	@Override protected Emitter.Factory particle(){ return ShaftParticle.FACTORY; }
	@Override protected float interval(){ return 0.9f; }
}
