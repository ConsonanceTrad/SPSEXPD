package pd.items.weapon.missiles.throwing;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Charm;
import pd.actors.buffs.HolyStun;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.TownNpc;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.plants.Plant;
import pd.sprites.ItemSpriteSheet;
import pd.sprites.LynnSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.SparseArray;
import com.watabou.noosa.Game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

public final class SpsLynnDollTest {

	private static final String ICON_HASH =
			"866D0F658ADFA30A1324A01777359C5C2F159C470AA1A92F48D9445D17D7DAE2";

	public static void main(String[] args) throws Exception {
		Game.version = "test";
		try {
			testItemNpcAndPenalty();
			testSummonAndCombat();
			testPersistenceAndIcon();
			System.out.println("SPS梦瑶娃娃测试通过：Lynn惩罚、远程召唤、诅咒少女AI、状态、存档和原始图像均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
		}
	}

	private static void testItemNpcAndPenalty() {
		level();
		Hero hero = hero(27);
		TownNpc lynn = new TownNpc().configure(TownNpc.Spec.LYNN);
		LynnDoll doll = (LynnDoll)lynn.SupercreateLoot();
		check(lynn.properties().contains(Char.Property.ELF), "Lynn缺少精灵属性");
		check(doll.quantity() == 1 && doll.min() == 1 && doll.max() == 1 && doll.STRReq() == 10
				&& doll.value() == 20 && !doll.isUpgradable() && doll.isIdentified()
				&& doll.image == ItemSpriteSheet.LYNN_DOLL, "梦瑶娃娃基础属性错误");
		doll.quantity(3);
		check(doll.collect(hero.belongings.backpack), "测试无法放入梦瑶娃娃");
		hero.HP = 99;
		check(lynn.applyLynnPenalty(hero) && hero.HP == 33 && hero.buff(HolyStun.class) != null
				&& hero.belongings.getItem(LynnDoll.class) == null, "Lynn没有销毁娃娃并施加原版惩罚");
	}

	private static void testSummonAndCombat() {
		Actor.clear();
		TestLevel level = level();
		Hero hero = hero(18);
		hero.lvl = 5;
		PlainMob target = new PlainMob();
		target.pos = 28;
		target.HP = target.HT = 1000;
		Actor.add(target);
		LynnDoll.CurseDoll doll = new LynnDoll().shatter(target, target.pos);
		check(doll != null && level.mobs.contains(doll) && doll.pos != target.pos
				&& Dungeon.level.insideMap(doll.pos), "诅咒少女没有在有效空格远程出现");
		check(doll.HT == 10000 && doll.HP == 10000 && doll.attackSkill(target) == 1000
				&& doll.speed() == 3f && doll.flying && doll.properties().contains(Char.Property.UNKNOW),
				"诅咒少女生命、命中、速度、飞行或属性错误");
		int before = target.HP;
		check(doll.attackProc(target, 100) == 0 && target.HP <= before - 20 && target.HP >= before - 40,
				"诅咒少女没有取消物理伤害并造成4~8倍英雄等级的能量伤害");
		check(doll.isImmune(Amok.class) && doll.isImmune(Charm.class), "诅咒少女缺少狂乱或魅惑免疫");
	}

	private static void testPersistenceAndIcon() throws Exception {
		PlainMob owner = new PlainMob();
		owner.pos = 20;
		Actor.add(owner);
		LynnDoll.CurseDoll doll = new LynnDoll.CurseDoll();
		doll.setPotInfo(28, owner);
		Bundle bundle = new Bundle();
		doll.storeInBundle(bundle);
		LynnDoll.CurseDoll restored = new LynnDoll.CurseDoll();
		restored.restoreFromBundle(bundle);
		check(restored.potPos() == 28 && restored.potHolder() == owner.id(), "诅咒少女目标存档错误");

		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 800; y < 816; y++) for (int x = 240; x < 256; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder actual = new StringBuilder(64);
		for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
		check(ICON_HASH.equals(actual.toString()), "梦瑶娃娃图标与旧版像素不一致：" + actual);
		check(LynnSprite.class != null && new File("sprites/npcs/sps_town_lynn.png").isFile(),
				"诅咒少女原始动画图集缺失");
	}

	private static TestLevel level() {
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		Arrays.fill(level.map, Terrain.EMPTY);
		Arrays.fill(level.passable, true);
		return level;
	}

	private static Hero hero(int pos) {
		Hero hero = new Hero();
		hero.pos = pos;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		return hero;
	}

	private static final class PlainMob extends Mob {
		{ alignment = Alignment.ENEMY; }
	}

	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
		}
		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsLynnDollTest() { }
}
