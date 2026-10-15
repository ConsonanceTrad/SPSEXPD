/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 反转怀表 —— 法师二档（王冠 B 路线）的神器。
 *
 * <p>保留精致怀表的「上弦」加速；额外多一个「反转时间」：_消耗全部充能_，
 * 把自己的_生命值、位置_一并退回到 _5 回合之前_，并清除这 5 回合内新中的负面状态。</p>
 */
public class RewindWatch extends FineWatch {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.MAGE_STAFF_T2_2_REVERSE;

	public static final String AC_REWIND = "REWIND";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(RewindWatch.class)
			.t("name", "反转怀表")
			.t("ac_wind", "上弦")
			.t("ac_rewind", "反转时间")
			.t("no_charge", "怀表还没走起来，暂时无法上弦。")
			.t("wound", "怀表在你口袋里飞快地滴答起来——时间在你身边稍稍加快了。")
			.t("rewind_no_echo", "怀表还没记录下足够的过去。")
			.t("rewound", "怀表的指针猛地倒转——你被拽回了 5 回合之前的自己。")
			.t("desc", "一只指针会往回走的怀表。\n\n「上弦」：把_全部充能_拧进发条，每 1 点充能换来 _%1$d 回合_的_时间加速_——期间法杖持续加速充能，移动也会变快。\n\n「反转时间」：_消耗全部充能_，让你的_生命值与位置_一并退回到 _5 回合之前_，并清除这 5 回合内新中的负面状态。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_REWIND);
		return actions;
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (ch instanceof Hero) {
			Buff.affect((Hero) ch, TimeEcho.class);
		}
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_REWIND.equals(action)) {
			if (charge <= 0) {
				GLog.w(Messages.get(this, "no_charge"));
				return;
			}
			TimeEcho echo = hero.buff(TimeEcho.class);
			if (echo == null || !echo.hasSnapshot()) {
				GLog.w(Messages.get(this, "rewind_no_echo"));
				return;
			}
			charge = 0;
			partialCharge = 0;
			echo.rewind(hero);
			GLog.p(Messages.get(this, "rewound"));
			hero.spendAndNext(1f);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 每回合记录一次「生命值 + 位置」，供反转使用。 */
	public static class TimeEcho extends Buff {

		public static final int TURNS = 5;

		private final int[] positions = new int[TURNS];
		private final int[] healths = new int[TURNS];
		private int idx = 0;
		private int filled = 0;

		{
			type = buffType.POSITIVE;
		}

		@Override
		public boolean act() {
			if (target instanceof Hero) {
				positions[idx] = target.pos;
				healths[idx] = target.HP;
				idx = (idx + 1) % TURNS;
				if (filled < TURNS) filled++;
			}
			spend(TICK);
			return true;
		}

		public boolean hasSnapshot() {
			return filled > 0;
		}

		/** SPSEXPD: 回退到最旧的一条记录（也就是 5 回合前的状态）。 */
		public void rewind(Hero hero) {
			if (filled == 0) return;

			int i = (filled < TURNS) ? 0 : idx;

			//回血
			hero.HP = Math.min(hero.HT, healths[i]);

			//回位置
			int p = positions[i];
			if (Dungeon.level != null && p >= 0 && p < Dungeon.level.length() && !Dungeon.level.solid[p]) {
				hero.pos = p;
				Dungeon.level.occupyCell(hero);
				if (hero.sprite != null) hero.sprite.place(p);
			}

			//清除这 5 回合内新中的负面状态
			ArrayList<Buff> negatives = new ArrayList<>();
			for (Buff b : hero.buffs()) {
				if (b != this && b.type == buffType.NEGATIVE) negatives.add(b);
			}
			for (Buff b : negatives) b.detach();

			Dungeon.observe();
			GameScene.updateFog();
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
