/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class SpsUGooSprite extends MobSprite {
	protected int row;

	public SpsUGooSprite() {
		this(0);
	}

	protected SpsUGooSprite(int row) {
		texture(Assets.Sprites.SPS_UGOO);
		TextureFilm frames = new TextureFilm(texture, 20, 16);
		int first = row * 10;
		idle = new Animation(10, true); idle.frames(frames, first, first + 1, first + 1, first + 2, first + 1, first + 1);
		run = new Animation(10, true); run.frames(frames, first, first + 1, first + 2, first + 1);
		attack = new Animation(10, false); attack.frames(frames, first + 3, first + 4, first + 5);
		die = new Animation(10, false); die.frames(frames, first + 6, first + 7, first + 8, first + 9);
		play(idle);
	}

	@Override public int blood() { return 0xFF000000; }

	public static class Ice extends SpsUGooSprite { public Ice() { super(1); } }
	public static class Earth extends SpsUGooSprite { public Earth() { super(2); } }
	public static class Fire extends SpsUGooSprite { public Fire() { super(3); } }
	public static class Shock extends SpsUGooSprite { public Shock() { super(4); } }
}
