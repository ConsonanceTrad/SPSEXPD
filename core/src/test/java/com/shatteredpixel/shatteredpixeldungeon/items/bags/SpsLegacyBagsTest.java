package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.AncientCoin;
import com.shatteredpixel.shatteredpixeldungeon.items.TenguKey;
import com.shatteredpixel.shatteredpixeldungeon.items.PotKey;
import com.shatteredpixel.shatteredpixeldungeon.items.TriforceOfCourage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.AdventureJournal;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges.WoodenBowN;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.watabou.noosa.Game;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class SpsLegacyBagsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll.initLabels();
		com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion.initColors();
		Ring.initGems();
		try {
			testContainerRules();
			testWandHolsterCharging();
			testStartingLoadouts();
			testBilingualResources();
			System.out.println("SPS旧版收纳袋通过：三十格容量、收纳分类、法杖充能生命周期、八职业开局、教程隔离和双语资源均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.challenges = 0;
			app.exit();
		}
	}

	private static void testContainerRules() {
		ArrowCollecter arrows = new ArrowCollecter();
		check(arrows.capacity() == 34 && arrows.value() == 50, "暗器袋容量或价值错误");
		check(arrows.canHold(new WoodenBowN()), "暗器袋没有收纳远程武器");
		check(arrows.canHold(new ThrowingKnife()), "暗器袋没有收纳投掷武器");
		check(!arrows.canHold(new PotionOfHealing()), "暗器袋错误收纳药剂");

		KeyRing ring = new KeyRing();
		check(ring.capacity() == 34 && ring.value() == 50, "钥匙环容量或价值错误");
		check(ring.canHold(new IronKey(1)), "钥匙环没有收纳地牢钥匙");
		check(ring.canHold(new PotKey()) && ring.canHold(new AncientCoin()) && ring.canHold(new TenguKey()),
				"钥匙环没有收纳SPS首领钥匙");
		check(ring.canHold(new TriforceOfCourage()), "钥匙环没有收纳传送道具");
		check(ring.canHold(new RingOfAccuracy()), "钥匙环没有收纳戒指");
		check(ring.canHold(new AdventureJournal()), "钥匙环没有收纳路线日志");
		check(!ring.canHold(new Firebloom.Seed()), "钥匙环错误收纳种子");

		WandHolster holster = new WandHolster();
		check(holster.capacity() == 34 && holster.value() == 50, "法杖套容量或价值错误");
		check(holster.canHold(new WandOfMagicMissile()), "法杖套没有收纳法杖");
		check(!holster.canHold(new Firebloom.Seed()), "法杖套错误收纳种子");
	}

	private static void testWandHolsterCharging() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		hero.HP = hero.HT;
		Dungeon.hero = hero;
		WandOfMagicMissile wand = new WandOfMagicMissile();
		check(wand.collect(hero.belongings.backpack)
				&& hero.buffs(Wand.Charger.class).size() == 1, "背包中的法杖没有开始充能");

		WandHolster holster = new WandHolster();
		check(holster.collect(hero.belongings.backpack), "法杖套无法收入英雄背包");
		check(holster.contains(wand) && hero.buffs(Wand.Charger.class).size() == 1,
				"拾取法杖套时内部法杖没有继续充能或重复充能");

		holster.detachAll(hero.belongings.backpack);
		check(hero.buffs(Wand.Charger.class).isEmpty(), "法杖套离开背包后内部法杖仍在充能");
	}

	private static void testStartingLoadouts() {
		Dungeon.challenges = 0;
		for (HeroClass heroClass : HeroClass.playableClasses()) {
			Actor.clear();
			Dungeon.LimitedDrops.reset();
			Dungeon.quickslot = new QuickSlot();
			Hero hero = new Hero();
			Dungeon.hero = hero;
			heroClass.initHero(hero);
			check(hero.belongings.getItem(ArrowCollecter.class) != null,
					heroClass + "开局缺少暗器袋");
			check(hero.belongings.getItem(KeyRing.class) != null,
					heroClass + "开局缺少钥匙环");
		}

		Dungeon.quickslot = new QuickSlot();
		Hero tutorial = new Hero();
		Dungeon.hero = tutorial;
		HeroClass.NEWPLAYER.initHero(tutorial);
		check(tutorial.belongings.getItem(ArrowCollecter.class) == null
				&& tutorial.belongings.getItem(KeyRing.class) == null,
				"教程角色不应获得正常开局收纳袋");
	}

	private static void testBilingualResources() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/items_zh.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/items.properties"), StandardCharsets.UTF_8);
		String[] keys = {"items.bags.arrowcollecter.name=", "items.bags.arrowcollecter.desc=",
				"items.bags.keyring.name=", "items.bags.keyring.desc="};
		for (String key : keys) check(zh.contains(key) && en.contains(key), "中英文资源缺少键：" + key);
		check(zh.contains("暗器袋") && zh.contains("钥匙环") && !zh.contains("�"), "收纳袋中文乱码或缺失");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsLegacyBagsTest() { }
}
