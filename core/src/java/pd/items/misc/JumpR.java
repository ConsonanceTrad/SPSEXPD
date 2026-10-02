/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.InfJump;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class JumpR extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JumpR.class)
			.t("name", "盗贼之鞋")
			.t("ac_jump", "跳跃")
			.t("prompt", "选择跳跃的目的地点")
			.t("rest", "盗贼之鞋的充能不足。")
			.t("charge", "充能：%1$d / %2$d。")
			.t("desc", "盗贼缺乏跳跃训练，只能跳跃至多两格，但会获得短暂漂浮，并有60%%概率隐身5回合。");
	}

	public static final String AC_JUMP = "JUMP";
	public static final int FULL_CHARGE = 40;
	public static final int JUMP_COST = 10;
	public static final int RANGE = 2;
	private static final String CHARGE = "charge";

	private int charge;

	{
		image = EquipmentNonEquipDict.JUMP_BOOTS;
		defaultAction = AC_JUMP;
		unique = true;
		usesTargeting = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (canJump(hero)) actions.add(AC_JUMP);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_JUMP.equals(action)) {
			if (!canJump(hero)) GLog.i(Messages.get(this, "rest"));
			else { curUser = hero; GameScene.selectCell(jumper); }
		} else super.execute(hero, action);
	}

	public boolean canJump(Hero hero) {
		return hero != null && (charge >= JUMP_COST || hero.buff(InfJump.class) != null);
	}

	public boolean jumpTo(Hero hero, int target) {
		if (!canJump(hero) || Dungeon.level == null || hero.rooted
				|| !Dungeon.level.insideMap(target) || target == hero.pos) return false;

		Ballistica route = new Ballistica(hero.pos, target, Ballistica.PROJECTILE);
		int landingIndex = Math.min(route.dist, RANGE);
		if (landingIndex <= 0) return false;
		int cell = route.path.get(landingIndex);
		while (landingIndex > 0 && (Actor.findChar(cell) != null
				|| (!Dungeon.level.passable[cell] && !Dungeon.level.avoid[cell]))) {
			cell = route.path.get(--landingIndex);
		}
		if (landingIndex <= 0 || cell == hero.pos || Actor.findChar(cell) != null) return false;

		Buff.prolong(hero, Levitation.class, 2f);
		hero.move(cell, false);
		Dungeon.level.pressCell(cell);
		Dungeon.observe();
		if (Random.Int(10) > 3) Buff.prolong(hero, Invisibility.class, 5f);
		if (hero.buff(InfJump.class) == null) charge -= JUMP_COST;
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public void gainCharge() { if (charge < FULL_CHARGE) charge++; }
	public void gainCharge(int amount) { charge = Math.min(FULL_CHARGE, charge + Math.max(0, amount)); }
	public int charge() { return charge; }
	@Override public String status() { return Integer.toString(charge / JUMP_COST); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }

	private final CellSelector.Listener jumper = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null) jumpTo(curUser, target); }
		@Override public String prompt() { return Messages.get(JumpR.class, "prompt"); }
	};
}
