/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;

/**
 * SPSEXPD: 经卷耳环 —— 苦修者开局的两件专属物品之一（神器）。
 *
 * <p>主动技「祷诵」：选中一个敌人，让它陷入短暂的_狂乱_（{@link Amok}）——
 * 它会转而攻击离它最近的任何角色。每次使用后有冷却。</p>
 */
public class ScrollEarring extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_BASE_A;

	public static final String AC_CONFUSE = "CONFUSE";

	/** 狂乱持续回合。 */
	public static final float AMOK_DURATION = 6f;
	/** 冷却回合。 */
	public static final float COOLDOWN = 20f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ScrollEarring.class)
			.t("name", "经卷耳环")
			.t("ac_confuse", "祷诵")
			.t("prompt", "选择要使其狂乱的目标")
			.t("no_target", "那里没有能被经卷扰乱的生物。")
			.t("cooldown", "经卷还没有平息，暂时无法再次祷诵（剩余 %d 回合）。")
			.t("confused", "经卷的低语钻进了%s的脑海，它陷入了狂乱！")
			.t("desc", "一枚挂着微型经卷的耳环。据说它记着某位苦修者的戒律——当你对着某个生物默诵它时，那些支离破碎的句子会钻进对方的脑子，让它短暂地分不清敌我，转而去攻击离它最近的任何角色。\n\n每次祷诵后，经卷需要 %1$d 回合才能重新平息。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", (int)COOLDOWN);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CONFUSE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_CONFUSE.equals(action)) return;

		EarringCooldown cd = hero.buff(EarringCooldown.class);
		if (cd != null) {
			GLog.w(Messages.get(this, "cooldown", Math.max(1, (int)Math.ceil(cd.cooldown()))));
			return;
		}
		curUser = hero;
		GameScene.selectCell(confuser);
	}

	private final CellSelector.Listener confuser = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || curUser == null) return;
			Char ch = Actor.findChar(cell);
			if (!(ch instanceof Mob) || ch.buff(Amok.class) != null) {
				GLog.w(Messages.get(ScrollEarring.class, "no_target"));
				return;
			}
			Buff.affect(ch, Amok.class, AMOK_DURATION);
			Buff.affect(curUser, EarringCooldown.class, COOLDOWN);
			GLog.p(Messages.get(ScrollEarring.class, "confused", ch.name()));
			curUser.spendAndNext(1f);
		}
		@Override
		public String prompt() {
			return Messages.get(ScrollEarring.class, "prompt");
		}
	};

	/** SPSEXPD: 祷诵后的冷却（纯计时，无图标）。 */
	public static class EarringCooldown extends FlavourBuff {
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
