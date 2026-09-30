/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import pd.actors.mobs.Shell;
import pd.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
public class ShellSprite extends MobSprite {
	public ShellSprite() { texture(Assets.Sprites.SPS_LIGHTNING_SHELL); TextureFilm f = new TextureFilm(texture, 16, 16); idle = new Animation(10, true); idle.frames(f, 0); run = idle.clone(); die = idle.clone(); attack = idle.clone(); zap = attack.clone(); play(idle); }
	@Override public void zap(int cell) { super.zap(cell); MagicMissile.boltFromChar(parent, MagicMissile.SHAMAN_BLUE, this, cell, () -> ((Shell)ch).onZapComplete()); }
}
