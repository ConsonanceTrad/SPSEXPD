/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.eggs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.actors.mobs.pets.BlueDragon;
import pd.actors.mobs.pets.BlueGirl;
import pd.actors.mobs.pets.BugDragon;
import pd.actors.mobs.pets.GoldDragon;
import pd.actors.mobs.pets.GreenDragon;
import pd.actors.mobs.pets.LegacyPet;
import pd.actors.mobs.pets.LeryFire;
import pd.actors.mobs.pets.LightDragon;
import pd.actors.mobs.pets.RedDragon;
import pd.actors.mobs.pets.Scorpion;
import pd.actors.mobs.pets.ShadowDragon;
import pd.actors.mobs.pets.VioletDragon;
import pd.effects.Pushing;
import pd.items.Item;
import pd.items.eggs.randomone.RandomEgg;
import pd.items.sellitem.VIPcard;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Calendar;

/** The original SPS mob soul, whose absorbed energies determine its hatchling. */
public class Egg extends Item {

	public static final String AC_BREAK = "BREAK";
	public static final int VIP_DROP_DENOMINATOR = 10;
	private static final float TIME_TO_USE = 1f;

	public int moves;
	public int burns;
	public int freezes;
	public int poisons;
	public int lits;
	public int darks;
	public int lights;

	{
		image = ItemSpriteSheet.SPS_PET_EGG;
		stackable = false;
		defaultAction = AC_BREAK;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (LegacyPet.active() == null || petHomeDepth()) actions.add(AC_BREAK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_BREAK.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (LegacyPet.active() != null && !petHomeDepth()) {
			GLog.w(Messages.get(this, "notready"));
			return;
		}
		LegacyPet pet = hatchling();
		if (pet == null) {
			dropBreakBonus(hero);
			Item reward = failedHatchReward();
			if (reward != null) Dungeon.level.drop(reward, hero.pos).sprite.drop();
			detach(hero.belongings.backpack);
			Statistics.eggBreak++;
			GLog.w(Messages.get(this, "yolk"));
			hero.spendAndNext(TIME_TO_USE);
			return;
		}
		int spawn = spawnCell(hero.pos);
		if (spawn < 0) {
			GLog.w(Messages.get(this, "prevent"));
			return;
		}
		pet.updateStats(true);
		pet.pos = spawn;
		pet.state = pet.HUNTING;
		GameScene.add(pet);
		Actor.add(new Pushing(pet, hero.pos, spawn));
		dropBreakBonus(hero);
		detach(hero.belongings.backpack);
		Statistics.eggBreak++;
		GLog.w(Messages.get(this, "hatch"));
		hero.spendAndNext(TIME_TO_USE);
	}

	protected Item failedHatchReward() {
		return moves >= 100 ? new RandomEgg() : null;
	}

	private void dropBreakBonus(Hero hero) {
		if (Random.Int(VIP_DROP_DENOMINATOR) == 0) Dungeon.level.drop(new VIPcard(), hero.pos).sprite.drop();
	}

	protected LegacyPet hatchling() {
		if (freezes >= 20 && poisons >= 20 && burns >= 20 && lits >= 20
				&& lights >= 20 && darks >= 20 && moves >= 2000) {
			return Calendar.getInstance().get(Calendar.MONTH) == Calendar.SEPTEMBER || Random.Int(50) == 0
					? new BugDragon() : new GoldDragon();
		}
		if (poisons >= 30 && lights >= 66 && lights <= 122) return new BlueGirl();
		if (freezes >= 5 && poisons >= 5 && burns >= 5 && lits >= 5 && moves >= 50) return new LeryFire();
		if (lights >= 20) return new ShadowDragon();
		if (freezes >= 20) return new BlueDragon();
		if (darks >= 20) return new LightDragon();
		if (poisons >= 20) return new VioletDragon();
		if (lits >= 20) return new GreenDragon();
		if (burns >= 20) return new RedDragon();
		if (moves >= 2000) return new Scorpion();
		return null;
	}

	protected int spawnCell(int center) {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = center + offset;
			if (cell >= 0 && cell < Dungeon.level.length()
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])
					&& Actor.findChar(cell) == null) cells.add(cell);
		}
		return cells.isEmpty() ? -1 : Random.element(cells);
	}

	public static boolean petHomeDepth() {
		return Dungeon.legacyDepth() == 50;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		GLog.w(Messages.get(this, "warmhome"));
		if (hero.belongings.getItem(Egg.class) != null) GLog.w(Messages.get(this, "onlyone"));
		return super.doPickUp(hero, pos);
	}

	public static Egg carried() {
		return Dungeon.hero == null ? null : Dungeon.hero.belongings.getItem(Egg.class);
	}

	@Override
	public String info() {
		return desc() + "\n\n" + Messages.get(this, "moves", moves)
				+ "\n" + Messages.get(this, "burns", burns)
				+ "\n" + Messages.get(this, "freezes", freezes)
				+ "\n" + Messages.get(this, "poisons", poisons)
				+ "\n" + Messages.get(this, "lits", lits)
				+ "\n" + Messages.get(this, "darks", darks)
				+ "\n" + Messages.get(this, "lights", lights);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 50 * quantity; }

	private static final String MOVES = "moves";
	private static final String BURNS = "burns";
	private static final String FREEZES = "freezes";
	private static final String POISONS = "poisons";
	private static final String LITS = "lits";
	private static final String DARKS = "darks";
	private static final String LIGHTS = "lights";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(MOVES, moves); bundle.put(BURNS, burns); bundle.put(FREEZES, freezes);
		bundle.put(POISONS, poisons); bundle.put(LITS, lits); bundle.put(DARKS, darks); bundle.put(LIGHTS, lights);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		moves = bundle.getInt(MOVES); burns = bundle.getInt(BURNS); freezes = bundle.getInt(FREEZES);
		poisons = bundle.getInt(POISONS); lits = bundle.getInt(LITS); darks = bundle.getInt(DARKS); lights = bundle.getInt(LIGHTS);
	}
}
