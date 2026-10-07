package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Waterskin;
import render.noosa.Game;
import render.utils.math.Random;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Headless checks for the ported goddess radiance: charging, radiance burst, growth and mental evasion. */
public final class SpsGoddessRadianceTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350535241444941L);
		try {
			testStatsAndActions();
			testGrowthAndRecharge();
			testRadianceAndMental();
			testPoolAndMessages();
			System.out.println("SPS圣者之辉测试通过：初始充能、动作门槛、露珠祝福与随时间成长、激活光耀、满级视野与精神免疫、神器池与双语文本均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	private static void testStatsAndActions() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		GoddessRadiance radiance = new GoddessRadiance();
		check(radiance.levelCap() == 10 && radiance.chargeCap() == 100 && radiance.charge() == 100,
				"圣者之辉初始等级上限或充能错误");
		check(radiance.image == pd.atlas.items.SpecificPlaceHolderDict.ARTIFACT_HOLDER_0,
				"圣者之辉占位图标错误");
		check(!radiance.actions(hero).contains(GoddessRadiance.AC_ACTIVATE), "未装备时错误开放激活动作");

		hero.belongings.backpack.items.add(radiance);
		check(radiance.doEquip(hero), "圣者之辉无法装备");
		check(radiance.actions(hero).contains(GoddessRadiance.AC_ACTIVATE), "满充能时没有开放激活动作");
		check(!radiance.actions(hero).contains(GoddessRadiance.AC_BLESS), "没有露水瓶时不该开放祝福动作");

		radiance.charge = 50;
		check(!radiance.actions(hero).contains(GoddessRadiance.AC_ACTIVATE), "充能不足时仍开放激活动作");
	}

	private static void testGrowthAndRecharge() throws Exception {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		GoddessRadiance radiance = new GoddessRadiance();
		hero.belongings.backpack.items.add(radiance);
		check(radiance.doEquip(hero), "圣者之辉无法装备");
		radiance.activate(hero);

		//成长：0 → 1 级需要 15 点经验
		radiance.earnExp(14);
		check(radiance.level() == 0 && radiance.exp() == 14, "经验不足时不该升级：" + radiance.level());
		radiance.earnExp(1);
		check(radiance.level() == 1 && radiance.exp() == 0, "达到门槛后没有升级：" + radiance.level());

		//随时间充能与成长：每回合 +1 经验
		radiance.charge = 0;
		radiance.partialCharge = 0f;
		int expBefore = radiance.exp();
		GoddessRadiance.Recharge recharge = radiance.new Recharge();
		check(recharge.attachTo(hero), "圣者之辉充能状态无法附加");
		for (int i = 0; i < 5; i++) recharge.act();
		check(radiance.charge() > 0, "随时间没有恢复充能：" + radiance.charge());
		check(radiance.exp() == expBefore + 5, "随时间没有累积经验：" + radiance.exp());

		//露珠祝福：消耗露水换取经验
		Waterskin flask = new pd.items.DewVial();
		flask.fill();
		hero.belongings.backpack.items.add(flask);
		check(radiance.actions(hero).contains(GoddessRadiance.AC_BLESS), "有露水瓶时没有开放祝福动作");
		int beforeBless = radiance.exp();
		check(radiance.bless(hero), "祝福失败");
		check(flask.checkVol() == 0, "祝福没有清空露水：" + flask.checkVol());
		check(radiance.exp() > beforeBless || radiance.level() > 1, "祝福没有转化为成长");
	}

	private static void testRadianceAndMental() throws Exception {
		Hero hero = new Hero();
		hero.pos = 27;
		Dungeon.hero = hero;
		GoddessRadiance radiance = new GoddessRadiance();
		hero.belongings.backpack.items.add(radiance);
		check(radiance.doEquip(hero), "圣者之辉无法装备");
		radiance.activate(hero);

		boolean inPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == GoddessRadiance.class) inPool = true;
		}
		check(inPool, "圣者之辉没有进入普通神器池");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		//满级视野 +1
		GoddessRadiance.Recharge recharge = radiance.new Recharge();
		check(recharge.attachTo(hero), "圣者之辉充能状态无法附加");
		check(recharge.viewAmend() == 0, "未满级不该提供额外视野");
		radiance.level(10);
		check(recharge.viewAmend() == 1, "满级没有提供额外视野");
		check(recharge.evadeRatio() > 0.25f && recharge.evadeRatio() < 0.3f,
				"满级精神免疫概率异常：" + recharge.evadeRatio());

		//精神类状态免疫：低级时概率低，强制多次尝试应当能命中若干次
		check(GoddessRadiance.isMental(new Terror()), "恐惧应被判定为精神类");
		check(!GoddessRadiance.isMental(new Blindness()), "致盲不该被判定为精神类");

		int evaded = 0;
		for (int i = 0; i < 200; i++) {
			Terror terror = new Terror();
			if (!terror.attachTo(hero)) evaded++;
			else terror.detach();
		}
		check(evaded > 0, "满级时没有出现过任何精神免疫");
	}

	private static void testPoolAndMessages() throws Exception {
		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.artifacts.goddessradiance.desc="), file + "缺少圣者之辉描述");
			check(text.contains("items.equipment.artifacts.goddessradiance.ac_activate="), file + "缺少激活动作名");
			check(text.contains("items.equipment.artifacts.goddessradiance.ac_bless="), file + "缺少祝福动作名");
		}
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsGoddessRadianceTest() { }
}
