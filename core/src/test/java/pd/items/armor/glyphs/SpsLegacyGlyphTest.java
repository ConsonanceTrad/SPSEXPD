package pd.items.armor.glyphs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.actors.buffs.armorbuff.ArmorGlyphBuff;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.armor.Armor;
import pd.items.armor.normalarmor.ClothArmor;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** Headless regression checks for all thirteen SPS-PD 0.9.8 armor glyphs. */
public final class SpsLegacyGlyphTest {

	private static final Class<?>[] GLYPHS = {
			Changeglyph.class, Crystalglyph.class, Darkglyph.class, Earthglyph.class,
			Electricityglyph.class, Fireglyph.class, Iceglyph.class, Lightglyph.class,
			Revivalglyph.class, Testglyph.class, AdaptGlyph.class, RecoilGlyph.class,
			Energyglyph.class
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x535053474C595048L);
		try {
			testGeneratorDeck();
			testElementalMarkers();
			testZeroLevelCrashFixes();
			testCombatEffects();
			testDeferredDamageSave();
			testCrystalDelayProtection();
			System.out.println("SPS旧版13种护甲刻印测试通过：生成牌组、元素抗性、触发效果、零级边界和存档均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testGeneratorDeck() {
		check(Armor.Glyph.common.length == GLYPHS.length, "SPS护甲刻印牌组数量错误");
		check(Armor.Glyph.uncommon.length == 0 && Armor.Glyph.rare.length == 0,
				"破碎版护甲刻印仍在普通生成牌组中");
		check(Armor.Glyph.typeChances[0] == 100f && Armor.Glyph.typeChances[1] == 0f
				&& Armor.Glyph.typeChances[2] == 0f, "SPS护甲刻印生成分组权重错误");
		for (int i = 0; i < GLYPHS.length; i++) {
			check(Armor.Glyph.common[i] == GLYPHS[i], "SPS护甲刻印顺序错误：" + i);
		}
		for (int i = 0; i < 500; i++) {
			Armor.Glyph glyph = Armor.Glyph.random();
			boolean found = false;
			for (Class<?> type : GLYPHS) if (glyph.getClass() == type) found = true;
			check(found, "普通生成出现非SPS护甲刻印：" + glyph.getClass().getSimpleName());
		}
	}

	private static void testElementalMarkers() {
		Hero defender = hero();
		TestMob attacker = mob(100);
		Armor armor = new ClothArmor();
		new Fireglyph().proc(armor, attacker, defender, 10);
		check(defender.buff(pd.actors.buffs.armorbuff.GlyphFire.class) != null,
				"火罩刻印没有设置火焰抗性");
		check(defender.isImmune(DamageType.Fire.class), "火罩刻印没有免疫火焰类型伤害");
		new Iceglyph().proc(armor, attacker, defender, 0);
		check(defender.buff(pd.actors.buffs.armorbuff.GlyphFire.class) == null
				&& defender.buff(pd.actors.buffs.armorbuff.GlyphIce.class) != null,
				"元素刻印抗性没有互斥切换");
		check(defender.isImmune(DamageType.Ice.class), "雪屋刻印没有免疫冰冻类型伤害");
		new Energyglyph().proc(armor, attacker, defender, 10);
		check(defender.buff(pd.actors.buffs.armorbuff.GlyphEnergy.class) != null
				&& Math.abs(defender.resist(DamageType.Energy.class) - 0.5f) < 0.0001f,
				"缓冲刻印没有提供能量伤害抗性");
	}

	private static void testZeroLevelCrashFixes() {
		Hero defender = hero();
		TestMob attacker = mob(100);
		Armor armor = new ClothArmor();
		for (int i = 0; i < 100; i++) {
			new Electricityglyph().proc(armor, attacker, defender, 5);
			new Revivalglyph().proc(armor, attacker, defender, 5);
			new Darkglyph().proc(armor, attacker, defender, 5);
		}
	}

	private static void testCombatEffects() {
		Hero defender = hero();
		TestMob attacker = mob(2_000);
		ClothArmor armor = new ClothArmor();
		armor.upgrade(20);

		for (int i = 0; i < 100 && attacker.buff(Burning.class) == null; i++) {
			new Fireglyph().proc(armor, attacker, defender, 20);
		}
		check(attacker.buff(Burning.class) != null, "火罩刻印没有点燃攻击者");

		for (int i = 0; i < 100 && attacker.buff(Bleeding.class) == null; i++) {
			new RecoilGlyph().proc(armor, attacker, defender, 20);
		}
		check(attacker.buff(Bleeding.class) != null, "反冲刻印没有造成流血");

		Buff.affect(defender, Paralysis.class, 100f);
		for (int i = 0; i < 100 && defender.buff(Paralysis.class) != null; i++) {
			new Revivalglyph().proc(armor, attacker, defender, 20);
		}
		check(defender.buff(Paralysis.class) == null, "复生刻印没有驱散负面状态");

		defender.HP = 20;
		int attackerStart = attacker.HP;
		for (int i = 0; i < 200 && attacker.HP == attackerStart; i++) {
			new Darkglyph().proc(armor, attacker, defender, 20);
		}
		check(attacker.HP < attackerStart, "暗契刻印没有伤害攻击者");
	}

	private static void testDeferredDamageSave() {
		Iceglyph.DeferedDamage source = new Iceglyph.DeferedDamage();
		source.prolong(37);
		Bundle bundle = new Bundle();
		source.storeInBundle(bundle);
		Iceglyph.DeferedDamage restored = new Iceglyph.DeferedDamage();
		restored.restoreFromBundle(bundle);
		check(restored.remainingDamage() == 37, "雪屋刻印延缓伤害没有随存档恢复");
	}

	private static void testCrystalDelayProtection() {
		Hero defender = hero();
		TestMob attacker = mob(100);
		ClothArmor armor = new ClothArmor();
		armor.upgrade(20);
		for (int i = 0; i < 100 && defender.buff(DelayProtect.class) == null; i++) {
			new Crystalglyph().proc(armor, attacker, defender, 40);
		}
		DelayProtect delay = defender.buff(DelayProtect.class);
		check(delay != null, "晶化刻印没有产生延时保护");
		delay.act();
		check(defender.buff(GlassShield.class) != null && defender.buff(GlassShield.class).turns() == 1,
				"延时保护结束后没有生成一层玻璃保护");
	}

	private static Hero hero() {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		hero.STR = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static TestMob mob(int health) {
		TestMob mob = new TestMob();
		mob.HP = mob.HT = health;
		return mob;
	}

	private static final class TestMob extends Mob {
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int drRoll() { return 0; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsLegacyGlyphTest() {
	}
}
