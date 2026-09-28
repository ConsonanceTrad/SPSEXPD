package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.relic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorruptGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BerryRegeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.AresLeech;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.CromLuck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.JupitersHorror;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.NeptuneShock;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsRelicWeaponTest {

	private static final int CENTER = 8 + 8 * 16;

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testBaseStatsEnchantmentsAndPersistence();
			testChargeAndActions();
			testPassiveEnchantments();
			testAreaPowers();
			testResources();
			System.out.println("SPS遗物武器测试通过：五件遗物、专属附魔、充能、主动技能、连锁电击、强毒、延迟治疗、售价及存档均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
		}
	}

	private static void testBaseStatsEnchantmentsAndPersistence() {
		SpsRelicWeapon[] weapons = {new AresSword(), new CromCruachAxe(), new JupitersWraith(),
				new LokisFlail(), new NeptunusTrident()};
		Class<?>[] enchants = {AresLeech.class, CromLuck.class, JupitersHorror.class,
				com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.LokisPoison.class,
				NeptuneShock.class};
		int[] maximums = {40, 33, 25, 50, 33};
		for (int i = 0; i < weapons.length; i++) {
			SpsRelicWeapon weapon = weapons[i];
			check(weapon instanceof RelicMeleeWeapon && weapon.min() == 6 && weapon.max() == maximums[i],
					weapon.getClass().getSimpleName() + "基础伤害错误");
			check(weapon.STRReq() == 20 && weapon.reinforced, weapon.getClass().getSimpleName() + "力量或强化标记错误");
			check(weapon.enchantment != null && weapon.enchantment.getClass() == enchants[i],
					weapon.getClass().getSimpleName() + "专属附魔缺失");
			weapon.upgrade(6).identify();
			check(weapon.level() == 6 && weapon.min() == 18 && weapon.max() == maximums[i] + 24,
					weapon.getClass().getSimpleName() + "+6成长错误");
			check(weapon.enchantment.getClass() == enchants[i], weapon.getClass().getSimpleName() + "强化后丢失专属附魔");
			check(weapon.value() == 3150, weapon.getClass().getSimpleName() + "旧版售价错误：" + weapon.value());
			weapon.charge = 637;
			Bundle saved = new Bundle();
			weapon.storeInBundle(saved);
			SpsRelicWeapon restored = newWeapon(i);
			restored.restoreFromBundle(saved);
			check(restored.level() == 6 && restored.charge == 637 && restored.enchantment.getClass() == enchants[i],
					weapon.getClass().getSimpleName() + "等级、充能或附魔存档错误");
		}
	}

	private static void testChargeAndActions() throws Exception {
		Hero hero = freshHero(new TestLevel());
		AresSword sword = new AresSword();
		sword.upgrade(6);
		hero.belongings.weapon = sword;
		sword.activate(hero);
		Field chargeBuff = SpsRelicWeapon.class.getDeclaredField("chargeBuff");
		chargeBuff.setAccessible(true);
		Object buff = chargeBuff.get(sword);
		Method act = buff.getClass().getDeclaredMethod("act");
		act.setAccessible(true);
		act.invoke(buff);
		check(sword.charge == 6, "遗物没有按等级逐回合充能");
		sword.charge = SpsRelicWeapon.CHARGE_CAP;
		check(sword.actions(hero).contains(AresSword.AC_REGEN), "满充能遗物没有显示主动技能");
		hero.HP = 20;
		sword.execute(hero, AresSword.AC_REGEN);
		check(sword.charge == 0 && hero.buff(BerryRegeneration.class) != null, "萃魂长剑主动治疗错误");

		CromCruachAxe axe = new CromCruachAxe();
		axe.upgrade(6);
		hero.belongings.weapon = axe;
		axe.charge = SpsRelicWeapon.CHARGE_CAP;
		axe.execute(hero, CromCruachAxe.AC_DISPEL);
		MagicImmunity immunity = hero.buff(MagicImmunity.class);
		check(axe.charge == 0 && immunity != null && immunity.cooldown() == 0f, "碎肉巨斧主动魔免错误");
		check(hero.isImmune(ParalyticGas.class) && hero.isImmune(ToxicGas.class)
				&& hero.isImmune(ConfusionGas.class) && hero.isImmune(StenchGas.class)
				&& hero.isImmune(CorruptGas.class) && hero.isImmune(Electricity.class)
				&& hero.isImmune(Burning.class) && hero.isImmune(Poison.class), "奥术护盾免疫集合不完整");
		check(hero.buff(MagicImmune.class) == null, "碎肉巨斧错误使用了会关闭装备的破碎版魔免");
	}

	private static void testPassiveEnchantments() {
		TestLevel level = new TestLevel();
		Hero hero = freshHero(level);
		hero.HT = 100;
		hero.HP = 20;
		TestMob nearby = mobAt(level, CENTER + 1);
		AresSword sword = new AresSword();
		sword.upgrade(6);
		for (int i = 0; i < 30 && hero.buff(AresLeech.HealDamage.class) == null; i++) sword.proc(hero, nearby, 30);
		AresLeech.HealDamage healing = hero.buff(AresLeech.HealDamage.class);
		check(healing != null && healing.remaining() == 30 && sword.charge > 0, "抽灵没有生成延迟治疗或吸取充能");
		int hp = hero.HP;
		healing.act();
		check(hero.HP > hp && healing.remaining() < 30, "抽灵延迟治疗没有逐回合生效");

		TestMob strong = mobAt(level, CENTER + 2);
		strong.fixedDamage = 20;
		TestMob target = mobAt(level, CENTER + 3);
		int before = target.HP;
		new CromLuck().proc(new CromCruachAxe(), strong, target, 5);
		check(target.HP == before - 15, "锯骨没有补上更高伤害差值");

		LokisFlail flail = new LokisFlail();
		flail.upgrade(30);
		for (int i = 0; i < 20 && target.buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LokisPoison.class) == null; i++) {
			flail.proc(strong, target, 10);
		}
		com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LokisPoison poison =
				target.buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LokisPoison.class);
		check(poison != null, "洛基链枷没有施加强毒");
		before = target.HP;
		poison.act();
		check(before - target.HP == 16, "强毒没有按剩余30回合造成16点伤害");

		JupitersWraith wraith = new JupitersWraith();
		wraith.upgrade(30);
		for (int i = 0; i < 20 && target.buff(Terror.class) == null; i++) wraith.proc(strong, target, 10);
		check(target.buff(Terror.class) != null, "落岩圆刃没有威慑普通目标");
	}

	private static void testAreaPowers() {
		Actor.clear();
		TestLevel level = new TestLevel();
		Hero hero = freshHero(level);
		Actor.add(hero);
		TestMob first = mobAt(level, CENTER + 1);
		TestMob second = mobAt(level, CENTER + 2);
		Actor.add(first);
		Actor.add(second);

		NeptunusTrident trident = new NeptunusTrident();
		trident.upgrade(30);
		trident.charge = 1000;
		int firstHP = first.HP;
		int secondHP = second.HP;
		for (int i = 0; i < 30 && second.HP == secondHP; i++) trident.proc(hero, first, 30);
		check(first.HP < firstHP && second.HP < secondHP, "休克附魔没有连锁命中相邻目标");
		check(trident.charge <= 990, "休克附魔没有消耗10点充能");

		trident.charge = 1000;
		hero.belongings.weapon = trident;
		trident.execute(hero, NeptunusTrident.AC_FLOOD);
		check(trident.charge == 0 && level.map[CENTER + 1] == Terrain.WATER
				&& first.buff(Slow.class) != null, "三叉水戟主动技能没有造水或减速");

		JupitersWraith wraith = new JupitersWraith();
		wraith.upgrade(6);
		wraith.charge = 1000;
		hero.belongings.weapon = wraith;
		firstHP = first.HP;
		wraith.execute(hero, JupitersWraith.AC_EXPLODE);
		check(wraith.charge == 0 && first.HP < firstHP, "落岩圆刃主动范围伤害错误");
	}

	private static void testResources() throws Exception {
		String zhItems = Files.readString(Paths.get("messages/items/items_zh.properties"), StandardCharsets.UTF_8);
		String zhActors = Files.readString(Paths.get("messages/actors/actors_zh.properties"), StandardCharsets.UTF_8);
		check(zhItems.contains("items.weapon.enchantments.aresleech.name=抽灵%s")
				&& zhItems.contains("items.weapon.melee.relic.aressword.name=萃魂长剑")
				&& zhActors.contains("actors.buffs.lokispoison.name=猛毒")
				&& zhActors.contains("actors.buffs.magicimmunity.name=奥术护盾")
				&& zhActors.contains("剩余的护盾效果时长：%s回合"), "遗物武器中文资源缺失");
		check(!zhItems.contains("com.shatteredpixel.shatteredpixeldungeon.")
				&& !zhActors.contains("com.shatteredpixel.shatteredpixeldungeon."), "资源键仍含错误完整包名前缀");
	}

	private static SpsRelicWeapon newWeapon(int index) {
		switch (index) {
			case 0: return new AresSword();
			case 1: return new CromCruachAxe();
			case 2: return new JupitersWraith();
			case 3: return new LokisFlail();
			default: return new NeptunusTrident();
		}
	}

	private static Hero freshHero(TestLevel level) {
		Dungeon.quickslot = new QuickSlot();
		Dungeon.level = level;
		Hero hero = new Hero();
		hero.pos = CENTER;
		hero.HT = hero.HP = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static TestMob mobAt(TestLevel level, int pos) {
		TestMob mob = new TestMob();
		mob.pos = pos;
		mob.HT = mob.HP = 1000;
		level.mobs.add(mob);
		return mob;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TestMob extends Mob {
		int fixedDamage = 10;
		@Override public int damageRoll() { return fixedDamage; }
		@Override public int drRoll() { return 0; }
		@Override public void damage(int damage, Object source) { HP = Math.max(0, HP - Math.max(0, damage)); }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) { heap = new Heap(); heap.pos = cell; heaps.put(cell, heap); }
			heap.drop(item);
			return heap;
		}
	}

	private SpsRelicWeaponTest() { }
}
