/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RedWraithSprite;

/** Original SPS-PD runtime and save identity for the chaos wraith. */
public class RedWraith extends SpsCityMobs.RedWraith {

	{
		spriteClass = RedWraithSprite.class;
	}

	public static RedWraith spawnAt(int cell) {
		if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell] || Actor.findChar(cell) != null) return null;
		RedWraith wraith = new RedWraith();
		wraith.adjustStats(Math.max(1, Dungeon.legacyDepth()));
		wraith.pos = cell;
		wraith.state = wraith.HUNTING;
		GameScene.add(wraith, 2f);
		if (wraith.sprite != null) {
			wraith.sprite.alpha(0);
			wraith.sprite.emitter().burst(ShadowParticle.CURSE, 5);
		}
		return wraith;
	}
}
