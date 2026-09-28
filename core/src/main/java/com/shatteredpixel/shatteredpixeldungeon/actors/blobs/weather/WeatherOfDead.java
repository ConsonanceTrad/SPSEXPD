package com.shatteredpixel.shatteredpixeldungeon.actors.blobs.weather;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DeadRaise;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hot;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.DeadParticle;
import com.watabou.noosa.particles.Emitter;
public class WeatherOfDead extends SpsWeather {
	@Override protected void affectHero(){ Buff.prolong(Dungeon.hero, DeadRaise.class, 2f); Buff.detach(Dungeon.hero, Hot.class); }
	@Override protected Emitter.Factory particle(){ return DeadParticle.FACTORY; }
	@Override protected float interval(){ return 0.3f; }
}
