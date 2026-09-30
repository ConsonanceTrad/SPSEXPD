/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.actors.Char;
import pd.actors.mobs.npcs.GiftNpc;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.PixelParticle;

/** Uses the original standalone animation sheet selected by each gift resident. */
public class GiftNpcSprite extends MobSprite {

	private GiftNpc.Visual visual;
	private PixelParticle coin;

	@Override
	public void link(Char ch) {
		visual = ((GiftNpc)ch).visual();
		texture(visual.asset);
		TextureFilm frames = new TextureFilm(texture, visual.frameWidth, visual.frameHeight);
		idle = animation(frames, visual.idleFps, true, visual.idleFrames);
		run = animation(frames, visual.runFps, true, visual.runFrames);
		attack = animation(frames, visual.attackFps, false, visual.attackFrames);
		zap = attack.clone();
		die = animation(frames, visual.dieFps, false, visual.dieFrames);
		if (visual == GiftNpc.Visual.MEAT_SELLER) {
			run = idle.clone();
			attack = idle.clone();
			zap = attack.clone();
			die = idle.clone();
		}
		play(idle);
		super.link(ch);
		if (visual == GiftNpc.Visual.TORCH) add(State.BURNING);
	}

	private static Animation animation(TextureFilm film, int fps, boolean looped, int[] frameIds) {
		return new Animation(fps, looped).frames(film, frameIds);
	}

	@Override
	public void onComplete(Animation anim) {
		super.onComplete(anim);
		if (visual == GiftNpc.Visual.MEAT_SELLER && visible && anim == idle) {
			if (coin == null) {
				coin = new PixelParticle();
				parent.add(coin);
			}
			coin.reset(x + (flipHorizontal ? 0 : 13), y + 7, 0xFFFF00, 1, 0.5f);
			coin.speed.y = -40;
			coin.acc.y = 160;
		}
	}

	@Override
	public void die() {
		super.die();
		if (visual == GiftNpc.Visual.TORCH) remove(State.BURNING);
	}

	@Override
	public int blood() {
		return visual == GiftNpc.Visual.TORCH ? 0xFFFF7D13 : super.blood();
	}
}
