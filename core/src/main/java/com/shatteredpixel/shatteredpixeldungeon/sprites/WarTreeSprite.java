/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

/** Original SPS-PD 0.9.8 town war-tree sprite. */
public class WarTreeSprite extends MobSprite {
	public WarTreeSprite() {
		texture(Assets.Sprites.SPS_WAR_TREE);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(12, true); idle.frames(frames, 1, 1, 1, 5, 5, 5);
		run = new Animation(10, true); run.frames(frames, 0, 1, 2, 2, 1, 0);
		attack = new Animation(15, false); attack.frames(frames, 1, 3, 4, 5);
		die = new Animation(10, false); die.frames(frames, 1, 6, 7, 8);
		play(idle);
	}
}
