package pd.journal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.FrostIce;
import pd.actors.damagetype.DamageType;
import pd.actors.mobs.Assassin;
import pd.actors.mobs.BambooMob;
import pd.actors.mobs.BombBug;
import pd.actors.mobs.ExBambooMob;
import pd.actors.mobs.FireSuccubus;
import pd.actors.mobs.Greatmoss;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Shielded;
import pd.actors.mobs.SpsCityMobs;
import pd.actors.mobs.SpsExitMobs;
import pd.actors.mobs.SpsPrisonMobs;
import pd.actors.mobs.YogDzewa;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.wands.WandOfFreeze;
import pd.items.equipment.wands.fusion.WandOfFlow;
import pd.items.equipment.weapon.enchantments.EnchantmentIce2;
import pd.items.equipment.weapon.enchantments.EnchantmentIce;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Reflection;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Checks the complete, player-visible SPS-PD 0.9.8 creature catalog. */
public final class SpsMobCatalogTest {

	private static final Pattern ENTRY = Pattern.compile(
			"([A-Z]+)\\.seen\\.put\\(\\s*([A-Za-z0-9_]+)\\.class");

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Dungeon.depth = 1;
		testExactLegacyLists();
		testConstructionAndRestoredTypes();
		testUiEntryAndUtf8Titles();
		System.out.println("SPS怪物目录测试通过：七组113个旧版条目、顺序、实例化、缺失实体、当前日志入口及多语言UTF-8标题均正常。");
	}

	private static void testExactLegacyLists() throws Exception {
		Path oldSource = Path.of("..", "..", "..", "..", "SPS-PD-0.9.8", "SPS-PD-0.9.8",
				"java", "com", "hmdzl", "spspd", "infos", "NewMobCatalog.java");
		if (!Files.exists(oldSource)) {
			//外部 0.9.8 基准目录缺失时回退到仓库内置参考源码（内容同为 0.9.8 NewMobCatalog.java）
			oldSource = Path.of("..", "..", "..", "_ref", "ref", "SPS-PD",
					"java", "com", "hmdzl", "spspd", "infos", "NewMobCatalog.java");
		}
		Map<SpsMobCatalog, LinkedHashSet<String>> expected = new EnumMap<>(SpsMobCatalog.class);
		for (SpsMobCatalog catalog : SpsMobCatalog.values()) expected.put(catalog, new LinkedHashSet<>());
		for (String line : Files.readAllLines(oldSource, StandardCharsets.UTF_8)) {
			if (line.trim().startsWith("//")) continue;
			Matcher matcher = ENTRY.matcher(line);
			if (matcher.find()) expected.get(SpsMobCatalog.valueOf(matcher.group(1))).add(matcher.group(2));
		}

		int total = 0;
		for (SpsMobCatalog catalog : SpsMobCatalog.values()) {
			List<String> actual = new ArrayList<>();
			for (Class<? extends Mob> type : catalog.mobs()) actual.add(legacyName(type));
			check(new ArrayList<>(expected.get(catalog)).equals(actual), catalog + "目录条目或顺序与0.9.8不一致");
			check(catalog.totalSeen() == catalog.totalMobs(), catalog + "没有按旧版默认全部可见");
			total += catalog.totalMobs();
		}
		check(total == 113, "SPS怪物目录总数错误: " + total);
	}

	private static String legacyName(Class<?> type) {
		if (type == SpsCityMobs.GreatMoss.class || type == Greatmoss.class) return "Greatmoss";
		if (type == YogDzewa.class) return "Yog";
		return type.getSimpleName();
	}

	private static void testConstructionAndRestoredTypes() {
		for (SpsMobCatalog catalog : SpsMobCatalog.values()) {
			for (Class<? extends Mob> type : catalog.mobs()) {
				check(Reflection.newInstance(type) != null, "目录怪物无法实例化: " + type.getName());
			}
		}
		check(SpsMobCatalog.PRISON.mobs().contains(BambooMob.class), "竹子怪没有恢复为真实旧版类型");
		BambooMob bamboo = new BambooMob();
		check(bamboo.properties().contains(Char.Property.PLANT), "竹子怪缺少旧版植物属性");
		check(bamboo.resist(pd.actors.buffs.Roots.class) < 1f,
				"竹子怪缺少旧版扎根抗性");
		check(bamboo.isImmune(pd.actors.buffs.Poison.class),
				"竹子怪缺少旧版植物中毒免疫");
		check(bamboo.weak(pd.actors.buffs.Ooze.class) == 1.5f,
				"竹子怪缺少旧版酸蚀弱点");
		check(bamboo.weak(pd.items.equipment.wands.Wand.class) == 1.5f,
				"竹子怪缺少旧版法杖弱点");
		check(bamboo.SupercreateLoot() instanceof Armor, "竹子怪的高级掉落不是随机护甲");
		check(Reflection.newInstance(SpsPrisonMobs.BambooMob.class) != null,
				"迁移期竹子怪存档兼容类型无法实例化");
		check(SpsMobCatalog.PRISON.mobs().contains(ExBambooMob.class), "竹子精没有恢复为真实旧版类型");
		ExBambooMob exBamboo = new ExBambooMob();
		check(exBamboo.properties().contains(Char.Property.PLANT), "竹子精缺少继承的植物属性");
		check(exBamboo.weak(pd.actors.buffs.Ooze.class) == 1.5f,
				"竹子精缺少继承的酸蚀弱点");
		check(exBamboo.isImmune(DamageType.Earth.class), "竹子精没有免疫实际地元素伤害源");
		check(SpsMobCatalog.PRISON.mobs().contains(Assassin.class), "暗杀者没有恢复为真实旧版类型");
		Dungeon.depth = 8;
		Assassin assassin = new Assassin();
		check(assassin.HT >= 90 && assassin.HT <= 105, "暗杀者生命值偏离旧版固定系数范围");
		check(assassin.properties().contains(Char.Property.HUMAN), "暗杀者缺少旧版人类属性");
		check(assassin.resist(pd.actors.blobs.ToxicGas.class) < 1f,
				"暗杀者缺少旧版毒气抗性");
		check(assassin.resist(pd.actors.buffs.Poison.class) < 1f,
				"暗杀者缺少旧版中毒抗性");
		check(Reflection.newInstance(SpsPrisonMobs.Assassin.class) != null,
				"迁移期暗杀者存档兼容类型无法实例化");
		check(SpsMobCatalog.CAVE.mobs().contains(Shielded.class), "持盾豺狼没有恢复为真实旧版类型");
		check(SpsMobCatalog.CAVE.mobs().contains(BombBug.class), "霜石虫没有恢复为真实旧版类型");
		check(SpsMobCatalog.HALL.mobs().contains(FireSuccubus.class), "烈焰魅魔没有恢复为真实旧版类型");
		boolean generatedBombBug = false;
		Random.pushGenerator(0x424F4D42425547L);
		try {
			for (int i = 0; i < 100; i++) {
				Mob guard = SpsExitMobs.randomForDepth(12);
				check(guard instanceof BombBug || guard instanceof Shielded,
						"洞穴出口生成了旧版表外守卫");
				generatedBombBug |= guard instanceof BombBug;
			}
		} finally {
			Random.popGenerator();
		}
		check(generatedBombBug, "洞穴出口没有生成真实霜石虫");
		BombBug bombBug = new BombBug();
		check(bombBug.properties().contains(Char.Property.BEAST), "霜石虫缺少旧版兽类属性");
		check(!bombBug.properties().contains(Char.Property.ICY), "霜石虫错误保留了非源码冰系属性");
		check(bombBug.resist(DamageType.Ice.class) < 1f, "霜石虫缺少旧版冰伤抗性");
		check(bombBug.resist(WandOfFlow.class) < 1f, "霜石虫缺少旧版流水法杖抗性");
		check(bombBug.resist(WandOfFreeze.class) < 1f, "霜石虫缺少旧版冻结法杖抗性");
		check(bombBug.isImmune(FrostIce.class), "霜石虫缺少旧版霜冻免疫");
		check(bombBug.isImmune(EnchantmentIce.class), "霜石虫缺少旧版冰附魔免疫");
		check(bombBug.isImmune(EnchantmentIce2.class), "霜石虫缺少旧版强冰附魔免疫");
		check(Reflection.newInstance(SpsExitMobs.GuardBombBug.class) != null,
				"迁移期霜石虫存档兼容类型无法实例化");
		check(SpsExitMobs.randomForDepth(22) instanceof FireSuccubus, "大厅出口没有生成真实烈焰魅魔");
	}

	private static void testUiEntryAndUtf8Titles() throws Exception {
		Path window = Path.of("..", "java", "pd",
				"windows", "WndJournal.java");
		String source = Files.readString(window, StandardCharsets.UTF_8);
		check(source.contains("for (SpsMobCatalog catalog : SpsMobCatalog.values())"),
				"当前日志仍未使用完整SPS怪物目录");
		for (String file : new String[]{"en/journal.properties", "zh/journal.properties",
				"zh-hant/journal.properties", "ru/journal.properties"}) {
			Path path = Path.of("messages", "journal", file);
			String text = StandardCharsets.UTF_8.newDecoder()
					.onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT)
					.decode(java.nio.ByteBuffer.wrap(Files.readAllBytes(path))).toString();
			for (SpsMobCatalog catalog : SpsMobCatalog.values()) {
				check(text.contains("journal.spsmobcatalog." + catalog.name().toLowerCase() + ".title="),
						file + "缺少" + catalog + "目录标题");
			}
			check(text.indexOf('\uFFFD') < 0, file + "含UTF-8替换字符");
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsMobCatalogTest() { }
}
