package pd.items;

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.actors.hero.Hero;
import pd.items.equipment.armor.Armor;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * The original no-material class armor kit.
 *
 * SPSEXPD: 由「一次性制作职业护甲」改为可反复使用的换皮道具 ——
 * 不再消耗自身、也不再生成新护甲，而是给选中的护甲换上/卸下对应职业的英雄护甲外观
 * （只影响显示图标，护甲数值、等级、纹章与技能一概不变；对同一件护甲再用一次即还原）。
 */
public class ArmorKit extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ArmorKit.class)
			.t("name", "护甲配件包")
			.t("ac_apply", "换外观")
			.t("prompt", "选择要更换外观的护甲")
			.t("skinned", "%s换上了英雄护甲的外观。")
			.t("restored", "%s恢复了原本的外观。")
			.t("no_armor", "这件护甲没有对应的英雄外观。")
			.t("desc", "这套工具与材料可以把一件护甲的外观改成对应职业的英雄护甲样式，对同一件护甲再用一次即可还原。它只换外观，护甲的数值、等级、纹章与技能都不受影响，工具包本身也不会被消耗。");
	}



	public static final String AC_APPLY = "APPLY";
	{ image = ConsumUsefulProcessEnhanceDict.ARMORKIT; unique = true; defaultAction = AC_APPLY; }
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero); actions.add(AC_APPLY); return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (!AC_APPLY.equals(action)) { super.execute(hero, action); return; }
		//SPSEXPD: 工具包升格为可反复使用的特殊物品：不消耗自身，改为给选中的护甲切换英雄护甲外观
		curUser = hero;
		GameScene.selectItem(armorSelector);
	}

	private final WndBag.ItemSelector armorSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(ArmorKit.class, "prompt"); }
		@Override public boolean itemSelectable(Item item) { return item instanceof Armor; }
		@Override public void onSelect(Item item) {
			if (item == null || curUser == null) return;
			Armor armor = (Armor) item;
			if (!armor.toggleHeroicSkin()) {
				GLog.w(Messages.get(ArmorKit.class, "no_armor"));
				return;
			}
			GLog.i(Messages.get(ArmorKit.class, armor.heroicSkin ? "skinned" : "restored", armor.name()));
			curUser.spendAndNext(2f);
		}
	};

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}
