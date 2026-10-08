package pd.actors.mobs;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Badges;
import pd.Challenges;
import pd.Dungeon;
import pd.QuickSlot;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.DarkFallen;
import pd.actors.buffs.HiddenShadow;
import pd.actors.buffs.Hunger;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Terror;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.RedDewdrop;
import pd.items.consum.food.staplefood.Pasty;
import pd.items.consum.potions.PotionOfShield;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.equipment.wands.WandOfMagicMissile;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.normalweapon.BattleAxe;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.Trap;
import pd.plants.Plant;
import pd.sprites.CharSprite;
import pd.sprites.ErrorSprite;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.serialize.Bundle;
import render.utils.serialize.FileUtils;
import render.utils.serialize.Reflection;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsLegacyChallengesTest {

	private static final int WIDTH = 32;
	private static final int CENTER = 16 + 16 * WIDTH;
	private static final String ERROR_SPRITE_HASH =
			"76039300D2E4D464911D7104729AA65E2D4983638062B87DDE51639962D3CEBC";
	private static final String SHADOW_RAT_SPRITE_HASH =
			"7B7580B84402C02D3E895891611087E4B363C1826E4FF22BA37F0233C866DC4E";

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		FileUtils.setDefaultFileProperties(Files.FileType.Absolute,
				System.getProperty("java.io.tmpdir") + "sps-legacy-challenges" + File.separator);
		Game.version = "test";
		Badges.loadGlobal();
		Scroll.initLabels();
		try {
			testChallengeRegistryAndStart();
			testEnergyLost();
			testDarknessClockAndMemory();
			testDarknessStartAndBuff();
			testDarkLiverSpawnAndRules();
			testDarkLiverCombatAndMapping();
			testAbrasion();
			testElementalStorm();
			testVirusStatsAndDecay();
			testSpawnAndFallback();
			testSummonRecursionGuards();
			testGasResourcesAndSprite();
			System.out.println("SPS旧版挑战通过：梦魇病毒、能量流失、没入黑暗、严重磨损与元素风暴的开局补偿、战斗规则、昼夜、地图记忆、夜影、耐久、边界保护、双语资源与原始素材均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			Dungeon.challenges = 0;
			app.exit();
		}
	}

	private static void testChallengeRegistryAndStart() throws Exception {
		check(Challenges.NIGHTMARE_VIRUS == 8192, "梦魇病毒没有使用独立挑战位");
		check(Challenges.MAX_VALUE == 262143 && Challenges.MAX_CHALS == 18,
				"挑战位上限没有包含元素风暴");
		check(Challenges.MASKS[11] == Challenges.NIGHTMARE_VIRUS
				&& "nightmare_virus".equals(Challenges.NAME_IDS[11])
				&& Challenges.MASKS[12] == Challenges.ENERGY_LOST
				&& "energy_lost".equals(Challenges.NAME_IDS[12])
				&& Challenges.MASKS[13] == Challenges.DEW_REJECTION
				&& Challenges.MASKS[14] == Challenges.SPS_DARKNESS
				&& "sps_darkness".equals(Challenges.NAME_IDS[14])
				&& Challenges.MASKS[15] == Challenges.ABRASION
				&& "abrasion".equals(Challenges.NAME_IDS[15])
				&& Challenges.MASKS[16] == Challenges.ELE_STOME
				&& "ele_stome".equals(Challenges.NAME_IDS[16])
				&& Challenges.MASKS[17] == Challenges.TEST_TIME,
				"梦魇病毒、能量流失、没入黑暗、严重磨损或元素风暴没有按旧版顺序接入挑战列表");

		freshLevel();
		Dungeon.challenges = Challenges.NIGHTMARE_VIRUS;
		Hero hero = Dungeon.hero;
		Method challengeStarts = HeroClass.class.getDeclaredMethod("applySpsChallengeStarts", Hero.class);
		challengeStarts.setAccessible(true);
		challengeStarts.invoke(null, hero);
		//SPSEXPD: 安卡不再有「未祝福」形态，补偿直接给安卡
		check(hero.belongings.getAllItems(pd.items.Ankh.class).size() == 1,
				"梦魇病毒开局没有补偿安卡");
	}

	private static void testDarknessClockAndMemory() {
		Statistics.reset();
		check(Statistics.spsTime == 360 && Statistics.spsDays == 0 && Statistics.spsNight(),
				"SPS昼夜没有从第0天06:00夜间边界开始");
		Statistics.advanceSpsTime(721);
		check(Statistics.spsTime == 1081 && Statistics.spsDays == 0 && Statistics.spsNight(),
				"SPS夜间边界推进错误");
		Statistics.advanceSpsTime(3239);
		check(Statistics.spsTime == 0 && Statistics.spsDays == 3 && Statistics.spsNight(),
				"SPS昼夜没有正确处理整点或多日跨越");
		Bundle saved = new Bundle();
		Statistics.storeInBundle(saved);
		Statistics.spsTime = 500;
		Statistics.spsDays = 0;
		Statistics.restoreFromBundle(saved);
		check(Statistics.spsTime == 0 && Statistics.spsDays == 3, "SPS昼夜没有随存档恢复");
		Bundle legacy = new Bundle();
		Statistics.restoreFromBundle(legacy);
		check(Statistics.spsTime == 360 && Statistics.spsDays == 0,
				"缺少昼夜字段的旧存档没有使用兼容默认值");

		TestLevel level = freshLevel();
		int far = 30 + 30 * WIDTH;
		level.visited[far] = true;
		level.mapped[far] = true;
		Dungeon.observe(4);
		check(level.visited[far] && level.mapped[far], "普通模式错误遗忘已探索地图");
		Dungeon.challenges = Challenges.SPS_DARKNESS;
		Dungeon.observe(4);
		check(!level.visited[far] && level.mapped[far],
				"没入黑暗没有遗忘视野外地图，或错误清除了永久映射");

		Statistics.reset();
		FixedHero hero = (FixedHero) Dungeon.hero;
		hero.spendTime(1081);
		check(Statistics.spsTime == 1 && Statistics.spsDays == 1,
				"英雄行动耗时没有推进SPS昼夜并正确跨日");
	}

	private static void testDarknessStartAndBuff() throws Exception {
		freshLevel();
		Dungeon.challenges = Challenges.SPS_DARKNESS;
		Hero hero = Dungeon.hero;
		Method challengeStarts = HeroClass.class.getDeclaredMethod("applySpsChallengeStarts", Hero.class);
		challengeStarts.setAccessible(true);
		challengeStarts.invoke(null, hero);
		ScrollOfMagicMapping mapping = hero.belongings.getItem(ScrollOfMagicMapping.class);
		check(mapping != null && mapping.quantity() == 2 && mapping.isKnown(),
				"没入黑暗开局没有两张已识别探地卷轴");
		hero.live();
		check(hero.buff(DarkFallen.class) != null, "没入黑暗没有为英雄附加暗影降临状态");
	}

	private static void testDarkLiverSpawnAndRules() {
		TestLevel level = freshLevel();
		Arrays.fill(level.heroFOV, false);
		Dungeon.depth = 2;
		DarkFallen fallen = new DarkFallen();
		fallen.act();
		check(countDarkLivers(level) == 1, "非商店层没有生成一只夜影");
		fallen.act();
		check(countDarkLivers(level) == 1, "同一层生成了多只夜影");

		level = freshLevel();
		Arrays.fill(level.heroFOV, false);
		Dungeon.depth = 6;
		new DarkFallen().act();
		check(countDarkLivers(level) == 0, "商店层错误生成夜影");

		level = freshLevel();
		Arrays.fill(level.passable, false);
		Arrays.fill(level.heroFOV, false);
		Dungeon.depth = 2;
		new DarkFallen().act();
		check(countDarkLivers(level) == 0, "没有合法出生点时仍生成夜影");

		Statistics.spsDays = 12;
		TestDarkLiver liver = new TestDarkLiver();
		check(liver.HT == 22 && liver.HP == 22 && liver.defenseSkill == 12
				&& liver.EXP == 1 && liver.viewDistance == 3 && liver.flying,
				"夜影生命、闪避、经验、视野或飞行属性不符合旧版");
		check(liver.properties().contains(Char.Property.UNKNOW)
				&& liver.attackSkill(Dungeon.hero) == 17 && liver.drRoll() == 12,
				"夜影属性、命中或防御没有随天数成长");
		for (int i = 0; i < 100; i++) {
			int damage = liver.damageRoll();
			check(damage >= 12 && damage <= 48, "夜影伤害超出天数至四倍天数范围：" + damage);
		}
		liver.pos = CENTER;
		Dungeon.hero.pos = CENTER + 1;
		Statistics.spsTime = 1200;
		check(liver.canStrike(Dungeon.hero), "夜间相邻夜影不能攻击英雄");
		Statistics.spsTime = 700;
		check(!liver.canStrike(Dungeon.hero), "白天夜影仍能攻击英雄");
	}

	private static void testDarkLiverCombatAndMapping() {
		TestLevel level = freshLevel();
		Statistics.spsDays = 5;
		TestDarkLiver liver = new TestDarkLiver();
		liver.pos = CENTER;
		liver.sprite = new SilentSprite();
		liver.HP = 1;
		liver.setEnemy(null);
		liver.recover();
		check(liver.HP == 6, "夜影未与目标相邻时没有恢复5点生命");

		liver.move(CENTER + 1, false);
		check(liver.buff(HiddenShadow.class) != null, "夜影移动后没有获得3回合隐藏");
		for (int i = 0; i < 100 && Dungeon.hero.buff(Terror.class) == null; i++) {
			liver.attackProc(Dungeon.hero, 1);
		}
		check(liver.buff(HiddenShadow.class) == null, "夜影攻击时没有移除隐藏");
		check(Dungeon.hero.buff(Terror.class) != null, "夜影多次攻击始终未触发20%恐惧");

		Arrays.fill(level.mapped, false);
		Arrays.fill(level.discoverable, true);
		int deathPos = liver.pos;
		liver.reveal();
		for (int cell = 0; cell < level.length(); cell++) {
			if (level.distance(cell, deathPos) < 3) {
				check(level.mapped[cell], "夜影死亡没有永久绘制半径3内格子：" + cell);
			}
		}
	}

	private static int countDarkLivers(TestLevel level) {
		int count = 0;
		for (Mob mob : level.mobs()) if (mob instanceof DarkFallen.DarkLiver) count++;
		return count;
	}

	private static void testAbrasion() throws Exception {
		for (int i = 0; i < Generator.Category.WEAPON.classes.length; i++) {
			Weapon weapon = (Weapon) Reflection.newInstance(Generator.Category.WEAPON.classes[i]);
			check(weapon.usesSpsAbrasion() == (i < 30),
					"严重磨损武器白名单与旧版前30件不一致，索引：" + i);
		}

		TestLevel level = freshLevel();
		TestMob target = mobAt(level, CENTER);
		BattleAxe normal = new BattleAxe();
		Dungeon.hero.belongings.weapon = normal;
		normal.proc(Dungeon.hero, target, 10);
		check(normal.spsDurability() == 100, "未开启严重磨损时错误消耗武器耐久");

		Dungeon.challenges = Challenges.ABRASION;
		normal.proc(target, Dungeon.hero, 10);
		check(normal.spsDurability() == 100, "非英雄攻击错误消耗武器耐久");
		for (int i = 0; i < 90; i++) normal.proc(Dungeon.hero, target, 10);
		check(normal.spsDurability() == 10 && Dungeon.hero.belongings.weapon == normal,
				"严重磨损没有在第90次命中降到警告耐久");
		for (int i = 0; i < 10; i++) normal.proc(Dungeon.hero, target, 10);
		check(normal.spsDurability() == 0 && Dungeon.hero.belongings.weapon == null,
				"常规武器耐久耗尽后没有报废并清空装备槽");

		BattleAxe upgraded = new BattleAxe();
		upgraded.upgrade();
		check(upgraded.spsDurability() == 110, "强化没有增加10点武器耐久");
		Dungeon.hero.belongings.secondWep = upgraded;
		for (int i = 0; i < 110; i++) upgraded.proc(Dungeon.hero, target, 10);
		check(Dungeon.hero.belongings.secondWep == null,
				"副武器耐久耗尽后没有安全清理实际装备槽");

		BattleAxe savedWeapon = new BattleAxe();
		Dungeon.hero.belongings.weapon = savedWeapon;
		for (int i = 0; i < 43; i++) savedWeapon.proc(Dungeon.hero, target, 10);
		Bundle bundle = new Bundle();
		savedWeapon.storeInBundle(bundle);
		BattleAxe restored = new BattleAxe();
		restored.restoreFromBundle(bundle);
		check(restored.spsDurability() == 57, "常规武器剩余耐久没有随存档恢复");

		freshLevel();
		Dungeon.challenges = Challenges.ABRASION;
		Hero hero = Dungeon.hero;
		Method challengeStarts = HeroClass.class.getDeclaredMethod("applySpsChallengeStarts", Hero.class);
		challengeStarts.setAccessible(true);
		challengeStarts.invoke(null, hero);
		ScrollOfUpgrade upgrade = hero.belongings.getItem(ScrollOfUpgrade.class);
		ScrollOfMagicalInfusion infusion = hero.belongings.getItem(ScrollOfMagicalInfusion.class);
		check(upgrade != null && upgrade.isKnown() && infusion != null && infusion.isKnown(),
				"严重磨损开局没有已识别的升级卷轴与注魔卷轴");
	}

	private static void testEnergyLost() throws Exception {
		freshLevel();
		Hunger normal = Buff.affect(Dungeon.hero, Hunger.class);
		normal.affectHunger(-100);
		normal.satisfy(50);
		check(normal.hunger() == 50, "未开启能量流失时错误削减饱食收益");
		WandOfMagicMissile normalWand = new WandOfMagicMissile();
		normalWand.level(5);
		check(normalWand.maxCharges == 13,
				"未开启能量流失时魔弹法杖没有按每级+2、上限21计算：" + normalWand.maxCharges);

		freshLevel();
		Dungeon.challenges = Challenges.ENERGY_LOST;
		Hunger challenged = Buff.affect(Dungeon.hero, Hunger.class);
		challenged.affectHunger(-100);
		challenged.satisfy(50);
		check(challenged.hunger() == 80, "能量流失没有把正向饱食收益降为40%");
		challenged.satisfy(-10);
		check(challenged.hunger() == 90, "能量流失错误缩放了负向饱食变化");

		WandOfMagicMissile wand = new WandOfMagicMissile();
		wand.level(5);
		check(wand.maxCharges == 5, "能量流失下5级魔弹法杖最大充能不是5：" + wand.maxCharges);
		wand.curCharges = 20;
		wand.level(20);
		check(wand.maxCharges == 11 && wand.curCharges == 11,
				"能量流失没有按每5级+2、上限21计算并收束当前充能");

		Hero hero = Dungeon.hero;
		Method challengeStarts = HeroClass.class.getDeclaredMethod("applySpsChallengeStarts", Hero.class);
		challengeStarts.setAccessible(true);
		challengeStarts.invoke(null, hero);
		check(hero.belongings.getItem(Pasty.class) != null,
				"能量流失开局没有补偿额外干粮");
	}

	private static void testElementalStorm() throws Exception {
		TestLevel level = freshLevel();
		TestMob attacker = mobAt(level, CENTER);
		RecordingHero target = new RecordingHero();
		Statistics.deepestFloor = 24;
		check(attacker.attackProc(target, 11) == 11 && target.damageCalls == 0,
				"未开启元素风暴时怪物错误追加了能量伤害");

		Dungeon.challenges = Challenges.ELE_STOME;
		attacker.attackProc(target, 11);
		check(target.damageCalls == 1 && target.lastDamage == 4
				&& target.lastSource == DamageType.ENERGY_DAMAGE,
				"元素风暴追加伤害不是最深层数除以5的能量伤害");
		Statistics.deepestFloor = 4;
		attacker.attackProc(target, 11);
		check(target.damageCalls == 2 && target.lastDamage == 0,
				"元素风暴在第5层前没有保留源码的0点追加伤害");

		TestMob wandTarget = mobAt(level, CENTER + 1);
		wandTarget.HT = wandTarget.HP = 100;
		wandTarget.damage(11, new WandOfMagicMissile());
		check(wandTarget.HP == 91, "元素风暴没有把11点法杖伤害向上取整为9点");
		wandTarget.damage(11, new Object());
		check(wandTarget.HP == 80, "元素风暴错误缩放了非法杖伤害");

		freshLevel();
		Dungeon.challenges = Challenges.ELE_STOME;
		Hero hero = Dungeon.hero;
		Method challengeStarts = HeroClass.class.getDeclaredMethod("applySpsChallengeStarts", Hero.class);
		challengeStarts.setAccessible(true);
		challengeStarts.invoke(null, hero);
		ScrollOfPsionicBlast blast = hero.belongings.getItem(ScrollOfPsionicBlast.class);
		PotionOfShield shield = hero.belongings.getItem(PotionOfShield.class);
		check(blast != null && blast.isKnown() && shield != null && shield.isKnown(),
				"元素风暴开局没有已识别的灵能爆发卷轴与护盾药水");
	}

	private static void testVirusStatsAndDecay() {
		freshLevel();
		FixedHero hero = (FixedHero) Dungeon.hero;
		hero.HT = hero.HP = 100;
		hero.lvl = 20;
		TestVirus virus = new TestVirus();
		check(virus.HT == 20 && virus.HP == 20 && virus.EXP == 0,
				"病毒生命或经验没有复制旧版规则");
		check(virus.attackSkill(hero) == 37 && virus.defenseSkill == 29,
				"病毒没有复制英雄的命中与闪避");
		for (int i = 0; i < 100; i++) {
			int damage = virus.damageRoll();
			check(damage >= 10 && damage <= 20, "病毒伤害超出英雄等级一半至英雄等级范围：" + damage);
		}
		check(virus.drRoll() == 0 && virus.speed() == 1f,
				"病毒防御或速度不符合旧版");
		check(virus.properties().contains(Char.Property.UNKNOW)
				&& virus.properties().contains(Char.Property.BOSS),
				"病毒缺少UNKNOW或BOSS属性");
		check(virus.isImmune(Burning.class) && virus.isImmune(ToxicGas.class)
				&& virus.isImmune(ScrollOfPsionicBlast.class) && virus.isImmune(CorruptGas.class),
				"病毒缺少旧版四项免疫");
		check(!virus.add(new Poison()) && virus.buff(Poison.class) == null,
				"病毒仍可被直接附加Buff或Debuff");
		virus.pos = CENTER;
		virus.sprite = new SilentSprite();
		virus.sprite.visible = false;
		int oldHP = virus.HP;
		virus.tick();
		check(virus.HP == oldHP - 1, "病毒每回合没有自损1点生命");
	}

	private static void testSpawnAndFallback() {
		TestLevel level = freshLevel();
		TestMob mob = mobAt(level, CENTER);
		check(mob.spawnVirus() == null && level.mobs().size() == 1,
				"未开启挑战时错误生成病毒");

		Dungeon.challenges = Challenges.NIGHTMARE_VIRUS;
		Virus virus = mob.spawnVirus();
		check(virus != null && level.mobs().contains(virus), "普通怪死亡路径没有生成病毒");
		int delta = Math.abs(virus.pos - CENTER);
		check(delta == 1 || delta == WIDTH, "病毒没有生成在尸体四个正方向：" + virus.pos);

		level = freshLevel();
		Dungeon.challenges = Challenges.NIGHTMARE_VIRUS;
		mob = mobAt(level, 33);
		Arrays.fill(level.passable, false);
		level.passable[1] = true;
		check(mob.spawnVirus() == null, "地图边缘把跨边界格错误当作病毒出生点");
		Heap edgeFallback = level.heaps.get(33);
		check(edgeFallback != null && edgeFallback.peek() instanceof RedDewdrop,
				"地图边缘无合法位置时没有掉落红色露珠");

		level = freshLevel();
		Dungeon.challenges = Challenges.NIGHTMARE_VIRUS;
		mob = mobAt(level, CENTER);
		Arrays.fill(level.passable, false);
		check(mob.spawnVirus() == null, "四周堵塞时错误生成病毒");
		Heap fallback = level.heaps.get(CENTER);
		check(fallback != null && fallback.peek() instanceof RedDewdrop,
				"四周堵塞时没有在尸体处掉落红色露珠");
	}

	private static void testSummonRecursionGuards() {
		freshLevel();
		check(!new TestVirus().canSpawnVirus(), "病毒体死亡会递归生成病毒");
		Swarm original = new Swarm();
		check(original.spawnsNightmareVirusOnDeath(), "原生蝇群被错误排除在挑战外");
		original.generation = 1;
		check(!original.spawnsNightmareVirusOnDeath(), "蝇群分裂体仍会递归生成病毒");
		check(!new SpsCaveMobs.SandMob.MiniSand().spawnsNightmareVirusOnDeath(),
				"沙虫召唤体仍会递归生成病毒");
		check(!new SpsCityMobs.SummonedSkeleton().spawnsNightmareVirusOnDeath(),
				"骷髅召唤体仍会递归生成病毒");
	}

	private static void testGasResourcesAndSprite() throws Exception {
		freshLevel();
		TestVirus virus = new TestVirus();
		virus.pos = CENTER;
		virus.releaseGas();
		check(Blob.volumeAt(CENTER, CorruptGas.class) == 20,
				"病毒死亡没有释放20量腐化气体");
		check(virus.spriteClass == ErrorSprite.class, "病毒没有使用旧版错误精灵");

		String miscZh = read("messages/misc/zh/misc.properties");
		String miscEn = read("messages/misc/en/misc.properties");
		String actorsZh = read("messages/actors/zh/actors.properties");
		String actorsEn = read("messages/actors/en/actors.properties");
		check(miscZh.contains("challenges.nightmare_virus=梦魇病毒")
				&& miscEn.contains("challenges.nightmare_virus=nightmare virus")
				&& miscZh.contains("challenges.energy_lost=能量流失")
				&& miscEn.contains("challenges.energy_lost=energy lost")
				&& actorsZh.contains("actors.mobs.virus.name=")
				&& actorsEn.contains("actors.mobs.virus.name=")
				&& miscZh.contains("challenges.sps_darkness=没入黑暗")
				&& miscEn.contains("challenges.sps_darkness=Into darkness")
				&& actorsZh.contains("actors.buffs.darkfallen$darkliver.name=夜影")
				&& actorsZh.contains("actors.buffs.darkfallen$darkliver.desc=和时间相关，只在晚上攻击。")
				&& actorsEn.contains("actors.buffs.darkfallen$darkliver.name=night liver")
				&& miscZh.contains("challenges.abrasion=严重磨损")
				&& miscEn.contains("challenges.abrasion=abrasion")
				&& miscZh.contains("challenges.ele_stome=元素风暴")
				&& miscZh.contains("-敌人额外造成属性法术伤害。")
				&& miscEn.contains("challenges.ele_stome=Elemental storm")
				&& miscEn.contains("-The enemy deals energy damage once a attack."),
				"梦魇病毒、没入黑暗、严重磨损或元素风暴中英文资源缺失");
		check(!miscZh.contains("�") && !actorsZh.contains("�"), "梦魇病毒中文资源存在乱码");
		check(ERROR_SPRITE_HASH.equals(fileHash("sprites/mobs/sps_error.png")),
				"梦魇病毒精灵不是旧版原始素材");
		check(SHADOW_RAT_SPRITE_HASH.equals(fileHash("sprites/mobs/sps_shadow_rat.png")),
				"夜影精灵不是旧版原始素材");
	}

	private static TestLevel freshLevel() {
		Actor.clear();
		Dungeon.depth = 1;
		Dungeon.branch = 0;
		Dungeon.challenges = 0;
		Dungeon.quickslot = new QuickSlot();
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		FixedHero hero = new FixedHero();
		hero.heroClass = HeroClass.WARRIOR;
		hero.pos = 66;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return level;
	}

	private static TestMob mobAt(TestLevel level, int pos) {
		TestMob mob = new TestMob();
		mob.pos = pos;
		mob.alignment = Char.Alignment.NEUTRAL;
		level.mobs().add(mob);
		Actor.add(mob);
		return mob;
	}

	private static String read(String path) throws Exception {
		return java.nio.file.Files.readString(Paths.get(path), StandardCharsets.UTF_8);
	}

	private static String fileHash(String path) throws Exception {
		byte[] bytes = java.nio.file.Files.readAllBytes(Paths.get(path));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
		StringBuilder result = new StringBuilder(digest.length * 2);
		for (byte value : digest) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class FixedHero extends Hero {
		@Override public int attackSkill(Char target) { return 37; }
		@Override public int defenseSkill(Char enemy) { return 29; }
		void spendTime(float time) { spend(time); }
	}

	private static final class RecordingHero extends Hero {
		int damageCalls;
		int lastDamage;
		Object lastSource;
		@Override public void damage(int damage, Object source) {
			damageCalls++;
			lastDamage = damage;
			lastSource = source;
		}
	}

	private static final class TestDarkLiver extends DarkFallen.DarkLiver {
		boolean canStrike(Char enemy) { return canAttack(enemy); }
		void setEnemy(Char enemy) { this.enemy = enemy; }
		void recover() { recoverWhileSeparated(); }
		void reveal() { revealSurroundings(); }
	}

	private static final class SilentSprite extends CharSprite {
		@Override public void showAlert() { }
		@Override public void hideAlert() { }
		@Override public void hideLost() { }
		@Override public void hideInvestigate() { }
	}

	private static final class TestVirus extends Virus {
		boolean tick() { return super.act(); }
		boolean canSpawnVirus() { return spawnsNightmareVirusOnDeath(); }
		void releaseGas() { releaseCorruptGas(); }
	}

	private static final class TestMob extends Mob {
		Virus spawnVirus() { return spawnNightmareVirusOnDeath(); }
		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(WIDTH, WIDTH);
			Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<Trap>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
			discoverable = new boolean[length()];
			Arrays.fill(discoverable, true);
			Arrays.fill(heroFOV, true);
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
		@Override public Heap drop(Item item, int cell) {
			Heap heap = heaps.get(cell);
			if (heap == null) {
				heap = new Heap();
				heap.pos = cell;
				heaps.put(cell, heap);
			}
			heap.drop(item);
			return heap;
		}
	}

	private SpsLegacyChallengesTest() { }
}
