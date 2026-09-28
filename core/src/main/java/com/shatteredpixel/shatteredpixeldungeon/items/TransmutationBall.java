package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

import java.util.ArrayList;

public class TransmutationBall extends Item {

	private static final String AC_USE = "USE";

	{
		image = ItemSpriteSheet.TRANSMUTATION_BALL;
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
