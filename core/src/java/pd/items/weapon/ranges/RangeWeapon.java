/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.ranges;

import pd.atlas.IconEntry;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.Speck;
import pd.effects.Splash;
import pd.items.rings.RingOfSharpshooting;
import pd.items.wands.WandOfBlastWave;
import pd.items.weapon.SpsRangedWeapon;
import pd.items.weapon.missiles.MissileWeapon;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;

/** SPS-PD's infinite-ammunition bow base. */
public abstract class RangeWeapon extends SpsRangedWeapon {
	public static final String AC_SHOOT = "SHOOT";

	public enum Variant {
		LIGHT(-1, 0.8f), NORMAL(0, 1f), HEAVY(1, 1.25f);
		final int strengthShift;
		final float delay;
		Variant(int strengthShift, float delay) {
			this.strengthShift = strengthShift;
			this.delay = delay;
		}
	}

	protected final int bowTier;
	protected final Variant variant;

	protected RangeWeapon(int tier, Variant variant, IconEntry image) {
		this.bowTier = tier;
		this.variant = variant;
		this.image = image;
		DLY = variant.delay;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}

	@Override
	public int min(int lvl) {
		int base = (int)((bowTier + 3) * variant.delay);
		return base + (variant == Variant.LIGHT ? 0 : lvl);
	}

	@Override
	public int max(int lvl) {
		int base = (int)((bowTier * bowTier - bowTier + 8) * variant.delay);
		int growth = 1 + bowTier / 2;
		if (variant == Variant.HEAVY) growth += bowTier + 1;
		return base + growth * lvl;
	}

	@Override public int STRReq(int lvl) { return 8 + bowTier * 2 + variant.strengthShift; }
	@Override public boolean isUpgradable() { return true; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (canShoot(hero)) actions.add(AC_SHOOT);
		return actions;
	}

	protected boolean canShoot(Hero hero) {
		return isEquipped(hero) || hero.subClass == HeroSubClass.AGENT;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			if (!canShoot(hero)) GLog.i(Messages.get(RangeWeapon.class, "need_to_equip"));
			else GameScene.selectCell(shooter);
		} else {
			super.execute(hero, action);
		}
	}

	@Override
	public int damageRoll(Char owner) {
		if (!(owner instanceof Hero) || ((Hero)owner).STR() < STRReq()) return 0;
		return Random.Int(min(), max()) / 2;
	}

	public int rangedDamageRoll(Char owner) {
		int damage = Random.Int(min(), max());
		if (owner instanceof Hero && ((Hero)owner).STR() >= STRReq()) {
			if (owner.buff(TargetShoot.class) != null) damage = (int)(damage * 1.5f);
			if (owner.buff(MechArmor.class) != null) damage = (int)(damage * 1.5f);
			int bonus = Math.min(RingOfSharpshooting.levelDamageBonus(owner), 30);
			if (bonus > 0 && Random.Int(10) < 3) {
				damage = (int)(damage * (1.5f + 0.25f * bonus));
				if (owner.sprite != null) owner.sprite.emitter().burst(Speck.factory(Speck.STAR), 8);
			}
		}
		return Math.max(0, damage);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Dungeon.level != null && attacker != null && defender != null) {
			int opposite = attacker.pos + attacker.pos - defender.pos;
			Ballistica trajectory = new Ballistica(attacker.pos, opposite, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(attacker, trajectory, 1, false, false, this);
			Buff.prolong(attacker, HasteBuff.class, 2f);
			Buff.prolong(attacker, Levitation.class, 3f);
		}
		return super.proc(attacker, defender, damage);
	}

	private int rangedProc(Char attacker, Char defender, int damage) {
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String info() {
		String info = desc();
		if (levelKnown) {
			info += "\n\n" + Messages.get(RangeWeapon.class, "stats_known",
					bowTier, min(), max(), STRReq(), variant.delay);
		} else {
			info += "\n\n" + Messages.get(RangeWeapon.class, "stats_unknown",
					bowTier, min(0), max(0), STRReq(0));
		}
		return info;
	}

	@Override
	public int value() {
		int result = 100;
		if (enchantment != null) result = Math.round(result * 1.5f);
		if (cursed && cursedKnown) result /= 2;
		if (levelKnown) {
			if (trueLevel() > 0) result *= trueLevel() + 1;
			else if (trueLevel() < 0) result /= 1 - trueLevel();
		}
		return Math.max(1, result);
	}

	@Override public int targetingPos(Hero user, int dst) { return new NormalArrow().targetingPos(user, dst); }

	public class NormalArrow extends MissileWeapon {
		{
			image = SpecificPlaceHolderDict.SOMETHING_0;
			tier = bowTier;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return RangeWeapon.this.min(); }
		@Override public int max(int lvl) { return RangeWeapon.this.max(); }
		@Override public int STRReq(int lvl) { return 8 + bowTier * 2; }
		@Override public int damageRoll(Char owner) { return rangedDamageRoll(owner); }
		@Override public float delayFactor(Char owner) {
			float delay = variant.delay;
			if (owner instanceof Hero) {
				int encumbrance = STRReq() - ((Hero)owner).STR();
				if (encumbrance > 0) delay *= Math.pow(1.2, encumbrance);
			}
			return delay / RangeWeapon.this.speedMultiplier(owner);
		}
		@Override protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
			else if (!curUser.shoot(enemy, this)) Splash.at(cell, 0xCC99FFFF, 1);
		}
		@Override protected void rangedHit(Char enemy, int cell) { }
		@Override protected void rangedMiss(int cell) { }
		@Override public int proc(Char attacker, Char defender, int damage) {
			return rangedProc(attacker, defender, damage);
		}
	}

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target != null) new NormalArrow().cast(curUser, target);
		}
		@Override public String prompt() { return Messages.get(RangeWeapon.class, "prompt"); }
	};
}
