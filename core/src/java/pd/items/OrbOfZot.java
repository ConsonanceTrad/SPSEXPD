/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.mobs.OrbOfZotMob;
import pd.effects.particles.ElmoParticle;
import pd.items.journalpages.EnergyCore;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/** Yog's rechargeable energy core, restored from SPS-PD 0.9.8. */
public class OrbOfZot extends Item {

	public static final int FULL_CHARGE = 500;
	public static final String AC_ACTIVATETHROW = "ACTIVATETHROW";
	public static final String AC_BREAK = "BREAK";

	private static final String CHARGE = "charge";

	private int charge;
	private transient boolean activatedThrow;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		defaultAction = AC_ACTIVATETHROW;
		unique = true;
		usesTargeting = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= FULL_CHARGE) actions.add(AC_ACTIVATETHROW);
		actions.add(AC_BREAK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_ACTIVATETHROW.equals(action)) {
			if (charge < FULL_CHARGE) {
				activatedThrow = false;
				GLog.i(Messages.get(this, "rest"));
				return;
			}
			activatedThrow = true;
			super.execute(hero, AC_THROW);
			return;
		}

		activatedThrow = false;
		if (AC_BREAK.equals(action)) {
			breakOpen(hero);
			return;
		}
		super.execute(hero, action);
	}

	/** Breaks this orb and drops its hidden route page. */
	public boolean breakOpen(Hero hero) {
		if (hero == null || Dungeon.level == null
				|| !hero.belongings.backpack.contains(this)) return false;

		detachAll(hero.belongings.backpack);
		Heap heap = Dungeon.level.drop(new EnergyCore(), hero.pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.BLAST);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		return true;
	}

	@Override
	protected void onThrow(int cell) {
		boolean deploy = activatedThrow && charge >= FULL_CHARGE;
		activatedThrow = false;
		if (Dungeon.level == null) return;
		if (!Dungeon.level.insideMap(cell)) return;
		if (!deploy) {
			safeDrop(cell);
			return;
		}

		int spawnCell = summonCell(cell);
		if (spawnCell == -1 || OrbOfZotMob.spawnAt(spawnCell) == null) {
			// The item has already been detached by Item.cast. Never consume it if
			// the requested cell and all neighbouring cells are unavailable.
			safeDrop(cell);
		}
	}

	private void safeDrop(int cell) {
		Heap heap = Dungeon.level.drop(this, cell);
		if (heap != null && heap.sprite != null && !heap.isEmpty()) heap.sprite.drop(cell);
	}

	/** Resolves an occupied or unsafe throw target without crossing map edges. */
	public static int summonCell(int target) {
		if (validSummonCell(target)) return target;
		if (Dungeon.level == null || !Dungeon.level.insideMap(target)) return -1;
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = target + offset;
			if (validSummonCell(cell)) candidates.add(cell);
		}
		return candidates.isEmpty() ? -1 : Random.element(candidates);
	}

	private static boolean validSummonCell(int cell) {
		return Dungeon.level != null && Dungeon.level.insideMap(cell)
				&& Dungeon.level.passable[cell] && !Dungeon.level.pit[cell]
				&& Actor.findChar(cell) == null;
	}

	public void gainCharge() {
		if (charge < FULL_CHARGE) charge++;
	}

	public void gainCharge(int amount) {
		charge = Math.min(FULL_CHARGE, charge + Math.max(0, amount));
	}

	public int charge() {
		return charge;
	}

	@Override public String status() { return charge + "/" + FULL_CHARGE; }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE)));
		activatedThrow = false;
	}
}
