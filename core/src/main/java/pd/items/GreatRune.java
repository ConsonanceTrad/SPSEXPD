/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 */

package pd.items;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.armor.Armor;
import pd.items.scrolls.ScrollOfRemoveCurse;
import pd.items.weapon.Weapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;

import java.util.ArrayList;

public class GreatRune extends Item {

	public static final String AC_INSCRIBE = "INSCRIBE";

	{
		image = ItemSpriteSheet.STONE_ENCHANT;
		stackable = true;
		defaultAction = AC_INSCRIBE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_INSCRIBE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_INSCRIBE.equals(action)) {
			curUser = hero;
			GameScene.selectItem(SELECTOR);
		}
	}

	private final WndBag.ItemSelector SELECTOR = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(GreatRune.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof Weapon || item instanceof Armor;
		}

		@Override
		public void onSelect(Item item) {
			if (item instanceof Weapon) chooseWeapon((Weapon)item);
			else if (item instanceof Armor) chooseArmor((Armor)item);
		}
	};

	private void chooseWeapon(final Weapon weapon) {
		final Weapon.Enchantment[] choices = {
				Weapon.Enchantment.random(), Weapon.Enchantment.random(), Weapon.Enchantment.random()
		};
		GameScene.show(new WndOptions(Messages.titleCase(name()),
				Messages.get(this, "weapon"),
				choices[0].name(), choices[1].name(), choices[2].name(), Messages.get(this, "cancel")) {
			@Override
			protected void onSelect(int index) {
				if (index < 3) {
					weapon.enchant(choices[index]);
					complete(weapon);
				}
			}
		});
	}

	private void chooseArmor(final Armor armor) {
		final Armor.Glyph[] choices = {Armor.Glyph.random(), Armor.Glyph.random(), Armor.Glyph.random()};
		GameScene.show(new WndOptions(Messages.titleCase(name()),
				Messages.get(this, "armor"),
				choices[0].name(), choices[1].name(), choices[2].name(), Messages.get(this, "cancel")) {
			@Override
			protected void onSelect(int index) {
				if (index < 3) {
					armor.inscribe(choices[index]);
					complete(armor);
				}
			}
		});
	}

	private void complete(Item item) {
		if (detach(Dungeon.hero.belongings.backpack) == null) return;
		ScrollOfRemoveCurse.uncurse(Dungeon.hero, item);
		item.identify();
		Dungeon.hero.spend(2f);
		GLog.p(Messages.get(this, "item"));
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 30 * quantity;
	}
}
