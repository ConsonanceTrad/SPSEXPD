/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.special;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Vertigo;
import pd.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.items.wands.fusion.WandOfFlow;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import render.utils.Bundle;

public class NinjaFan extends NormalMeleeWeapon {
	private static final String CHARGE = "charge";
	private int charge;

	public NinjaFan() { super(1, 1f, 1f, 2, 1, 10, ItemSpriteSheet.SPS_NINJA_FAN); }

	@Override public int proc(Char attacker, Char defender, int damage) {
		charge++;
		if (charge > 6) {
			if (Dungeon.level != null) {
				int opposite = defender.pos + defender.pos - attacker.pos;
				WandOfFlow.throwChar(defender,
						new Ballistica(defender.pos, opposite, Ballistica.MAGIC_BOLT), 2);
			}
			Buff.prolong(defender, Vertigo.class, 3f);
			charge = 0;
		}
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public int charge() { return charge; }
	@Override public String status() { return charge + "/6"; }
	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "charge", charge, 6); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(6, bundle.getInt(CHARGE))); }
}
