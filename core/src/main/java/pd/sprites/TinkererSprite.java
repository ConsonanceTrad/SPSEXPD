package pd.sprites;

import pd.Assets;
import pd.actors.Char;
import pd.effects.particles.ElmoParticle;
import render.noosa.TextureFilm;

public class TinkererSprite extends MobSprite {

	public TinkererSprite() {
		texture(Assets.Sprites.TINKERER);
		TextureFilm frames = new TextureFilm(texture, 12, 14);

		idle = new Animation(10, true);
		idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3,
				3, 3, 3, 3, 3, 2, 1);
		run = new Animation(20, true);
		run.frames(frames, 0);
		die = new Animation(20, false);
		die.frames(frames, 0);
		play(idle);
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		add(State.SHIELDED);
	}

	@Override
	public void die() {
		super.die();
		processStateRemoval(State.SHIELDED);
		emitter().start(ElmoParticle.FACTORY, 0.03f, 60);
	}
}
