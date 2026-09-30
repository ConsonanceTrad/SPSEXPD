/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise Pixel Dungeon behavior restored from SPS-PD 0.9.8.
 */

package pd.items.artifacts;

import com.badlogic.gdx.Gdx;
import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.CounterBuff;
import pd.actors.buffs.GoldTouch;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.particles.ElmoParticle;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StoneOre;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MasterThievesArmband extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_ARMBAND;
		levelCap = 5;
		charge = 0;
		partialCharge = 0;
		chargeCap = 1 + level();
		defaultAction = AC_STEAL;
	}

	public static final String AC_STEAL = "STEAL";
	public static final String AC_GOLDTOUCH = "GOLDTOUCH";

	@Override
	public String status() {
		return levelKnown ? charge + "/" + chargeCap : null;
	}

	@Override
	public Item upgrade() {
		chargeCap = Math.min(6, chargeCap + 1);
		return super.upgrade();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge > 0 && !cursed) actions.add(AC_STEAL);
		if (!isEquipped(hero) && level() > 1 && !cursed) actions.add(AC_GOLDTOUCH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (AC_STEAL.equals(action)) {
			curUser = hero;
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
				usesTargeting = false;
			} else if (charge < 1) {
				GLog.i(Messages.get(this, "no_charge"));
				usesTargeting = false;
			} else if (cursed) {
				GLog.w(Messages.get(this, "cursed"));
				usesTargeting = false;
			} else {
				usesTargeting = true;
				GameScene.selectCell(targeter);
			}
		}
		if (AC_GOLDTOUCH.equals(action)) {
			applyGoldTouch(hero);
		}
	}

	protected void applyGoldTouch(Hero hero) {
		Buff.affect(hero, GoldTouch.class, level() * 5f);
		if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		hero.spend(1f);
		hero.busy();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		level(level() - 1);
		updateQuickslot();
	}

	public final CellSelector.Listener targeter = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null) return;
			if (curUser == null || Dungeon.level == null || target < 0 || target >= Dungeon.level.length()
					|| !Dungeon.level.adjacent(curUser.pos, target)) {
				GLog.w(Messages.get(MasterThievesArmband.class, "no_target"));
				return;
			}

			Char targetChar = Actor.findChar(target);
			if (!(targetChar instanceof Mob)) return;

			final Mob mob = (Mob) targetChar;
			curUser.busy();
			Callback finish = new Callback() {
				@Override
				public void call() {
					if (Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.HIT);
					performLegacySteal(mob);
				}
			};
			if (curUser.sprite != null) curUser.sprite.attack(target, finish);
			else finish.call();
		}

		@Override
		public String prompt() {
			return Messages.get(MasterThievesArmband.class, "prompt");
		}
	};

	protected void performLegacySteal(Mob mob) {
		Item loot = takeLegacyLoot(mob);
		if (Dungeon.level != null && curUser != null && loot != null) {
			Heap heap = Dungeon.level.drop(loot, curUser.pos);
			if (heap != null && heap.sprite != null) heap.sprite.drop();
		}

		recordLegacySteal();
		if (curUser != null) curUser.next();
	}

	protected void recordLegacySteal() {
		charge--;
		exp++;
		while (exp >= level() && level() < levelCap) {
			exp = 0;
			GLog.p(Messages.get(MasterThievesArmband.class, "level_up"));
			upgrade();
		}
		updateQuickslot();
	}

	protected Item takeLegacyLoot(Mob mob) {
		if (mob.firstItem) {
			mob.firstItem = false;
			Item loot = mob.SupercreateLoot();
			return loot == null ? new StoneOre() : loot;
		}
		return new StoneOre();
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Thievery();
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)) desc += "\n\n" + Messages.get(this, "desc_worn");
		return desc;
	}

	private static final String LEGACY_PARTIAL_CHARGE = "partialCharge";
	private static final String SAVED_CHARGE = "charge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEGACY_PARTIAL_CHARGE, partialCharge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		int savedCharge = bundle.getInt(SAVED_CHARGE);
		super.restoreFromBundle(bundle);
		if (level() > levelCap) level(levelCap);
		chargeCap = Math.min(6, 1 + level());
		charge = Math.max(0, Math.min(chargeCap, savedCharge));
		if (bundle.contains(LEGACY_PARTIAL_CHARGE)) {
			partialCharge = bundle.getFloat(LEGACY_PARTIAL_CHARGE);
		}
	}

	public class Thievery extends ArtifactBuff {
		@Override
		public boolean act() {
			if (cursed && Dungeon.gold > 0 && Random.Int(5) == 0) Dungeon.gold--;

			if (charge < chargeCap) {
				partialCharge += 1f;
				if (partialCharge >= 400f) {
					charge++;
					partialCharge = 0;
					if (charge == chargeCap) partialCharge = 0;
				}
			} else {
				partialCharge = 0;
			}

			updateQuickslot();
			spend(TICK);
			return true;
		}

		public void gainCharge() {
			if (cursed) return;
			if (charge < chargeCap) {
				partialCharge += level();
				while (partialCharge > 400f) {
					partialCharge = 0;
					charge++;
					updateQuickslot();
					if (charge == chargeCap) partialCharge = 0;
				}
			} else {
				partialCharge = 0f;
			}
		}

		// Retained only so Shattered's shop code compiles; zero keeps that non-SPS action hidden.
		public boolean steal(Item item) { return false; }
		public float stealChance(Item item) { return 0f; }
		public int chargesToUse(Item item) { return 0; }
	}

	/** Kept to deserialize saves made by earlier SPS-SPD development builds. */
	public static class StolenTracker extends CounterBuff {
		{ revivePersists = true; }
		public void setItemStolen(boolean stolen) { if (stolen) countUp(1); }
		public boolean itemWasStolen() { return count() > 0; }
	}
}
