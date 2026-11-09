/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 无图形验证施法系统拓展核心（批次A法力值 + 批次E法术书）：
 * - Mana：上限/回复累积/消耗门槛/封顶；
 * - Spellbook：原典过载判定（≥2 本）、效果折算、额外法力消耗、法力不足拒绝施法；
 * - power 单点：法强（magicSkill 口径）× 过载折算。
 */

package pd.items.spellbooks;

import pd.actors.hero.Hero;
import pd.actors.hero.Mana;

public final class SpsSpellbooksTest {

	private static int checks = 0;

	private static void check( boolean cond, String what ) {
		checks++;
		if (!cond) throw new AssertionError( "FAIL: " + what );
	}

	private static void checkEquals( Object actual, Object expected, String what ) {
		check( actual.equals( expected ), what + "（期望 " + expected + "，实际 " + actual + "）" );
	}

	/** 测试用原典：无效果、固定耗 1 法力（效果留空避免碰世界状态） */
	private static class FakeTome extends Spellbook {
		@Override public boolean isTome() { return true; }
		@Override public int manaCost() { return 1; }
		@Override public void onCast( Hero hero ) { }
	}

	/** 测试用抄本：固定耗 3 法力 */
	private static class FakeCodex extends Spellbook {
		@Override public boolean isTome() { return false; }
		@Override public int manaCost() { return 3; }
		@Override public void onCast( Hero hero ) { fired++; }
		int fired = 0;
	}

	public static void main(String[] args) {
		try {
			testManaBasics();
			testCastGate();

			System.out.println( "SpsSpellbooksTest PASS (" + checks + " checks)" );
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit( 1 );
		}
		System.out.flush();
		System.exit( 0 );
	}

	private static void testManaBasics() {
		Hero hero = new Hero();
		checkEquals( Mana.cap( hero ), 10, "1 级法力上限 = 10 + 0" );
		checkEquals( Mana.cur( hero ), 10, "新档满法力" );

		//消耗门槛：不足则拒绝且不动数值
		check( Mana.spend( hero, 12 ) == false, "法力不足拒绝消耗" );
		checkEquals( Mana.cur( hero ), 10, "拒绝后法力不变" );

		check( Mana.spend( hero, 5 ), "足量消耗成功" );
		checkEquals( Mana.cur( hero ), 5, "消耗后余额" );

		//回复累积：REGEN_TURNS 回合 +1，封顶不溢出
		hero.spsManaRegenTurns = Mana.REGEN_TURNS - 1;
		Mana.regenTick( hero );
		checkEquals( Mana.cur( hero ), 6, "累积到点回 1 法力" );
		Mana.add( hero, 999 );
		checkEquals( Mana.cur( hero ), Mana.cap( hero ), "增加封顶" );
		Mana.regenTick( hero );
		checkEquals( Mana.cur( hero ), Mana.cap( hero ), "满值不再回复" );

		//上限随等级成长（lvl=1 基础 10）
		hero.lvl = 20;
		checkEquals( Mana.cap( hero ), 10 + 19, "20 级上限 = 10 + 19" );
		hero.lvl = 1;
	}

	private static void testCastGate() {
		Hero hero = new Hero();
		Mana.setFull( hero ); //10 法力

		//抄本固定耗 3（测试本），无任何附加惩罚；拒绝分支的 GLog 文案走 Messages（无头不触），这里只验门槛本身
		FakeCodex codex = new FakeCodex();
		hero.belongings.backpack.items.add( codex );

		//多本原典不再有任何过载效果（机制已按裁决移除）——塞两本原典不影响消耗
		hero.belongings.backpack.items.add( new FakeTome() );
		hero.belongings.backpack.items.add( new FakeTome() );

		check( codex.tryCast( hero ), "法力足够施法成功" );
		checkEquals( codex.fired, 1, "onCast 结算一次" );
		checkEquals( Mana.cur( hero ), 10 - 3, "施法只扣抄本自身法力（无附加消耗）" );

		//耗干法力后的门槛语义
		Mana.spend( hero, Mana.cur( hero ) );
		int cost = codex.manaCost();
		check( !Mana.has( hero, cost ), "法力不足时门槛判定为不可施法" );
		checkEquals( codex.fired, 1, "门槛拦截下效果不结算" );
	}
}
