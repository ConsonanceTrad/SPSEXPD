/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 电钻 —— 演员一档（面具 B 路线）的武器。
 *
 * <p>_攻击速度很快_（delayFactor 更低），而且可以用「钻掘」动作一点点_把墙壁磨开_。</p>
 */
public class PowerDrill extends MeleeWeapon implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_T1_2_DRILL;

	public static final String AC_DRILL = "DRILL";

	/** 磨开一格墙需要钻几次。 */
	public static final int DIGS_REQUIRED = 3;

	/** 已钻的次数与目标格。 */
	protected int digProgress = 0;
	protected int digCell = -1;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(PowerDrill.class)
			.t("name", "电钻")
			.t("ac_drill", "钻掘")
			.t("drill_prompt", "选择相邻的墙壁")
			.t("drill_far", "只能钻相邻的墙壁。")
			.t("drill_not_wall", "那里不是墙壁。")
			.t("drill_progress", "钻头啃进墙面（%d/%d）。")
			.t("drill_done", "哗啦一声，那面墙被钻穿了一个洞。")
			.t("desc", "……安迪，是你在用电钻吗？\n\n它转得飞快，_攻击速度远高于同类武器_。\n\n它还可以「钻掘」：对着_相邻的墙壁_使用，一点点把它磨开（%1$d 次一格）。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 2;
		unique = true;
	}

	/** SPSEXPD: 高攻速（延迟更短）。 */
	@Override public float speedMultiplier(Char owner) {
		return super.speedMultiplier(owner) * 1.6f;
	}

	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 6 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 11; }

	/** 磨开一格墙需要的次数。 */
	protected int digsRequired() {
		return DIGS_REQUIRED;
	}

	/** SPSEXPD: 默认动作就是「钻掘」——放进快捷栏点一下即可采掘（不再走普攻）。 */
	@Override
	public String defaultAction() {
		return AC_DRILL;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", digsRequired());
	}

	/** 钻掘的回合消耗。 */
	protected float digDelay() {
		return 1f;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_DRILL);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_DRILL.equals(action)) {
			curUser = hero;
			usesTargeting = true;
			GameScene.selectCell(drillListener);
			return;
		}
		super.execute(hero, action);
	}

	private final CellSelector.Listener drillListener = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || curUser == null || Dungeon.level == null) return;
			if (Dungeon.level.distance(curUser.pos, cell) != 1) {
				GLog.w(Messages.get(PowerDrill.this, "drill_far"));
				return;
			}
			if (!Dungeon.level.solid[cell]) {
				GLog.w(Messages.get(PowerDrill.this, "drill_not_wall"));
				return;
			}
			if (digCell != cell) {
				digCell = cell;
				digProgress = 0;
			}
			digProgress++;
			if (digProgress >= digsRequired()) {
				Dungeon.level.set(cell, pd.levels.Terrain.EMPTY);
				GameScene.updateMap(cell);
				digProgress = 0;
				digCell = -1;
				GLog.p(Messages.get(PowerDrill.this, "drill_done"));
			} else {
				GLog.i(Messages.get(PowerDrill.this, "drill_progress", digProgress, digsRequired()));
			}
			curUser.spendAndNext(digDelay());
		}
		@Override
		public String prompt() {
			return Messages.get(PowerDrill.this, "drill_prompt");
		}
	};

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
