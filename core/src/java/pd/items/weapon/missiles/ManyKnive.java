/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles;

import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.TargetShoot;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.effects.Splash;
import pd.items.Heap;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.items.rings.RingOfSharpshooting;
import pd.items.weapon.Weapon;
import pd.items.weapon.missiles.throwing.EscapeKnive;
import pd.items.weapon.spammo.SpAmmo;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/** Izayoi's reusable knife set from SPS-PD. */
public class ManyKnive extends Weapon {
	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_AMMO = "AMMO";
	private static final String SP_AMMO = "sp_ammo";

	private SpAmmo ammo;

	{
		image = EquipmentEquipWeaponUniqueWeaponDict.MANY_KNIVE_0;
		stackable = false;
		unique = true;
		bones = false;
		reinforced = true;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 4 + lvl; }
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
		actions.add(AC_AMMO);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			GameScene.selectCell(shooter);
		} else if (AC_AMMO.equals(action)) {
			curUser = hero;
			GameScene.selectItem(ammoSelector);
		} else {
			super.execute(hero, action);
		}
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
		if (ammo != null) ammo.onHit(attacker, defender, damage);
		maybeDropEscapeKnife(defender);
		return super.proc(attacker, defender, damage);
	}

	boolean maybeDropEscapeKnife(Char defender) {
		if (Dungeon.level == null || defender == null || Random.Int(50) != 0) return false;
		Heap heap = Dungeon.level.drop(new EscapeKnive(1), defender.pos);
		if (heap != null && heap.sprite != null) heap.sprite.drop();
		return true;
	}

	public void loadAmmo(SpAmmo ammo) {
		this.ammo = ammo;
		if (ammo != null) {
			ammo.identify();
			ammo.cursed = false;
		}
		updateQuickslot();
	}

	public boolean loadAmmoFromBackpack(Hero owner, SpAmmo selected) {
		if (owner == null || selected == null || !owner.belongings.backpack.contains(selected)) return false;
		Item consumed = selected.detach(owner.belongings.backpack);
		if (!(consumed instanceof SpAmmo)) return false;
		loadAmmo((SpAmmo)consumed);
		return true;
	}

	public SpAmmo loadedAmmo() { return ammo; }

	private final WndBag.ItemSelector ammoSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(ManyKnive.class, "prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item instanceof SpAmmo; }
		@Override public void onSelect(Item item) {
			if (!(item instanceof SpAmmo) || curUser == null) return;
			SpAmmo selected = (SpAmmo)item;
			if (ammo == null) consumeAndLoad(selected);
			else GameScene.show(new WndOptions(new ItemSprite(selected), Messages.titleCase(selected.name()),
					Messages.get(ManyKnive.class, "replace", ammo.name(), selected.name()),
					Messages.get(ManyKnive.class, "yes"), Messages.get(ManyKnive.class, "no")) {
				@Override protected void onSelect(int index) { if (index == 0) consumeAndLoad(selected); }
			});
		}
	};

	private void consumeAndLoad(SpAmmo selected) {
		if (!loadAmmoFromBackpack(curUser, selected)) return;
		if (curUser.sprite != null) curUser.sprite.operate(curUser.pos);
		Sample.INSTANCE.play(Assets.Sounds.EVOKE);
		curUser.spendAndNext(2f);
	}

	@Override
	public String info() {
		String info = desc() + "\n\n" + Messages.get(this, "damage", min(), max());
		if (ammo != null) info += "\n\n" + Messages.get(this, "ammo", ammo.name());
		return info;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (ammo != null) bundle.put(SP_AMMO, ammo);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		Object restored = bundle.get(SP_AMMO);
		ammo = restored instanceof SpAmmo ? (SpAmmo)restored : null;
	}

	public class KniveAmmo extends MissileWeapon {
		{
			image = SpecificPlaceHolderDict.SOMETHING_0;
			tier = 1;
			DLY = 0.25f;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return ManyKnive.this.min(); }
		@Override public int max(int lvl) { return ManyKnive.this.max(); }
		@Override public int STRReq(int lvl) { return 10; }
		@Override public int damageRoll(Char owner) { return ManyKnive.this.damageRoll(owner); }
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
			if (target != null) new KniveAmmo().cast(curUser, target);
		}
		@Override public String prompt() { return Messages.get(ManyKnive.class, "shoot_prompt"); }
	};
}
