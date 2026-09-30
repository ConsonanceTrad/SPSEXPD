/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.effects.Beam;
import pd.tiles.DungeonTilemap;
import render.noosa.TextureFilm;

public class OrbOfZotSprite extends MobSprite {

	private int attackPos;

	public OrbOfZotSprite() {
		texture(Assets.Sprites.SPS_ORB_OF_ZOT);
		TextureFilm frames = new TextureFilm(texture, 16, 18);
		idle = new Animation(2, true);
		idle.frames(frames, 1, 2, 3, 4);
		run = new Animation(12, true);
		run.frames(frames, 2, 3, 4);
		attack = new Animation(15, false);
		attack.frames(frames, 1, 5);
		die = new Animation(8, false);
		die.frames(frames, 1, 0, 6);
		play(idle);
	}

	@Override
	public void attack(int pos) {
		attackPos = pos;
		super.attack(pos);
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == attack && parent != null && ch != null && Dungeon.level != null
				&& attackPos >= 0 && attackPos < Dungeon.level.length()
				&& (Dungeon.level.heroFOV[ch.pos] || Dungeon.level.heroFOV[attackPos])) {
			parent.add(new Beam.DeathRay(center(), DungeonTilemap.raisedTileCenterToWorld(attackPos)));
		}
		super.onComplete(anim);
	}
}
