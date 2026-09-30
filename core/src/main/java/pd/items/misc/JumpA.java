/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.InfJump;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Item;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.Bundle;
import render.utils.Random;

import java.util.ArrayList;

public class JumpA extends Item {
	public static final String AC_JUMP = "JUMP";
	public static final int FULL_CHARGE = 40;
	public static final int JUMP_COST = 20;
	public static final int RANGE = 4;
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = ItemSpriteSheet.LEGACY_ATTACK_SHOES;
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
		Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET | Ballistica.STOP_SOLID);
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
		if (hero.sprite != null) CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 10);
		Sample.INSTANCE.play(Assets.Sounds.PUFF);
		Dungeon.observe();
		rollHaste(hero);
		if (hero.buff(InfJump.class) == null) charge -= JUMP_COST;
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public boolean rollHaste(Hero hero) {
		if (hero != null && Random.Int(10) < 4) {
			Buff.affect(hero, HasteBuff.class, 4f);
			return true;
		}
		return false;
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
		@Override public String prompt() { return Messages.get(JumpA.class, "prompt"); }
	};
}
