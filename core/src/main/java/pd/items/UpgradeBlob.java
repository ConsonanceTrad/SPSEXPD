package pd.items;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.bags.Bag;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;

import java.util.ArrayList;

public abstract class UpgradeBlob extends Item {

	private static final String AC_APPLY = "APPLY";

	{
		stackable = true;
		defaultAction = AC_APPLY;
	}

	protected abstract int upgrades();

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_APPLY)) {
			curUser = hero;
			GameScene.selectItem(itemSelector);
		}
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() {
			return Messages.get(UpgradeBlob.class, "prompt");
		}
		@Override public Class<? extends Bag> preferredBag() {
			return Belongings.Backpack.class;
		}
		@Override public boolean itemSelectable(Item item) {
			return item.isUpgradable() && !item.isEquipped(Dungeon.hero);
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			UpgradeBlob.this.detach(curUser.belongings.backpack);
			for (int i = 0; i < upgrades(); i++) item.upgrade();
			Badges.validateItemLevelAquired(item);
			Item.updateQuickslot();
			curUser.sprite.operate(curUser.pos);
			curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
			curUser.spendAndNext(Actor.TICK);
			GLog.p(Messages.get(UpgradeBlob.class, "applied", item.name(), upgrades()));
		}
	};

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }
}
