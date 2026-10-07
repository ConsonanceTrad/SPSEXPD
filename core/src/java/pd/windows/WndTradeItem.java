/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.windows;

import pd.atlas.items.SpecificCurrencyDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.Shopkeeper;
import pd.items.EquipableItem;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.Item;
import pd.items.equipment.artifacts.MasterThievesArmband;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;
import pd.ui.CurrencyIndicator;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.messages.InlineText;

public class WndTradeItem extends WndInfoItem {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndTradeItem.class)
			.t("buy", "花%d金币购买")
			.t("steal", "以%1$d%%成功率偷窃\n消耗%2$d充能")
			.t("steal_warn", "你的袖章充能无法保证稳妥地窃取这件道具。一旦失败，店主就会关店跑路。你确定要尝试继续窃取吗？")
			.t("steal_warn_yes", "是的，我确定")
			.t("steal_warn_no", "不，我改主意了")
			.t("sell", "卖出以获得%d金币")
			.t("sell_1", "卖出1个以获得%d金币")
			.t("sell_all", "全部卖出以获得%d金币");
	}




	private static final float GAP		= 2;
	private static final int BTN_HEIGHT	= 18;

	private WndBag owner;

	private boolean selling = false;

	//selling
	public WndTradeItem( final Item item, WndBag owner ) {

		super(item);

		selling = true;

		this.owner = owner;

		float pos = height;

		//find the shopkeeper in the current level
		Shopkeeper shop = null;
		for (Char ch : Actor.chars()){
			if (ch instanceof Shopkeeper){
				shop = (Shopkeeper) ch;
				break;
			}
		}
		final Shopkeeper finalShop = shop;

		if (item.quantity() == 1 || (item instanceof MissileWeapon && item.isUpgradable())) {

			if (item instanceof MissileWeapon && ((MissileWeapon) item).extraThrownLeft){
				RenderedTextBlock warn = PixelScene.renderTextBlock(Messages.get(WndUpgrade.class, "thrown_dust"), 6);
				warn.hardlight(CharSprite.WARNING);
				warn.maxWidth(this.width);
				warn.setPos(0, pos + GAP);
				add(warn);
				pos = warn.bottom();
			}

			RedButton btnSell = new RedButton( Messages.get(this, "sell", item.value()) ) {
				@Override
				protected void onClick() {
					sell( item, finalShop);
					hide();
				}
			};
			btnSell.setRect( 0, pos + GAP, width, BTN_HEIGHT );
			btnSell.icon(new ItemSprite(SpecificCurrencyDict.GOLD_0));
			add( btnSell );

			pos = btnSell.bottom();

		} else {

			int priceAll= item.value();
			RedButton btnSell1 = new RedButton( Messages.get(this, "sell_1", priceAll / item.quantity()) ) {
				@Override
				protected void onClick() {
					sellOne( item, finalShop );
					hide();
				}
			};
			btnSell1.setRect( 0, pos + GAP, width, BTN_HEIGHT );
			btnSell1.icon(new ItemSprite(SpecificCurrencyDict.GOLD_0));
			add( btnSell1 );
			RedButton btnSellAll = new RedButton( Messages.get(this, "sell_all", priceAll ) ) {
				@Override
				protected void onClick() {
					sell( item, finalShop );
					hide();
				}
			};
			btnSellAll.setRect( 0, btnSell1.bottom() + 1, width, BTN_HEIGHT );
			btnSellAll.icon(new ItemSprite(SpecificCurrencyDict.GOLD_0));
			add( btnSellAll );

			pos = btnSellAll.bottom();

		}

		resize( width, (int)pos );
	}

	//buying
	public WndTradeItem( final Heap heap ) {

		super(heap);

		selling = false;
		CurrencyIndicator.showGold = true;

		Item item = heap.peek();

		float pos = height;

		final int price = Shopkeeper.sellPrice( item );

		RedButton btnBuy = new RedButton( Messages.get(this, "buy", price) ) {
			@Override
			protected void onClick() {
				hide();
				buy( heap );
			}
		};
		btnBuy.setRect( 0, pos + GAP, width, BTN_HEIGHT );
		btnBuy.icon(new ItemSprite(SpecificCurrencyDict.GOLD_0));
		btnBuy.enable( price <= Dungeon.gold );
		add( btnBuy );

		pos = btnBuy.bottom();

		resize(width, (int) pos);
	}
	
	@Override
	public void hide() {
		
		super.hide();
		CurrencyIndicator.showGold = false;
		
		if (owner != null) {
			owner.hide();
		}
		if (selling) Shopkeeper.sell();
	}

	public static void sell( Item item ) {
		sell(item, null);
	}

	public static void sell( Item item, Shopkeeper shop ) {
		
		Hero hero = Dungeon.hero;
		
		if (item.isEquipped( hero ) && !((EquipableItem)item).doUnequip( hero, false )) {
			return;
		}
		item.detachAll( hero.belongings.backpack );

		if (item instanceof MissileWeapon && item.isUpgradable()){
			Buff.affect(hero, MissileWeapon.UpgradedSetTracker.class).levelThresholds.put(((MissileWeapon) item).setID, Integer.MAX_VALUE);
		}

		//selling items in the sell interface doesn't spend time
		hero.spend(-hero.cooldown());

		new Gold( item.value() ).doPickUp( hero );

		if (shop != null){
			shop.buybackItems.add(item);
			while (shop.buybackItems.size() > Shopkeeper.MAX_BUYBACK_HISTORY){
				shop.buybackItems.remove(0);
			}
		}
	}

	public static void sellOne( Item item ) {
		sellOne( item, null );
	}

	public static void sellOne( Item item, Shopkeeper shop ) {
		
		if (item.quantity() <= 1) {
			sell( item, shop );
		} else {
			
			Hero hero = Dungeon.hero;
			
			item = item.detach( hero.belongings.backpack );

			//selling items in the sell interface doesn't spend time
			hero.spend(-hero.cooldown());

			new Gold( item.value() ).doPickUp( hero );

			if (shop != null){
				shop.buybackItems.add(item);
				while (shop.buybackItems.size() > Shopkeeper.MAX_BUYBACK_HISTORY){
					shop.buybackItems.remove(0);
				}
			}
		}
	}
	
	private void buy( Heap heap ) {
		
		Item item = heap.pickUp();
		if (item == null) return;
		
		int price = Shopkeeper.sellPrice( item );
		Dungeon.gold -= price;
		Catalog.countUses(Gold.class, price);
		
		if (!item.doPickUp( Dungeon.hero )) {
			Dungeon.level.drop( item, heap.pos ).sprite.drop();
		}

		//SPS: 卖光检查；另外免费拿货时商人会心疼地喊一句
		for (Mob mob : Dungeon.level.mobs()) {
			if (mob instanceof Shopkeeper) {
				Shopkeeper shop = (Shopkeeper) mob;
				if (price == 0) {
					shop.yell( Messages.get(Shopkeeper.class, "free_buy") );
				}
				if (shop.shopRoom != null) shop.shopRoom.checkRestock();
			}
		}
	}
}
