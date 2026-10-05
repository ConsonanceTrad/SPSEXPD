package pd.items.consum.food.vegetable;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.*;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.UpgradeEatBall;
import pd.items.equipment.bags.Bag;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.windows.WndBag;
import pd.messages.InlineText;
import pd.atlas.items.ConsumPotionSeedSeedDict;

import java.util.ArrayList;

/**
 * SPSEXPD: 吞星花蔬菜——主要用途是“吞噬物品并提炼精华”，直接食用则会灼伤自己。
 */
public class StarEaterFlower extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(StarEaterFlower.class)
			.t("name", "吞星花")
			.t("desc", "吞星花的一部分。它真正的用途是吞噬物品并提炼其中的精华；若直接食用，其中的消化液会灼伤你（损失 40% 最大生命）。")
			.t("ac_consume", "吞噬")
			.t("consume_prompt", "选择一件未装备的物品供吞星花吞噬提炼");
	}

	private static final String AC_CONSUME = "CONSUME";

	{ image = ConsumPotionSeedSeedDict.STAREATER_FLOWER; defaultAction = AC_CONSUME; }

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CONSUME);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (action.equals(AC_CONSUME)) {
			curUser = hero;
			GameScene.selectItem(consumeSelector);
		} else {
			super.execute(hero, action);
		}
	}

	private final WndBag.ItemSelector consumeSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(StarEaterFlower.class, "consume_prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return item != StarEaterFlower.this && UpgradeEatBall.consumable(item);
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			Item result = UpgradeEatBall.essenceFrom(item);
			item.detach(curUser.belongings.backpack);
			StarEaterFlower.this.detach(curUser.belongings.backpack);
			if (!result.collect()) Dungeon.level.drop(result, curUser.pos).sprite.drop();
			curUser.sprite.operate(curUser.pos);
			curUser.spendAndNext(Actor.TICK);
		}
	};

	@Override protected void onEat(Hero hero) {
		hero.damage(Math.max(1, Math.round(hero.HT * 0.4f)), this);
	}
}
