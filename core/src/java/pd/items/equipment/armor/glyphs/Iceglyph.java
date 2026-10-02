/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Frost;
import pd.actors.buffs.armorbuff.GlyphIce;
import pd.items.equipment.armor.Armor;
import pd.messages.Messages;
import pd.sprites.ItemSprite;
import pd.ui.BuffIndicator;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public class Iceglyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0x0000FF);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphIce.class);
		if (damage == 0) return 0;
		int level = level(armor);
		if (attacker != null && roll(level + 6, 5, defender, 3)) {
			Buff.affect(attacker, Frost.class, Frost.DURATION * Random.Float(1f, 1.5f));
		}
		if (Random.Int(level + 7) >= 6) {
			Buff.affect(defender, DeferedDamage.class).prolong(damage);
			return 0;
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }

	public static class DeferedDamage extends Buff {
		private static final String DAMAGE = "damage";
		private int damage;
		public void prolong(int amount) { damage = Math.max(0, damage + amount); }
		public int remainingDamage() { return damage; }
		@Override public boolean act() {
			if (target == null || !target.isAlive() || damage <= 0) {
				detach();
				return true;
			}
			int tickDamage = Math.max(1, (int)(damage * 0.1f));
			target.damage(tickDamage, this);
			damage -= tickDamage;
			if (damage <= 0 || !target.isAlive()) detach();
			else spend(TICK);
			return true;
		}
		@Override public int icon() { return BuffIndicator.DEFERRED; }
		@Override public String desc() { return Messages.get(this, "desc", damage); }
		@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(DAMAGE, damage); }
		@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); damage = Math.max(0, bundle.getInt(DAMAGE)); }
	}
}
