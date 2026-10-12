package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.ConsumThrowsDict;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.BanditKing;
import render.noosa.Game;
import render.utils.serialize.Bundle;

/** Headless checks for the Bandit King drop and immediate-return boomerang. */
public final class SpsBoomerangTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		testLegacyStatsAndSave();
		testBanditKingDrop();
		testChargeState();
		System.out.println("SPS回旋镖测试通过：旧版数值、强化存档、盗贼王掉落及充能状态均正常。");
	}

	private static void testLegacyStatsAndSave() {
		Boomerang boomerang = new Boomerang();
		check(!boomerang.stackable && boomerang.unique, "回旋镖不是唯一单件物品");
		check(boomerang.min(0) == 3 && boomerang.max(0) == 6 && boomerang.STRReq(0) == 10,
				"回旋镖基础数值与旧版源码不一致");
		check(boomerang.min(4) == 7 && boomerang.max(4) == 14,
				"回旋镖升级成长与旧版源码不一致");
		check(boomerang.isUpgradable() && boomerang.isReinforced(), "回旋镖强化或可升级状态错误");
		check(boomerang.image == ConsumThrowsDict.BOOMERANG_0, "回旋镖没有使用旧版独立图标");

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

	//SPSEXPD: 回旋镖已改造为近战武器（充能投掷/命中麻痹/接住重置），原「立即返还」逻辑
	//改由 MissileShield 式的 ReturnShot 实现，无头环境无法驱动弹回动画，这里只校验充能状态。
	private static void testChargeState() {
		Boomerang boomerang = new Boomerang();
		check(boomerang.charge() == 0, "回旋镖初始充能不为 0");
		for (int i = 0; i < Boomerang.FULL_CHARGE + 5; i++) boomerang.gainCharge();
		check(boomerang.charge() == Boomerang.FULL_CHARGE, "回旋镖充能没有封顶");

		Bundle saved = new Bundle();
		boomerang.storeInBundle(saved);
		Boomerang restored = new Boomerang();
		restored.restoreFromBundle(saved);
		check(restored.charge() == Boomerang.FULL_CHARGE, "回旋镖充能状态存档失败");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
