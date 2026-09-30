package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.mobs.Gnoll;
import pd.items.Generator;
import pd.items.rings.Ring;
import watabou.noosa.Game;
import watabou.utils.Bundle;
import watabou.utils.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class SpsFlyChainsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(com.badlogic.gdx.Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-fly-chains" + File.separator);
		Game.version = "test";
		pd.items.scrolls.Scroll.initLabels();
		pd.items.potions.Potion.initColors();
		Ring.initGems();
		Generator.fullReset();
		try {
			Badges.loadGlobal();
			testRechargeLevelAndSave();
			testSeal();
			testKillExperienceHook();
			testSkinThreeStartsAndGeneratorWeight();
			testBilingualResources();
			System.out.println("SPS翔虫通过：锁链移动基底、充能、击杀成长、耗竭封印、皮肤3开局、存档和双语资源均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testRechargeLevelAndSave() {
		FlyChains chains = new FlyChains();
		check(chains.charge() == 5 && chains.chargeTarget() == 5, "翔虫初始充能错误");
		chains.charge = 0;
		for (int i = 0; i < 29; i++) chains.advanceRecharge();
		check(chains.charge() == 0, "翔虫在旧版要求回合前提前充能");
		chains.advanceRecharge();
		check(chains.charge() == 1, "翔虫没有按旧版缺失充能公式恢复");

		FlyChains.chainsRecharge2 recharge = chains.new chainsRecharge2();
		recharge.gainExp(1f);
		check(chains.level() == 0 && chains.experience() == 100, "翔虫错误使用了大于等于升级门槛");
		recharge.gainExp(0.01f);
		check(chains.level() == 1 && chains.experience() == 1 && chains.chargeTarget() == 7,
				"翔虫首级击杀成长错误");

		Bundle bundle = new Bundle();
		chains.storeInBundle(bundle);
		FlyChains restored = new FlyChains();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 1 && restored.experience() == 1 && restored.charge() == chains.charge(),
				"翔虫等级、经验或充能存档丢失");
	}

	private static void testSeal() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		FlyChains chains = new FlyChains();
		chains.level(3);
		chains.identify();
		hero.belongings.misc = chains;
		chains.activate(hero);
		check(chains.actions(hero).contains(FlyChains.AC_CAST)
				&& chains.actions(hero).contains(FlyChains.AC_LOCKED), "已装备+3翔虫缺少动作");

		Gnoll target = new Gnoll();
		int beforeCharge = chains.charge();
		check(chains.sealTarget(target), "翔虫封印没有命中有效目标");
		check(chains.level() == 2 && chains.charge() == beforeCharge, "翔虫封印没有只消耗一级");
		check(target.buff(Locked.class) != null && target.buff(Locked.class).cooldown() == 12f,
				"翔虫锁定时长错误");
		check(target.buff(Silent.class) != null && target.buff(Silent.class).cooldown() == 12f,
				"翔虫沉默时长错误");
		check(target.buff(AttackDown.class) != null && target.buff(AttackDown.class).level() == 90
				&& target.buff(AttackDown.class).cooldown() == 12f, "翔虫攻击削弱错误");
		check(target.buff(Slow.class) != null && target.buff(Slow.class).cooldown() == 12f,
				"翔虫减速时长错误");
	}

	private static void testKillExperienceHook() {
		Actor.clear();
		Hero hero = new Hero();
		hero.heroClass = HeroClass.WARRIOR;
		Dungeon.hero = hero;
		FlyChains chains = new FlyChains();
		hero.belongings.misc = chains;
		chains.activate(hero);
		hero.earnExp(1, Gnoll.class);
		check(chains.experience() == Math.round(100f / hero.maxExp()),
				"英雄击杀经验没有传给已装备翔虫");
	}

	private static void testSkinThreeStartsAndGeneratorWeight() {
		int index = -1;
		for (int i = 0; i < Generator.Category.ARTIFACT.classes.length; i++) {
			if (Generator.Category.ARTIFACT.classes[i] == FlyChains.class) index = i;
		}
		check(index >= 0 && Generator.Category.ARTIFACT.defaultProbs[index] == 0f,
				"翔虫没有以旧版零权重登记在神器池");

		Dungeon.challenges = 0;
		for (HeroClass heroClass : HeroClass.playableClasses()) {
			Actor.clear();
			Dungeon.LimitedDrops.reset();
			Dungeon.quickslot = new QuickSlot();
			Hero hero = new Hero();
			hero.skin = 3;
			Dungeon.hero = hero;
			heroClass.initHero(hero);
			check(hero.belongings.misc instanceof FlyChains && hero.belongings.misc.level() == 3,
					heroClass + "的皮肤3开局没有装备+3翔虫");
			check(hero.buff(FlyChains.chainsRecharge2.class) != null,
					heroClass + "的皮肤3翔虫没有激活");
		}
	}

	private static void testBilingualResources() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/items_zh.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/items.properties"), StandardCharsets.UTF_8);
		String[] keys = {"items.artifacts.flychains.name=", "items.artifacts.flychains.ac_locked=",
				"items.artifacts.flychains.desc=", "items.artifacts.flychains$chainsrecharge2.levelup="};
		for (String key : keys) check(zh.contains(key) && en.contains(key), "中英文资源缺少键：" + key);
		check(zh.contains("翔虫") && zh.contains("耗竭-封印") && !zh.contains("�"), "翔虫中文乱码或缺失");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsFlyChainsTest() { }
}
