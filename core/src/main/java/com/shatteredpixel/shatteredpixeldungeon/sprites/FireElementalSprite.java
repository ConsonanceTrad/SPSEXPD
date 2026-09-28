/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.noosa.TextureFilm;

/** Original SPS-PD fire-elemental sprite identity and atlas layout. */
public class FireElementalSprite extends MobSprite {
	public FireElementalSprite() {
		texture(Assets.Sprites.SPS_SEWER_ELEMENTAL);
		TextureFilm frames = new TextureFilm(texture, 12, 14);
		idle = new Animation(10, true); idle.frames(frames, 0, 1, 2);
		run = new Animation(12, true); run.frames(frames, 0, 1, 3);
		attack = new Animation(15, false); attack.frames(frames, 4, 5, 6);
		zap = attack.clone();
		die = new Animation(15, false); die.frames(frames, 7, 8, 9, 10, 11, 12, 13, 12);
		play(idle);
	}
	@Override public void link(Char ch) { super.link(ch); add(State.BURNING); }
	@Override public void die() { super.die(); remove(State.BURNING); }
	@Override public int blood() { return 0xFFFF7D13; }
}
