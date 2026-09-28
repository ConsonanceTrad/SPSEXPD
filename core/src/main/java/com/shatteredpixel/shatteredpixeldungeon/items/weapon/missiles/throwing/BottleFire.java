/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.throwing;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireFollower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;

/** Lery's bottled flame, which leaves a thirty-turn trail of delayed fire. */
public class BottleFire extends TossWeapon {

	{
		image = ItemSpriteSheet.BOTTLE_FIRE;
		tier = 1;
		baseUses = 1;
		bones = false;
	}

	@Override public int min(int lvl) { return 1; }
	@Override public int max(int lvl) { return 1; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override
	protected void onThrow(int cell) {
		Char enemy = Actor.findChar(cell);
		if (enemy == null) igniteArea(curUser, cell);
		else super.onThrow(cell);
	}

	void igniteArea(Hero owner, int center) {
		if (Dungeon.level == null) return;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int cell = center + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			if (Dungeon.level.flamable[cell] || Actor.findChar(cell) != null
					|| Dungeon.level.heaps.get(cell) != null) {
				GameScene.add(Blob.seed(cell, 5, Fire.class));
			}
		}
		if (owner != null) Buff.affect(owner, FireFollower.class).set(FireFollower.DURATION);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Burning.class).reignite(defender, 10f);
		Buff.affect(attacker, FireFollower.class).set(FireFollower.DURATION);
		return super.proc(attacker, defender, damage);
	}

	@Override public int value() { return 0; }
}
