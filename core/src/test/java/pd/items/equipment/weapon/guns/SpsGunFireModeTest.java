package pd.items.equipment.weapon.guns;

import pd.actors.hero.Hero;
import pd.items.equipment.weapon.enchantments.Puncturing;
import pd.items.equipment.weapon.enchantments.Ricochet;
import pd.items.equipment.weapon.enchantments.Vorpal;
import pd.items.misc.GunOfSoldier;
import render.utils.math.Random;

/**
 * SPSEXPD: 枪械核心机制的无头校验——类型基准、单次击发、逐颗/整匣装填、
 * 出膛偏差（流弹）、后坐缓冲、附魔白名单、充能型自然回充。
 */
public final class SpsGunFireModeTest {

	public static void main(String[] args) throws Exception {
		Random.pushGenerator(0x5350534746495245L);
		try {
			testArchetypes();
			testMagazineAndShots();
			testReloadModes();
			testReloadWaitsForTime();
			testHitChance();
			testRecoilBuffer();
			testEnchantWhitelist();
			testEquipAndDualRules();
			testAmmoDamage();
			testChargeGun();
			System.out.println("枪械核心机制测试通过：类型基准、单次击发、装填方式、出膛偏差、后坐缓冲、附魔白名单与充能回充均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	/** 类型基准：弹匣 / 单次击发 / 完美力量 / 精准范围 / 噪声 / 自充 / 穿透。 */
	private static void testArchetypes() {
		check(GunArchetype.PISTOL.magazine == 7 && GunArchetype.PISTOL.shotsPerFire == 1
						&& GunArchetype.PISTOL.perfectSTR == 12 && GunArchetype.PISTOL.range == 4
						&& GunArchetype.PISTOL.noise == 4 && GunArchetype.PISTOL.pierce == 0,
				"手枪基准参数错误");
		check(GunArchetype.SMG.magazine == 20 && GunArchetype.SMG.shotsPerFire == 4
						&& GunArchetype.SMG.perfectSTR == 16 && GunArchetype.SMG.noise == 5,
				"冲锋枪基准参数错误");
		check(GunArchetype.SHOTGUN.magazine == 4
						&& GunArchetype.SHOTGUN.perfectSTR == 18 && GunArchetype.SHOTGUN.range == 2
						&& GunArchetype.SHOTGUN.noise == 7,
				"霰弹枪基准参数错误");
		check(GunArchetype.SNIPER.magazine == 5 && GunArchetype.SNIPER.perfectSTR == 20
						&& GunArchetype.SNIPER.range == 12 && GunArchetype.SNIPER.pierce == Integer.MAX_VALUE,
				"狙击枪基准参数错误");
		check(GunArchetype.CHARGE.magazine == 3 && GunArchetype.CHARGE.perfectSTR == 10
						&& GunArchetype.CHARGE.regenPerTurn == 0.5f,
				"电磁手枪基准参数错误");
	}

	/** 重铸后的五把枪确实按类型基准工作。 */
	private static void testMagazineAndShots() {
		check(new GunA().fullCharge() == 7 && new GunA().shotsPerFire == 1, "手枪弹匣或单次击发错误");
		check(new GunB().fullCharge() == 20 && new GunB().shotsPerFire == 4, "冲锋枪弹匣或单次击发错误");
		check(new GunC().fullCharge() == 4, "霰弹枪弹匣错误");
		check(new GunD().fullCharge() == 5 && new GunD().pierce == Integer.MAX_VALUE, "狙击枪弹匣或穿透错误");
		check(new GunE().fullCharge() == 6 && new GunE().pierce == Integer.MAX_VALUE, "至高无上弹匣或穿透错误");
		check(new ToyGun().fullCharge() == 10, "玩具枪弹匣错误");
		//SPSEXPD: 物品图标的力量角标显示完美力量（零后坐缓冲所需力量）
		check(new GunA().perfectSTR() == 12 && new GunB().perfectSTR() == 16 && new GunD().perfectSTR() == 20,
				"枪械完美力量（力量角标显示值）错误");
	}

	/**
	 * SPSEXPD: 装填要等装填时间结束才补满——装填期间弹匣为空（阻止提前射击），
	 * 装填时间走完（Reloading 结束）后才由 finishReload() 补满。
	 */
	private static void testReloadWaitsForTime() throws java.io.IOException {
		Hero hero = new Hero();
		pd.Dungeon.hero = hero;
		GunA gun = new GunA();
		hero.belongings.weapon = gun;

		//装填开始：弹药交出去装填，弹匣归 0 并进入「等待装填」
		gun.charge(0);
		gun.pendingReload = true;
		check(gun.isPendingReload(), "装填开始后没有进入等待装填状态");
		check(!gun.hasAmmo(), "装填期间枪械仍然可以射击");

		//装填时间走完才补满
		gun.finishReload();
		check(!gun.isPendingReload(), "装填完成后仍处于等待装填状态");
		check(gun.charge() == gun.fullCharge(), "装填完成后弹匣没有补满");
		check(gun.hasAmmo(), "装填完成后枪械仍然无法射击");

		//没有在等待装填时 finishReload 不应改动弹匣
		gun.charge(1);
		gun.finishReload();
		check(gun.charge() == 1, "未在装填时 finishReload 意外改动了弹匣");

		//真实路径：装填 buff 结束时（装填时间走完）由 buff 负责补满
		GunA gun2 = new GunA();
		gun2.charge(0);
		gun2.pendingReload = true;
		pd.actors.buffs.Reloading reloading = new pd.actors.buffs.Reloading();
		reloading.bind(gun2);
		reloading.finish();
		check(gun2.charge() == gun2.fullCharge(), "装填 buff 结束时没有补满弹匣");
		check(!gun2.isPendingReload(), "装填 buff 结束后仍处于等待装填状态");

		//双持：装填 buff 结束时两把枪都要补满（buff 可能已被后一把枪复用、绑定被覆盖）
		Hero hero3 = new Hero();
		pd.Dungeon.hero = hero3;
		GunA mainGun = new GunA();
		GunB offGun = new GunB();
		hero3.belongings.weapon = mainGun;
		hero3.belongings.secondWep = offGun;
		mainGun.charge(0); mainGun.pendingReload = true;
		offGun.charge(0); offGun.pendingReload = true;
		pd.actors.buffs.Reloading reloading2 = new pd.actors.buffs.Reloading();
		reloading2.finish();
		check(mainGun.charge() == mainGun.fullCharge() && offGun.charge() == offGun.fullCharge(),
				"双持时装填结束没有补满两把枪");

		//装填耗材不足的提示键必须都能在英文模板里取到（曾出现 TEXT NOT FOUND）
		String en = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(
				"messages/items/en/items.properties")), java.nio.charset.StandardCharsets.UTF_8);
		for (String key : new String[]{"guns.gunweapon.full", "guns.gunweapon.empty", "guns.gunweapon.reloading",
				"guns.dewcannon.no_dew", "guns.bomblauncher.no_bomb", "melee.crossbow.no_twig"}) {
			check(en.contains("items.equipment.weapon." + key + "="),
					"装填提示键在英文模板里缺失：" + key);
		}
	}

	/** 装填：一个通用弹匣装满任意容量。 */
	private static void testReloadModes() {
		Hero hero = new Hero();
		pd.Dungeon.hero = hero;
		hero.belongings.backpack.items.add(new pd.items.Magazine());
		GunC shotgun = new GunC();
		check(shotgun.reloadMagazine(hero) && shotgun.charge() == 4, "霰弹枪没有被一个通用弹匣装满");

		GunA pistol = new GunA();
		check(!pistol.reloadMagazine(hero), "无通用弹匣时仍能装填");
	}

	/** 弹道精准度（不再有出膛偏差）：精准射击范围内必定命中，超出后每格衰减 17%，最低 5%。 */
	private static void testHitChance() {
		final int range = GunArchetype.PISTOL.range;
		final float decay = GunFire.HIT_DECAY_PER_TILE;
		final float floor = GunFire.MIN_HIT_CHANCE;

		check(GunFire.hitChance(range - 1, range, decay, floor) == 1f, "精准射击范围内不是必定命中");
		check(GunFire.hitChance(range, range, decay, floor) == 1f, "精准射击范围边界不是必定命中");
		check(GunFire.hitChance(range + 1, range, decay, floor) < 1f, "超出精准范围后命中率没有下降");

		float near = GunFire.hitChance(range + 1, range, decay, floor);
		float far = GunFire.hitChance(range + 3, range, decay, floor);
		check(Math.abs(near - 0.83f) < 0.001f, "超出 1 格的命中率不是 83%（每格降 17%）：" + near);
		check(far < near, "命中率没有随超出距离继续下降");
		check(GunFire.hitChance(60, range, decay, floor) == floor, "命中率没有收敛到下限 5%");
	}

	/** 后坐缓冲：完美阈值越高惩罚越重；达阈值时为 0。 */
	private static void testRecoilBuffer() {
		Hero hero = new Hero();
		int str = hero.STR();
		GunA pistol = new GunA();
		float expected = Math.max(0, pistol.perfectSTR - str) * 0.5f;
		check(Math.abs(pistol.shotBuffer(hero) - expected) < 0.00001f, "后坐缓冲公式错误");
		for (GunArchetype type : new GunArchetype[]{GunArchetype.PISTOL, GunArchetype.SMG,
				GunArchetype.SHOTGUN, GunArchetype.SNIPER}) {
			check(type.perfectSTR >= type.strBase + 2, "完美阈值没有明显高于力量需求基准");
		}
	}

	/** 附魔白名单：近战专属附魔不适用于枪械，枪械专属附魔可用。 */
	private static void testEnchantWhitelist() {
		GunA gun = new GunA();
		check(gun.isIncompatible(new Vorpal()), "斩首附魔不应适用于枪械");
		check(!gun.isIncompatible(new Puncturing()), "穿甲附魔应适用于枪械");
		check(!gun.isIncompatible(new Ricochet()), "弹射附魔应适用于枪械");
	}

	/** SPSEXPD: 枪械必须装备才能射击；双持时枪械豁免力量惩罚。 */
	private static void testEquipAndDualRules() {
		Hero hero = new Hero();
		pd.Dungeon.hero = hero;
		GunA gun = new GunA();
		check(!gun.canShoot(hero), "未装备的枪械仍然可以射击");
		check(pd.items.SecondaryEquip.weaponSTRReq(gun, 12) == 12,
				"枪械的力量需求被双持惩罚加成了");
		check(!pd.items.SecondaryEquip.dualWieldTooHeavy(hero, gun, new GunA()),
				"枪械被误判为双持难以掌控");

		//SPSEXPD: 装备的枪械没有弹药时不参与近战，改为徒手攻击
		hero.belongings.weapon = gun;
		gun.charge(0);
		check(hero.damageRoll() > 0, "无弹药的枪械没有回落为徒手攻击");
		gun.charge(1);
		check(hero.damageRoll() == 0, "有弹药的枪械参与了近战伤害");
	}

	/**
	 * SPSEXPD: 枪弹的伤害与命中——伤害必须走枪械伤害公式（min~max），
	 * 命中基准必须足够高（对齐洛克手炮 MegaAmmo ACC=100f 的做法），否则实战会大量落空、看起来「没有伤害」。
	 */
	private static void testAmmoDamage() {
		Hero hero = new Hero();
		pd.Dungeon.hero = hero;
		GunA gun = new GunA();
		hero.belongings.weapon = gun;
		gun.charge(gun.fullCharge());

		GunWeapon.GunAmmo shot = gun.new GunAmmo();
		//SPSEXPD: 凭空弹体必须 quantity=1——否则 Item.detach 会走 split()，而 Reflection 造不出非静态内部类（返回 null），
		//Item.cast 里的 `if (i != null) i.onThrow(cell)` 被跳过，子弹飞出去却完全没有伤害结算
		check(shot.defaultQuantity() == 1,
				"枪弹默认堆叠不是 1：detach 会走 split() 失败，onThrow 永远不会被调用（射击没有伤害）");
		for (int i = 0; i < 20; i++) {
			int dmg = shot.damageRoll(hero);
			check(dmg >= gun.min() && dmg <= gun.max(),
					"枪弹伤害超出 " + gun.min() + "~" + gun.max() + " 范围：" + dmg);
		}
		check(shot.accuracyFactor(hero, hero) > 50f, "枪弹命中基准过低，实战会大量落空");

		//SPSEXPD: 一次命中只结算一次伤害——GunAmmo.proc 必须原样返回伤害（不再拆分法术伤害）
		check(shot.proc(hero, hero, 7) == 7, "枪弹 proc 改动了伤害（一次命中只应结算一次）");
	}

	/** 充能型枪械：未开火时每回合自然回充 0.5 发（显示取整）。 */
	private static void testChargeGun() {
		check(GunOfSoldier.MAGAZINE == 3 && GunArchetype.CHARGE.regenPerTurn == 0.5f,
				"电磁手枪弹匣或回充常数错误");
		GunOfSoldier gun = new GunOfSoldier();
		gun.gainCharge();
		check(gun.charge() == 0, "半发回充不应立刻增加整发");
		gun.gainCharge();
		check(gun.charge() == 1, "两回合回充应累积为 1 发");
		for (int i = 0; i < 10; i++) gun.gainCharge();
		check(gun.charge() == GunOfSoldier.MAGAZINE, "电磁手枪回充没有封顶");
		check(gun.fullCharge() == GunOfSoldier.MAGAZINE, "电磁手枪弹匣容量错误");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsGunFireModeTest() { }
}
