/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.InfJump;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Item;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class JumpW extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(JumpW.class)
			.t("name", "战士之鞋")
			.t("ac_jump", "跳跃")
			.t("prompt", "选择跳跃的目的地点")
			.t("rest", "战士之鞋的充能不足。")
			.t("charge", "充能：%1$d / %2$d。")
			.t("desc", "战士的冲锋训练使其可以跳跃至多五格。落点周围的每个单位都有70%%概率被麻痹。");
	}



	public static final String AC_JUMP = "JUMP";
	//SPSEXPD: charge 现在只表示冷却剩余回合；跳跃固定消耗 100 露珠（见 JumpBoots）
	public static final int FULL_CHARGE = JumpBoots.COOLDOWN;
	public static final int RANGE = 5;
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
			if (!canJump(hero)) {
				GLog.i(Messages.get(JumpBoots.class, "blocked"));
			} else {
				curUser = hero;
				GameScene.selectCell(jumper);
			}
		} else {
			super.execute(hero, action);
		}
	}

	public boolean canJump(Hero hero) {
		return hero != null && (hero.buff(InfJump.class) != null || (charge <= 0 && JumpBoots.hasDew(hero)));
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

		hero.move(cell, false);
		Dungeon.level.pressCell(cell);
		Dungeon.observe();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int adjacent = cell + offset;
			if (!Dungeon.level.insideMap(adjacent)) continue;
			Char targetChar = Actor.findChar(adjacent);
			if (targetChar != null && targetChar != hero && targetChar.isAlive() && Random.Int(10) > 2) {
				Buff.prolong(targetChar, Paralysis.class, 3f);
			}
		}
		if (hero.sprite != null) CellEmitter.center(cell).burst(Speck.factory(Speck.DUST), 10);
		if (hero.buff(InfJump.class) == null) { JumpBoots.spendDew(hero); charge = FULL_CHARGE; }
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public void gainCharge() { if (charge > 0) charge--; }
	public void gainCharge(int amount) { charge = Math.min(FULL_CHARGE, charge + Math.max(0, amount)); }
	public int charge() { return charge; }

	@Override public String status() { return charge <= 0 ? null : Integer.toString(charge); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(JumpBoots.class, "cooldown", charge, FULL_CHARGE, JumpBoots.DEW_COST); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }

	private final CellSelector.Listener jumper = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null) jumpTo(curUser, target); }
		@Override public String prompt() { return Messages.get(JumpW.class, "prompt"); }
	};
}
