/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import watabou.noosa.TextureFilm;
public class FrogPetSprite extends MobSprite {
	public FrogPetSprite() { texture(Assets.Sprites.SPS_FROG_PET); TextureFilm f = new TextureFilm(texture, 19, 14);
		idle = new Animation(5, true); idle.frames(f, 0, 1, 2, 3, 0, 1, 2, 3, 0, 11, 2, 3);
		run = new Animation(10, true); run.frames(f, 0, 4, 5, 6, 7, 0); attack = new Animation(15, false); attack.frames(f, 0, 8, 9);
		zap = attack.clone(); die = new Animation(10, false); die.frames(f, 0, 10, 12, 13, 14); play(idle); }
}
