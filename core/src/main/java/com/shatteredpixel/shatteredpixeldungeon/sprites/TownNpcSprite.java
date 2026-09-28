/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TownNpc;
import com.watabou.noosa.TextureFilm;

/** Loads each town resident's original standalone sprite sheet. */
public class TownNpcSprite extends MobSprite {

	@Override
	public void link(Char ch) {
		TownNpc.Spec spec = ((TownNpc) ch).spec();
		texture(spec.asset);
		TextureFilm frames = new TextureFilm(texture, spec.frameWidth, spec.frameHeight);
		int first = spec == TownNpc.Spec.OLD_NEW_STWIST
				? (Dungeon.gnollMission ? 0 : 8)
				: spec.firstFrame;
		idle = new Animation(8, true);
		idle.frames(frames, first, first, first, first + 1, first + 1,
				first + 2, first + 2, first + 3, first + 3);
		run = new Animation(12, true);
		run.frames(frames, first);
		attack = new Animation(12, false);
		attack.frames(frames, first);
		die = new Animation(12, false);
		die.frames(frames, first);
		play(idle);
		super.link(ch);
	}
}
