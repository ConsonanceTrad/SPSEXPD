package pd.items.weapon.missiles.throwing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.BanditKing;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.Game;
import watabou.utils.Bundle;

/** Headless checks for the Bandit King drop and immediate-return boomerang. */
public final class SpsBoomerangTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		testLegacyStatsAndSave();
		testBanditKingDrop();
		testImmediateReturn();
		System.out.println("SPS回旋镖测试通过：旧版数值、强化存档、盗贼王掉落及命中或落空后的立即返还均正常。");
	}

	private static void testLegacyStatsAndSave() {
		Boomerang boomerang = new Boomerang();
		check(boomerang.defaultQuantity() == 1 && !boomerang.stackable && boomerang.unique,
				"回旋镖不是唯一单件物品");
		check(boomerang.min(0) == 3 && boomerang.max(0) == 6 && boomerang.STRReq(0) == 10,
				"回旋镖基础数值与旧版源码不一致");
		check(boomerang.min(4) == 7 && boomerang.max(4) == 14,
				"回旋镖升级成长与旧版源码不一致");
		check(boomerang.isUpgradable() && boomerang.isReinforced()
				&& boomerang.durabilityPerUse(0) == 0f, "回旋镖强化或无限耐久错误");
		check(boomerang.image == ItemSpriteSheet.LEGACY_BOOMERANG, "回旋镖没有使用旧版独立图标");

		boomerang.level(3);
		Bundle saved = new Bundle();
		boomerang.storeInBundle(saved);
		Boomerang restored = new Boomerang();
		restored.restoreFromBundle(saved);
		check(restored.level() == 3 && restored.isReinforced(), "回旋镖等级或强化状态存档失败");
	}

	private static void testBanditKingDrop() {
		Dungeon.depth = 12;
		Dungeon.hero = new Hero();
		Dungeon.hero.heroClass = HeroClass.WARRIOR;
		BanditKing king = new BanditKing();
		check(king.createLoot() instanceof Boomerang, "盗贼王的20%掉落没有产出回旋镖");
		check(Math.abs(king.lootChance() - 0.2f) < 0.00001f, "盗贼王基础回旋镖掉率不是20%");
	}

	private static void testImmediateReturn() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		ExposedBoomerang boomerang = new ExposedBoomerang();
		boomerang.exposeReturn(17, hero);
		check(hero.belongings.getItem(Boomerang.class) == boomerang, "落空后回旋镖没有立即回到背包");
		hero.belongings.backpack.items.remove(boomerang);
		boomerang.exposeReturn(18, hero);
		check(hero.belongings.getItem(Boomerang.class) == boomerang, "命中后回旋镖没有立即回到背包");
	}

	private static final class ExposedBoomerang extends Boomerang {
		void exposeReturn(int from, Hero owner) { returnToOwner(from, owner); }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
