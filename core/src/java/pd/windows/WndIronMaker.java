/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 */

package pd.windows;

import pd.atlas.items.SpecificTaskDict;

import pd.Dungeon;
import pd.items.EquipableItem;
import pd.items.Garbage;
import pd.items.Generator;
import pd.items.GreatRune;
import pd.items.GreenDewdrop;
import pd.items.Item;
import pd.items.PocketBall;
import pd.items.StoneOre;
import pd.items.Stylus;
import pd.items.Torch;
import pd.items.equipment.artifacts.AlienBag;
import pd.items.equipment.bombs.BuildBomb;
import pd.items.equipment.bombs.HugeBomb;
import pd.items.consum.food.WaterItem;
import pd.items.consum.food.completefood.FruitCandy;
import pd.items.consum.food.completefood.Gel;
import pd.items.consum.food.completefood.Mediummeat;
import pd.items.consum.food.completefood.MixPizza;
import pd.items.consum.food.completefood.NutCookie;
import pd.items.consum.food.fruit.Fruit;
import pd.items.consum.food.fusion.Nut;
import pd.items.consum.food.meatfood.MeatFood;
import pd.items.consum.food.staplefood.StapleFood;
import pd.items.consum.food.vegetable.Vegetable;
import pd.items.consum.medicine.Timepill2;
import pd.items.consum.potions.Potion;
import pd.items.quest.DarkGold;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.consum.stones.StoneOfAugmentation;
import pd.items.equipment.weapon.spammo.BattleAmmo;
import pd.items.equipment.weapon.spammo.BlindAmmo;
import pd.items.equipment.weapon.spammo.DewAmmo;
import pd.items.equipment.weapon.spammo.DreamAmmo;
import pd.items.equipment.weapon.spammo.EmptyAmmo;
import pd.items.equipment.weapon.spammo.EvolveAmmo;
import pd.items.equipment.weapon.spammo.FireAmmo;
import pd.items.equipment.weapon.spammo.GoldAmmo;
import pd.items.equipment.weapon.spammo.HeavyAmmo;
import pd.items.equipment.weapon.spammo.IceAmmo;
import pd.items.equipment.weapon.spammo.MossAmmo;
import pd.items.equipment.weapon.spammo.RotAmmo;
import pd.items.equipment.weapon.spammo.SandAmmo;
import pd.items.equipment.weapon.spammo.StarAmmo;
import pd.items.equipment.weapon.spammo.StormAmmo;
import pd.items.equipment.weapon.spammo.SunAmmo;
import pd.items.equipment.weapon.spammo.ThornAmmo;
import pd.items.equipment.weapon.spammo.WoodenAmmo;
import pd.messages.Messages;
import pd.plants.BlandfruitBush;
import pd.plants.Blindweed;
import pd.plants.Dewcatcher;
import pd.plants.Dreamfoil;
import pd.plants.Earthroot;
import pd.plants.Fadeleaf;
import pd.plants.Firebloom;
import pd.plants.Freshberry;
import pd.plants.Icecap;
import pd.plants.NutPlant;
import pd.plants.Plant;
import pd.plants.ReNepenth;
import pd.plants.Rotberry;
import pd.plants.Seedpod;
import pd.plants.Sorrowmoss;
import pd.plants.StarEater;
import pd.plants.Starflower;
import pd.plants.Stormvine;
import pd.plants.Sungrass;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

public class WndIronMaker extends WndOptions {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndIronMaker.class)
			.t("title", "铁砧")
			.t("text", "这座铁砧已经弃用，它的锻造公式（弹药、炸弹、装备重铸等）全部转移到了炼金釜。")
			.t("select", "选择装备或材料")
			.t("add", "添加材料")
			.t("combine", "锻造")
			.t("cancel", "取消")
			.t("empty", "尚未选择材料。")
			.t("selected", "已选择：%s");
	}




	private final Session session;

	public WndIronMaker() {
		this(new Session());
	}

	private WndIronMaker(Session session) {
		super(new ItemSprite(SpecificTaskDict.ORE_0),
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

	static Item recipe(ArrayList<Item> items) {
		return pd.items.IronMakerRecipes.forge(items);
	}

}
