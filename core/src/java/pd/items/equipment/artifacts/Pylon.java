/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.MagicMissile;
import pd.items.Item;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.levels.Level;
import pd.levels.Transitions;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite.Glowing;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/** The follower's original portable teleportation pylon. */
public class Pylon extends Artifact {

	public static final String AC_ZAP = "ZAP";
	public static final String AC_SET = "SET";
	public static final String AC_RETURN = "RETURN";
	public static final String AC_RANKUP = "RANKUP";
	public static final int MAX_CHARGE = 3;
	public static final int MAX_LEVEL = 3;

	private static final String RETURN_DEPTH = "return_depth";
	private static final String RETURN_BRANCH = "return_branch";
	private static final String RETURN_POS = "return_pos";
	private static final String LEGACY_DEPTH = "depth";
	private static final String LEGACY_POS = "pos";

	private int returnDepth = -1;
	private int returnBranch;
	private int returnPos = -1;

	{
		image = EquipmentJewelleryArtifactDict.ARTIFACT_BEACON_0;
		levelCap = MAX_LEVEL;
		chargeCap = MAX_CHARGE;
		defaultAction = AC_ZAP;
		usesTargeting = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ZAP);
		actions.add(AC_SET);
		if (returnDepth != -1) actions.add(AC_RETURN);
		if (level() == MAX_LEVEL) actions.add(AC_RANKUP);
		return actions;
	}

	@Override
	public boolean isEquipped(Hero hero) {
		return super.isEquipped(hero) || (hero != null && hero.skin == 7
				&& hero.heroClass == pd.actors.hero.HeroClass.ASCETIC
				&& hero.belongings.backpack.contains(this));
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_ZAP.equals(action) && !AC_SET.equals(action)
				&& !AC_RETURN.equals(action) && !AC_RANKUP.equals(action)) {
			super.execute(hero, action);
			return;
		}

		if (AC_ZAP.equals(action)) {
			curUser = hero;
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
				QuickSlotButton.cancel();
			} else if (charge < 1) {
				GLog.i(Messages.get(this, "no_charge"));
				QuickSlotButton.cancel();
			} else {
				GameScene.selectCell(zapper);
			}
			return;
		}

		if (AC_RANKUP.equals(action)) {
			rankUp(hero);
			return;
		}

		if (!canUseBeacon(hero)) return;
		if (AC_SET.equals(action)) setReturnPoint(hero);
		else returnToPoint(hero);
	}

	public boolean canUseBeacon(Hero hero) {
		if (hero == null || Dungeon.level == null || Dungeon.bossLevel()
				|| Dungeon.depth > 25 || !Dungeon.interfloorTeleportAllowed()) {
			if (hero != null) hero.spend(1f);
			GLog.w(Messages.get(this, "preventing"));
			return false;
		}
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = hero.pos + offset;
			if (Dungeon.level.insideMap(cell) && Actor.findChar(cell) != null) {
				GLog.w(Messages.get(this, "creatures"));
				return false;
			}
		}
		return true;
	}

	public void setReturnPoint(Hero hero) {
		returnDepth = Dungeon.depth;
		returnBranch = Dungeon.branch;
		returnPos = hero.pos;
		hero.spend(1f);
		hero.busy();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.BEACON);
		GLog.i(Messages.get(this, "return"));
		updateQuickslot();
	}

	public boolean returnToPoint(Hero hero) {
		if (returnDepth == -1) return false;
		if (returnDepth == Dungeon.depth && returnBranch == Dungeon.branch) {
			if (!ScrollOfTeleportation.teleportToLocation(hero, returnPos)) return false;
			return true;
		}
		Transitions.beforeTransition();
		Invisibility.dispel();
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = returnDepth;
		InterlevelScene.returnBranch = returnBranch;
		InterlevelScene.returnPos = returnPos;
		Game.switchScene(InterlevelScene.class);
		return true;
	}

	public boolean rankUp(Hero hero) {
		if (hero == null || level() != MAX_LEVEL) return false;
		hero.HTBoost += 5;
		hero.improveAttackSkill(1);
		hero.improveDefenseSkill(1);
		hero.improveMagicSkill(1);
		level(0);
		hero.updateHT(true);
		if (hero.sprite != null) hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "rankup"));
		updateQuickslot();
		return true;
	}

	public boolean teleportTarget(Char target) {
		if (target == null || Dungeon.level == null || Dungeon.bossLevel()
				|| Char.hasProp(target, Char.Property.IMMOVABLE)) return false;
		int pos = -1;
		for (int attempts = 0; attempts < 10 && pos == -1; attempts++) {
			pos = Dungeon.level.randomRespawnCell(target);
		}
		if (pos == -1) {
			ArrayList<Integer> fallback = new ArrayList<>();
			for (int cell = 0; cell < Dungeon.level.length(); cell++) {
				if ((Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])
						&& Actor.findChar(cell) == null
						&& (!Char.hasProp(target, Char.Property.LARGE) || Dungeon.level.openSpace[cell])) {
					fallback.add(cell);
				}
			}
			if (!fallback.isEmpty()) pos = render.utils.math.Random.element(fallback);
		}
		if (pos == -1) return false;
		target.pos = pos;
		Dungeon.level.occupyCell(target);
		if (target instanceof Mob && ((Mob)target).state == ((Mob)target).HUNTING) {
			((Mob)target).state = ((Mob)target).WANDERING;
		}
		if (target.sprite != null) {
			target.sprite.place(pos);
			target.sprite.visible = Dungeon.level.heroFOV[pos];
		}
		return true;
	}

	private final CellSelector.Listener zapper = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null) return;
			Invisibility.dispel();
			charge--;
			updateQuickslot();

			if (Actor.findChar(target) == curUser) {
				ScrollOfTeleportation.teleportChar(curUser);
				curUser.spendAndNext(1f);
				return;
			}

			final Ballistica bolt = new Ballistica(curUser.pos, target, Ballistica.MAGIC_BOLT);
			final Char ch = Actor.findChar(bolt.collisionPos);
			if (ch == curUser) {
				ScrollOfTeleportation.teleportChar(curUser);
				curUser.spendAndNext(1f);
				return;
			}

			Sample.INSTANCE.play(Assets.Sounds.ZAP);
			curUser.busy();
			if (curUser.sprite == null) {
				finishZap(ch);
				return;
			}
			curUser.sprite.zap(bolt.collisionPos);
			MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.BEACON,
					curUser.sprite, bolt.collisionPos, new Callback() {
						@Override public void call() { finishZap(ch); }
					});
		}

		private void finishZap(Char ch) {
			if (ch != null && !teleportTarget(ch)) {
				GLog.w(Messages.get(Pylon.class, Char.hasProp(ch, Char.Property.IMMOVABLE) ? "tele_fail" : "no_tele"));
			}
			curUser.spendAndNext(1f);
		}

		@Override public String prompt() { return Messages.get(Pylon.class, "prompt"); }
	};

	@Override
	protected ArtifactBuff passiveBuff() {
		return new BeaconRecharge();
	}

	@Override
	public Item upgrade() {
		if (level() == levelCap) return this;
		if (charge < chargeCap) charge++;
		GLog.p(Messages.get(this, "levelup"));
		return super.upgrade();
	}

	public int charge() { return charge; }
	public int experience() { return exp; }
	public int returnDepth() { return returnDepth; }
	public int returnBranch() { return returnBranch; }
	public int returnPos() { return returnPos; }
	public void gainCharge(int amount) { charge = Math.min(chargeCap, charge + Math.max(0, amount)); }
	public void reset() { returnDepth = -1; returnBranch = 0; returnPos = -1; }

	@Override
	public String desc() {
		String desc = super.desc();
		if (returnDepth != -1) desc += "\n\n" + Messages.get(this, "desc_set", returnDepth);
		return desc;
	}

	private static final Glowing WHITE = new Glowing(0xFFFFFF);
	@Override public Glowing glowing() { return returnDepth != -1 ? WHITE : null; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(RETURN_DEPTH, returnDepth);
		bundle.put(RETURN_BRANCH, returnBranch);
		bundle.put(RETURN_POS, returnPos);
		bundle.put(LEGACY_DEPTH, returnDepth);
		if (returnDepth != -1) bundle.put(LEGACY_POS, returnPos);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		returnDepth = bundle.contains(RETURN_DEPTH) ? bundle.getInt(RETURN_DEPTH)
				: bundle.contains(LEGACY_DEPTH) ? bundle.getInt(LEGACY_DEPTH) : -1;
		returnBranch = bundle.getInt(RETURN_BRANCH);
		returnPos = bundle.contains(RETURN_POS) ? bundle.getInt(RETURN_POS)
				: bundle.contains(LEGACY_POS) ? bundle.getInt(LEGACY_POS) : -1;
	}

	public class BeaconRecharge extends ArtifactBuff {
		@Override
		public boolean act() {
			if (charge < chargeCap && !cursed) {
				partialCharge += 1f / (100f - (chargeCap - charge) * 10f);
				if (partialCharge >= 1f) {
					partialCharge--;
					charge++;
					if (charge == chargeCap) partialCharge = 0;
				}
			}
			updateQuickslot();
			spend(TICK);
			return true;
		}

		public void gainExp(float levelPortion) {
			if (cursed || levelPortion <= 0) return;
			exp += Math.round(levelPortion * 100);
			if (exp > 300 && level() < levelCap) {
				exp -= 300;
				upgrade();
			}
		}
	}
}
