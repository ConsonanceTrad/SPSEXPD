/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.effects.Beam;
import pd.tiles.DungeonTilemap;
import com.watabou.noosa.TextureFilm;

public class SpsNormalCellSprite extends MobSprite {
	private int attackPos;

	public SpsNormalCellSprite() {
		texture(Assets.Sprites.SPS_NORMAL_CELL);
		TextureFilm frames = new TextureFilm(texture, 16, 18);
		idle = new Animation(2, true); idle.frames(frames, 1, 2, 3, 4);
		run = new Animation(12, true); run.frames(frames, 2, 3, 4);
		attack = new Animation(15, false); attack.frames(frames, 1, 5);
		die = new Animation(8, false); die.frames(frames, 1, 0, 6);
		play(idle);
	}

	@Override public void attack(int pos) {
		attackPos = pos;
		super.attack(pos);
	}

	@Override public void onComplete(Animation anim) {
		super.onComplete(anim);
		if (anim == attack && ch != null && parent != null && Dungeon.level != null
				&& (Dungeon.level.heroFOV[ch.pos] || Dungeon.level.heroFOV[attackPos])) {
			parent.add(new Beam.DeathRay(center(), DungeonTilemap.tileCenterToWorld(attackPos)));
		}
	}
}
