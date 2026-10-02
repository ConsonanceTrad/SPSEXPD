package pd.items;

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.equipment.armor.normalarmor.NormalArmor;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The original no-material class armor kit. */
public class ArmorKit extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ArmorKit.class)
			.t("name", "护甲配件包")
			.t("ac_apply", "制作")
			.t("desc", "使用这套工具和材料，可以无需裁缝材料制作对应职业的专属护甲。");
	}



	public static final String AC_APPLY = "APPLY";
	{ image = ConsumUsefulProcessEnhanceDict.KIT_0; unique = true; defaultAction = AC_APPLY; }
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
