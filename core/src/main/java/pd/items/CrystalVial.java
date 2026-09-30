/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Speck;
import pd.items.bags.Bag;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndBag;
import pd.windows.WndUseItem;
import render.noosa.audio.Sample;
import render.utils.Bundle;

import java.util.ArrayList;

/** Bone Star's secondary vial, charged whenever the main waterskin is used. */
public class CrystalVial extends Item {

	private static final String AC_DRINK = "DRINK";
	private static final String AC_BLESS = "BLESS";
	private static final String AC_CHOOSE = "CHOOSE";
	private static final String VOLUME = "volume";
	private static final float TIME_TO_DRINK = 2f;

	private int volume;

	{
		image = ItemSpriteSheet.CRYSTAL_VIAL;
		defaultAction = AC_CHOOSE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (volume > 10) actions.add(AC_DRINK);
		if (volume > 50) actions.add(AC_BLESS);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_CHOOSE.equals(action)) {
			GameScene.show(new WndUseItem(null, this));
		} else if (AC_DRINK.equals(action)) {
			drink(hero);
		} else if (AC_BLESS.equals(action) && Dungeon.dewDraw && volume > 50) {
			curUser = hero;
			GameScene.selectItem(itemSelector);
		}
	}

	public boolean drink(Hero hero) {
		if (volume <= 10) return false;
		int healing = Math.min(hero.HT / 5, hero.HT - hero.HP);
		hero.HP += healing;
		volume -= 10;
		hero.spend(TIME_TO_DRINK);
		hero.busy();
		Sample.INSTANCE.play(Assets.Sounds.DRINK);
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			if (healing > 0) hero.sprite.showStatus(CharSprite.POSITIVE,
					Messages.get(this, "value", healing));
		}
		updateQuickslot();
		return true;
	}

	public boolean blessItem(Hero hero, Item item) {
		if (item == null || !item.isUpgradable() || volume <= 50) return false;
		item.upgrade();
		volume -= 50;
		Badges.validateItemLevelAquired(item);
		if (hero.sprite != null) {
			hero.sprite.operate(hero.pos);
			hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
		}
		hero.busy();
		updateQuickslot();
		return true;
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(CrystalVial.class, "select"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item != null && item.isUpgradable(); }
		@Override public void onSelect(Item item) { blessItem(curUser, item); }
	};

	public void fill() {
		if (volume < 50) volume += 5;
		updateQuickslot();
	}

	public int volume() {
		return volume;
	}

	public void volume(int value) {
		volume = Math.max(0, value);
		updateQuickslot();
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public String status() { return Integer.toString(volume); }

	public String status2() {
		return Messages.format("%d/%d", volume, 50);
	}

	@Override
	public String toString() {
		return super.toString() + " (" + status2() + ")";
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VOLUME, volume);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		volume = Math.max(0, bundle.getInt(VOLUME));
	}
}
