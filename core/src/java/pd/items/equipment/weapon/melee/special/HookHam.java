/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.effects.Speck;
import render.utils.math.Random;

public class HookHam extends SpsSpecialMeleeWeapon {
	public HookHam() { super(1, 1f, 1f, 1, 1, 5, SpecificPlaceHolderDict.SOMETHING_0); usesTargeting = true; }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) > 40) Buff.affect(defender, Bleeding.class).set(safeRandom(5, damage));
		if (Random.Int(100) < 20 && attacker.HP < attacker.HT) {
			attacker.HP = Math.min(attacker.HT, attacker.HP + 10);
			if (attacker.sprite != null) attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), .4f, 1);
		}
		if (Random.Int(100) == 98) dropRandomItems(defender.pos, 1);
		return super.proc(attacker, defender, damage);
	}
}
