/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Vertigo;
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

public class AttackShield extends Item {
	public static final String AC_CAST = "CAST";
	public static final String AC_BLAST = "BLAST";
	public static final int FULL_CHARGE = 20;
	private static final String CHARGE = "charge";
	private int charge;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_CAST;
		unique = true;
		usesTargeting = true;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= 10) actions.add(AC_CAST);
		if (charge >= FULL_CHARGE) actions.add(AC_BLAST);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_CAST.equals(action)) {
			if (charge < 10) GLog.i(Messages.get(this, "rest"));
			else { curUser = hero; GameScene.selectCell(shooter); }
		} else if (AC_BLAST.equals(action)) {
			if (!blast(hero)) GLog.i(Messages.get(this, "rest"));
		} else super.execute(hero, action);
	}

	public void gainCharge() { charge = Math.min(FULL_CHARGE, charge + 1); updateQuickslot(); }
	public int charge() { return charge; }
	public int min(Hero hero) { return 1 + (hero == null ? 0 : hero.lvl); }
	public int max(Hero hero) { return 3 + 2 * (hero == null ? 0 : hero.lvl); }
	public int damageRoll(Hero hero) { return Random.NormalIntRange(min(hero), max(hero)); }

	public boolean blast(Hero hero) {
		if (hero == null || charge < FULL_CHARGE) return false;
		Buff.affect(hero, LongBuff.class);
		Buff.prolong(hero, ArmorBreak.class, 100f).level(50);
		Buff.prolong(hero, AttackUp.class, 50f).level(100);
		charge -= FULL_CHARGE;
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	public boolean castAt(Hero hero, int target) {
		if (hero == null || Dungeon.level == null || charge < 10 || !Dungeon.level.insideMap(target)) return false;
		Ballistica shot = new Ballistica(hero.pos, target, Ballistica.MAGIC_BOLT);
		Char defender = Actor.findChar(shot.collisionPos);
		if (defender == null || defender == hero) return false;
		charge -= 10;
		defender.damage(damageRoll(hero), this);
		float ratio = defender.HT <= 0 ? 0f : Math.max(0f, defender.HP / (float)defender.HT);
		float scale = Char.hasProp(defender, Char.Property.BOSS) || Char.hasProp(defender, Char.Property.MINIBOSS) ? .25f : .5f;
		defender.damage(Math.round(defender.HT * ratio * scale), this);
		if (scale == .5f) Buff.prolong(defender, Vertigo.class, 5f);
		hero.spendAndNext(1f);
		updateQuickslot();
		return true;
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null && !castAt(curUser, target)) GLog.i(Messages.get(AttackShield.class, "not")); }
		@Override public String prompt() { return Messages.get(AttackShield.class, "prompt"); }
	};

	@Override public String info() { return super.info() + "\n\n" + Messages.get(this, "damage", min(Dungeon.hero), max(Dungeon.hero)) + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(CHARGE, charge); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE))); }

	public static class LongBuff extends Buff {
		{ type = buffType.POSITIVE; announced = true; }
		@Override public boolean act() { spend(TICK); return true; }
	}
}
