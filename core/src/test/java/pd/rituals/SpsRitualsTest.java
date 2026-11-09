/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 无图形验证仪式机制核心（pd.rituals）：
 * - Rituals.pickMatch 纯匹配规则（足量、条目数优先、总量并列、无命中）；
 * - 注册表登记 + 仪式讲解册（Document.RITUAL_GUIDE）页同步与知识判定门槛。
 */

package pd.rituals;

import java.util.HashMap;
import java.util.Map;

import pd.items.Amulet;
import pd.items.Gold;
import pd.items.Item;
import render.noosa.Game;

public final class SpsRitualsTest {

	private static int checks = 0;

	private static void check(boolean cond, String what) {
		checks++;
		if (!cond) throw new AssertionError("FAIL: " + what);
	}

	private static void checkEquals(Object actual, Object expected, String what) {
		check(actual.equals(expected), what + "（期望 " + expected + "，实际 " + actual + "）");
	}

	/** 匹配配额计数表 */
	private static Map<Class<? extends Item>, Integer> counts(Object... kv) {
		Map<Class<? extends Item>, Integer> map = new HashMap<>();
		for (int i = 0; i < kv.length; i += 2) {
			map.put((Class<? extends Item>) kv[i], (Integer) kv[i + 1]);
		}
		return map;
	}

	/** 测试用仪式：id/name/desc 固定，materials 固定，act 只计数（不触世界状态） */
	private static class FakeRitual extends Ritual {
		final String id;
		final RitualMaterial[] mats;
		int fired = 0;

		FakeRitual(String id, RitualMaterial... mats) {
			this.id = id;
			this.mats = mats;
		}

		@Override public String id() { return id; }
		@Override public String name() { return "测试仪式" + id; }
		@Override public String desc() { return "测试用。"; }
		@Override public java.util.List<RitualMaterial> materials() {
			return java.util.Arrays.asList(mats);
		}
		@Override public void act(pd.actors.hero.Hero caster, int center) { fired++; }
	}

	public static void main(String[] args) {
		//SPSEXPD: 仪式核心测试不触 Gdx/natives/音频粒子——InlineText 注册是纯内存表，
		//只需给 Game.version 赋值让 DeviceCompat.isDebug() 可用，其余全走纯逻辑
		Game.version = "test";
		try {
			testPickMatchRules();
			testRegistryAndKnowledge();

			System.out.println("SpsRitualsTest PASS (" + checks + " checks)");
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		System.out.flush();
		System.exit(0);
	}

	/** SPSEXPD: pickMatch —— 足量才候选；条目数多者优先；并列按总量；不足返回 null */
	private static void testPickMatchRules() {
		FakeRitual twoGoldOneAmulet = new FakeRitual("t_gold_amulet",
				new RitualMaterial(Gold.class, 2), new RitualMaterial(Amulet.class, 1));
		FakeRitual oneGold = new FakeRitual("t_one_gold",
				new RitualMaterial(Gold.class, 1));

		//两配方都命中时，条目数多（材料组合更具体）者优先
		Ritual picked = Rituals.pickMatch(counts(Gold.class, 3, Amulet.class, 1),
				java.util.Arrays.<Ritual>asList(twoGoldOneAmulet, oneGold));
		checkEquals(picked, twoGoldOneAmulet, "条目数多者优先");

		//反序候选仍应命中更具体的那个
		picked = Rituals.pickMatch(counts(Gold.class, 3, Amulet.class, 1),
				java.util.Arrays.<Ritual>asList(oneGold, twoGoldOneAmulet));
		checkEquals(picked, twoGoldOneAmulet, "候选顺序不影响「更具体优先」");

		//只有一条目配方命中时
		picked = Rituals.pickMatch(counts(Gold.class, 1),
				java.util.Arrays.<Ritual>asList(twoGoldOneAmulet, oneGold));
		checkEquals(picked, oneGold, "缺料仪式不命中");

		//条目数并列时，总需求量大者优先
		FakeRitual oneGold1 = new FakeRitual("t_gold1", new RitualMaterial(Gold.class, 1));
		FakeRitual oneGold2 = new FakeRitual("t_gold2", new RitualMaterial(Gold.class, 2));
		picked = Rituals.pickMatch(counts(Gold.class, 3),
				java.util.Arrays.<Ritual>asList(oneGold1, oneGold2));
		checkEquals(picked, oneGold2, "并列场景总需求量大者优先");

		//重复抽取一致性
		picked = Rituals.pickMatch(counts(Gold.class, 3),
				java.util.Arrays.<Ritual>asList(oneGold2, oneGold1));
		checkEquals(picked, oneGold2, "总需求量大者优先与候选顺序无关");

		//毫无命中返回 null
		check(Rituals.pickMatch(counts(Gold.class, 0),
				java.util.Arrays.<Ritual>asList(oneGold1, oneGold2)) == null, "缺料无命中返回 null");
	}

	/** SPSEXPD: 注册表登记 + 讲解册页同步 + 知识判定门槛（未获得页 → 不算知晓） */
	private static void testRegistryAndKnowledge() {
		FakeRitual ritual = new FakeRitual("sps_test_ritual", new RitualMaterial(Gold.class, 1));

		Rituals.register(ritual);
		check(Rituals.byId("sps_test_ritual") == ritual, "注册表登记");
		check(Rituals.byPage("sps_test_ritual") == ritual, "讲解页 id 反查（页 id = 仪式 id）");

		check(pd.journal.Document.RITUAL_GUIDE.pageNames().contains("sps_test_ritual"), "讲解册同步登记页");
		//非 debug 档默认未获得 → 不算知晓（布阵石门槛），仪式本身不受影响
		check(!RitualKnowledge.known(ritual), "未获得讲解页不算知晓");
		RitualKnowledge.learn(ritual);
		check(RitualKnowledge.known(ritual), "登记 found 后算知晓");

		//自我中心默认允许
		check(ritual.selfChannel(), "默认允许自我中心激发");

		//重复登记幂等
		int sizeAfterFirst = pd.journal.Document.RITUAL_GUIDE.pageNames().size();
		Rituals.register(ritual);
		check(pd.journal.Document.RITUAL_GUIDE.pageNames().size() == sizeAfterFirst, "重复登记幂等");
		check(Rituals.byId("sps_test_ritual") == ritual, "重复登记不覆盖");

		//效果结算计数（供后续布阵石/激发链路的复用锚点）
		ritual.act(null, 0);
		checkEquals(ritual.fired, 1, "act 计数");
	}
}
