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
import pd.items.equipment.bags.Bag;
import pd.items.consum.food.Food;
import pd.items.consum.potions.PotionOfHealing;
import pd.items.quest.AdventureJournal;
import pd.items.consum.scrolls.ScrollOfIdentify;
import pd.items.consum.scrolls.ScrollOfMagicMapping;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.items.equipment.weapon.melee.fusion.FusionWeapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ShopkeeperSprite;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndMessage;
import pd.windows.WndOptions;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class AdventureGuide extends NPC {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(AdventureGuide.class)
			.t("name_0", "居所记录员")
			.t("desc_0", "记录员负责维持返回主地牢的稳定路线。安全居所不会生成可反复获取的补给。")
			.t("dialogue_0", "异界日志已经与居所路标同步，推箱练习场的路线已被记录。")
			.t("name_5", "多利亚镇委托人")
			.t("desc_5", "委托人负责登记城镇远征。完成这次交谈后，春节庭院与矿区路线会依次开放。")
			.t("dialogue_5", "城镇档案已经认可你的异界日志，春节庭院被登记为下一项委托。")
			.t("name_8", "新居看守者")
			.t("desc_8", "看守者守护着从和平地图通往危险远征链的路线。")
			.t("dialogue_8", "准备已经完成，寄生虫巢被登记为第一项战斗远征。")
			.t("town_shop", "查看有限补给")
			.t("town_leave", "暂时离开")
			.t("shop_title", "多利亚镇补给")
			.t("shop_desc", "这些货物每局各有一件，售出后不会补货。信徒职业享受九折价格。")
			.t("stock_0", "治疗药剂 - %d金币")
			.t("stock_1", "食物 - %d金币")
			.t("stock_2", "魔法地图卷轴 - %d金币")
			.t("stock_3", "鉴定卷轴 - %d金币")
			.t("sold", "（已售出）")
			.t("not_enough", "你的金币不够。")
			.t("bought", "你用%2$d金币购买了%1$s。")
			.t("forge", "异界锻造 - %d金币")
			.t("forge_spent", "异界锻造（本局已使用）")
			.t("forge_prompt", "选择一件未强化的融合武器")
			.t("forge_already", "这件武器已经强化过，无法接受这次有限锻造。")
			.t("forged", "%s被稳定强化到+1。异界锻炉在本局中已经熄灭。");
	}


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
