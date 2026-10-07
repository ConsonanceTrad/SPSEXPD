package pd.items.equipment.weapon.melee.special;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.TrinityStance;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.atlas.items.SpecificPlaceHolderDict;
import render.noosa.Game;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Headless checks for the original Trinity Force weapon, its war-dance stances and its progress. */
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
			testKillProgress();
			testSaving();
			testMessages();
			System.out.println("SPS三相之力测试通过：数值、七段独立攻击、战舞姿态、攻速叠加、移速惩罚、击杀进度、存档与双语文本均正常。");
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
		check(TrinityForce.TRAINED_KILLS == 50 && TrinityForce.VANGUARD_KILLS == 150,
				"击杀进度阈值不是50/150");
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

		SlainMob weak = new SlainMob(1);
		weapon.proc(hero, weak, 70);
		check(weapon.kills() == 1, "三相之力整次挥击没有按单个击杀计数：" + weapon.kills());

		int killsBefore = weapon.kills();
		weapon.proc(hero, new SlainMob(1000), 0);
		check(weapon.kills() == killsBefore, "零伤害挥击不应计入击杀");
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
		check(Math.abs(stance.speedMultiplier(weapon) - 0.8f) < 0.00001f, "冲锋姿态移速惩罚不是 0.8");

		check(stance.enterDefend() && stance.defending(), "无法进入防御姿态");
		check(stance.defendTurnsLeft() == TrinityStance.DEFEND_TURNS, "防御姿态不是 5 回合");
		check(stance.reduceDamage(10) == 5, "防御姿态的 50% 减伤错误");
		check(stance.reduceDamage(1) == 1, "防御姿态对小伤害的结算错误");
		check(Math.abs(stance.speedMultiplier(weapon) - 0.5f) < 0.00001f, "防御姿态移速不是 0.5");
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

	private static void testKillProgress() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		TrinityForce weapon = new TrinityForce();
		hero.belongings.weapon = weapon;
		weapon.activate(hero);
		TrinityStance stance = hero.buff(TrinityStance.class);
		check(stance != null, "装备三相之力没有获得战舞姿态");
		check(Math.abs(stance.speedMultiplier(weapon) - 0.8f) < 0.00001f, "冲锋姿态初始移速惩罚不是 0.8");

		for (int i = 0; i < TrinityForce.TRAINED_KILLS - 1; i++) weapon.proc(hero, new SlainMob(1), 70);
		check(weapon.kills() == TrinityForce.TRAINED_KILLS - 1, "击杀计数错误：" + weapon.kills());
		check(Math.abs(TrinityStance.chargeSpeedMultiplier(weapon.kills()) - 0.8f) < 0.00001f,
				"不足 50 次击杀时移速惩罚不该减轻");
		check(Math.abs(TrinityStance.chargeSpeedMultiplier(TrinityForce.TRAINED_KILLS) - 0.9f) < 0.00001f,
				"50 次击杀后移速惩罚不是 0.9");
		check(Math.abs(TrinityStance.chargeSpeedMultiplier(TrinityForce.VANGUARD_KILLS) - 1f) < 0.00001f,
				"150 次击杀后移速惩罚没有取消");
		check(Math.abs(TrinityStance.chargeSpeedMultiplier(TrinityForce.VANGUARD_KILLS + 1) - 1f) < 0.00001f,
				"超过 150 次击杀后移速惩罚异常");
	}

	private static void testSaving() {
		Bundle bundle = new Bundle();
		new TrinityForce().storeInBundle(bundle);
		bundle.put("kills", TrinityForce.VANGUARD_KILLS);
		TrinityForce restored = new TrinityForce();
		restored.restoreFromBundle(bundle);
		check(restored.kills() == TrinityForce.VANGUARD_KILLS, "击杀进度没有随存档恢复");
		check(new TrinityForce().kills() == 0, "击杀进度没有实例隔离");

		Hero hero = new Hero();
		Dungeon.hero = hero;
		hero.belongings.weapon = restored;
		restored.activate(hero);
		check(restored.actions(hero).contains(TrinityForce.AC_VANGUARD), "150 次击杀后没有解锁先锋之刃");
		check(restored.actions(hero).contains(TrinityForce.AC_DEFEND), "战舞姿态可用时缺少防御姿态动作");
	}

	private static void testMessages() throws Exception {
		for (String file : new String[]{"messages/items/zh/items.properties", "messages/items/en/items.properties"}) {
			String text = read(file);
			check(text.contains("items.equipment.weapon.melee.special.trinityforce.desc="),
					file + "缺少三相之力描述");
			check(text.contains("items.equipment.weapon.melee.special.trinityforce.ac_vanguard="),
					file + "缺少先锋之刃动作名");
		}
		for (String file : new String[]{"messages/actors/zh/actors.properties", "messages/actors/en/actors.properties"}) {
			check(read(file).contains("actors.buffs.trinitystance.desc="), file + "缺少战舞姿态描述");
		}
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
