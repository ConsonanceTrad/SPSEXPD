package pd.items.equipment.rings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.actors.Actor;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.DarkGas;
import pd.actors.blobs.Electricity;
import pd.actors.blobs.VenomGas;
import pd.actors.buffs.AflyBless;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicImmunity;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.actors.mobs.BrokenRobot;
import pd.actors.mobs.SpsCaveMobs;
import pd.actors.mobs.SpsDM300;
import pd.actors.mobs.Yog;
import pd.levels.traps.SpearTrap;
import render.noosa.Game;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class SpsRingOfElementsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Ring.initGems();
		try {
			testDurationFactorsAndLists();
			testBuffIntegration();
			testMagicDamageResistance();
			testResources();
			System.out.println("SPS元素戒指测试通过：正负状态时长、法术减伤、等级上限、诅咒倍率、效果名单及双语原文均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testDurationFactorsAndLists() {
		Hero hero = hero();
		RingOfElements ring = activeRing(hero, 15);
		check(close(RingOfElements.fintime(hero, Blindness.class), 0.70f), "+15负面状态倍率不是0.70");
		check(close(RingOfElements.fintime(hero, AflyBless.class), 2f), "+15增益状态倍率不是2.00");
		check(close(RingOfElements.fintime(hero, String.class), 1f), "名单外状态被元素戒指改变");

		ring.level(30);
		check(close(RingOfElements.fintime(hero, Blindness.class), 0.40f), "+30负面状态没有达到0.40下限");
		check(close(RingOfElements.fintime(hero, AflyBless.class), 3f), "+30增益状态没有达到3.00上限");

		ring.level(-2);
		check(close(RingOfElements.fintime(hero, Blindness.class), 1.04f), "负等级没有延长负面状态");
		check(close(RingOfElements.fintime(hero, AflyBless.class), 13f / 15f), "负等级没有缩短增益状态");

		check(RingOfElements.REDUCE.contains(VenomGas.class)
				&& RingOfElements.REDUCE.contains(SpearTrap.class)
				&& RingOfElements.REDUCE.contains(DarkGas.class)
				&& RingOfElements.REDUCE.contains(Electricity.class)
				&& RingOfElements.REDUCE.contains(ConfusionGas.class)
				&& RingOfElements.REDUCE.contains(pd.actors.mobs.GnollShaman.class)
				&& RingOfElements.REDUCE.contains(SpsDM300.class)
				&& RingOfElements.REDUCE.contains(BrokenRobot.class)
				&& RingOfElements.REDUCE.contains(Yog.BurningFist.class)
				&& RingOfElements.REDUCE.contains(Yog.PinningFist.class), "旧版负面效果或首领名单缺失");
		check(RingOfElements.IMPROVE.size() == 21
				&& RingOfElements.IMPROVE.contains(MagicImmunity.class), "旧版21项增益名单不完整");
	}

	private static void testBuffIntegration() {
		Hero negativeHero = hero();
		activeRing(negativeHero, 15);
		Blindness blindness = Buff.affect(negativeHero, Blindness.class, 10f);
		check(close(blindness.cooldown(), 7f), "Buff.affect没有接入负面状态缩短");

		Hero positiveHero = hero();
		activeRing(positiveHero, 15);
		MagicImmunity immunity = Buff.affect(positiveHero, MagicImmunity.class, 10f);
		check(close(immunity.cooldown(), 20f), "Buff.affect没有接入增益状态延长");
	}

	private static void testMagicDamageResistance() {
		Hero hero = hero();
		RingOfElements ring = activeRing(hero, 15);
		hero.HT = hero.HP = 1000;
		hero.damage(100, DamageType.FIRE_DAMAGE);
		check(hero.HP == 920, "+15元素戒指没有把法术伤害降至80%");
		hero.damage(100, new Object());
		check(hero.HP == 820, "元素戒指错误降低物理伤害");

		ring.level(30);
		hero.damage(100, DamageType.ICE_DAMAGE);
		check(hero.HP == 760, "+30元素戒指没有达到60%法术伤害下限，实际生命=" + hero.HP);

		ring.level(-2);
		hero.damage(75, DamageType.DARK_DAMAGE);
		check(hero.HP == 683, "负等级法术伤害倍率错误，实际生命=" + hero.HP);
	}

	private static void testResources() throws Exception {
		String zh = Files.readString(Paths.get("messages/items/zh/items.properties"), StandardCharsets.UTF_8);
		String en = Files.readString(Paths.get("messages/items/en/items.properties"), StandardCharsets.UTF_8);
		check(zh.contains("items.equipment.rings.ringofelements.name=元素戒指")
				&& zh.contains("增益效果提升至_%1$s%%_倍，负面效果降低至_%2$s%%_倍")
				&& zh.contains("在30级时这个效果达到上限") && !zh.contains("�"), "元素戒指中文原文缺失或乱码");
		check(en.contains("Gain element resistance, improve buff by %1$s%%, and reduce debuff by %2$s%%.")
				&& en.contains("Limit at 30 level."), "元素戒指英文原文缺失");
	}

	private static RingOfElements activeRing(Hero hero, int level) {
		RingOfElements ring = new RingOfElements();
		ring.level(level);
		ring.activate(hero);
		return ring;
	}

	private static Hero hero() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Hero hero = new Hero();
		Dungeon.hero = hero;
		return hero;
	}

	private static boolean close(float first, float second) {
		return Math.abs(first - second) < 0.0001f;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsRingOfElementsTest() { }
}
