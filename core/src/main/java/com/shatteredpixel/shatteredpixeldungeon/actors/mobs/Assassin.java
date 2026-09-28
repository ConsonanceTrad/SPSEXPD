/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AssassinSprite;

/** Original SPS runtime/save identity for the fully migrated assassin. */
public class Assassin extends SpsPrisonMobs.Assassin {
	{ spriteClass = AssassinSprite.class; }

	public static Assassin spawnAt(int cell) {
		if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell]
				|| Actor.findChar(cell) != null) return null;
		Assassin mob = new Assassin();
		mob.pos = cell;
		mob.state = mob.HUNTING;
		GameScene.add(mob, 2f);
		return mob;
	}
}
