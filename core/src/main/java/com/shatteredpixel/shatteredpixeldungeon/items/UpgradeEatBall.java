package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Seedpod;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class UpgradeEatBall extends Item {

	private static final String AC_USE = "USE";

	{
		image = ItemSpriteSheet.UPGRADE_EATER;
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
