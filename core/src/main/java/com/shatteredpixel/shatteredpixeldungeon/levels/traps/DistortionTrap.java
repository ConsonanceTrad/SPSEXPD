package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.watabou.noosa.Game;

public class DistortionTrap extends Trap {

	{
		color = WHITE;
		shape = LARGE_DOT;
	}

	@Override
	public void activate() {
		Char target = Actor.findChar(pos);
		if (!prepareLegacyReset(target)) return;
		Game.switchScene(InterlevelScene.class);
	}

	protected boolean prepareLegacyReset(Char target) {
		if (target == null || !target.isAlive() || Dungeon.hero == null) return false;

		InterlevelScene.returnDepth = Dungeon.depth;
		InterlevelScene.returnBranch = Dungeon.branch;
		for (Item item : Dungeon.hero.belongings.backpack.items.toArray(new Item[0])) {
			if (item instanceof Key && ((Key)item).depth == Dungeon.depth) {
				item.detachAll(Dungeon.hero.belongings.backpack);
			}
		}
		InterlevelScene.mode = InterlevelScene.Mode.RESET;
		return true;
	}
}
