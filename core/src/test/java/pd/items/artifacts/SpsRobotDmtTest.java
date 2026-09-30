package pd.items.artifacts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Charm;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Dewcharge;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.MoonFury;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpiderQueen;
import pd.items.Generator;
import pd.items.Item;
import pd.items.armor.normalarmor.ErrorArmor;
import pd.items.wands.WandOfError;
import pd.items.weapon.melee.special.ErrorW;
import pd.items.weapon.missiles.throwing.ErrorAmmo;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.Game;
import watabou.utils.Bundle;
import watabou.utils.Random;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.imageio.ImageIO;

public final class SpsRobotDmtTest {
	private static final String[] ICON_HASHES = {
			"C49768450BA26A453223AA60C9DC57A7E80B8B6DB4B4977C7CA250FA50B15CE2",
			"61A3FE51A21CAF9F86E9BDBB6EBA1EC02DF708D182C55A2472D426161183BF55",
			"946A7D4B4405B50C513581DA18A9A1251912D96B0CFF1D163D812A5CA8481843",
			"E36CDE9810254A29EB4CE7CBD62FD16707A39BBA38F6B86E53165D3CC22E79ED"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534552524F52L);
		try {
			testChargeAndSave();
			testAnalysisResults();
			testErrorItemsAndRoutes();
			testIcons();
			System.out.println("SPS机械核心测试通过：充能、十种解析、异常装备、存档、生成与原始图标均正常。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testChargeAndSave() {
		RobotDMT robot = new RobotDMT();
		for (int i = 0; i < 499; i++) robot.advanceCharge();
		check(robot.charge() == 99, "机械核心在500回合前提前充满");
		robot.advanceCharge();
		check(robot.charge() == RobotDMT.FULL_CHARGE, "机械核心未在500回合充满");
		for (int i = 0; i < 20; i++) robot.advanceCharge();
		check(robot.charge() == RobotDMT.FULL_CHARGE, "机械核心充能超过100");
		robot.level(10);
		robot.resolveAnalysis(hero(), 8);

		Bundle bundle = new Bundle();
		robot.storeInBundle(bundle);
		RobotDMT restored = new RobotDMT();
		restored.restoreFromBundle(bundle);
		check(restored.level() == 10 && restored.charge() == 100 && restored.error(),
				"机械核心等级、充能或错误状态未随存档恢复");
	}

	private static void testAnalysisResults() {
		Hero patience = hero();
		new RobotDMT().resolveAnalysis(patience, 0);
		check(patience.buff(Invisibility.class) != null, "耐心解析未给予隐身");
		Hero bravery = hero();
		new RobotDMT().resolveAnalysis(bravery, 1);
		check(bravery.buff(AttackUp.class) != null && bravery.buff(AttackUp.class).level() == 20
				&& bravery.buff(DefenceUp.class) != null && bravery.buff(DefenceUp.class).level() == 20,
				"勇气解析未给予20级攻防强化");
		Hero integrity = hero(); new RobotDMT().resolveAnalysis(integrity, 2);
		check(integrity.buff(MindVision.class) != null, "诚实解析未给予灵视");
		Hero perseverance = hero(); new RobotDMT().resolveAnalysis(perseverance, 3);
		check(perseverance.buff(Bless.class) != null, "坚毅解析未给予祝福");
		Hero kindness = hero(); new RobotDMT().resolveAnalysis(kindness, 4);
		check(kindness.buff(BerryRegeneration.class) != null, "慷慨解析未给予莓果恢复");
		Hero justice = hero(); new RobotDMT().resolveAnalysis(justice, 5);
		check(justice.buff(MoonFury.class) != null, "公正解析未给予月怒");
		Hero soul = hero(); new RobotDMT().resolveAnalysis(soul, 6);
		check(soul.buff(Dewcharge.class) != null, "灵魂解析未给予露珠充能");
		Hero friendship = hero(); new RobotDMT().resolveAnalysis(friendship, 7);
		check(friendship.buffs().isEmpty(), "友谊解析在旧版无效果，不应伪造状态");
		RobotDMT chaos = new RobotDMT(); chaos.resolveAnalysis(hero(), 8);
		check(chaos.error(), "混沌解析未解锁错误分解");
		RobotDMT determination = new RobotDMT(); determination.resolveAnalysis(hero(), 9);
		check(!determination.error(), "决心解析不应解锁错误分解");
	}

	private static void testErrorItemsAndRoutes() {
		check(RobotDMT.errorReward(0) instanceof ErrorW
				&& RobotDMT.errorReward(1) instanceof WandOfError
				&& RobotDMT.errorReward(2) instanceof ErrorArmor
				&& RobotDMT.errorReward(3) instanceof ErrorAmmo,
				"机械核心四种错误奖励映射不符合旧版");
		ErrorAmmo ammo = new ErrorAmmo(3);
		check(ammo.quantity() == 3 && ammo.min() == 10000 && ammo.max() == 10000 && ammo.STRReq() == 0,
				"错误弹丸数量、伤害或力量需求错误");
		ErrorArmor armor = new ErrorArmor();
		armor.upgrade(10);
		check(armor.DRMin() == 0 && armor.DRMax() == 0 && armor.STRReq() == 0,
				"错误护甲升级后不应产生防御或力量需求");
		ErrorW weapon = new ErrorW();
		check(weapon.min() == 0 && weapon.STRReq() == 0, "错误武器初始数值错误");
		testErrorWeaponBehavior(weapon);
		check(Arrays.asList(Generator.Category.ARTIFACT.classes).contains(RobotDMT.class),
				"机械核心未接入普通神器生成池");
		check(SpiderQueen.rareLoot() instanceof RobotDMT
				&& SpiderQueen.rareLoot().isIdentified()
				&& new SpiderQueen().properties().contains(pd.actors.Char.Property.BEAST),
				"蜘蛛女王稀有奖励或野兽属性未恢复");
		check(new RobotDMT().image == ItemSpriteSheet.SPS_ROBOT_HEART,
				"机械核心图标槽位错误");
	}

	private static void testErrorWeaponBehavior(ErrorW weapon) {
		for (int i = 0; i < 20; i++) weapon.upgrade(false);
		check(weapon.min() == 0 && weapon.max() == 20 && weapon.STRReq() == 0,
				"错误武器强化后的伤害成长错误");
		check(weapon.legacyAccuracy() != 1f || weapon.legacyDelay() != 1f,
				"错误武器强化没有随机改变命中或延迟");
		Bundle saved = new Bundle();
		weapon.storeInBundle(saved);
		ErrorW restored = new ErrorW();
		restored.restoreFromBundle(saved);
		check(Math.abs(restored.legacyAccuracy() - weapon.legacyAccuracy()) < 0.0001f
				&& Math.abs(restored.legacyDelay() - weapon.legacyDelay()) < 0.0001f,
				"错误武器随机命中或延迟没有随存档恢复");

		Actor.clear();
		Hero attacker = hero();
		attacker.HP = 50;
		RecordingMob target = new RecordingMob(false);
		target.HP = target.HT = 1_000;
		Actor.add(attacker);
		Actor.add(target);
		for (int i = 0; i < 20_000 && !allErrorEffects(attacker, target); i++) {
			target.HP = target.HT;
			restored.proc(attacker, target, 0);
		}
		check(target.normalDamage >= 1_000 && target.normalDamage < 2_000,
				"错误武器对普通敌人的致死伤害越界：" + target.normalDamage);
		check(allErrorEffects(attacker, target), "错误武器没有触发全部随机状态或治疗分支");

		RecordingMob boss = new RecordingMob(true);
		boss.HP = boss.HT = 1_000;
		Actor.add(boss);
		for (int i = 0; i < 20_000 && boss.bossDamage == 0; i++) restored.proc(attacker, boss, 0);
		check(boss.bossDamage >= 125 && boss.bossDamage < 250,
				"错误武器对首领的生命比例伤害越界：" + boss.bossDamage);
	}

	private static boolean allErrorEffects(Hero attacker, RecordingMob target) {
		return target.normalDamage > 0 && attacker.HP > 50
				&& target.buff(Cripple.class) != null && target.buff(Bleeding.class) != null
				&& target.buff(Vertigo.class) != null && target.buff(Terror.class) != null
				&& target.buff(Paralysis.class) != null && target.buff(Roots.class) != null
				&& target.buff(Ooze.class) != null && target.buff(Charm.class) != null;
	}

	private static Hero hero() {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static final class RecordingMob extends Mob {
		int normalDamage;
		int bossDamage;
		RecordingMob(boolean boss) {
			if (boss) properties.add(Char.Property.BOSS);
		}
		@Override public void damage(int damage, Object source) {
			if (properties().contains(Char.Property.BOSS)) bossDamage = damage;
			else normalDamage = damage;
		}
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		for (int i = 0; i < ICON_HASHES.length; i++) {
			String actual = hash(sheet, 32 + i * 16, 880);
			check(ICON_HASHES[i].equals(actual),
					"机械核心或错误装备第" + (i + 1) + "个图标与旧版像素不一致：" + actual);
		}
	}

	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder out = new StringBuilder(64);
		for (byte value : digest) out.append(String.format("%02X", value & 0xFF));
		return out.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsRobotDmtTest() { }
}
