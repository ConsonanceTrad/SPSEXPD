/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Slow;
import pd.actors.damagetype.DamageType;
import pd.sprites.ItemSprite;

public class DreamAmmo extends SpAmmo {
	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x22CC44);
	@Override public ItemSprite.Glowing glowing() { return GREEN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.20f * damage), DamageType.DARK_DAMAGE);
		Buff.prolong(defender, ArmorBreak.class, 6f).level(25);
		Buff.prolong(defender, Slow.class, 3f);
	}
}
