package pd.items.equipment.weapon.melee.start;

import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;

public final class SpsLegacyStartMeleeStatsTest {

	public static void main(String[] args) {
		check(new BeastKnive(), 4, 10, 1, 2);
		check(new BraveBook(), 4, 14, 2, 3);
		check(new BunnyDagger(), 5, 10, 2, 2);
		check(new BunnySpanner(), 8, 15, 3, 4);
		check(new DemonBlade(), 7, 14, 3, 4);
		check(new DiamondPickaxe(), 2, 8, 2, 3);
		check(new EleKatana(), 5, 20, 2, 3);
		check(new HolyMace(), 8, 20, 2, 5);
		check(new LinkSword(), 1, 5, 2, 4);
		check(new PixelTorch(), 3, 15, 1, 2);
		check(new Whisk(), 8, 15, 1, 2);
		check(new XSaber(), 6, 10, 3, 4);
		System.out.println("SPS起始近战武器测试通过：12件武器的0至2级基础伤害与旧版强化调用链一致。");
	}

	private static void check(NormalMeleeWeapon weapon, int baseMin, int baseMax,
			int minGrowth, int maxGrowth) {
		for (int level = 0; level <= 2; level++) {
			int expectedMin = baseMin + minGrowth * level;
			int expectedMax = baseMax + maxGrowth * level;
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
