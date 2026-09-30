/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;
import pd.Assets;
import pd.actors.mobs.SpsHermitCrab;
import pd.effects.MagicMissile;
import render.noosa.TextureFilm;
public class SpsHermitCrabSprite extends MobSprite {
	public SpsHermitCrabSprite() { texture(Assets.Sprites.SPS_HERMIT_CRAB); TextureFilm f = new TextureFilm(texture, 16, 16); idle = new Animation(2, true); idle.frames(f, 0, 0, 0, 1, 0, 0, 1, 1); run = new Animation(12, true); run.frames(f, 2, 3, 4, 5, 6); attack = new Animation(12, false); attack.frames(f, 6, 7, 8, 9); zap = attack.clone(); die = new Animation(12, false); die.frames(f, 10, 11, 12, 13); play(idle); }
	@Override public void zap(int cell) { super.zap(cell); MagicMissile.boltFromChar(parent, MagicMissile.SHAMAN_BLUE, this, cell, () -> ((SpsHermitCrab)ch).onZapComplete()); }
}
