package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.effects.Transmuting;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.bags.Bag;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.brews.Brew;
import pd.items.consum.potions.elixirs.Elixir;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfTransmutation;
import pd.items.consum.stones.Runestone;
import pd.items.equipment.trinkets.Trinket;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.missiles.darts.Dart;
import pd.items.equipment.weapon.missiles.darts.TippedDart;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.windows.WndBag;

import java.util.ArrayList;

public class TransmutationBall extends Item {

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
		@Override public String textPrompt() { return Messages.get(TransmutationBall.class, "prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			if (item.isEquipped(Dungeon.hero) || item == TransmutationBall.this) return false;
			if (item instanceof StrBottle) return true;
			if (item instanceof MeleeWeapon) return true;
			if (item instanceof MissileWeapon) return item.getClass() != Dart.class;
			if (item instanceof Potion) return !(item instanceof Elixir || item instanceof Brew);
			if (item instanceof Scroll) return true;
			if (item instanceof Artifact) return !item.unique;
			return item instanceof Ring || item instanceof Wand || item instanceof Trinket
					|| item instanceof Plant.Seed || item instanceof Runestone;
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			Item result = changeItem(item);
			if (result == null) return;
			int slot = Dungeon.quickslot.getSlot(item);
			if (item instanceof MissileWeapon && !(item instanceof TippedDart)) {
				item.detachAll(curUser.belongings.backpack);
			} else {
				item.detach(curUser.belongings.backpack);
			}
			TransmutationBall.this.detach(curUser.belongings.backpack);
			if (!result.collect()) Dungeon.level.drop(result, curUser.pos).sprite.drop();
			if (slot != -1 && result.defaultAction() != null && Dungeon.hero.belongings.contains(result)) {
				Dungeon.quickslot.setSlot(slot, result);
			}
			Transmuting.show(curUser, item, result);
			curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
			curUser.spendAndNext(Actor.TICK);
		}
	};

	static Item changeItem(Item item) {
		return item instanceof StrBottle ? new MitBottle() : ScrollOfTransmutation.changeItem(item);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 50 * quantity; }
}
