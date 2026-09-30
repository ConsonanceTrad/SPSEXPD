package pd.actors.blobs.weather;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.effects.BlobEmitter;
import pd.messages.Messages;
import render.noosa.particles.Emitter;

abstract class SpsWeather extends Blob {
	@Override
	protected void evolve() {
		for (int cell = 0; cell < cur.length; cell++) {
			off[cell] = cur[cell];
			volume += off[cell];
		}
		if (Dungeon.hero != null && Dungeon.hero.isAlive() && Dungeon.hero.pos >= 0
				&& Dungeon.hero.pos < cur.length && cur[Dungeon.hero.pos] > 0) affectHero();
	}
	protected abstract void affectHero();
	protected abstract Emitter.Factory particle();
	protected abstract float interval();
	@Override public void use(BlobEmitter emitter) { super.use(emitter); emitter.start(particle(), interval(), 0); }
	@Override public String tileDesc() { return Messages.get(this, "desc"); }
}
