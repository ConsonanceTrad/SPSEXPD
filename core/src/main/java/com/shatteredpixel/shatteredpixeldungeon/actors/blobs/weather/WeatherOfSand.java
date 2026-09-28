package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dry;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Wet;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SandParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfSand extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, Dry.class, Dry.DURATION); Buff.detach(Dungeon.hero, Wet.class); }
	@Override protected Emitter.Factory particle(){ return SandParticle.FACTORY; }
	@Override protected float interval(){ return 0.5f; }
}
