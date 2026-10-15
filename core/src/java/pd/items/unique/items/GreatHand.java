/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.Preparation;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.artifacts.Artifact;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import render.utils.math.Random;

import java.util.ArrayList;

/**
 * SPSEXPD: 大盗之手 —— 盗贼一档（面具 A 路线）的神器。
 *
 * <p>装备即提供两项本事：_潜行_（对自己隐身并准备刺杀）与_偷取_（对敌对生物或 NPC 下手）。</p>
 */
public class GreatHand extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ROGUE_CLOAK_T1_1_HAND;

	public static final String AC_STEALTH = "STEALTH";
	public static final String AC_STEAL = "STEAL";

	/** 潜行持续回合。 */
	public static final float STEALTH_TURNS = 8f;
	/** 偷取成功率。 */
	public static final float STEAL_CHANCE = 0.6f;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(GreatHand.class)
			.t("name", "大盗之手")
			.t("ac_stealth", "潜行")
			.t("ac_steal", "偷取")
			.t("steal_prompt", "选择要下手的对象")
			.t("stealthed", "你把自己藏进阴影，同时开始伺机而动。")
			.t("no_target", "那里没有可以下手的对象。")
			.t("steal_none", "%s身上已经没东西可摸了。")
			.t("steal_ok", "你从%s身上摸走了%s。")
			.t("steal_fail", "你没能从%s身上摸到东西——它护得太紧了。")
			.t("desc", "一只永远凉得恰到好处的皮手套，戴上时你的手指会自己记得该往哪儿伸。\n\n「潜行」：_对自己隐身并进入刺杀准备_（持续 %1$d 回合）。\n\n「偷取」：对_敌对生物或 NPC_ 下手，按成功率摸走对方身上的东西。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", (int)STEALTH_TURNS);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_STEALTH);
		actions.add(AC_STEAL);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (AC_STEALTH.equals(action)) {
			Buff.affect(hero, Invisibility.class, STEALTH_TURNS);
			Buff.affect(hero, Preparation.class);
			GLog.p(Messages.get(this, "stealthed"));
			hero.spendAndNext(1f);
		} else if (AC_STEAL.equals(action)) {
			curUser = hero;
			GameScene.selectCell(stealListener);
		}
	}

	protected final CellSelector.Listener stealListener = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer cell) {
			if (cell == null || curUser == null) return;
			Char ch = Actor.findChar(cell);
			if (ch == null || ch == curUser) {
				GLog.w(Messages.get(GreatHand.class, "no_target"));
				return;
			}
			steal(ch);
			curUser.spendAndNext(1f);
		}
		@Override
		public String prompt() {
			return Messages.get(GreatHand.class, "steal_prompt");
		}
	};

	/** SPSEXPD: 对一个目标下手——成功率固定，偷它的首件战利品。 */
	protected void steal(Char target) {
		Item loot = null;
		if (target instanceof Mob) {
			Mob mob = (Mob) target;
			loot = mob.firstItem ? mob.SupercreateLoot() : null;
		}
		if (loot == null) {
			GLog.w(Messages.get(this, "steal_none", target.name()));
			return;
		}
		if (Random.Float() < STEAL_CHANCE) {
			if (target instanceof Mob) ((Mob) target).firstItem = false;
			loot.collect();
			GLog.p(Messages.get(this, "steal_ok", target.name(), loot.name()));
		} else {
			GLog.w(Messages.get(this, "steal_fail", target.name()));
		}
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
