package pd.items;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.armor.normalarmor.NormalArmor;
import pd.sprites.ItemSpriteSheet;

import java.util.ArrayList;

/** The original no-material class armor kit. */
public class ArmorKit extends Item {
	public static final String AC_APPLY = "APPLY";
	{ image = ItemSpriteSheet.KIT; unique = true; defaultAction = AC_APPLY; }
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero); actions.add(AC_APPLY); return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (!AC_APPLY.equals(action)) { super.execute(hero, action); return; }
		detach(hero.belongings.backpack);
		NormalArmor armor = NormalArmor.upgrade(hero);
		if (armor == null) return;
		if (!armor.collect(hero.belongings.backpack)) Dungeon.level.drop(armor, hero.pos).sprite.drop();
		hero.sprite.operate(hero.pos);
		hero.spendAndNext(2f);
	}
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
