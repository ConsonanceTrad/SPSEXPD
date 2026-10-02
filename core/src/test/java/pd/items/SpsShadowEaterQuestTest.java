package pd.items;

import pd.atlas.items.EquipmentNonEquipDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.SpecificTaskDict;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Bleeding;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Blacksmith;
import pd.items.equipment.bags.HeartOfScarecrow;
import pd.items.consum.potions.Potion;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.Scroll;
import pd.items.equipment.weapon.melee.special.ShadowEater;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** Headless checks for the complete three-material Shadow Eater quest chain. */
public final class SpsShadowEaterQuestTest {

	public static void main(String[] args) {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(
				new ApplicationAdapter() { @Override public void create() { } },
				new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5348445745415445L);
		try {
			Scroll.initLabels();
			Potion.initColors();
			Ring.initGems();
			Generator.fullReset();
			testMaterialsAndPortalState();
			testForgingAndWeapon();
			testClaimPersistence();
			System.out.println("SPS暗噬任务测试通过：三件材料、两条传送状态、铁匠锻造、武器充能与一次性领取存档均正常。");
		} finally {
			Random.popGenerator();
			app.exit();
		}
	}

	private static void testMaterialsAndPortalState() {
		Hero hero = freshHero();
		check(!new EmptyBody().actions(hero).contains(Item.AC_DROP), "虚无之体仍可丢弃");
		check(!new ChaosPack().actions(hero).contains(Item.AC_THROW), "混沌之契仍可投掷");
		check(new PotKey().actions(hero).contains(PotKey.AC_PORT), "罐罐挑战函缺少使用动作");
		check(new ShadowEaterKey().actions(hero).contains(ShadowEaterKey.AC_PORT), "暗噬原型缺少传送动作");
		check(PotKey.BRANCH != ShadowEaterKey.BRANCH, "两条特殊路线使用了同一分支");
		check(new HeartOfScarecrow().canHold(new ShadowEaterKey()), "草靶子不能收纳旧版暗噬原型");
		check(EquipmentNonEquipDict.CHAOS_PACK != SpecificPlaceHolderDict.SOMETHING_0
				&& SpecificPlaceHolderDict.SOMETHING_0 != SpecificPlaceHolderDict.SOMETHING_0
				&& SpecificPlaceHolderDict.SOMETHING_0 != SpecificTaskDict.POT_KEY_0, "任务图标槽发生重叠");

		Bundle potState = new Bundle();
		new PotKey().storeInBundle(potState);
		potState.put("return_depth", 12);
		potState.put("return_branch", 3);
		potState.put("return_pos", 456);
		PotKey restoredPot = new PotKey();
		restoredPot.restoreFromBundle(potState);
		Bundle potRoundTrip = new Bundle();
		restoredPot.storeInBundle(potRoundTrip);
		check(potRoundTrip.getInt("return_depth") == 12
				&& potRoundTrip.getInt("return_branch") == 3
				&& potRoundTrip.getInt("return_pos") == 456, "罐罐挑战函回程状态存档失败");
	}

	private static void testForgingAndWeapon() {
		Hero hero = freshHero();
		hero.belongings.backpack.items.add(new CurseBlood());
		hero.belongings.backpack.items.add(new EmptyBody());
		hero.belongings.backpack.items.add(new ChaosPack());
		check(Blacksmith.forgeShadowEater(), "铁匠没有识别完整的三件材料");
		check(hero.belongings.getItem(CurseBlood.class) == null
				&& hero.belongings.getItem(EmptyBody.class) == null
				&& hero.belongings.getItem(ChaosPack.class) == null, "铁匠锻造后没有消耗材料");
		ShadowEater weapon = hero.belongings.getItem(ShadowEater.class);
		check(weapon != null, "铁匠没有产出暗噬武器");
		check(weapon.min(0) == 9 && weapon.max(0) == 24 && weapon.STRReq(0) == 15,
				"暗噬基础数值与旧版源码不一致");
		check(weapon.cursed && weapon.isReinforced(), "暗噬没有保留诅咒或强化属性");
		weapon.uncurse();
		check(weapon.cursed, "普通净化错误地移除了暗噬诅咒");

		Hero target = new Hero();
		target.HP = target.HT = 1;
		for (int i = 0; i < ShadowEater.MAX_CHARGE; i++) weapon.proc(hero, target, 10);
		check(weapon.charge() == ShadowEater.MAX_CHARGE, "暗噬击杀充能没有达到20");
		check(weapon.actions(hero).contains(ShadowEater.AC_AWAKE), "暗噬满充能后不能唤醒");

		Bundle saved = new Bundle();
		weapon.storeInBundle(saved);
		ShadowEater restored = new ShadowEater();
		restored.restoreFromBundle(saved);
		check(restored.charge() == ShadowEater.MAX_CHARGE, "暗噬充能存档失败");
		restored.execute(hero, ShadowEater.AC_AWAKE);
		check(restored.charge() == 0 && hero.buff(AttackUp.class) != null
				&& hero.buff(AttackUp.class).level() == 300 && hero.buff(Bleeding.class) != null,
				"暗噬唤醒的增伤、流血或耗能错误");
		restored.execute(hero, ShadowEater.AC_UNCURSE);
		check(!restored.cursed, "暗噬主动驱逐没有暂时解除诅咒");
	}

	private static void testClaimPersistence() {
		Statistics.reset();
		Statistics.potKeyClaimed = true;
		Bundle bundle = new Bundle();
		Statistics.storeInBundle(bundle);
		Statistics.potKeyClaimed = false;
		Statistics.restoreFromBundle(bundle);
		check(Statistics.potKeyClaimed, "罐罐挑战函一次性领取标记未写入存档");
	}

	private static Hero freshHero() {
		Dungeon.depth = 12;
		Dungeon.branch = 0;
		Dungeon.hero = new Hero();
		Dungeon.hero.HP = Dungeon.hero.HT = 100;
		return Dungeon.hero;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
