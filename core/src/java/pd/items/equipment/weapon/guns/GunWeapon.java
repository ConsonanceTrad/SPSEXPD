/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.guns;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.TargetShoot;
import pd.actors.buffs.Vertigo;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.Speck;
import pd.effects.Splash;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.rings.RingOfSharpshooting;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.items.equipment.weapon.SpsRangedWeapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.spammo.SpAmmo;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** SPS-PD's magazine-fed firearm base. */
public class GunWeapon extends SpsRangedWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunWeapon.class)
			.t("ac_shoot", "射击")
			.t("ac_reload", "填弹")
			.t("ac_ammo", "切换子弹")
			.t("reloading", "填弹中……")
			.t("need_to_equip", "你需要先装备这件武器才能射击。")
			.t("prompt2", "选择加载的强化弹药")
			.t("full", "弹匣已满。")
			.t("empty", "弹药不足。")
			.t("ammo_add", "当前强化弹药：%s")
			.t("warning", "这把枪当前装有%1$s。是否改用%2$s？原有强化弹药将会消失。")
			.t("yes", "是")
			.t("no", "否")
			.t("prompt", "选择射击目标")
			.t("stats_known", "这件_%1$d阶_枪械可以造成_%2$d～%3$d点伤害_，并且需要_%4$d点力量_来正常使用。")
			.t("charge", "弹匣：%1$d/%2$d");
	}

	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_RELOAD = "RELOAD";
	public static final String AC_AMMO = "AMMO";

	private static final String CHARGE = "charge";
	private static final String RESERVE = "reserve";
	private static final String SP_AMMO = "sp_ammo";

	protected final int gunTier;
	protected final int fullCharge;
	protected int charge;
	protected int reserveAmmo = 100;
	private SpAmmo ammo;

	protected GunWeapon(int tier, int fullCharge) {
		this.gunTier = tier;
		this.fullCharge = fullCharge;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return gunTier + 2 + lvl; }
	@Override public int max(int lvl) { return gunTier * gunTier - gunTier + 8 + (2 + gunTier / 2) * lvl; }
	@Override public int STRReq(int lvl) { return 8 + gunTier * 2; }
	@Override public int damageRoll(Char owner) { return 0; }
	@Override public boolean isUpgradable() { return true; }

	@Override
	public Item upgrade() {
		reserveAmmo += 10;
		return super.upgrade();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (canShoot(hero)) actions.add(AC_SHOOT);
		if (canReload(hero)) actions.add(AC_RELOAD);
		if (supportsSpecialAmmo()) actions.add(AC_AMMO);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			if (!canShoot(hero)) {
				GLog.i(Messages.get(GunWeapon.class, "need_to_equip"));
			} else if (charge <= 0) {
				reload(hero, true);
			} else {
				GameScene.selectCell(shooter);
			}
		} else if (AC_RELOAD.equals(action)) {
			curUser = hero;
			if (!canReload(hero)) GLog.i(Messages.get(GunWeapon.class, "need_to_equip"));
			else reload(hero, false);
		} else if (AC_AMMO.equals(action) && supportsSpecialAmmo()) {
			curUser = hero;
			GameScene.selectItem(ammoSelector);
		} else {
			super.execute(hero, action);
		}
	}

	protected boolean supportsSpecialAmmo() { return true; }
	protected boolean canShoot(Hero hero) {
		return isEquipped(hero) || hero.subClass == HeroSubClass.AGENT;
	}
	protected boolean canReload(Hero hero) { return true; }

	private void reload(Hero hero, boolean automatic) {
		if (charge >= fullCharge) {
			GLog.n(Messages.get(GunWeapon.class, "full"));
			return;
		}
		int missing = fullCharge - charge;
		int loaded = reloadMagazine();
		if (loaded <= 0) {
			GLog.n(Messages.get(GunWeapon.class, "empty"));
			return;
		}
		float time = reloadTime(hero, automatic, Math.min(missing, loaded));
		if (hero.sprite != null) hero.sprite.showStatus(CharSprite.DEFAULT, Messages.get(GunWeapon.class, "reloading"));
		hero.spendAndNext(Math.max(0.1f, time));
	}

	/** Reloads without scheduling a turn, also used by deterministic tests. */
	public int reloadMagazine() {
		int missing = Math.max(0, fullCharge - charge);
		if (missing == 0 || reserveAmmo <= 0) return 0;
		int loaded = Math.min(missing, reserveAmmo);
		charge += loaded;
		return loaded;
	}

	protected float reloadTime(Hero hero, boolean automatic, int loaded) {
		if (hero.subClass == HeroSubClass.AGENT) return 0.5f;
		return automatic ? fullCharge / 2f : loaded / 2f;
	}

	public int charge() { return charge; }
	public void charge(int value) {
		charge = Math.max(0, Math.min(fullCharge, value));
		updateQuickslot();
	}
	public boolean addRound() {
		if (charge >= fullCharge) return false;
		charge++;
		updateQuickslot();
		return true;
	}
	public int fullCharge() { return fullCharge; }
	public int reserveAmmo() { return reserveAmmo; }
	boolean consumeRound() {
		if (charge <= 0) return false;
		charge--;
		updateQuickslot();
		return true;
	}

	public int gunDamageRoll(Char owner) {
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

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Dungeon.level != null && attacker != null && defender != null) {
			int opposite = defender.pos + defender.pos - attacker.pos;
			Ballistica trajectory = new Ballistica(defender.pos, opposite, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(defender, trajectory, 1, false, false, this);
			if (causesVertigo()) Buff.prolong(defender, Vertigo.class, 3f);
		}
		return damage;
	}

	protected boolean causesVertigo() { return true; }
	protected boolean addsEnergyDamage() { return true; }

	@Override
	public int value() {
		int value = 100;
		if (enchantment != null) value = Math.round(value * 1.5f);
		if (cursed && cursedKnown) value /= 2;
		if (levelKnown) {
			if (trueLevel() > 0) value *= trueLevel() + 1;
			else if (trueLevel() < 0) value /= 1 - trueLevel();
		}
		return Math.max(1, value);
	}

	@Override
	public String info() {
		String info = desc() + "\n\n" + Messages.get(GunWeapon.class, "stats_known",
				gunTier, min(), max(), STRReq());
		if (ammo != null) info += "\n\n" + Messages.get(GunWeapon.class, "ammo_add", ammo.name());
		info += "\n\n" + Messages.get(GunWeapon.class, "charge", charge, fullCharge);
		return info;
	}

	@Override public String status() { return levelKnown ? charge + "/" + fullCharge : null; }

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
		@Override public String textPrompt() { return Messages.get(GunWeapon.class, "prompt2"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item instanceof SpAmmo; }
		@Override public void onSelect(Item item) {
			if (!(item instanceof SpAmmo) || curUser == null) return;
			SpAmmo selected = (SpAmmo)item;
			if (ammo == null) consumeAndLoad(selected);
			else GameScene.show(new WndOptions(new ItemSprite(selected), Messages.titleCase(selected.name()),
					Messages.get(GunWeapon.class, "warning", ammo.name(), selected.name()),
					Messages.get(GunWeapon.class, "yes"), Messages.get(GunWeapon.class, "no")) {
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

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) { if (target != null && charge > 0) new GunAmmo().cast(curUser, target); }
		@Override public String prompt() { return Messages.get(GunWeapon.class, "prompt"); }
	};

	public class GunAmmo extends MissileWeapon {
		{
			image = SpecificPlaceHolderDict.SOMETHING_0;
			tier = Math.max(1, gunTier);
			ACC = 1.3f;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return GunWeapon.this.min(); }
		@Override public int max(int lvl) { return GunWeapon.this.max(); }
		@Override public int STRReq(int lvl) { return Math.max(1, GunWeapon.this.STRReq()); }
		@Override public int damageRoll(Char owner) { return GunWeapon.this.gunDamageRoll(owner); }
		@Override public float delayFactor(Char owner) {
			if (owner instanceof Hero && ((Hero)owner).subClass == HeroSubClass.AGENT && ((Hero)owner).justMoved) return 0.1f;
			return super.delayFactor(owner);
		}
		@Override protected void onThrow(int cell) {
			Char enemy = Actor.findChar(cell);
			if (enemy == null || enemy == curUser) Splash.at(cell, 0xCC99FFFF, 1);
			else if (!curUser.shoot(enemy, this)) Splash.at(cell, 0xCC99FFFF, 1);
		}
		@Override protected void rangedHit(Char enemy, int cell) { }
		@Override protected void rangedMiss(int cell) { }
		@Override public int proc(Char attacker, Char defender, int damage) {
			if (ammo != null) ammo.onHit(attacker, defender, damage);
			if (addsEnergyDamage()) {
				defender.damage(gunDamageRoll(attacker) / 2, DamageType.ENERGY_DAMAGE);
			}
			return super.proc(attacker, defender, damage);
		}
		@Override public void cast(Hero user, int dst) {
			if (!consumeRound()) return;
			super.cast(user, dst);
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		bundle.put(RESERVE, reserveAmmo);
		if (ammo != null) bundle.put(SP_AMMO, ammo);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(fullCharge, bundle.getInt(CHARGE)));
		reserveAmmo = bundle.contains(RESERVE) ? Math.max(0, bundle.getInt(RESERVE)) : 100 + 10 * Math.max(0, trueLevel());
		Object restored = bundle.get(SP_AMMO);
		ammo = restored instanceof SpAmmo ? (SpAmmo)restored : null;
	}
}
