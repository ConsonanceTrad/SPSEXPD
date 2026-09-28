/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.noosa.TextureFilm;

/** Original animated cat summoned by the Wand of Smart Meow. */
public class CatSheepSprite extends MobSprite {

	public CatSheepSprite() {
		texture("sprites/npcs/sps_town_catsheep.png");
		TextureFilm frames = new TextureFilm(texture, 16, 16);

		idle = new Animation(15, true);
		idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 3);
		run = new Animation(20, true);
		run.frames(frames, 0);
		attack = new Animation(12, false);
		attack.frames(frames, 0, 2, 3);
		die = new Animation(20, false);
		die.frames(frames, 0);
		play(idle);
	}
}
