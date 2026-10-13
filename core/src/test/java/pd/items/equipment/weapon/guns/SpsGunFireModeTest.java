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

	public static void main(String[] args) {
		Random.pushGenerator(0x5350534746495245L);
		try {
			testArchetypes();
			testMagazineAndShots();
			testReloadModes();
			testSpread();
			testRecoilBuffer();
			testEnchantWhitelist();
			testEquipAndDualRules();
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
		check(new Sling().fullCharge() == 1 && new ToyGun().fullCharge() == 10, "投石索或玩具枪弹匣错误");
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

	/** 出膛偏差（流弹）：范围内且力量达标 = 0；超出范围或力量不足则增大。 */
	private static void testSpread() {
		check(GunFire.spreadOffset(3, 4, 12, 12) == 0, "精准范围内且有力量时不应有偏差");
		check(GunFire.spreadOffset(10, 4, 12, 12) > 0, "超出精准范围后没有产生偏差");
		check(GunFire.spreadOffset(3, 4, 12, 8) == 3, "力量不足产生的偏差错误（应为 4，封顶 3）");
		check(GunFire.spreadOffset(3, 4, 12, 4) == 3, "偏差没有封顶到 3 格");
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

	/** 充能型枪械：未开火时每回合自然回充 0.5 发（显示取整）。 */
	private static void testChargeGun() {
		check(GunOfSoldier.MAGAZINE == 3 && GunOfSoldier.REGEN_PER_TURN == 0.5f, "电磁手枪弹匣或回充常数错误");
		GunOfSoldier gun = new GunOfSoldier();
		gun.gainCharge();
		check(gun.charge() == 0, "半发回充不应立刻增加整发");
		gun.gainCharge();
		check(gun.charge() == 1, "两回合回充应累积为 1 发");
		for (int i = 0; i < 10; i++) gun.gainCharge();
		check(gun.charge() == GunOfSoldier.MAGAZINE, "电磁手枪回充没有封顶");
		check(gun.consumeShot() && gun.charge() == GunOfSoldier.MAGAZINE - 1, "电磁手枪消耗一发失败");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsGunFireModeTest() { }
}
