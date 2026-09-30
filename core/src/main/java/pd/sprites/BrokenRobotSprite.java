/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.sprites;

import pd.Assets;
import pd.Dungeon;
import pd.effects.Beam;
import pd.tiles.DungeonTilemap;
import render.noosa.TextureFilm;

public class BrokenRobotSprite extends MobSprite {
	private int attackPos;

	public BrokenRobotSprite() {
		texture(Assets.Sprites.SPS_BROKEN_ROBOT);
		TextureFilm frames = new TextureFilm(texture, 16, 18);
		idle = new Animation(2, true); idle.frames(frames, 0, 1, 0, 1);
		run = new Animation(12, true); run.frames(frames, 2, 3, 4, 5, 6, 7);
		attack = new Animation(15, false); attack.frames(frames, 8, 9);
		die = new Animation(8, false); die.frames(frames, 10, 11, 12, 13);
		play(idle);
	}

	@Override
	public void attack(int pos) {
		attackPos = pos;
		super.attack(pos);
	}

	@Override
	public void onComplete(Animation anim) {
		if (anim == attack && ch != null && parent != null && Dungeon.level != null
				&& Dungeon.level.heroFOV != null && Dungeon.level.insideMap(attackPos)
				&& (Dungeon.level.heroFOV[ch.pos] || Dungeon.level.heroFOV[attackPos])) {
			parent.add(new Beam.LightRay(center(), DungeonTilemap.raisedTileCenterToWorld(attackPos)));
		}
		super.onComplete(anim);
	}
}
