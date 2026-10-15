/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.SpsArrow;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.artifacts.Artifact;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 亚卡箭 —— 星兵一档（面具 A 路线）的特殊神器（召唤类）。
 *
 * <p>吹哨唤出一支哨箭，或者指挥场上的哨箭飞向你指定的地点。
 * 哨箭靠声音指挥，因此_每次下令都会惊醒周围一定范围的敌人_。</p>
 */
public class YakaArrow extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.SOLDIER_GUN_T1_1_YAKA;

	public static final String AC_COMMAND = "COMMAND";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(YakaArrow.class)
			.t("name", "亚卡箭")
			.t("ac_command", "吹哨指挥")
			.t("prompt", "选择哨箭要飞向的地方")
			.t("summoned", "你吹响哨子，一支哨箭应声而起。")
			.t("commanded", "哨箭朝你指的方向掠去。")
			.t("desc", "一支刻着哨声符文的短箭，不需要弓就能飞出去。\n\n「吹哨指挥」：若场上还没有你的哨箭，它会唤出一支；若已经有了，则指挥那支箭_朝指定地点直线飞去_。\n\n哨箭每回合能飞 _4 格_，_可以穿过生物_，但撞到墙壁就会停下；沿途被它掠过的敌人都会受到物理伤害。它还能在抵达当前目标前_提前变向_。\n\n代价是：哨箭靠_声音_指挥——每次下令都会_惊醒周围 6 格内的敌人_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_COMMAND);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_COMMAND.equals(action)) return;
		curUser = hero;
		GameScene.selectCell(commander);
	}

	/** SPSEXPD: 场上指定种类的哨箭（没有则 null）。 */
	public static SpsArrow findArrow(SpsArrow.Kind kind) {
		if (Dungeon.level == null) return null;
		for (Mob m : Dungeon.level.mobs().snapshot()) {
			if (m instanceof SpsArrow && ((SpsArrow) m).kind() == kind && m.isAlive()) {
				return (SpsArrow) m;
			}
		}
		return null;
	}

	/** SPSEXPD: 唤出一支（或指挥已有的）哨箭。 */
	protected static void summonOrCommand(SpsArrow.Kind kind, int cell, Hero hero) {
		SpsArrow arrow = findArrow(kind);
		if (arrow == null) {
			arrow = new SpsArrow();
			arrow.setKind(kind);
			arrow.pos = hero.pos;
			GameScene.add(arrow);
			GLog.p(Messages.get(YakaArrow.class, "summoned"));
		}
		if (cell != hero.pos) {
			arrow.commandTo(cell);
			GLog.i(Messages.get(YakaArrow.class, "commanded"));
		}
	}

	private final CellSelector.Listener commander = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || !(curUser instanceof Hero)) return;
			Hero hero = (Hero) curUser;
			summonOrCommand(SpsArrow.Kind.YAKA, cell, hero);
			hero.spendAndNext(1f);
		}
		@Override
		public String prompt() {
			return Messages.get(YakaArrow.class, "prompt");
		}
	};

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
