package pd.items.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BeTired;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.EnergyArmor;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.HighLight;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.Rhythm;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Silent;
import pd.actors.buffs.faithbuff.BalanceFaith;
import pd.actors.buffs.faithbuff.DemonFaith;
import pd.actors.buffs.faithbuff.FaithBuff;
import pd.actors.buffs.faithbuff.HumanFaith;
import pd.actors.buffs.faithbuff.LifeFaith;
import pd.actors.buffs.faithbuff.MechFaith;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.DM300;
import pd.actors.mobs.Rat;
import pd.actors.mobs.Tengu;
import pd.actors.mobs.Zot;
import pd.items.weapon.melee.normalweapon.TrickSand;
import pd.items.weapon.melee.normalweapon.WoodenStaff;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.Game;
import watabou.utils.Bundle;
import watabou.utils.Random;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsFaithAsceticStartTest {

	private static final String[] ICON_HASHES = {
			"49C6702D643DE390FAE02010934935E059A50D75E4C461B67C68ED565E6D1530",
			"7ABCCCD571CBD6A67BFFE96A42B3D9CE293C1365E9B2B0B5089027E7389A6874",
			"8387D643695230A22AB74D1ED4D746814986EBB1AB66D75452C668243F59E2BB",
			"C9AEE734D0DE1EB0C6AFF5EEA117CBAD222BB67423C9C00B8ECC6382EF2C4E23"
	};

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350534641495448L);
		try {
			testWoodenStaff();
			testTrickSand();
			testFaiths();
			testFatigueAndBattery();
			testItemsAndIcons();
			System.out.println("SPS信徒与苦修者开局测试通过：木杖、诡异沙尘、五派信仰、蓄电池、疲劳、存档及原始图标均正常。");
		} finally {
			Random.popGenerator();
		}
	}

	private static void testWoodenStaff() {
		Hero hero = hero();
		TestMob defender = mob(1000);
		WoodenStaff charged = new WoodenStaff();
		WoodenStaff isolated = new WoodenStaff();
		for (int i = 0; i < 8; i++) charged.proc(hero, defender, 1);
		check(charged.charge() == 8 && isolated.charge() == 0, "木杖充能被不同实例共享");
		Random.pushGenerator(12345L);
		int ordinary = isolated.damageRoll(hero);
		Random.popGenerator();
		Random.pushGenerator(12345L);
		int empowered = charged.damageRoll(hero);
		Random.popGenerator();
		check(empowered == ordinary * 5, "木杖满充能没有造成五倍伤害");
		charged.proc(hero, defender, empowered);
		check(charged.charge() == 1, "木杖强化攻击后的积蓄重置错误");
		Bundle bundle = new Bundle();
		charged.storeInBundle(bundle);
		WoodenStaff restored = new WoodenStaff();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 1, "木杖积蓄没有随存档恢复");
		check(close(restored.legacyAccuracy(8), 1.2f), "木杖升级命中成长错误");
	}

	private static void testTrickSand() {
		Hero hero = hero();
		CountingMob target = new CountingMob();
		Buff.affect(target, ShieldArmor.class).level(20);
		TrickSand sand = new TrickSand();
		sand.proc(hero, target, 10);
		check(target.damageCalls == 1 && target.totalDamage == 10, "诡异沙尘没有对护盾追加等量伤害");
		check(target.buff(Silent.class) != null, "诡异沙尘没有施加6回合沉默");
		sand.proc(hero, target, 10);
		check(target.damageCalls == 3 && target.totalDamage == 25, "诡异沙尘没有对已沉默目标追加半额伤害");
		hero.belongings.weapon = sand;
		check(sand.reachFactor(hero) == 2 && sand.min(4) == 9 && sand.max(4) == 18, "诡异沙尘距离或升级成长错误");
	}

	private static void testFaiths() {
		Hero hero = hero();
		TestMob nature = mob(100).with(Char.Property.BEAST);
		TestMob mech = mob(100).with(Char.Property.MECH);
		TestMob holy = mob(100).with(Char.Property.HUMAN);
		TestMob demon = mob(100).with(Char.Property.DEMONIC);
		TestMob boss = mob(100).with(Char.Property.BOSS);
		check(FaithBuff.nature(new Rat()), "老鼠没有归入旧版自然派系");
		check(FaithBuff.mechanical(new DM300()), "DM-300没有归入旧版机械派系");
		check(FaithBuff.holy(new Tengu()), "天狗没有归入旧版神圣派系");
		check(FaithBuff.demonic(new Zot()), "Zot没有归入旧版恶魔派系");

		Buff.affect(hero, MechFaith.class);
		check(close(FaithBuff.outgoingMultiplier(hero, nature), 1.5f)
				&& close(FaithBuff.incomingMultiplier(hero, mech), .75f), "机械信仰倍率错误");
		Buff.detach(hero, MechFaith.class);
		Buff.affect(hero, LifeFaith.class);
		check(close(FaithBuff.outgoingMultiplier(hero, mech), 1.5f)
				&& close(FaithBuff.incomingMultiplier(hero, nature), .75f), "自然信仰倍率错误");
		Buff.detach(hero, LifeFaith.class);
		Buff.affect(hero, DemonFaith.class);
		check(close(FaithBuff.outgoingMultiplier(hero, holy), 1.5f)
				&& close(FaithBuff.incomingMultiplier(hero, demon), .75f), "恶魔信仰倍率错误");
		Buff.detach(hero, DemonFaith.class);
		Buff.affect(hero, HumanFaith.class);
		check(close(FaithBuff.outgoingMultiplier(hero, demon), 1.5f)
				&& close(FaithBuff.incomingMultiplier(hero, holy), .75f), "神圣信仰倍率错误");
		Buff.detach(hero, HumanFaith.class);
		Buff.affect(hero, BalanceFaith.class);
		check(close(FaithBuff.outgoingMultiplier(hero, boss), 1.5f)
				&& close(FaithBuff.incomingMultiplier(hero, boss), .75f), "平衡信仰倍率错误");
	}

	private static void testFatigueAndBattery() {
		Hero hero = hero();
		TestMob target = mob(100);
		BeTired fatigue = Buff.affect(target, BeTired.class).set(30f);
		for (int i = 0; i < 16; i++) target.damage(0, hero);
		check(fatigue.level() == 16, "疲劳没有逐次累计受击次数");
		fatigue.act();
		check(target.HP == 90 && target.buff(BeTired.class) == null, "疲劳满载没有造成最大生命10%的伤害");

		BigBattery battery = new BigBattery();
		for (int i = 0; i < 50; i++) battery.gainCharge();
		check(battery.charge() == 20 && battery.consumeCharge() && battery.charge() == 5, "蓄电池充能上限或消耗错误");
		battery.empower(hero);
		check(hero.buff(Recharging.class) != null && hero.buff(Arcane.class) != null
				&& hero.buff(HighLight.class) != null && hero.buff(AttackUp.class).level() == 30
				&& hero.buff(DefenceUp.class).level() == 30 && hero.buff(HasteBuff.class) != null
				&& hero.buff(Rhythm.class) != null, "蓄电池充能强化效果不完整");
		Bundle bundle = new Bundle();
		battery.storeInBundle(bundle);
		BigBattery restored = new BigBattery();
		restored.restoreFromBundle(bundle);
		check(restored.charge() == 5, "蓄电池能量没有随存档恢复");
	}

	private static void testItemsAndIcons() throws Exception {
		FaithSign sign = new FaithSign();
		Hero hero = hero();
		check(sign.value() == 30 && sign.actions(hero).size() >= 5, "信标盒价格或派系动作不完整");
		check(sign.image == ItemSpriteSheet.LEGACY_FAITH_SIGN && new BigBattery().image == ItemSpriteSheet.LEGACY_BIG_BATTERY
				&& new WoodenStaff().image == ItemSpriteSheet.LEGACY_WOODEN_STAFF
				&& new TrickSand().image == ItemSpriteSheet.LEGACY_TRICK_SAND, "信徒或苦修者物品图标槽错误");
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		for (int i = 0; i < ICON_HASHES.length; i++) {
			check(ICON_HASHES[i].equals(hash(sheet, i * 16, 192)), "信徒或苦修者第" + (i + 1) + "个原始图标错误");
		}
	}

	private static Hero hero() {
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static TestMob mob(int health) { TestMob mob = new TestMob(); mob.HP = mob.HT = health; return mob; }
	private static boolean close(float a, float b) { return Math.abs(a - b) < .0001f; }
	private static String hash(BufferedImage sheet, int left, int top) throws Exception {
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = top; y < top + 16; y++) for (int x = left; x < left + 16; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder result = new StringBuilder(64);
		for (byte value : digest) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}
	private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }

	private static class TestMob extends Mob {
		TestMob with(Char.Property property) { properties.add(property); return this; }
		@Override public int drRoll() { return 0; }
	}

	private static final class CountingMob extends TestMob {
		int damageCalls;
		int totalDamage;
		@Override public void damage(int damage, Object source) { damageCalls++; totalDamage += damage; }
	}

	private SpsFaithAsceticStartTest() { }
}
