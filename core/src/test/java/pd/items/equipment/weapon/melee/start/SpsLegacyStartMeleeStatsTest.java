package pd.items.equipment.weapon.melee.start;

import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;

public final class SpsLegacyStartMeleeStatsTest {

	public static void main(String[] args) {
		//SPSEXPD: 数值曲线统一到破碎式（MeleeWeapon）后，伤害只由 tier 决定，
		// 起始武器各自的旧版 baseMin/baseMax 数值表不再参与计算。
		check(new BeastKnive(), 1);
		check(new BraveBook(), 2);
		check(new BunnyDagger(), 1);
		check(new BunnySpanner(), 1);
		check(new DemonBlade(), 2);
		check(new DiamondPickaxe(), 3);
		check(new EleKatana(), 2);
		check(new HolyMace(), 3);
		check(new LinkSword(), 1);
		check(new PixelTorch(), 2);
		check(new Whisk(), 3);
		check(new XSaber(), 1);
		System.out.println("SPS起始近战武器测试通过：12件武器的0至2级伤害符合破碎式统一曲线。");
	}

	private static void check(NormalMeleeWeapon weapon, int tier) {
		for (int level = 0; level <= 2; level++) {
			int expectedMin = tier + level;
			int expectedMax = 5 * (tier + 1) + level * (tier + 1);
			if (weapon.min(level) != expectedMin || weapon.max(level) != expectedMax) {
				throw new AssertionError(weapon.getClass().getSimpleName() + " +" + level
						+ "伤害错误：实际 " + weapon.min(level) + "-" + weapon.max(level)
						+ "，预期 " + expectedMin + "-" + expectedMax);
			}
		}
	}

	private SpsLegacyStartMeleeStatsTest() {
	}
}
