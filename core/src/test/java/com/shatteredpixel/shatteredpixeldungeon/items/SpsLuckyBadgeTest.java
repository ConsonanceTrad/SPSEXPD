package com.shatteredpixel.shatteredpixeldungeon.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AflyBless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.misc.LuckyBadge;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;

/** Headless checks for the complete legacy luck formula and its loop guard. */
public final class SpsLuckyBadgeTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x4C55434B59535053L);
		try {
			testLuckSources();
			testProbabilitiesAndBound();
			System.out.println("SPS幸运系统测试通过：徽章、士兵、超级明星、Afly祝福、稀有奖励概率和额外物品上限均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testLuckSources() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		hero.heroClass = HeroClass.WARRIOR;
		hero.subClass = HeroSubClass.NONE;
		check(LuckyBadge.luckBonus(hero) == 0, "空背包仍产生运气加成");

		LuckyBadge badge = (LuckyBadge)new LuckyBadge().level(4);
		hero.belongings.backpack.items.add(badge);
		check(badge.image == ItemSpriteSheet.LUCKY_BADGE, "幸运徽章未使用旧版独立图标");
		check(LuckyBadge.luckBonus(hero) == 4, "徽章等级没有计入运气");

		hero.heroClass = HeroClass.SOLDIER;
		hero.subClass = HeroSubClass.SUPERSTAR;
		Buff.affect(hero, AflyBless.class);
		check(LuckyBadge.luckBonus(hero) == 15, "士兵、超级明星或Afly祝福加成错误");
	}

	private static void testProbabilitiesAndBound() {
		check(close(LuckyBadge.rareRewardChance(0), 0f), "零运气仍会生成稀有奖励");
		check(close(LuckyBadge.rareRewardChance(5), 1f - (float)Math.pow(0.95f, 5)),
				"稀有奖励概率不符合旧版公式");
		check(close(LuckyBadge.additionalItemChance(0), 0.3f), "基础额外物品概率错误");
		check(close(LuckyBadge.additionalItemChance(10), 0.8f)
				&& close(LuckyBadge.additionalItemChance(100), 0.8f), "额外物品运气没有在10点封顶");

		Hero hero = new Hero();
		Dungeon.hero = hero;
		hero.heroClass = HeroClass.SOLDIER;
		for (int i = 0; i < 20000; i++) {
			int extra = LuckyBadge.rollExtraItems(hero);
			check(extra >= 0 && extra <= LuckyBadge.MAX_EXTRA_ITEMS, "额外物品循环突破安全上限");
		}
	}

	private static boolean close(float left, float right) {
		return Math.abs(left - right) < 0.00001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
