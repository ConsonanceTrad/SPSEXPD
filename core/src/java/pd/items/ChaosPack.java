/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.actors.hero.Hero;

import java.util.ArrayList;

/** The contract obtained from Ice13 and used to forge Shadow Eater. */
public class ChaosPack extends Item {
	{
		image = EquipmentNonEquipDict.CHAOS_PACK;
		stackable = false;
		unique = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
}
