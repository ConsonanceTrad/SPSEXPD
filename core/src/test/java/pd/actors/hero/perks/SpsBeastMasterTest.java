/* Special Surprise Pixel Dungeon, GPLv3 or later. */

package pd.actors.hero.perks;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;

import java.util.ArrayList;

import pd.actors.mobs.pets.*;
import pd.items.consum.eggs.Egg;
import render.noosa.Game;

/**
 * 无图形验证「驯兽大师」：3 级设计、35 种生物的能力特质映射、炸环增幅。
 * 注意：能力特质类的静态块会注册 InlineText（依赖 Gdx.app），所以要补一个 headless app。
 */
public final class SpsBeastMasterTest {

	private static int checks = 0;

	private static void check(boolean ok, String what) {
		checks++;
		if (!ok) throw new AssertionError("FAIL: " + what);
	}

	public static void main(String[] args) {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Gdx.app = new HeadlessApplication(new ApplicationAdapter() {});
		Game.version = "test";
		try {
			testMasteryTier();
			testAbilityMapping();
			testStrikeBoost();

			System.out.println("SpsBeastMasterTest PASS (" + checks + " checks)");
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		//SPSXPD: HeadlessApplication 的循环线程不是守护线程，测试跑完必须显式退出，
		//否则 JavaExec 任务会一直挂着（表现为 BUILD FAILED / WaitDelay expired）
		System.out.flush();
		System.exit(0);
	}

	/** 驯兽大师：1 级羁绊、2 级献祭、3 级炸环 */
	private static void testMasteryTier() {
		BeastMaster bm = new BeastMaster();
		check(bm.maxLevel() == 3, "驯兽大师应可升到 3 级");
		check(bm.title() != null && !bm.title().isEmpty(), "驯兽大师缺少名称");
		check(bm.description() != null && !bm.description().isEmpty(), "驯兽大师缺少描述");
	}

	/** 35 种生物都要有对应的能力特质 */
	private static void testAbilityMapping() {
		ArrayList<LegacyPet> pets = new ArrayList<>();
		pets.add(new Kodora());
		pets.add(new Snake());
		pets.add(new RibbonRat());
		pets.add(new GentleCrab());
		pets.add(new DogPet());
		pets.add(new Chocobo());
		pets.add(new Fly());
		pets.add(new Spider());
		pets.add(new Stone());
		pets.add(new DwarfBoy());
		pets.add(new ButterflyPet());
		pets.add(new Monkey());
		pets.add(new PigPet());
		pets.add(new Datura());
		pets.add(new FoxHelper());
		pets.add(new FrogPet());
		pets.add(new LitDemon());
		pets.add(new StarKid());
		pets.add(new Abi());
		pets.add(new Haro());
		pets.add(new CocoCat());
		pets.add(new Velocirooster());
		pets.add(new Bunny());
		pets.add(new YearPet());
		pets.add(new RedDragon());
		pets.add(new BlueDragon());
		pets.add(new GreenDragon());
		pets.add(new VioletDragon());
		pets.add(new LightDragon());
		pets.add(new ShadowDragon());
		pets.add(new GoldDragon());
		pets.add(new BugDragon());
		pets.add(new BlueGirl());
		pets.add(new LeryFire());
		pets.add(new Scorpion());
		check(pets.size() == 35, "应覆盖 35 种生物，实际 " + pets.size());

		ArrayList<String> missing = new ArrayList<>();
		for (LegacyPet pet : pets) {
			Perk ability = Egg.petAbilityOf(pet);
			if (ability == null) {
				missing.add(pet.getClass().getSimpleName());
				continue;
			}
			check(ability.title() != null && !ability.title().isEmpty(),
					pet.getClass().getSimpleName() + " 的能力特质缺少名称");
		}
		check(missing.isEmpty(), "这些生物缺少能力特质：" + missing);
		check(Egg.petAbilityOf(null) == null, "空生物不应有对应能力特质");
	}

	/** 炸环：按生物种类分档，并随培养程度提升 */
	private static void testStrikeBoost() {
		int dog = Egg.strikeBoost(new DogPet(), 0);
		int dragon = Egg.strikeBoost(new BlueDragon(), 0);
		int gold = Egg.strikeBoost(new GoldDragon(), 0);
		check(dragon > dog, "龙系的炸环增幅应高于普通宠物");
		check(gold > dragon, "金龙的炸环增幅应高于普通龙");
		check(Egg.strikeBoost(new DogPet(), 4) > dog, "炸环增幅应随培养程度提升");
		check(Egg.strikeBoost(new DogPet(), 0) > 0, "炸环增幅应大于 0");
	}
}