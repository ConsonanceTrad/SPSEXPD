/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class TowerSprite extends MobSprite {
	public TowerSprite() {
		texture(Assets.Sprites.SPS_TOWER);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(10, true); idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		run = idle.clone();
		attack = idle.clone();
		die = idle.clone();
		play(idle);
	}
}
