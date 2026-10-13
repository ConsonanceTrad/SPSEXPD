/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.guns;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.EndlessAmmo;
import pd.actors.buffs.MechArmor;
import pd.actors.buffs.TargetShoot;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.effects.Speck;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.Magazine;
import pd.items.equipment.rings.RingOfSharpshooting;
import pd.items.equipment.weapon.SpsRangedWeapon;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.enchantments.Puncturing;
import pd.items.equipment.weapon.enchantments.Ricochet;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.noosa.Game;
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
			.t("ac_reload", "装填")
			.t("reloading", "装填中……")
			.t("need_to_equip", "你需要先装备这件武器才能射击。")
			.t("need_target", "那里没有可以射击的目标。")
			.t("full", "弹匣已满。")
			.t("empty", "你没有可以装填的通用弹匣。")
			.t("prompt", "选择射击目标")
			.t("prompt_aim", "选择射击方向")
			.t("stats_1", "这把 _%1$s_ 有 _%2$d_ 发弹匣容量，单次击发 _%3$d_ ，造成 _%4$d~%5$d_ 点伤害")
			.t("stats_2", "你需要 _%1$d_ 点力量以完全克服其后坐力，以你目前的力量而言，每次击发需要 _%2$s_ 回合的缓冲时间。")
			.t("stats_3", "装填耗时 _%1$s_ ，精准范围 _%2$d_ ，噪声半径 _%3$d_ 。")
			.t("charge", "弹匣：%1$d/%2$d");
	}



	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_AIM = "AIM";
	public static final String AC_RELOAD = "RELOAD";

	private static final String CHARGE = "charge";

	/** SPSEXPD: 枪械不适用（近战专属）的附魔，随机附魔时排除。 */
	public static final Class<?>[] INCOMPATIBLE_ENCHANTS = new Class<?>[]{
			pd.items.equipment.weapon.enchantments.Vorpal.class,
			pd.items.equipment.weapon.enchantments.Grim.class,
			pd.items.equipment.weapon.enchantments.Vampiric.class,
			pd.items.equipment.weapon.enchantments.Blocking.class,
			pd.items.equipment.weapon.enchantments.Blooming.class,
	};

	/** SPSEXPD: 枪械专属附魔（由原特殊弹药效果转化而来），随机附魔时优先抽取。 */
	public static final Class<? extends Enchantment>[] GUN_ENCHANTS = new Class[]{
			Puncturing.class,
			Ricochet.class,
			pd.items.equipment.weapon.enchantments.Starfall.class,
			pd.items.equipment.weapon.enchantments.Hypnotic.class,
			pd.items.equipment.weapon.enchantments.Jackpot.class,
	};

	protected GunArchetype archetype;
	/** 弹匣容量（一次可连续击发的发数上限）。 */
	protected int fullCharge;
	/** 单次击发消耗的弹药数（冲锋枪 = 4）。 */
	protected int shotsPerFire;
	/** 零后坐缓冲所需的力量阈值。 */
	protected int perfectSTR;
	/** 精准射击范围（超出后命中率与弹道偏差变差）。 */
	protected int effectiveRange;

	protected int charge;

	/** 装填耗时（回合）。 */
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
		this.fullCharge = archetype.magazine;
		this.shotsPerFire = archetype.shotsPerFire;
		this.perfectSTR = archetype.perfectSTR;
		this.effectiveRange = archetype.range;
		this.baseReloadTime = archetype.reloadTime;
		this.noiseRadius = archetype.noise;
		this.regenPerTurn = archetype.regenPerTurn;
		this.pierce = archetype.pierce;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}

	//SPSEXPD: 不再分阶位——伤害与力量需求直接取自类型基准
	@Override public int min(int lvl) { return archetype.minBase + lvl; }
	@Override public int max(int lvl) { return archetype.maxBase + archetype.maxGrowth * lvl; }
	//SPSEXPD: 力量需求随等级下降（与近战/弓/护甲统一）
	@Override public int STRReq(int lvl) {
		int lv = Math.max(0, lvl);
		return archetype.strBase - (int)(Math.sqrt(8 * lv + 1) - 1) / 2;
	}
	@Override public int damageRoll(Char owner) { return 0; }
	@Override public boolean isUpgradable() { return true; }

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
		} else {
			super.execute(hero, action);
		}
	}

	protected boolean canShoot(Hero hero) {
		//SPSEXPD: 枪械必须装备才能射击（不再允许特工未装备时射击）
		return isEquipped(hero);
	}
	protected boolean canReload(Hero hero) { return true; }

	/**
	 * SPSEXPD: 点击敌人时的枪械自动射击（枪械不再有近战功能，装备枪械后一律转为射击）。
	 * 优先使用「已装备且有弹药」的枪械：先主手，再副手（主副都是枪械时优先主手）；
	 * 两把都没有弹药时不接管，回落为徒手攻击。返回 true 表示已用枪械开火。
	 */
	public static boolean autoAttack(Hero hero, Char enemy) {
		if (hero == null || enemy == null || Dungeon.level == null) return false;
		if (!hero.ready) return false;

		GunWeapon gun = readyGun(hero, hero.belongings.weapon());
		if (gun == null) gun = readyGun(hero, hero.belongings.secondWep());
		if (gun == null) return false;

		curUser = hero;
		gun.fire(hero, enemy.pos);
		return true;
	}

	private static GunWeapon readyGun(Hero hero, KindOfWeapon weapon) {
		if (!(weapon instanceof GunWeapon)) return null;
		GunWeapon gun = (GunWeapon)weapon;
		if (!gun.isEquipped(hero)) return null;
		if (gun.charge() <= 0) return null;
		return gun;
	}

	private void reload(Hero hero, boolean automatic) {
		if (charge >= fullCharge) {
			GLog.n(Messages.get(GunWeapon.class, "full"));
			return;
		}
		if (!reloadMagazine(hero)) {
			GLog.n(Messages.get(GunWeapon.class, reloadHintKey()));
			return;
		}
		float time = reloadTime(hero, automatic);
		if (hero.sprite != null) hero.sprite.showStatus(CharSprite.DEFAULT, Messages.get(GunWeapon.class, "reloading"));
		hero.spendAndNext(Math.max(0.1f, time));
	}

	/**
	 * SPSEXPD: 装填一次把弹匣填满，不排回合（供确定性测试调用）。
	 * 无论枪械容量与单次击发数，一次装填一律填满。
	 */
	public boolean reloadMagazine(Hero hero) {
		if (charge >= fullCharge) return false;
		if (!consumeReload(hero)) return false;
		charge = fullCharge;
		updateQuickslot();
		return true;
	}

	/**
	 * SPSEXPD: 一次装填的耗材来源；默认消耗一个「通用弹匣」（无限弹药袋生效期间无需消耗）。
	 * 特殊枪械覆写此方法改用炼金能量 / 露珠 / 炸弹 / 枯枝等。
	 */
	protected boolean consumeReload(Hero hero) {
		if (hero == null) return false;
		if (EndlessAmmo.isActive(hero)) return true;
		Item magazine = hero.belongings.getItem(Magazine.class);
		if (magazine == null || !hero.belongings.backpack.contains(magazine)) return false;
		return magazine.detach(hero.belongings.backpack) != null;
	}

	/** SPSEXPD: 耗材不足时的提示文案键（子类覆写成自己的键）。 */
	protected String reloadHintKey() { return "empty"; }

	/** SPSEXPD: 子弹命中时的额外效果钩子（特殊枪械覆写，如露珠炮对亡灵加成、爆弹发射器引爆）。 */
	protected void onBulletHit(Char attacker, Char defender, int damage) { }

	protected float reloadTime(Hero hero, boolean automatic) {
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
		//SPSEXPD: 枪械不再有近战功能（原击退与眩晕已彻底移除）
		return super.proc(attacker, defender, damage);
	}

	/** SPSEXPD: 随机附魔时排除近战专属附魔，并有机会获得枪械专属附魔。 */
	@Override
	public Weapon enchant() {
		Class<? extends Enchantment> old = enchantment != null ? enchantment.getClass() : null;
		if (Random.Int(10) < 3 && !isGunEnchant(old)) {
			Class<? extends Enchantment> pick = Random.element(java.util.Arrays.asList(GUN_ENCHANTS));
			return enchant(render.utils.serialize.Reflection.newInstance(pick));
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

	protected boolean isGunEnchant(Class<? extends Enchantment> type) {
		if (type == null) return false;
		for (Class<?> c : GUN_ENCHANTS) {
			if (c == type) return true;
		}
		return false;
	}

	protected boolean addsEnergyDamage() { return true; }

	/** SPSEXPD: 弹匣内是否还有弹药（无弹药时装备枪械改走徒手攻击）。 */
	public boolean hasAmmo() { return charge > 0; }

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
		return desc() + "\n\n"
				+ Messages.get(GunWeapon.class, "stats_1",
						archetype.displayName(), fullCharge, shotsPerFire, min(), max())
				+ "\n\n"
				+ Messages.get(GunWeapon.class, "stats_2", perfectSTR, num(shotBuffer(Dungeon.hero)))
				+ "\n\n"
				+ Messages.get(GunWeapon.class, "stats_3", num(baseReloadTime), effectiveRange, noiseRadius)
				+ "\n\n"
				+ Messages.get(GunWeapon.class, "charge", charge, fullCharge);
	}

	/** SPSEXPD: 整数就不带小数点（1 / 0.5）。 */
	private static String num(float v) {
		return v == Math.round(v) ? String.valueOf(Math.round(v)) : String.valueOf(v);
	}

	@Override public String status() { return levelKnown ? charge + "/" + fullCharge : null; }

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
			tier = 1; //SPSEXPD: 枪弹不再是分阶武器
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
			if (defender != null && defender.isAlive()) {
				GunWeapon.this.onBulletHit(attacker, defender, damage);
			}
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
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(fullCharge, bundle.getInt(CHARGE)));
	}
}
