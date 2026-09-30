/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.items.armor.normalarmor.NormalArmor;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

public class PerformerArmor extends NormalArmor {
	public PerformerArmor() { super(2, 4f, 12f, 3, 0, 10, -1, 1, 3, ItemSpriteSheet.SPS_ARMOR_PERFORMER); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (attacker != null && Random.Int(8) == 0) Buff.affect(attacker, Charm.class, 3f).object = defender.id();
		return super.proc(attacker, defender, damage);
	}
}
