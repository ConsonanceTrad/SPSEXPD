/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

/** Second-row bee animation used by the Leader's steel bee. */
public class SteelBeeSprite extends MobSprite {

	public SteelBeeSprite() {
		texture(Assets.Sprites.BEE);
		TextureFilm frames = new TextureFilm(texture, 16, 16);
		idle = new Animation(12, true);
		idle.frames(frames, 16, 17, 17, 16, 18, 18);
		run = new Animation(15, true);
		run.frames(frames, 16, 17, 17, 16, 18, 18);
		attack = new Animation(20, false);
		attack.frames(frames, 19, 20, 21, 22);
		die = new Animation(20, false);
		die.frames(frames, 23, 24, 25, 26);
		play(idle);
	}

	@Override public int blood() { return 0xffd500; }
}
