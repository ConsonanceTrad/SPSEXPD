package pd.items.equipment.weapon.melee.special;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.TrinityStance;
import pd.actors.Actor;
import pd.actors.hero.Hero;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.actors.mobs.Mob;
import pd.atlas.items.SpecificPlaceHolderDict;
import render.noosa.Game;
import render.utils.math.Random;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Headless checks for the original Trinity Force weapon, its war-dance stances and its constant movement penalty. */
public final class SpsTrinityForceTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Random.pushGenerator(0x5350535452494E49L);
		try {
			testStats();
			testSevenBlades();
			testStances();
			testActions();
			testDashApproach();
			testDashThroughProc();
			testMessages();
			System.out.println("SPS三相之力测试通过：数值、七段独立攻击、战舞姿态、攻速叠加、恒定移速、动作可用性、冲锋接近与双语文本均正常。");
		} finally {
			Random.popGenerator();
			Dungeon.hero = null;
			Dungeon.level = null;
		}
	}

	private static void testStats() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		TrinityForce weapon = new TrinityForce();
		check(weapon.tier == 5 && weapon.min(0) == 8 && weapon.max(0) == 20 && weapon.STRReq(0) == 18,
				"三相之力基础数值或力量需求错误");
		check(weapon.legacyReach(0) == 2, "三相之力攻击距离不是2");
		check(Math.abs(weapon.legacyDelay(0) - 1f) < 0.00001f, "三相之力攻击延迟不是1.0");
		check(Math.abs(weapon.legacyAccuracy(0) - 1f) < 0.00001f, "三相之力命中倍率不是1.0");
		check(weapon.unique && weapon.isReinforced() && weapon.image == SpecificPlaceHolderDict.SPS_PH_WEAPON,
				"三相之力唯一、强化或占位图标错误");
		check(TrinityForce.BLADES == 7, "三相之力不是七把飞刃");
		check(TrinityForce.VANGUARD_DEGREES == 60 && TrinityForce.VANGUARD_RANGE == 5
						&& Math.abs(TrinityForce.VANGUARD_SLOW - 5f) < 0.00001f,
				"先锋之刃扇形或减速时长错误");
	}

	private static void testSevenBlades() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		TrinityForce weapon = new TrinityForce();
		hero.belongings.weapon = weapon;

		//直接调用 proc 时第 1 片（返回值）由外层 Char.attack 结算，其余六片在这里立即结算
		SlainMob even = new SlainMob(1000);
		int main = weapon.proc(hero, even, 70);
		check(main == 10 && 1000 - even.HP == 60,
				"三相之力七段拆分错误：主片=" + main + " 追加=" + (1000 - even.HP));

		SlainMob odd = new SlainMob(1000);
		main = weapon.proc(hero, odd, 31);
		check(main == 5 && 1000 - odd.HP == 26,
				"三相之力余数分配错误：主片=" + main + " 追加=" + (1000 - odd.HP));

		SlainMob weakest = new SlainMob(1000);
		main = weapon.proc(hero, weakest, 7);
		check(main == 1 && 1000 - weakest.HP == 6,
				"三相之力最小拆分错误：主片=" + main + " 追加=" + (1000 - weakest.HP));
	}

	private static void testStances() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		TrinityForce weapon = new TrinityForce();
		hero.belongings.weapon = weapon;
		weapon.activate(hero);

		TrinityStance stance = hero.buff(TrinityStance.class);
		check(stance != null, "装备三相之力没有获得战舞姿态");
		check(!stance.defending() && stance.layers() == 0, "战舞姿态初始状态错误");
		check(stance.canDefend() && stance.defendCooldownLeft() == 0, "战舞姿态初始不可防御");

		for (int i = 0; i < TrinityStance.MAX_LAYERS + 3; i++) stance.onAttack();
		check(stance.layers() == TrinityStance.MAX_LAYERS, "攻速叠加超过五层上限：" + stance.layers());
		check(Math.abs(stance.attackSpeedMultiplier(1f) - 2f) < 0.00001f, "满层攻速加成不是 +100%");
		check(Math.abs(stance.attackSpeedMultiplier(1.5f) - 2.5f) < 0.00001f, "攻速加成没有叠在既有倍率上");
		check(Math.abs(stance.speedMultiplier() - 0.75f) < 0.00001f, "战舞移速倍率不是 0.75");

		check(stance.enterDefend() && stance.defending(), "无法进入防御姿态");
		check(stance.defendTurnsLeft() == TrinityStance.DEFEND_TURNS, "防御姿态不是 5 回合");
		check(stance.reduceDamage(10) == 5, "防御姿态的 50% 减伤错误");
		check(stance.reduceDamage(1) == 1, "防御姿态对小伤害的结算错误");
		check(Math.abs(stance.speedMultiplier() - 0.75f) < 0.00001f, "防御姿态的移速也应恒为 0.75");
		check(Math.abs(stance.attackSpeedMultiplier(1f) - 0.5f) < 0.00001f, "防御姿态攻速不是 0.5");
		check(Math.abs(stance.attackSpeedMultiplier(0.5f) - 0.25f) < 0.00001f, "防御姿态攻速没有叠乘既有倍率");

		for (int i = 0; i < TrinityStance.DEFEND_TURNS; i++) stance.act();
		check(!stance.defending(), "防御姿态没有在 5 回合后自动结束");
		check(!stance.canDefend() && stance.defendCooldownLeft() == TrinityStance.DEFEND_COOLDOWN,
				"防御姿态结束后没有 10 回合冷却");
		check(stance.reduceDamage(10) == 10, "离开防御姿态后仍在减伤");

		for (int i = 0; i < TrinityStance.DEFEND_COOLDOWN; i++) stance.act();
		check(stance.canDefend(), "冷却结束后仍无法进入防御姿态");

		for (int i = 0; i < TrinityStance.MAX_LAYERS; i++) stance.onAttack();
		check(stance.layers() == TrinityStance.MAX_LAYERS, "冷却期间无法叠加攻速");
		for (int i = 0; i < TrinityStance.IDLE_RESET - 1; i++) stance.act();
		check(stance.layers() == TrinityStance.MAX_LAYERS, "未满 5 回合就不该清零攻速叠加");
		stance.act();
		check(stance.layers() == 0, "连续 5 回合未攻击没有清零攻速叠加");

		stance.enterDefend();
		stance.leaveDefend();
		check(!stance.defending(), "先锋之刃没有回到冲锋姿态");
	}

	/**
	 * 冲锋接近的落点：命中两格或更远的敌人时都朝对方跨一格（恰好两格同样生效）。
	 * 只验证方向算法本身 —— dashTo 里的 hero.move 在 headless 下会碰 sprite，跑不了。
	 */
	private static void testDashApproach() {
		int w = 8;
		int hero = 3 * w + 1;

		//恰好两格（同排）
		check(TrinityForce.approachStep(hero, 3 * w + 3, w) == 3 * w + 2,
				"恰好两格的敌人没有让冲锋接近生效");
		//更远（同排五格）
		check(TrinityForce.approachStep(hero, 3 * w + 6, w) == 3 * w + 2,
				"远距离的敌人没有让冲锋接近生效");
		//斜向与纵向都按八向跨一格
		check(TrinityForce.approachStep(hero, 5 * w + 3, w) == 4 * w + 2,
				"斜向远距离的敌人没有按八向接近");
		check(TrinityForce.approachStep(hero, 6 * w + 1, w) == 4 * w + 1,
				"正下方的敌人没有接近");
		check(TrinityForce.approachStep(hero, 0 * w + 1, w) == 2 * w + 1,
				"正上方的敌人没有接近");
		//相邻时算出的落点就是对方所在格；dashTo 用 distance < 2 挡掉这种情况，避免来回踱步
		check(TrinityForce.approachStep(hero, 3 * w + 2, w) == 3 * w + 2,
				"相邻时落点应是对方所在格");
	}

	/** 两种姿态都不需要解锁：装备即可使用。 */
	private static void testActions() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		TrinityForce weapon = new TrinityForce();

		check(!weapon.actions(hero).contains(TrinityForce.AC_VANGUARD), "未装备时不该出现先锋之刃动作");

		hero.belongings.weapon = weapon;
		weapon.activate(hero);
		TrinityStance stance = hero.buff(TrinityStance.class);
		check(stance != null, "装备三相之力没有获得战舞姿态");
		check(Math.abs(stance.speedMultiplier() - 0.75f) < 0.00001f, "战舞移速倍率不是 0.75");
		check(weapon.actions(hero).contains(TrinityForce.AC_VANGUARD), "装备后先锋之刃就该可用，不需要解锁");
		check(weapon.actions(hero).contains(TrinityForce.AC_DEFEND), "战舞姿态可用时缺少防御姿态动作");

		check(weapon.defaultAction() == null, "三相之力不该有默认动作");
		check(weapon.canQuickSlot(), "三相之力应该允许加入快捷栏");
		check(weapon.quickSlotOpensMenu(), "快捷栏点击三相之力应该打开动作列表");
	}

	private static void testMessages() throws Exception {
		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.weapon.melee.special.trinityforce.desc="),
					file + "缺少三相之力描述");
			check(text.contains("items.equipment.weapon.melee.special.trinityforce.ac_vanguard="),
					file + "缺少先锋之刃动作名");
			check(!text.contains("trinityforce.unlock_") && !text.contains("trinityforce.progress_"),
					file + "仍残留解锁或战舞进度文本");
		}
		for (String file : new String[]{"messages/actors/zh/actors.properties", "messages/actors/en/actors.properties"}) {
			check(read(file).contains("actors.buffs.trinitystance.desc="), file + "缺少战舞姿态描述");
		}
		String zhItems = read("messages/items/zh/items.properties");
		check(zhItems.contains("四分之三"), "中文武器描述没有写明移速降到原先的四分之三");
		check(zhItems.contains("先锋之刃："), "中文武器描述没有写明先锋之刃的效果");

		//七片飞刃各自独立命中，每一片都要播命中音效：追加片循环里必须有 hitSound 调用
		//（音效本身无法断言 —— Sample 在 headless 下没加载任何采样，play 会静默跳过）
		String weapon = read("../java/pd/items/equipment/weapon/melee/special/TrinityForce.java");
		int loop = weapon.indexOf("for (int blade = 1;");
		check(loop > 0 && weapon.indexOf("hitSound(", loop) > loop,
				"追加的六片飞刃命中时没有播命中音效");
	}

	/**
	 * 端到端：攻击两格外的敌人时，proc → dashTo 应当真的让英雄靠近一格。
	 * Char.move 对 sprite 都有 null 检查、travelling=false 又跳过眩晕分支，所以 headless 能跑。
	 */
	private static void testDashThroughProc() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		TestLevel level = new TestLevel();
		Dungeon.level = level;
		hero.pos = 3 * 8 + 1;

		TrinityForce weapon = new TrinityForce();
		hero.belongings.weapon = weapon;
		weapon.activate(hero);

		SlainMob enemy = new SlainMob(100_000);
		enemy.pos = 3 * 8 + 3;   // 恰好两格
		level.mobs().add(enemy);
		Actor.add(enemy);

		int start = hero.pos;
		weapon.proc(hero, enemy, 70);
		check(hero.pos == start + 1, "攻击两格外的敌人后没有靠近一格：" + start + "→" + hero.pos);

		//防御姿态不靠近（原地格挡）
		TrinityStance stance = hero.buff(TrinityStance.class);
		check(stance != null, "装备三相之力没有获得战舞姿态");
		stance.enterDefend();
		hero.pos = 5 * 8 + 1;
		SlainMob guarded = new SlainMob(100_000);
		guarded.pos = 5 * 8 + 4;
		level.mobs().add(guarded);
		Actor.add(guarded);
		weapon.proc(hero, guarded, 70);
		check(hero.pos == 5 * 8 + 1, "防御姿态不该靠近：" + hero.pos);

		Actor.clear();
		Dungeon.level = null;
		Dungeon.hero = null;
	}

	/** 无头用的最小地图（8x8 空地）。 */
	private static final class TestLevel extends Level {
		TestLevel() {
			setSize(8, 8);
			//Level.setSize 后 map 默认全是墙，必须铺成空地并重建通行标记
			java.util.Arrays.fill(map, Terrain.EMPTY);
			mobs().clear();
			heaps = new render.utils.data.SparseArray<>();
			blobs = new java.util.HashMap<>();
			plants = new render.utils.data.SparseArray<>();
			traps = new render.utils.data.SparseArray<>();
			transitions = new java.util.ArrayList<>();
			customTiles = new java.util.ArrayList<>();
			customTerrain = new java.util.ArrayList<>();
			customWalls = new java.util.ArrayList<>();
			heroFOV = new boolean[length()];
			visited = new boolean[length()];
			mapped = new boolean[length()];
			//等 mobs/heaps/blobs 等集合就位后再重建通行标记（CellFlags.build 会读 blobs）
			buildFlagMaps();
		}

		@Override protected boolean build() { return true; }
		@Override protected void createMobs() { }
		@Override protected void createItems() { }
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static String read(String path) throws Exception {
		return new String(Files.readAllBytes(Path.of(path)), StandardCharsets.UTF_8);
	}

	private static final class SlainMob extends Mob {
		SlainMob(int health) {
			HP = HT = health;
		}

		@Override public int damageRoll() { return 1; }
		@Override public int attackSkill(Char target) { return 1; }
		@Override public int defenseSkill(Char enemy) { return 0; }
		@Override public int drRoll() { return 0; }
		@Override public void die(Object cause) { }
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsTrinityForceTest() { }
}
