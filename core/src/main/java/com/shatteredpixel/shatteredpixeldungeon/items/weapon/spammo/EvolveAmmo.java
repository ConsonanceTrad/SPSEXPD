/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.NormalCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class EvolveAmmo extends SpAmmo {
	private static final ItemSprite.Glowing DEEP_GREEN = new ItemSprite.Glowing(0x006633);
	@Override public ItemSprite.Glowing glowing() { return DEEP_GREEN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 3) {
			if (!transform(attacker, defender)) defender.damage((int)(0.10f * damage), attacker);
		} else {
			defender.damage((int)(0.10f * damage), attacker);
		}
	}

	static boolean transform(Char attacker, Char defender) {
		if (!(defender instanceof Mob) || defender == attacker || Dungeon.level == null
				|| !defender.isAlive() || !Dungeon.level.insideMap(defender.pos)
				|| Char.hasProp(defender, Char.Property.BOSS)
				|| Char.hasProp(defender, Char.Property.MINIBOSS)) return false;
		int pos = defender.pos;
		NormalCell cell = new NormalCell();
		cell.HT = cell.HP = Math.max(1, defender.HP);
		cell.pos = pos;
		Actor.remove(defender);
		Dungeon.level.mobs.remove(defender);
		if (defender.sprite != null) defender.sprite.killAndErase();
		GameScene.add(cell);
		Dungeon.level.occupyCell(cell);
		if (defender.sprite != null) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 4);
		return true;
	}
}
