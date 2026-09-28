/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Garbage;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.GreatRune;
import com.shatteredpixel.shatteredpixeldungeon.items.GreenDewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PocketBall;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlienBag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.BuildBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HugeBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.WaterItem;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.FruitCandy;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Gel;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.Mediummeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.MixPizza;
import com.shatteredpixel.shatteredpixeldungeon.items.food.completefood.NutCookie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fruit.Fruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.fusion.Nut;
import com.shatteredpixel.shatteredpixeldungeon.items.food.meatfood.MeatFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.staplefood.StapleFood;
import com.shatteredpixel.shatteredpixeldungeon.items.food.vegetable.Vegetable;
import com.shatteredpixel.shatteredpixeldungeon.items.medicine.Timepill2;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.BattleAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.BlindAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.DewAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.DreamAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.EmptyAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.EvolveAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.FireAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.GoldAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.HeavyAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.IceAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.MossAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.RotAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.SandAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.StarAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.StormAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.SunAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.ThornAmmo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo.WoodenAmmo;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dewcatcher;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Freshberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.NutPlant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.ReNepenth;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Seedpod;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.StarEater;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class WndIronMaker extends WndOptions {

	private final Session session;

	public WndIronMaker() {
		this(new Session());
	}

	private WndIronMaker(Session session) {
		super(new ItemSprite(ItemSpriteSheet.ORE),
				Messages.get(WndIronMaker.class, "title"),
				description(session),
				Messages.get(WndIronMaker.class, "add"),
				Messages.get(WndIronMaker.class, "combine"),
				Messages.get(WndIronMaker.class, "cancel"));
		this.session = session;
	}

	private static String description(Session session) {
		String text = Messages.get(WndIronMaker.class, "text");
		if (session.items.isEmpty()) return text + "\n\n" + Messages.get(WndIronMaker.class, "empty");
		StringBuilder selected = new StringBuilder();
		for (Item item : session.items) {
			if (selected.length() > 0) selected.append(", ");
			selected.append(item.name());
		}
		return text + "\n\n" + Messages.get(WndIronMaker.class, "selected", selected);
	}

	@Override
	protected void onSelect(int index) {
		if (index == 0) {
			if (session.items.size() < 5) GameScene.selectItem(new IngredientSelector(session));
		} else if (index == 1) {
			if (!session.items.isEmpty()) forge(session);
		}
	}

	private static final class Session {
		final ArrayList<Item> items = new ArrayList<>();

		int selected(Item item) {
			int result = 0;
			for (Item selected : items) if (selected == item) result++;
			return result;
		}
	}

	private static final class IngredientSelector extends WndBag.ItemSelector {
		private final Session session;

		IngredientSelector(Session session) {
			this.session = session;
		}

		@Override
		public String textPrompt() {
			return Messages.get(WndIronMaker.class, "select");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item != null && !item.isEquipped(Dungeon.hero)
					&& session.items.size() < 5 && session.selected(item) < item.quantity();
		}

		@Override
		public void onSelect(Item item) {
			if (item != null && itemSelectable(item)) session.items.add(item);
			GameScene.show(new WndIronMaker(session));
		}
	}

	private static void forge(Session session) {
		Item result = recipe(session.items);
		if (result == null) return;
		for (Item ingredient : session.items) ingredient.detach(Dungeon.hero.belongings.backpack);
		Dungeon.level.drop(result, Dungeon.hero.pos).sprite.drop();
	}

	@SuppressWarnings("unchecked")
	static Item recipe(ArrayList<Item> items) {
		int garbage = count(items, Garbage.class);
		int ore = count(items, StoneOre.class);
		int water = count(items, WaterItem.class);
		int equipment = count(items, EquipableItem.class);
		int seeds = count(items, Plant.Seed.class);
		int potions = count(items, Potion.class);
		int scrolls = count(items, Scroll.class);
		int meatfoods = count(items, MeatFood.class);

		if (items.size() == 5 && garbage + ore == 5) return Generator.random();
		if (items.size() == 5 && count(items, Nut.class) == 5) return new NutCookie(6);
		if (items.size() == 5 && ore == 4 && water == 1) return new Timepill2();
		if (items.size() == 5 && meatfoods == 1 && ore == 1
				&& count(items, StapleFood.class) == 1 && count(items, Fruit.class) == 1
				&& count(items, Vegetable.class) == 1) return new MixPizza(8);
		if (items.size() == 5 && potions == 1 && ore == 1 && scrolls == 1 && water == 1 && seeds == 1) {
			return new ScrollOfRemoveCurse();
		}
		if (items.size() == 4 && potions == 1 && ore == 1 && scrolls == 1 && water == 1) {
			return new ScrollOfIdentify();
		}
		if (items.size() == 3 && ore == 1 && seeds == 1 && scrolls == 1) return new BuildBomb();
		if (items.size() == 3 && count(items, BuildBomb.class) == 1 && seeds == 2) {
			return AlienBag.randomBombSupply();
		}
		if (items.size() == 3 && water == 1 && count(items, Fruit.class) == 1 && ore == 1) {
			return new FruitCandy(2);
		}
		if (items.size() == 4 && count(items, DarkGold.class) == 3 && ore == 1) return new PocketBall();
		if (items.size() == 2 && ore == 2) return new HeavyAmmo();
		if (items.size() == 2 && ore == 1) {
			if (count(items, NutPlant.Seed.class) == 1) return new WoodenAmmo();
			if (count(items, Firebloom.Seed.class) == 1) return new FireAmmo();
			if (count(items, Icecap.Seed.class) == 1) return new IceAmmo();
			if (count(items, Stormvine.Seed.class) == 1) return new StormAmmo();
			if (count(items, Sorrowmoss.Seed.class) == 1) return new MossAmmo();
			if (count(items, Blindweed.Seed.class) == 1) return new BlindAmmo();
			if (count(items, Starflower.Seed.class) == 1) return new StarAmmo();
			if (count(items, Dreamfoil.Seed.class) == 1) return new DreamAmmo();
			if (count(items, Dewcatcher.Seed.class) == 1) return new DewAmmo();
			if (count(items, Sungrass.Seed.class) == 1) return new SunAmmo();
			if (count(items, Fadeleaf.Seed.class) == 1) return new SandAmmo();
			if (count(items, Seedpod.Seed.class) == 1) return new GoldAmmo();
			if (count(items, Rotberry.Seed.class) == 1 || count(items, Freshberry.Seed.class) == 1) return new RotAmmo();
			if (count(items, Earthroot.Seed.class) == 1) return new ThornAmmo();
			if (count(items, BlandfruitBush.Seed.class) == 1) return new EmptyAmmo();
			if (count(items, ReNepenth.Seed.class) == 1) return new EvolveAmmo();
			if (count(items, StarEater.Seed.class) == 1) return new BattleAmmo();
		}
		if (items.size() == 2 && count(items, BuildBomb.class) == 2) return new HugeBomb();
		if (items.size() == 2 && count(items, StoneOfAugmentation.class) == 1
				&& count(items, Stylus.class) == 1) return new GreatRune();
		if (items.size() == 2 && water == 1 && meatfoods == 1) return new Mediummeat();
		if (items.size() == 1 && count(items, ScrollOfMagicalInfusion.class) == 1) return new GreatRune();
		if (items.size() == 1 && count(items, Gel.class) == 1) return new Torch();
		if (items.size() == 2 && equipment == 2 && items.get(0).getClass() == items.get(1).getClass()) {
			Item result = Reflection.newInstance((Class<? extends Item>)items.get(0).getClass());
			if (result != null && result.isUpgradable()) {
				result.level(items.get(0).level() + 1).identify();
				result.cursed = false;
				result.cursedKnown = true;
				return result;
			}
			return new Garbage(2);
		}
		if (equipment == 1 && water > 0 && equipment + water == items.size()) {
			Item source = first(items, EquipableItem.class);
			if (source != null && source.isUpgradable() && com.watabou.utils.Random.Int(100) < water * 15) {
				Item result = Reflection.newInstance((Class<? extends Item>)source.getClass());
				if (result == null) return new Garbage();
				result.level(source.level() + 1).identify();
				result.cursed = false;
				result.cursedKnown = true;
				return result;
			}
			return new Garbage();
		}
		if (items.size() == 5 && seeds == 5) return new Garbage(3);
		if (items.size() == 2 && seeds == 2) return new Garbage();
		if (items.size() == 1 && seeds == 1) return new GreenDewdrop();
		if (items.size() == 1 && equipment == 1) return new Garbage(2);
		return new Garbage(items.size());
	}

	private static int count(ArrayList<Item> items, Class<?> type) {
		int result = 0;
		for (Item item : items) if (type.isInstance(item)) result++;
		return result;
	}

	private static Item first(ArrayList<Item> items, Class<?> type) {
		for (Item item : items) if (type.isInstance(item)) return item;
		return null;
	}
}
