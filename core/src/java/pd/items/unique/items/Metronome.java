/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MetronomeBeat;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.hero.Hero;
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
 * SPSEXPD: 节拍器 —— 演员的初始神器（面具 B 路线）。
 *
 * <p>每走一步之前，你可以给它_定一个节拍_（周围可抵达的一格，陷阱格与虚空格也算）；
 * 连续 _4_ 次都踩在拍子上，就会换来一次_不消耗回合_的行动，外加_护盾_或_强化攻击_。</p>
 */
public class Metronome extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.PERFORMER_SHOVEL_BASE_B;

	public static final String AC_SET = "SET";
	public static final String AC_MODE = "MODE";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Metronome.class)
			.t("name", "节拍器")
			.t("ac_set", "定拍")
			.t("ac_mode", "切换奖励")
			.t("set_prompt", "选择下一步要踩上的格子")
			.t("bad_cell", "那一格到不了。")
			.t("beat_set", "节拍定在那一格上（%d/%d）。")
			.t("mode_shield", "节拍奖励切换为：护盾。")
			.t("mode_power", "节拍奖励切换为：强化攻击。")
			.t("on_beat", "四拍踩满——时间为你让了一步。")
			.t("desc", "一台给走路配乐的小机器，摆锤走到你脚上就响一下。\n\n「定拍」：指定_周围可抵达的一格_作为下一拍。_陷阱格_与_虚空格_同样可以指定——踩上去只是有后果而已。\n\n只要连续 _%1$d_ 次都踩在你定的拍子上，你就会获得一次_不消耗回合_的行动，外加一次_%2$s_。\n\n「切换奖励」：在_护盾_与_强化攻击_之间切换。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		String mode = Messages.get(this, "mode_shield");
		MetronomeBeat beat = Dungeon.hero != null ? Dungeon.hero.buff(MetronomeBeat.class) : null;
		if (beat != null && beat.mode() == MetronomeBeat.MODE_POWER) {
			mode = Messages.get(this, "mode_power");
		}
		return Messages.get(this, "desc", MetronomeBeat.BEATS_REQUIRED, mode.replace("：", ""));
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SET);
		actions.add(AC_MODE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SET.equals(action)) {
			curUser = hero;
			usesTargeting = true;
			GameScene.selectCell(cellSelector);
			return;
		}
		if (AC_MODE.equals(action)) {
			MetronomeBeat beat = Buff.affect(hero, MetronomeBeat.class);
			if (beat.mode() == MetronomeBeat.MODE_SHIELD) {
				beat.setMode(MetronomeBeat.MODE_POWER);
				GLog.i(Messages.get(this, "mode_power"));
			} else {
				beat.setMode(MetronomeBeat.MODE_SHIELD);
				GLog.i(Messages.get(this, "mode_shield"));
			}
			hero.spendAndNext(1f);
			return;
		}
		super.execute(hero, action);
	}

	private final CellSelector.Listener cellSelector = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || !(curUser instanceof Hero)) return;
			Hero hero = (Hero) curUser;
			if (!MetronomeBeat.designatable(cell)
					|| Dungeon.level.distance(hero.pos, cell) > 1) {
				GLog.w(Messages.get(Metronome.this, "bad_cell"));
				return;
			}
			MetronomeBeat beat = Buff.affect(hero, MetronomeBeat.class);
			beat.setTarget(cell);
			GLog.i(Messages.get(Metronome.this, "beat_set", beat.beats(), MetronomeBeat.BEATS_REQUIRED));
			hero.spendAndNext(0.5f);
		}
		@Override
		public String prompt() {
			return Messages.get(Metronome.this, "set_prompt");
		}
	};

	/** SPSEXPD: 维持「踩拍」状态。 */
	@Override
	protected ArtifactBuff passiveBuff() {
		return new Beat();
	}

	public class Beat extends ArtifactBuff {
		@Override
		public boolean act() {
			if (target instanceof Hero) Buff.affect((Hero) target, MetronomeBeat.class);
			spend(TICK);
			return true;
		}
	}

	/** SPSEXPD: 踩满四拍时的奖励——不消耗回合的行动 + 护盾/强化攻击。 */
	public static void grantReward(Hero hero) {
		if (hero == null) return;
		MetronomeBeat beat = hero.buff(MetronomeBeat.class);
		int mode = beat != null ? beat.mode() : MetronomeBeat.MODE_SHIELD;

		if (mode == MetronomeBeat.MODE_SHIELD) {
			Buff.affect(hero, Barkskin.class).set(hero.HT / 10, 1);
		} else {
			PhysicalEmpower emp = Buff.affect(hero, PhysicalEmpower.class);
			emp.dmgBoost = Math.max(1, hero.HT / 12);
			emp.left = 1;
		}
		//SPSEXPD: 这一下不消耗回合
		hero.spend(-1f);

		//SPSEXPD: 闪电竖琴——踩满节拍时额外获得一次玻璃保护
		if (hero.belongings.weapon() instanceof StormHarmonica
				|| hero.belongings.secondWep instanceof StormHarmonica) {
			Buff.affect(hero, Barkskin.class).set(Math.max(1, hero.HT / 8), 1);
			GLog.p(Messages.get(StormHarmonica.class, "glass"));
		}

		GLog.p(Messages.get(Metronome.class, "on_beat"));
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
