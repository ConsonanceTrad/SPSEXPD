package pd.actors.blobs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.QuickSlot;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.blobs.damageblobs.DarkEffectDamage;
import pd.actors.blobs.damageblobs.EarthEffectDamage;
import pd.actors.blobs.damageblobs.EnergyEffectDamage;
import pd.actors.blobs.damageblobs.FireEffectDamage;
import pd.actors.blobs.damageblobs.IceEffectDamage;
import pd.actors.blobs.damageblobs.LightEffectDamage;
import pd.actors.blobs.damageblobs.ShockEffectDamage;
import pd.actors.buffs.ShadowCurse;
import pd.actors.buffs.actbuff.NmImbue;
import pd.actors.damagetype.DamageType;
import pd.actors.hero.Hero;
import pd.actors.mobs.Rat;
import pd.items.Heap;
import pd.items.Item;
import pd.items.Torch;
import pd.items.potions.PotionOfHealing;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.levels.traps.damagetrap.FireDamageTrap;
import pd.plants.Plant;
import com.watabou.noosa.Game;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Reflection;
import com.watabou.utils.SparseArray;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class SpsLegacyBlobsTest {

	private static final int HERO_POS = 34;
	private static final int TARGET_POS = 35;

	private SpsLegacyBlobsTest() { }

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		try {
			testNanoSwarm();
			testCurseWeb();
			testElementalDamage();
			 testUtilityBlobs();
			testToxicGasDepthDamage();
			testRuntimeRoutes();
			testUtf8Messages();
			System.out.println("SPS旧版环境机制测试通过：纳米环绕、纳米云、暗影咒网、七类元素区域、真实陷阱入口和四语UTF-8均正常。");
		} finally {
			Actor.clear();
			Dungeon.level = null;
			Dungeon.hero = null;
			app.exit();
		}
	}

	private static void testToxicGasDepthDamage() {
		check(ToxicGas.legacyDamage(100, 430, 9) == 14,
				"85层毒气余数判定没有向上取整");
		check(ToxicGas.legacyDamage(100, 430, 10) == 13,
				"85层毒气基础伤害不符合0.9.8公式");
		check(ToxicGas.legacyDamage(100, 65, 4) == 5
				&& ToxicGas.legacyDamage(100, 65, 5) == 4,
				"主线12层毒气伤害不符合0.9.8公式");
	}

	private static void testNanoSwarm() {
		RecordingLevel level = freshLevel();
		Hero hero = new Hero();
		hero.pos = HERO_POS;
		hero.HP = hero.HT = 100;
		hero.spp = 0;
		Dungeon.hero = hero;
		Actor.add(hero);
		hero.belongings.backpack.items.add(new PotionOfHealing());

		NmImbue imbue = new NmImbue();
		check(imbue.attachTo(hero), "纳米环绕无法附着到英雄");
		check(hero.isImmune(NmGas.class), "纳米环绕没有赋予宿主纳米云免疫");
		imbue.act();
		check(hero.spp == 1 && hero.belongings.backpack.items.isEmpty(), "纳米环绕没有同化随机有效物品");
		NmGas gas = (NmGas) level.blobs.get(NmGas.class);
		check(gas != null && gas.cur[HERO_POS] == 30, "纳米环绕没有在英雄位置生成30点纳米云");

		RecordingRat target = new RecordingRat();
		target.pos = TARGET_POS;
		target.HP = target.HT = 100;
		Actor.add(target);
		hero.spp = 7;
		Blob.seed(TARGET_POS, 30, NmGas.class);
		gas.act();
		check(target.HP == 93 && target.lastSource == gas, "纳米云没有按英雄SPP造成伤害");
		check(hero.HP == 100, "纳米云伤害了拥有纳米环绕的宿主");
	}

	private static void testCurseWeb() {
		RecordingLevel level = freshLevel();
		Hero hero = new Hero();
		hero.pos = HERO_POS;
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		Actor.add(hero);
		CurseWeb web = Blob.seed(HERO_POS, 2, CurseWeb.class, level);
		Blob.seed(HERO_POS, 1, CurseWeb.class, level);
		check(web.cur[HERO_POS] == 2, "较弱暗影咒网错误覆盖了较强持续时间");
		web.act();
		check(hero.buff(ShadowCurse.class) != null, "暗影咒网没有施加暗影诅咒");
	}

	@SuppressWarnings("unchecked")
	private static void testElementalDamage() {
		freshLevel();
		Hero hero = new Hero();
		hero.pos = HERO_POS;
		Dungeon.hero = hero;
		Actor.add(hero);
		RecordingRat target = new RecordingRat();
		target.pos = TARGET_POS;
		target.HP = target.HT = 100;
		Actor.add(target);

		Class<? extends SpsElementalDamage>[] types = new Class[]{EnergyEffectDamage.class,
				FireEffectDamage.class, IceEffectDamage.class, ShockEffectDamage.class,
				EarthEffectDamage.class, LightEffectDamage.class, DarkEffectDamage.class};
		Object[] sources = {DamageType.ENERGY_DAMAGE, DamageType.FIRE_DAMAGE, DamageType.ICE_DAMAGE,
				DamageType.SHOCK_DAMAGE, DamageType.EARTH_DAMAGE, DamageType.LIGHT_DAMAGE,
				DamageType.DARK_DAMAGE};
		for (int i = 0; i < types.length; i++) {
			SpsElementalDamage blob = Reflection.newInstance(types[i]);
			blob.seed(Dungeon.level, TARGET_POS, 3);
			blob.seed(Dungeon.level, TARGET_POS, 9);
			check(blob.cur[TARGET_POS] == 3, types[i].getSimpleName() + "错误叠加了重复播种时长");
			target.lastSource = null;
			blob.act();
			check(target.lastSource == sources[i], types[i].getSimpleName() + "没有使用对应元素伤害类型");
			check(blob.cur[TARGET_POS] == 2, types[i].getSimpleName() + "没有逐回合衰减");
		}

		check(new SpsElementalDamage.Fire() instanceof FireEffectDamage,
				"迁移期火元素存档兼容别名失效");
	}

	private static void testRuntimeRoutes() {
		RecordingLevel level = freshLevel();
		FireDamageTrap trap = new FireDamageTrap();
		trap.pos = HERO_POS;
		trap.activate();
		check(level.blobs.containsKey(FireEffectDamage.class), "火焰伤害陷阱仍未接入旧版顶层区域类");
		check(!level.blobs.containsKey(SpsElementalDamage.Fire.class), "火焰伤害陷阱仍在生成迁移期兼容类");
	}

	private static void testUtilityBlobs() {
		RecordingLevel level = freshLevel();
		Hero hero = new Hero();
		hero.pos = HERO_POS;
		Dungeon.hero = hero;
		Actor.add(hero);

		Water water = Blob.seed(TARGET_POS, 40, Water.class, level);
		water.act();
		check(level.map[TARGET_POS] == Terrain.HIGH_GRASS, "旧版露水没有把空地转化为高草");

		TorchLight light = Blob.seed(TARGET_POS, 1, TorchLight.class, level);
		light.act();
		light.act();
		check(light.cur[TARGET_POS] == 1 && light.volume == 1, "放置火把的地面光斑没有永久保留");
		Torch torch = new Torch();
		check(torch.actions(hero).contains(Torch.AC_SET), "火把缺少旧版放置动作");

		Portal portal = new Portal();
		portal.seed(level, HERO_POS, 1);
		portal.seed(level, TARGET_POS, 2);
		portal.act();
		check(portal.cur[HERO_POS] == 0 && portal.cur[TARGET_POS] == 2,
				"旧版单格传送门视觉没有移动到最新位置");
	}

	private static void testUtf8Messages() throws Exception {
		String[] files = {"actors.properties", "actors_zh.properties", "actors_zh-hant.properties", "actors_ru.properties"};
		for (String file : files) {
			byte[] bytes = Files.readAllBytes(Paths.get("messages", "actors", file));
			String text = decodeUtf8(bytes, file);
			check(!text.contains("\uFFFD"), file + "包含UTF-8替换字符");
			check(text.contains("actors.blobs.nmgas.desc=")
					&& text.contains("actors.blobs.curseweb.desc=")
					&& text.contains("actors.blobs.torchlight.desc=")
					&& text.contains("actors.blobs.damageblobs.energyeffectdamage.desc=")
					&& text.contains("actors.buffs.actbuff.nmimbue.name="), file + "缺少旧版环境机制文本");
		}
		String simplified = decodeUtf8(Files.readAllBytes(Paths.get("messages", "actors", "actors_zh.properties")), "actors_zh.properties");
		check(simplified.contains("纳米环绕") && simplified.contains("暗影咒丝")
				&& simplified.contains("能量伤害"), "简体中文环境机制文本损坏");
		String itemText = decodeUtf8(Files.readAllBytes(Paths.get("messages", "items", "items_zh.properties")), "items_zh.properties");
		check(itemText.contains("items.torch.ac_set=放置"), "火把放置动作的简体中文文本缺失或损坏");
	}

	private static String decodeUtf8(byte[] bytes, String name) throws CharacterCodingException {
		try {
			return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes)).toString();
		} catch (CharacterCodingException error) {
			throw new CharacterCodingExceptionWithFile(name, error);
		}
	}

	private static RecordingLevel freshLevel() {
		Actor.clear();
		Dungeon.quickslot = new QuickSlot();
		Statistics.deepestFloor = 1;
		RecordingLevel level = new RecordingLevel();
		Dungeon.level = level;
		PathFinder.setMapSize(level.width(), level.height());
		return level;
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class CharacterCodingExceptionWithFile extends CharacterCodingException {
		private final String file;
		private final Throwable cause;
		CharacterCodingExceptionWithFile(String file, Throwable cause) { this.file = file; this.cause = cause; }
		@Override public String getMessage() { return file + "不是严格UTF-8"; }
		@Override public synchronized Throwable getCause() { return cause; }
	}

	private static final class RecordingRat extends Rat {
		Object lastSource;
		@Override public void damage(int damage, Object source) {
			lastSource = source;
			HP = Math.max(0, HP - damage);
		}
	}

	private static final class RecordingLevel extends Level {
		RecordingLevel() {
			setSize(16, 16);
			Arrays.fill(map, Terrain.EMPTY);
			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<Plant>();
			traps = new SparseArray<>();
			transitions = new ArrayList<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			buildFlagMaps();
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
}
