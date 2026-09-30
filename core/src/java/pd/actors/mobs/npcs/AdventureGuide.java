/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Special Surprise hub characters rebuilt for the Shattered 4.0 ruleset.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.items.food.Food;
import pd.items.potions.PotionOfHealing;
import pd.items.quest.AdventureJournal;
import pd.items.scrolls.ScrollOfIdentify;
import pd.items.scrolls.ScrollOfMagicMapping;
import pd.items.weapon.melee.MeleeWeapon;
import pd.items.weapon.melee.fusion.FusionWeapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ShopkeeperSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndMessage;
import pd.windows.WndOptions;
import render.utils.serialize.Bundle;

public class AdventureGuide extends NPC {

	private int destination;
	private int purchasedMask;
	private boolean crafted;

	{
		spriteClass = ShopkeeperSprite.class;
		properties.add(Property.IMMOVABLE);
	}

	public AdventureGuide configure(int destination) {
		this.destination = destination;
		return this;
	}

	@Override
	protected boolean act() {
		spend(TICK);
		return true;
	}

	@Override
	public int defenseSkill(Char enemy) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public String name() {
		return Messages.get(this, "name_" + destination);
	}

	@Override
	public String description() {
		return Messages.get(this, "desc_" + destination);
	}

	@Override
	public boolean interact(Char c) {
		if (c instanceof Hero) {
			AdventureJournal.complete(destination);
			if (destination == 5) showTown((Hero)c);
			else if (destination == 8) showWorkshop((Hero)c);
			else GameScene.show(new WndMessage(Messages.get(this, "dialogue_" + destination)));
		}
		return true;
	}

	private void showTown(final Hero hero) {
		GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
				Messages.get(this, "dialogue_5"),
				Messages.get(this, "town_shop"), Messages.get(this, "town_leave")) {
			@Override
			protected void onSelect(int index) {
				if (index == 0) showStock(hero);
			}
		});
	}

	private void showStock(final Hero hero) {
		final int[] prices = {180, 70, 100, 80};
		String[] options = new String[4];
		for (int i = 0; i < options.length; i++) {
			int price = priceFor(prices[i]);
			options[i] = Messages.get(this, "stock_" + i, price);
			if ((purchasedMask & (1 << i)) != 0) options[i] += Messages.get(this, "sold");
		}
		GameScene.show(new WndOptions(sprite(), Messages.get(this, "shop_title"),
				Messages.get(this, "shop_desc"), options) {
			@Override
			protected boolean enabled(int index) {
				return (purchasedMask & (1 << index)) == 0;
			}

			@Override
			protected void onSelect(int index) {
				buy(hero, index, priceFor(prices[index]));
			}
		});
	}

	private int priceFor(int basePrice) {
		return Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.FOLLOWER
				? Math.max(1, Math.round(basePrice * 0.9f)) : basePrice;
	}

	private void buy(Hero hero, int index, int price) {
		if (Dungeon.gold < price) {
			GLog.w(Messages.get(this, "not_enough"));
			return;
		}
		Item item;
		switch (index) {
			case 0: item = new PotionOfHealing(); break;
			case 1: item = new Food(); break;
			case 2: item = new ScrollOfMagicMapping(); break;
			default: item = new ScrollOfIdentify(); break;
		}
		Dungeon.gold -= price;
		purchasedMask |= 1 << index;
		if (!item.collect(hero.belongings.backpack)) Dungeon.level.drop(item, hero.pos).sprite.drop();
		hero.spendAndNext(1f);
		GLog.p(Messages.get(this, "bought", item.name(), price));
	}

	private void showWorkshop(final Hero hero) {
		String forge = crafted ? Messages.get(this, "forge_spent")
				: Messages.get(this, "forge", priceFor(450));
		GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()),
				Messages.get(this, "dialogue_8"), forge, Messages.get(this, "town_leave")) {
			@Override
			protected boolean enabled(int index) {
				return index != 0 || !crafted;
			}

			@Override
			protected void onSelect(int index) {
				if (index == 0) GameScene.selectItem(forgeSelector);
			}
		});
	}

	private final WndBag.ItemSelector forgeSelector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(AdventureGuide.class, "forge_prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return null;
		}

		@Override
		public boolean itemSelectable(Item item) {
			//显式标记（FusionWeapon）取代原先按包名后缀 ".fusion" 的判定：包名不再承载语义
			return item instanceof MeleeWeapon && item instanceof FusionWeapon;
		}

		@Override
		public void onSelect(Item item) {
			if (!(item instanceof MeleeWeapon) || crafted) return;
			int price = priceFor(450);
			if (Dungeon.gold < price) {
				GLog.w(Messages.get(AdventureGuide.class, "not_enough"));
				return;
			}
			if (item.level() > 0) {
				GLog.w(Messages.get(AdventureGuide.class, "forge_already"));
				return;
			}
			Dungeon.gold -= price;
			crafted = true;
			item.upgrade();
			item.identify();
			Dungeon.hero.spendAndNext(1f);
			GLog.p(Messages.get(AdventureGuide.class, "forged", item.name()));
		}
	};

	private static final String DESTINATION = "destination";
	private static final String PURCHASED = "purchased";
	private static final String CRAFTED = "crafted";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DESTINATION, destination);
		bundle.put(PURCHASED, purchasedMask);
		bundle.put(CRAFTED, crafted);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		destination = bundle.getInt(DESTINATION);
		purchasedMask = bundle.getInt(PURCHASED);
		crafted = bundle.getBoolean(CRAFTED);
	}
}
