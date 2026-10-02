package pd.items.equipment.weapon.melee.normalweapon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.Pushing;
import pd.mechanics.Ballistica;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Rapier extends NormalMeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Rapier.class)
			.t("name", "刺剑")
			.t("desc", "一件又细又长又尖的武器。——Snof33 \n高级穿刺");
	}

	public Rapier() { super(3, 1f, 1f, 2, 18, 25, SpecificPlaceHolderDict.SOMETHING_0); }
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
