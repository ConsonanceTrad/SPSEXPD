package pd.items;

import pd.atlas.items.SpecificTaskDict;

import pd.actors.hero.Hero;

import java.util.ArrayList;

abstract class TriforcePiece extends Item {
	{
		image = SpecificTaskDict.TRIFORCE;
		stackable = false;
		unique = true;
		keptThoughLostInvent = true;
	}
	protected abstract void collected();
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}
	@Override public boolean doPickUp(Hero hero, int pos) {
		if (!super.doPickUp(hero, pos)) return false;
		collected();
		return true;
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 10 * quantity; }
}
