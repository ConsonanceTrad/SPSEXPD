/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EarthImbue;
import pd.actors.buffs.FireImbue;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Recharging;
import pd.items.equipment.armor.Armor;
import pd.levels.Terrain;
import pd.sprites.ItemSprite;

public class AdaptGlyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0x006633);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		clearElementalMarker(defender);
		if (Dungeon.level == null || defender.pos < 0 || defender.pos >= Dungeon.level.length()
				|| !roll(level(armor) + 5, 4, defender, 2)) return damage;
		switch (Dungeon.level.map[defender.pos]) {
			case Terrain.GRASS: Buff.prolong(defender, EarthImbue.class, 5f); break;
			case Terrain.WATER: Buff.prolong(defender, HasteBuff.class, 5f); break;
			case Terrain.HIGH_GRASS: Buff.prolong(defender, Invisibility.class, 5f); break;
			case Terrain.CHASM: Buff.affect(defender, Levitation.class, 10f); break;
			case Terrain.INACTIVE_TRAP: Buff.prolong(defender, Recharging.class, 5f); break;
			case Terrain.EMBERS: Buff.affect(defender, FireImbue.class).set(5f); break;
			default: break;
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}
