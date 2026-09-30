/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Shocked;
import pd.actors.buffs.Slow;
import pd.actors.buffs.TargetShoot;
import pd.actors.buffs.Wet;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.rings.RingOfSharpshooting;
import pd.items.weapon.Weapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Bundle;
import watabou.utils.Random;

import java.util.ArrayList;

public class TaurcenBow extends Weapon {
	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_BREAK = "BREAK";
	public static final String AC_FIRE = "FIRE";
	public static final String AC_ICE = "ICE";
	public static final String AC_POISON = "POISON";
	public static final String AC_ELE = "ELE";
	public static final int SPECIAL_THRESHOLD = 8;
	private static final String CHARGE = "charge";
	private static final String ARROW = "arrow";

	public enum Arrow { NONE, FIRE, ICE, POISON, ELE }

	private Arrow arrow = Arrow.NONE;
	private int charge;

	{
		image = ItemSpriteSheet.SPS_TAURCEN_BOW;
		stackable = false;
		unique = true;
		bones = false;
		reinforced = true;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 4 + 3 * lvl; }
	@Override public int max(int lvl) { return 8 + 5 * lvl; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isUpgradable() { return true; }
	@Override public boolean isIdentified() { return true; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_EQUIP);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		actions.add(AC_SHOOT);
		actions.add(AC_BREAK);
		actions.add(AC_FIRE);
		actions.add(AC_ICE);
		actions.add(AC_POISON);
		actions.add(AC_ELE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			GameScene.selectCell(shooter);
		} else if (AC_BREAK.equals(action)) setArrow(Arrow.NONE);
		else if (AC_FIRE.equals(action)) setArrow(Arrow.FIRE);
		else if (AC_ICE.equals(action)) setArrow(Arrow.ICE);
		else if (AC_POISON.equals(action)) setArrow(Arrow.POISON);
		else if (AC_ELE.equals(action)) setArrow(Arrow.ELE);
		else super.execute(hero, action);
	}

	@Override
	public int damageRoll(Char owner) {
		int damage = Random.Int(min(), max());
		if (owner.buff(TargetShoot.class) != null) damage = (int)(damage * 1.5f);
		if (owner.buff(MechArmor.class) != null) damage = (int)(damage * 1.5f);
		int bonus = Math.min(RingOfSharpshooting.levelDamageBonus(owner), 30);
		if (bonus > 0 && Random.Int(10) < 3) {
			damage = (int)(damage * (1.5f + 0.25f * bonus));
			if (owner.sprite != null) owner.sprite.emitter().burst(Speck.factory(Speck.STAR), 8);
		}
		return Math.max(0, damage);
	}

	private int rangedProc(Char attacker, Char defender, int damage) {
		if (charge >= SPECIAL_THRESHOLD) {
			applySpecialArrow(attacker, defender, damage);
			charge = 0;
		}
		charge++;
		updateQuickslot();
		return super.proc(attacker, defender, damage);
	}

	public void applySpecialArrow(Char attacker, Char defender, int damage) {
		switch (arrow) {
			case NONE:
				if (defender.isAlive()) Buff.affect(defender, ArmorBreak.class, 5f).level(40);
				defender.damage(damage, this);
				break;
			case FIRE:
				if (defender.isAlive()) Buff.affect(defender, Burning.class).reignite(defender, 3f);
				defender.damage(damage / 2, this);
				break;
			case ICE:
				defender.damage(damage / 2, this);
				if (defender.isAlive()) {
					Buff.prolong(defender, Wet.class, 5f);
					Buff.prolong(defender, Slow.class, 5f);
				}
				break;
			case POISON:
				defender.damage(damage / 4, this);
				if (defender.isAlive()) Buff.affect(defender, Ooze.class).set(5f);
				break;
			case ELE:
				if (defender.isAlive()) Buff.affect(defender, Shocked.class).level(3);
				Buff.affect(attacker, AttackUp.class, 10f).level(30);
				defender.damage(damage / 3, this);
				break;
		}
	}

	public void setArrow(Arrow arrow) { this.arrow = arrow == null ? Arrow.NONE : arrow; updateQuickslot(); }
	public Arrow arrow() { return arrow; }
	public int charge() { return charge; }
	public void gainCharge(int amount) { charge = Math.max(0, charge + amount); updateQuickslot(); }
	@Override public String status() { return charge + "/" + SPECIAL_THRESHOLD; }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "damage", min(), max())
			+ "\n\n" + Messages.get(this, "charge", charge, SPECIAL_THRESHOLD); }
	@Override public int targetingPos(Hero user, int dst) { return new TaurcenBowArrow().targetingPos(user, dst); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ARROW, arrow);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		arrow = bundle.getEnum(ARROW, Arrow.class);
		if (arrow == null) arrow = Arrow.NONE;
		charge = Math.max(0, bundle.getInt(CHARGE));
	}

	public class TaurcenBowArrow extends MissileWeapon {
		{
			image = ItemSpriteSheet.POISON_DART;
			tier = 1;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return TaurcenBow.this.min(); }
		@Override public int max(int lvl) { return TaurcenBow.this.max(); }
		@Override public int STRReq(int lvl) { return 10; }
		@Override public int damageRoll(Char owner) { return TaurcenBow.this.damageRoll(owner); }
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
			if (target != null) new TaurcenBowArrow().cast(curUser, target);
		}
		@Override public String prompt() { return Messages.get(TaurcenBow.class, "prompt"); }
	};
}
