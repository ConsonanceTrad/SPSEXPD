/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.weapon.missiles.MissileWeapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.sprites.MissileSprite;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import watabou.utils.Bundle;
import watabou.utils.Random;

import java.util.ArrayList;

public class MissileShield extends Item {
	public static final String AC_CAST = "CAST";
	public static final String AC_SHIELD = "SHIELD";
	private static final String CHARGE = "charge";
	public static final int FULL_CHARGE = 10;
	private int charge;

	{
		image = ItemSpriteSheet.WOODEN_SHIELD;
		unique = true;
		defaultAction = AC_CAST;
		usesTargeting = true;
	}

	public int charge() { return charge; }
	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge > 9) actions.add(AC_CAST);
		if (charge > 5) actions.add(AC_SHIELD);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_CAST.equals(action)) {
			curUser = hero;
			if (charge < 10) GLog.i(Messages.get(this, "rest"));
			else GameScene.selectCell(shooter);
			return;
		}
		if (AC_SHIELD.equals(action)) {
			if (charge < 5) {
				GLog.i(Messages.get(this, "rest"));
				return;
			}
			Buff.prolong(hero, DefenceUp.class, 3f).level(50);
			Buff.affect(hero, ShieldArmor.class).level(hero.lvl);
			charge -= 5;
			updateQuickslot();
			return;
		}
		super.execute(hero, action);
	}

	public int min() { return 1 + (Dungeon.hero == null ? 0 : Dungeon.hero.lvl / 5); }
	public int max() { return 1 + (Dungeon.hero == null ? 0 : Dungeon.hero.lvl / 2); }
	public int damageRoll(Char owner) {
		int level = owner instanceof Hero ? ((Hero)owner).lvl : 1;
		return Random.Int(1 + level / 5, 1 + level / 2);
	}

	@Override public String desc() {
		return super.desc() + "\n\n" + Messages.get(this, "damage", min(), max())
				+ "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE);
	}
	@Override public String status() { return charge + "/" + FULL_CHARGE; }
	@Override public int visiblyUpgraded() { return Dungeon.hero == null ? 0 : Dungeon.hero.lvl / 5; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || charge < 10) return;
			MissileShieldAmmo ammo = new MissileShieldAmmo();
			int cell = ammo.throwPos(curUser, target);
			Char enemy = Actor.findChar(cell);
			charge -= 10;
			updateQuickslot();
			curUser.sprite.zap(cell);
			curUser.busy();
			ammo.throwSound();
			QuickSlotButton.target(enemy);
			((MissileSprite)curUser.sprite.parent.recycle(MissileSprite.class)).reset(
					curUser.sprite, cell, ammo, () -> {
						if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
						else if (!curUser.shoot(enemy, ammo)) Splash.at(cell, 0xCC99FFFF, 1);
						curUser.spendAndNext(1f);
					});
		}
		@Override public String prompt() { return Messages.get(MissileShield.class, "prompt"); }
	};

	private class MissileShieldAmmo extends MissileWeapon {
		{ image = ItemSpriteSheet.WOODEN_SHIELD; tier = 1; spawnedForEffect = true; setID = 0; }
		@Override public int defaultQuantity() { return 1; }
		@Override public int damageRoll(Char owner) { return MissileShield.this.damageRoll(owner); }
		@Override public float accuracyFactor(Char owner, Char target) { return 1000f; }
		@Override public int proc(Char attacker, Char defender, int damage) {
			if (Char.hasProp(defender, Char.Property.BOSS) || Char.hasProp(defender, Char.Property.MINIBOSS)) {
				defender.damage(damage, this);
			}
			Buff.prolong(defender, Paralysis.class, 3f);
			return super.proc(attacker, defender, damage);
		}
	}

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE)));
	}
}
