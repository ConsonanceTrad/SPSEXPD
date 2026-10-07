/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.Dungeon;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.Item;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 原创神器——虚空之手。
 *
 * <p>消耗金币把已探索区域里的掉落物直接取到手中：基础 60 枚，神器每升一级减免 5 枚，最低 10 枚。
 * 目标只需要是已探索（mapped）的格子，不需要在视野内，也不受距离限制。</p>
 */
public class VoidHand extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(VoidHand.class)
			.t("name", "虚空之手")
			.t("ac_pull", "收取")
			.t("prompt", "选择要收取的掉落物")
			.t("unexplored", "那片区域你还没有探索过。")
			.t("empty", "那里没有可以收取的掉落物。")
			.t("no_gold", "金币不足（需要 %1$d 枚）。")
			.t("done", "虚空之手取来了%1$s，花费 %2$d 枚金币。")
			.t("level_up", "虚空之手与你的联系更紧密了！")
			.t("desc", "一只从虚空里伸出的、看不见的手。它能把任何你已经看过的地方掉落的东西直接递到你手上——当然，虚空从不做白工。\n\n每件物品消耗金币，神器等级越高越便宜。")
			.t("desc_worn", "装备后可用_收取_取回已探索区域的掉落物：每件消耗 %1$d 枚金币。");
	}

	public static final String AC_PULL = "PULL";
	/** 基础费用；每升一级减免 COST_STEP。 */
	public static final int BASE_COST = 60;
	public static final int COST_STEP = 5;
	public static final int MIN_COST = 10;

	{
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		levelCap = 10;
		charge = 0;
		partialCharge = 0;
		chargeCap = 10;
		defaultAction = AC_PULL;
	}

	/** 当前每次收取的金币消耗。 */
	public int cost() {
		return Math.max(MIN_COST, BASE_COST - COST_STEP * level());
	}

	public int exp() {
		return exp;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && !cursed) actions.add(AC_PULL);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (!AC_PULL.equals(action)) return;

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			usesTargeting = false;
		} else if (cursed) {
			GLog.w(Messages.get(Artifact.class, "cursed"));
			usesTargeting = false;
		} else {
			curUser = hero;
			usesTargeting = true;
			GameScene.selectCell(puller);
		}
	}

	/** 取回一格上的掉落物；返回是否成功。 */
	public boolean pullFrom(Hero hero, int cell) {
		if (hero == null || Dungeon.level == null || !Dungeon.level.insideMap(cell)) return false;
		if (Dungeon.level.mapped != null && !Dungeon.level.mapped[cell]) return false;

		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap == null || heap.isEmpty()) return false;

		int cost = cost();
		if (Dungeon.gold < cost) return false;

		Item item = heap.pickUp();
		if (item == null) return false;

		Dungeon.gold -= cost;
		item.doPickUp(hero);
		grow(1);
		return true;
	}

	/** 成长：每取回一件 +1，达到等级门槛就升级（费用随之降低）。 */
	public void grow(int amount) {
		if (amount <= 0) return;
		exp += amount;
		while (level() < levelCap && exp >= level() + 1) {
			exp = 0;
			upgrade();
			GLog.p(Messages.get(this, "level_up"));
		}
		if (exp > level() + 1) exp = level() + 1;
		updateQuickslot();
	}

	public final CellSelector.Listener puller = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null || curUser == null || Dungeon.level == null) return;
			if (!Dungeon.level.insideMap(target)) return;

			if (Dungeon.level.mapped != null && !Dungeon.level.mapped[target]) {
				GLog.w(Messages.get(VoidHand.class, "unexplored"));
				return;
			}
			Heap heap = Dungeon.level.heaps.get(target);
			if (heap == null || heap.isEmpty()) {
				GLog.w(Messages.get(VoidHand.class, "empty"));
				return;
			}
			int cost = cost();
			if (Dungeon.gold < cost) {
				GLog.w(Messages.get(VoidHand.class, "no_gold", cost));
				return;
			}

			Item item = heap.pickUp();
			if (item == null) {
				GLog.w(Messages.get(VoidHand.class, "empty"));
				return;
			}
			Dungeon.gold -= cost;
			String name = item.name();
			item.doPickUp(curUser);
			grow(1);
			GLog.i(Messages.get(VoidHand.class, "done", name, cost));
			curUser.spendAndNext(1f);
		}

		@Override
		public String prompt() {
			return Messages.get(VoidHand.class, "prompt");
		}
	};

	@Override
	public String desc() {
		String desc = super.desc();
		if (isEquipped(Dungeon.hero)) desc += "\n\n" + Messages.get(this, "desc_worn", cost());
		return desc;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return null;
	}
}
