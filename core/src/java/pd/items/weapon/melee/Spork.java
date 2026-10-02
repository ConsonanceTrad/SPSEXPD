/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.effects.Speck;
import render.utils.math.Random;

public class Spork extends MeleeWeapon {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; tier = 3; ACC = 1f; DLY = 0.8f; }
	@Override public int min(int lvl) { return 8 + 2 * lvl; }
	@Override public int max(int lvl) { return 14 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 14; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (attacker.HP < attacker.HT) {
			int healing = Random.Int(Math.max(1, attacker.HT / 20));
			attacker.HP = Math.min(attacker.HT, attacker.HP + healing);
			if (healing > 0 && attacker.sprite != null) attacker.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
		}
		return super.proc(attacker, defender, damage);
	}
}
