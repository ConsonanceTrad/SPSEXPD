/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import com.watabou.noosa.TextureFilm;

/** Original SPS Goo animation sheet, kept separate from Shattered's Goo. */
public class SpsGooSprite extends MobSprite {
	private final Animation pump;
	private final Animation pumpAttack;

	public SpsGooSprite() {
		texture(Assets.Sprites.SPS_GOO);
		TextureFilm frames = new TextureFilm(texture, 20, 14);
		idle = new Animation(10, true); idle.frames(frames, 2, 1, 0, 0, 1);
		run = new Animation(15, true); run.frames(frames, 3, 2, 1, 2);
		pump = new Animation(20, true); pump.frames(frames, 4, 3, 2, 1, 0);
		pumpAttack = new Animation(20, false); pumpAttack.frames(frames, 4, 3, 2, 1, 0, 7);
		attack = new Animation(10, false); attack.frames(frames, 8, 9, 10);
		die = new Animation(10, false); die.frames(frames, 5, 6, 7);
		play(idle);
	}

	public void pumpUp() { play(pump); }
	public void pumpAttack() { play(pumpAttack); }

	@Override public int blood() { return 0xFF000000; }

	@Override
	public void onComplete(Animation anim) {
		super.onComplete(anim);
		if (anim == pumpAttack) {
			idle();
			ch.onAttackComplete();
		}
	}
}
