/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.actors.hero.Hero;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The contract obtained from Ice13 and used to forge Shadow Eater. */
public class ChaosPack extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChaosPack.class)
			.t("name", "混沌之契")
			.t("desc", "一张充满混沌之力的契约。暗噬1/3。");
	}



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
