/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import watabou.noosa.TextureFilm;
public class GentleCrabSprite extends MobSprite {
	public GentleCrabSprite() { texture(Assets.Sprites.SPS_GENTLE_CRAB); TextureFilm f = new TextureFilm(texture, 16, 16);
		idle = new Animation(5, true); idle.frames(f, 0, 1, 0, 2); run = new Animation(15, true); run.frames(f, 3, 4, 5, 6);
		attack = new Animation(12, false); attack.frames(f, 7, 8, 9); zap = attack.clone();
		die = new Animation(12, false); die.frames(f, 10, 11, 12, 13); play(idle); }
	@Override public int blood() { return 0xFFFFEA80; }
}
