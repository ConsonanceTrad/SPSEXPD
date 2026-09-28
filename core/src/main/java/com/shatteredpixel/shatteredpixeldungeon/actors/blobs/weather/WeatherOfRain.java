package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dry;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wet;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfRain extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Wet.class, Wet.DURATION); Buff.detach(Dungeon.hero, Dry.class); }
	@Override protected Emitter.Factory particle(){ return RainParticle.FACTORY; }
	@Override protected float interval(){ return 0.8f; }
}
