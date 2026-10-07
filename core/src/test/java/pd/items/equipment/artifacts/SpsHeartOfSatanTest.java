package pd.items.equipment.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.items.Generator;
import render.noosa.Game;
import render.utils.math.Random;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Headless checks for the ported heart of satan: prick damage/growth, level cap and the extra HP cap. */
public final class SpsHeartOfSatanTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534845415254L);
		try {
			testStatsAndActions();
			testPrickAndGrowth();
			testExtraCapAndPool();
			System.out.println("SPS撒旦之心测试通过：血祭伤害与升级、满级停止血祭、满级额外生命上限、取代圣杯、双语文本均正常。");
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
		HeartOfSatan heart = new HeartOfSatan();
		check(heart.levelCap() == 9 && heart.level() == 0, "撒旦之心初始等级上限或等级错误");
		check(heart.prickValue() == 0, "0 级血祭伤害应为 0：" + heart.prickValue());
		check(heart.image == pd.atlas.items.SpecificPlaceHolderDict.ARTIFACT_HOLDER_0,
				"撒旦之心占位图标错误");
		check(!heart.actions(hero).contains(HeartOfSatan.AC_PRICK), "未装备时错误开放血祭动作");

		hero.belongings.backpack.items.add(heart);
		check(heart.doEquip(hero), "撒旦之心无法装备");
		check(heart.actions(hero).contains(HeartOfSatan.AC_PRICK), "装备后没有开放血祭动作");

		heart.level(9);
		check(heart.prickValue() == 243, "9 级血祭伤害应为 243：" + heart.prickValue());
		check(!heart.actions(hero).contains(HeartOfSatan.AC_PRICK), "满级后仍开放血祭动作");
	}

	private static void testPrickAndGrowth() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		HeartOfSatan heart = new HeartOfSatan();
		hero.belongings.backpack.items.add(heart);
		check(heart.doEquip(hero), "撒旦之心无法装备");

		int before = hero.HP;
		heart.prick(hero);
		check(hero.isAlive(), "0 级血祭不该致死");
		check(hero.HP == before - 1, "0 级血祭至少应造成 1 点伤害：" + (before - hero.HP));
		check(heart.level() == 1, "血祭后没有让心脏升级：" + heart.level());

		heart.level(3);
		check(heart.prickValue() == 27, "3 级血祭伤害应为 27：" + heart.prickValue());
		before = hero.HP;
		hero.HP = hero.HT;
		before = hero.HP;
		heart.prick(hero);
		check(hero.HP <= before - 27, "3 级血祭没有按 prickValue 扣血：" + (before - hero.HP));
		check(heart.level() == 4, "血祭后没有继续升级：" + heart.level());
	}

	private static void testExtraCapAndPool() throws Exception {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		HeartOfSatan heart = new HeartOfSatan();
		hero.belongings.backpack.items.add(heart);
		check(heart.doEquip(hero), "撒旦之心无法装备");
		heart.activate(hero);
		check(hero.buff(HeartOfSatan.Regeneration.class) != null, "装备撒旦之心没有获得回复标记");

		hero.updateHT(false);
		int baseHT = hero.HT;
		check(heart.new Regeneration().extraCap() == 0, "未满级不该提供额外生命上限");

		heart.level(9);
		hero.updateHT(false);
		check(hero.HT == baseHT + baseHT / 2,
				"满级额外生命上限不是 HT/2：" + hero.HT + " vs " + baseHT);

		boolean heartInPool = false;
		boolean chaliceInPool = false;
		for (Class<?> type : Generator.Category.ARTIFACT.classes) {
			if (type == HeartOfSatan.class) heartInPool = true;
			if (type == ChaliceOfBlood.class) chaliceInPool = true;
		}
		check(heartInPool, "撒旦之心没有进入普通神器池");
		check(!chaliceInPool, "圣杯仍在普通神器池中（应已被撒旦之心取代）");
		check(Generator.Category.ARTIFACT.classes.length == Generator.Category.ARTIFACT.defaultProbs.length,
				"神器池与权重数组长度不一致");

		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.artifacts.heartofsatan.desc="), file + "缺少撒旦之心描述");
			check(text.contains("items.equipment.artifacts.heartofsatan.ac_prick="), file + "缺少血祭动作名");
		}
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsHeartOfSatanTest() { }
}
