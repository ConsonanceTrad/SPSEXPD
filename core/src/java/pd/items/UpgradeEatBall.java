package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.equipment.bags.Bag;
import pd.items.consum.potions.Potion;
import pd.items.consum.scrolls.Scroll;
import pd.messages.Messages;
import pd.plants.Seedpod;
import pd.scenes.GameScene;
import pd.windows.WndBag;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class UpgradeEatBall extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(UpgradeEatBall.class)
			.t("name", "吞星花果实")
			.t("ac_use", "使用")
			.t("prompt", "选择一件未装备的物品进行提炼")
			.t("desc", "吞星花结出的果实。它会摧毁一件物品，并尝试将其中的强化提炼为精华。");
	}


	private static final String AC_USE = "USE";

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
		defaultAction = AC_USE;
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_USE)) {
			curUser = hero;
			GameScene.selectItem(itemSelector);
		}
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(UpgradeEatBall.class, "prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return item != UpgradeEatBall.this && !item.isEquipped(Dungeon.hero)
					&& (item.isUpgradable() || item instanceof Scroll || item instanceof Potion || item instanceof Stylus);
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			Item result = essenceFrom(item);
			item.detach(curUser.belongings.backpack);
			UpgradeEatBall.this.detach(curUser.belongings.backpack);
			if (!result.collect()) Dungeon.level.drop(result, curUser.pos).sprite.drop();
			curUser.sprite.operate(curUser.pos);
			curUser.spendAndNext(Actor.TICK);
		}
	};

	private Item essenceFrom(Item item) {
		if (item.isUpgradable()) {
			int upgrades = Math.max(0, item.visiblyUpgraded());
			if (Random.Float() < upgrades / 10f) return new UpgradeBlobViolet();
			if (Random.Float() < upgrades / 5f) return new UpgradeBlobRed();
			if (Random.Float() < upgrades / 3f) return new UpgradeBlobYellow();
		} else if (Random.Float() < 0.1f) {
			return new UpgradeBlobYellow();
		}
		return new Seedpod.Seed();
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 50 * quantity; }
}
