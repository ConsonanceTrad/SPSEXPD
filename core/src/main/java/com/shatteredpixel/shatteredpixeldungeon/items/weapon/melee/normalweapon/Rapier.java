package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Rapier extends NormalMeleeWeapon {
	public Rapier() { super(3, 1f, 1f, 2, 18, 25, ItemSpriteSheet.SPS_WEP_RAPIER); }
	@Override protected void applyLegacyUpgrade(Stats s) { s.max += 4; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int roll = attackerRoll(attacker);
		if (Dungeon.level != null && Dungeon.level.distance(attacker.pos, defender.pos) == 2) {
			Ballistica route = new Ballistica(attacker.pos, defender.pos, Ballistica.PROJECTILE);
			int index = Math.max(0, route.dist - 1);
			if (index < route.path.size()) {
				int cell = route.path.get(index);
				if (cell != defender.pos && Actor.findChar(cell) == null && Dungeon.level.passable[cell]) {
					Actor.addDelayed(new Pushing(attacker, attacker.pos, cell), -1f);
					attacker.pos = cell;
					Dungeon.level.occupyCell(attacker);
					defender.damage(roll, this);
				}
			}
		}
		if (Random.Int(100) < 75) defender.damage(safeRandom(roll / 4, roll / 2), this);
		return super.proc(attacker, defender, damage);
	}
}
