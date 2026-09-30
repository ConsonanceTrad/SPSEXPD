/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import render.noosa.TextureFilm;
public class CrabKingSprite extends MobSprite {
	public CrabKingSprite() {
		texture(Assets.Sprites.SPS_CRAB_KING); TextureFilm f = new TextureFilm(texture, 16, 16);
		idle = new Animation(2, true); idle.frames(f, 0, 1, 2, 3, 10, 11, 12);
		run = new Animation(15, false); run.frames(f, 4, 5, 6, 10, 11, 12);
		attack = new Animation(15, false); attack.frames(f, 7, 8, 9);
		zap = attack.clone(); die = new Animation(8, false); die.frames(f, 8, 9, 10, 10, 10, 10, 10, 10); play(idle);
	}
}
