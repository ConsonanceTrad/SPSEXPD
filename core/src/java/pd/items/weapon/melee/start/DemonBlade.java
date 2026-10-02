/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.start;

import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.hero.Hero;
import pd.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import render.utils.math.Random;

public class DemonBlade extends NormalMeleeWeapon {

	public DemonBlade() {
		super(2, 1f, 1f, 1, 7, 14, EquipmentEquipWeaponUniqueWeaponDict.DEMON_BLADE);
	}

	@Override
	protected void applyLegacyUpgrade(Stats stats) {
		if (stats.accuracy < 1.2f) stats.accuracy += 0.05f;
		if (stats.accuracy > 1.2f && stats.delay > 0.8f) stats.delay -= 0.05f;
		if (stats.delay < 0.8f && stats.reach < 2) stats.reach++;
		stats.min += 2;
		stats.max += 2;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int low = Math.max(0, damage / 4);
		int high = Math.max(low, damage / 2);
		if (high > 0) defender.damage(high <= low ? low : Random.Int(low, high), this);
		if (Random.Int(8) == 0) Buff.affect(defender, Burning.class).reignite(defender, 3f);
		if (Random.Int(3) == 0) {
			Hero hero = attacker instanceof Hero ? (Hero) attacker : Dungeon.hero;
			if (hero != null) hero.spp++;
		}
		return super.proc(attacker, defender, damage);
	}
}
