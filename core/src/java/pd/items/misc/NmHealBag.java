/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndUseItem;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class NmHealBag extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NmHealBag.class)
			.t("name", "纳米维生包")
			.t("ac_choose", "选择")
			.t("ac_heal", "纳米医疗")
			.t("ac_cook", "纳米重组")
			.t("ac_add", "纳米增值")
			.t("need_charge", "纳米点数不足。")
			.t("desc", "纳米维生包可以帮助你控制纳米点数。纳米医疗会把全部纳米点数转化为生命并清除数种有害状态；纳米重组消耗10点制造食物或召唤物；纳米增值会把大部分生命转化为纳米点数。");
	}




	public static final String AC_CHOOSE = "CHOOSE";
	public static final String AC_HEAL = "HEAL";
	public static final String AC_COOK = "COOK";
	public static final String AC_ADD = "ADD";
	public static final int COOK_COST = 10;

	{ image = SpecificPlaceHolderDict.SOMETHING_0; unique = true; defaultAction = AC_CHOOSE; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_HEAL);
		actions.add(AC_COOK);
		actions.add(AC_ADD);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHOOSE.equals(action)) GameScene.show(new WndUseItem(null, this));
		else if (AC_HEAL.equals(action)) heal(hero);
		else if (AC_COOK.equals(action)) { if (!rebuild(hero)) GLog.p(Messages.get(this, "need_charge")); }
		else if (AC_ADD.equals(action)) improve(hero);
		else super.execute(hero, action);
	}

	public boolean heal(Hero hero) {
		if (hero == null || hero.spp <= 0) return false;
		hero.HP = Math.min(hero.HT, hero.HP + hero.spp);
		hero.spp = 0;
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, AttackDown.class);
		Buff.detach(hero, ArmorBreak.class);
		hero.spendAndNext(1f);
		return true;
	}

	public boolean rebuild(Hero hero) {
		if (hero == null || Dungeon.level == null || hero.spp < COOK_COST) return false;
		Item result = Generator.random(Random.Int(4) == 0 ? Generator.Category.HIGHFOOD : Generator.Category.SUMMONED);
		if (result == null) return false;
		hero.spp -= COOK_COST;
		Heap heap = Dungeon.level.drop(result, hero.pos);
		if (heap.sprite != null) heap.sprite.drop();
		hero.spendAndNext(1f);
		return true;
	}

	public boolean improve(Hero hero) {
		if (hero == null || hero.HP <= 10) return false;
		hero.spp += hero.HP / 4;
		hero.HP = 1;
		hero.spendAndNext(1f);
		return true;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
