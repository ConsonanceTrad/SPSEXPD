/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.guns;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.EndlessAmmo;
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
import pd.items.equipment.weapon.SpsRangedWeapon;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.enchantments.Puncturing;
import pd.items.equipment.weapon.enchantments.Ricochet;
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
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;

/** SPS-PD's magazine-fed firearm base. */
public class GunWeapon extends SpsRangedWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GunWeapon.class)
			.t("ac_shoot", "射击目标")
			.t("ac_aim", "定向射击")
			.t("ac_reload", "填弹")
			.t("ac_ammo", "切换子弹")
			.t("reloading", "填弹中……")
			.t("need_to_equip", "你需要先装备这件武器才能射击。")
			.t("need_target", "那里没有可以射击的目标。")
			.t("prompt2", "选择加载的强化弹药")
			.t("full", "弹匣已满。")
			.t("empty", "弹药不足。")
			.t("ammo_add", "当前强化弹药：%s")
			.t("warning", "这把枪当前装有%1$s。是否改用%2$s？原有强化弹药将会消失。")
			.t("yes", "是")
			.t("no", "否")
			.t("prompt", "选择射击目标")
			.t("prompt_aim", "选择射击方向")
			.t("stats_known", "这件_%1$d阶_枪械可以造成_%2$d～%3$d点伤害_，并且需要_%4$d点力量_来正常使用。")
			.t("charge", "弹匣：%1$d/%2$d");
	}



	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_AIM = "AIM";
	public static final String AC_RELOAD = "RELOAD";
	public static final String AC_AMMO = "AMMO";

	private static final String CHARGE = "charge";
	private static final String RESERVE = "reserve";
	private static final String SP_AMMO = "sp_ammo";

	/** SPSEXPD: 枪械不适用（近战专属）的附魔，随机附魔时排除。 */
	public static final Class<?>[] INCOMPATIBLE_ENCHANTS = new Class<?>[]{
			pd.items.equipment.weapon.enchantments.Vorpal.class,
			pd.items.equipment.weapon.enchantments.Grim.class,
			pd.items.equipment.weapon.enchantments.Vampiric.class,
			pd.items.equipment.weapon.enchantments.Blocking.class,
			pd.items.equipment.weapon.enchantments.Blooming.class,
	};

	protected GunArchetype archetype;
	protected final int gunTier;
	/** 弹匣容量（一次可连续击发的发数上限）。 */
	protected int fullCharge;
	/** 单次击发消耗的弹药数（冲锋枪 = 4）。 */
	protected int shotsPerFire;
	/** 零后坐缓冲所需的力量阈值。 */
	protected int perfectSTR;
	/** 精准射击范围（超出后命中率与弹道偏差变差）。 */
	protected int effectiveRange;

	protected int charge;
	protected int reserveAmmo = 100;
	private SpAmmo ammo;

	/** 装填方式；耗时按枪型固定（霰弹枪为逐颗）。 */
	protected GunArchetype.Reload reloadMode;
	protected float baseReloadTime;
	/** 枪声传播半径。 */
	protected int noiseRadius;
	/** 充能型：未开火时每回合自然回充的发数（0 = 普通枪械）。 */
	protected float regenPerTurn;
	/** 弹道穿透：0 = 命中即止；Integer.MAX_VALUE = 命中后继续飞至撞墙。 */
	protected int pierce;

	private float regenProgress = 0f;
	private boolean firedSinceLastCharge = false;

	/** 连射状态：对同一目标连续射击不消耗回合，但仍受后坐缓冲限制。 */
	protected int lastTargetPos = -1;
	protected float lastShotTime = -Float.MAX_VALUE;

	protected GunWeapon(GunArchetype archetype) {
		this.archetype = archetype;
		this.gunTier = archetype.tier;
		this.fullCharge = archetype.magazine;
		this.shotsPerFire = archetype.shotsPerFire;
		this.perfectSTR = archetype.perfectSTR;
		this.effectiveRange = archetype.range;
		this.reloadMode = archetype.reload;
		this.baseReloadTime = archetype.reloadTime;
		this.noiseRadius = archetype.noise;
		this.regenPerTurn = archetype.regenPerTurn;
		this.pierce = archetype.pierce;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}

	@Override public int min(int lvl) { return gunTier + 2 + lvl; }
	@Override public int max(int lvl) { return gunTier * gunTier - gunTier + 8 + (2 + gunTier / 2) * lvl; }
	//SPSEXPD: 力量需求随等级下降（与近战/弓/护甲统一）
	@Override public int STRReq(int lvl) {
		int lv = Math.max(0, lvl);
		return (8 + gunTier * 2) - (int)(Math.sqrt(8 * lv + 1) - 1) / 2;
	}
	@Override public int damageRoll(Char owner) { return 0; }
	@Override public boolean isUpgradable() { return true; }

	@Override
	public Item upgrade() {
		reserveAmmo += 10;
		return super.upgrade();
	}

	/** SPSEXPD: 枪械精准——不吃天赋/戒指的近战精准，只由力量与距离决定（力量越高越准）。 */
	@Override
	public float accuracyFactor(Char owner, Char target) {
		float acc = this.ACC;
		if (owner instanceof Hero) {
			int diff = ((Hero)owner).STR() - perfectSTR;
			acc *= (float)Math.pow(1.1, diff);
		}
		if (target != null && Dungeon.level != null) {
			int dist = Dungeon.level.distance(owner.pos, target.pos);
			if (dist > effectiveRange) {
				acc *= Math.max(0.2f, 1f - 0.15f * (dist - effectiveRange));
			}
		}
		return Math.max(0.05f, acc);
	}

	/** SPSEXPD: 枪械攻速极快——连射同目标不耗回合，换目标才消耗一个（较短的）回合。 */
	@Override
	public float delayFactor(Char owner) {
		return Math.max(0.2f, super.delayFactor(owner) * 0.5f);
	}

	/** 后坐缓冲：力量每差完美阈值 1 点，每次击发后需额外等待 0.5 回合。 */
	protected float shotBuffer(Hero hero) {
		if (hero == null) return 0f;
		int lack = perfectSTR - hero.STR();
		return lack > 0 ? lack * 0.5f : 0f;
	}

	/** 计算本次击发的回合代价，并更新连射/缓冲状态。 */
	protected float shotDelay(Hero hero, int cell) {
		Char target = Actor.findChar(cell);
		boolean sameTarget = target != null && target.pos == lastTargetPos;

		float buffer = shotBuffer(hero);
		float wait = Math.max(0f, lastShotTime + buffer - Game.timeTotal);
		float delay = wait + (sameTarget ? 0f : delayFactor(hero));

		lastTargetPos = target != null ? target.pos : -1;
		lastShotTime = Game.timeTotal + delay;
		return delay;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (canShoot(hero)) {
			actions.add(AC_SHOOT);
			actions.add(AC_AIM);
		}
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
				GameScene.selectCell(targetShooter);
			}
		} else if (AC_AIM.equals(action)) {
			curUser = hero;
			if (!canShoot(hero)) {
				GLog.i(Messages.get(GunWeapon.class, "need_to_equip"));
			} else if (charge <= 0) {
				reload(hero, true);
			} else {
				GameScene.selectCell(aimShooter);
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
		int loaded = reloadMagazine();
		if (loaded <= 0) {
			GLog.n(Messages.get(GunWeapon.class, "empty"));
			return;
		}
		float time = reloadTime(hero, automatic, loaded);
		if (hero.sprite != null) hero.sprite.showStatus(CharSprite.DEFAULT, Messages.get(GunWeapon.class, "reloading"));
		hero.spendAndNext(Math.max(0.1f, time));
	}

	/** Reloads without scheduling a turn, also used by deterministic tests. */
	public int reloadMagazine() {
		int missing = Math.max(0, fullCharge - charge);
		if (missing == 0 || reserveAmmo <= 0) return 0;
		int want = reloadMode == GunArchetype.Reload.SINGLE ? 1 : missing;
		int loaded = Math.min(want, reserveAmmo);
		charge += loaded;
		updateQuickslot();
		return loaded;
	}

	protected float reloadTime(Hero hero, boolean automatic, int loaded) {
		if (hero.subClass == HeroSubClass.AGENT) return 0.5f;
		return baseReloadTime;
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

	/** SPSEXPD: 充能型枪械的每回合自然回充（本回合开过火则不回充）。 */
	public void gainCharge() {
		if (regenPerTurn <= 0f) return;
		if (firedSinceLastCharge) {
			firedSinceLastCharge = false;
			return;
		}
		regenProgress += regenPerTurn;
		while (regenProgress >= 1f) {
			regenProgress -= 1f;
			if (charge < fullCharge) charge++;
		}
		updateQuickslot();
	}

	boolean consumeRound() {
		//SPSEXPD: 无限弹药袋生效期间不消耗弹匣
		if (EndlessAmmo.isActive(curUser)) return true;
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
		if (Dungeon.level != null && attacker != null && defender != null && defender.isAlive()) {
			int opposite = defender.pos + defender.pos - attacker.pos;
			Ballistica trajectory = new Ballistica(defender.pos, opposite, Ballistica.MAGIC_BOLT);
			pd.items.equipment.wands.WandOfBlastWave.throwChar(defender, trajectory, 1, false, false, this);
			if (causesVertigo()) Buff.prolong(defender, Vertigo.class, 3f);
		}
		//SPSEXPD: 走 Weapon.proc —— 让枪械附魔（含穿甲/弹射）与识别进度正常生效
		return super.proc(attacker, defender, damage);
	}

	/** SPSEXPD: 随机附魔时排除近战专属附魔，并有机会获得枪械专属附魔。 */
	@Override
	public Weapon enchant() {
		Class<? extends Enchantment> old = enchantment != null ? enchantment.getClass() : null;
		if (Random.Int(10) < 3 && !Puncturing.class.equals(old) && !Ricochet.class.equals(old)) {
			return enchant(Random.Int(2) == 0 ? new Puncturing() : new Ricochet());
		}
		Enchantment ench;
		int guard = 0;
		do {
			ench = Enchantment.random(old);
			guard++;
		} while (guard < 20 && isIncompatible(ench));
		return enchant(ench);
	}

	protected boolean isIncompatible(Enchantment ench) {
		if (ench == null) return false;
		for (Class<?> c : INCOMPATIBLE_ENCHANTS) {
			if (c == ench.getClass()) return true;
		}
		return false;
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

	/** 一次击发：按单次弹药数连续发射，每颗子弹各自独立结算。 */
	protected void fire(Hero hero, int cell) {
		if (hero == null || charge <= 0) return;
		float delay = shotDelay(hero, cell);
		firedSinceLastCharge = true;

		int shots = Math.min(shotsPerFire, charge);
		for (int i = 0; i < shots; i++) {
			GunAmmo shot = new GunAmmo();
			shot.preDelay = (i == 0) ? delay : 0f;
			shot.cast(hero, cell);
		}

		GunFire.alertNearby(hero.pos, noiseRadius);
	}

	private final CellSelector.Listener targetShooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || curUser == null) return;
			if (Actor.findChar(target) == null) {
				GLog.i(Messages.get(GunWeapon.class, "need_target"));
				return;
			}
			fire(curUser, target);
		}
		@Override public String prompt() { return Messages.get(GunWeapon.class, "prompt"); }
	};

	private final CellSelector.Listener aimShooter = new CellSelector.Listener() {
		@Override public void onSelect(Integer target) {
			if (target == null || curUser == null) return;
			fire(curUser, target);
		}
		@Override public String prompt() { return Messages.get(GunWeapon.class, "prompt_aim"); }
	};

	public class GunAmmo extends MissileWeapon {
		/** 本次击发的回合代价（由 GunWeapon.fire 预先算好）。 */
		float preDelay = -1f;

		{
			image = EquipmentEquipWeaponUniqueWeaponDict.PAINTBALL_GUN;
			tier = Math.max(1, gunTier);
			ACC = 1.3f;
			spawnedForEffect = true;
		}
		@Override public int min(int lvl) { return GunWeapon.this.min(); }
		@Override public int max(int lvl) { return GunWeapon.this.max(); }
		@Override public int STRReq(int lvl) { return Math.max(1, GunWeapon.this.STRReq()); }
		@Override public int damageRoll(Char owner) { return GunWeapon.this.gunDamageRoll(owner); }
		@Override public float castDelay(Char user, int cell) {
			return preDelay >= 0f ? preDelay : super.castDelay(user, cell);
		}
		@Override public float delayFactor(Char owner) {
			if (owner instanceof Hero && ((Hero)owner).subClass == HeroSubClass.AGENT && ((Hero)owner).justMoved) return 0.1f;
			return GunWeapon.this.delayFactor(owner);
		}
		@Override public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
			return GunWeapon.this.hasEnchant(type, owner);
		}
		@Override public float accuracyFactor(Char owner, Char target) {
			return GunWeapon.this.accuracyFactor(owner, target);
		}
		@Override protected void onThrow(int cell) {
			if (curUser == null) return;
			GunFire.resolveShot(curUser, this, cell, effectiveRange, perfectSTR, pierce);
		}
		@Override protected void rangedHit(Char enemy, int cell) { }
		@Override protected void rangedMiss(int cell) { }
		@Override public int proc(Char attacker, Char defender, int damage) {
			if (ammo != null) ammo.onHit(attacker, defender, damage);
			if (addsEnergyDamage()) {
				defender.damage(gunDamageRoll(attacker) / 2, DamageType.ENERGY_DAMAGE);
			}
			//SPSEXPD: 转发给枪本体，使枪上的附魔（穿甲/弹射等）生效
			return GunWeapon.this.proc(attacker, defender, damage);
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
