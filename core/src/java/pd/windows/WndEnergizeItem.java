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

import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.Hero;
import pd.items.EnergyCrystal;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.equipment.trinkets.Trinket;
import pd.messages.Messages;
import pd.scenes.AlchemyScene;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.ui.RedButton;
import pd.utils.GLog;
import render.noosa.Game;
import pd.messages.InlineText;

public class WndEnergizeItem extends WndInfoItem {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndEnergizeItem.class)
			.t("prompt", "提炼一件物品")
			.t("energize", "转化为%d点能量")
			.t("energize_1", "转化一个为%d点能量")
			.t("energize_all", "转化全部为%d点能量")
			.t("trinket_warn", "你确定要炼化你的饰物吗？你会获得5点炼金能量返还，但_饰物将会永远消失！_")
			.t("trinket_yes", "是，将其炼化")
			.t("trinket_no", "不，算了");
	}




	private static final float GAP		= 2;
	private static final int BTN_HEIGHT	= 18;

	private WndBag owner;

	public WndEnergizeItem(Item item, WndBag owner) {
		super(item);

		this.owner = owner;

		float pos = height;

		if (item.quantity() == 1) {

			RedButton btnEnergize = new RedButton( Messages.get(this, "energize", item.energyVal()) ) {
				@Override
				protected void onClick() {
					if (item instanceof Trinket){
						Game.scene().addToFront(new WndOptions(new ItemSprite(item), Messages.titleCase(item.name()),
								Messages.get(WndEnergizeItem.class, "trinket_warn"),
								Messages.get(WndEnergizeItem.class, "trinket_yes"),
								Messages.get(WndEnergizeItem.class, "trinket_no")){

							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									energizeAll(item);
								}
								openItemSelector();
							}

							@Override
							public void hide() {
								super.hide();
								WndEnergizeItem.this.hide();
							}
						});
					} else {
						energizeAll(item);
						hide();
					}
				}
			};
			btnEnergize.setRect( 0, pos + GAP, width, BTN_HEIGHT );
			btnEnergize.icon(new ItemSprite(SpecificCurrencyDict.ENERGY_0));
			add( btnEnergize );

			pos = btnEnergize.bottom();

		} else {

			int energyAll = item.energyVal();
			RedButton btnEnergize1 = new RedButton( Messages.get(this, "energize_1", energyAll / item.quantity()) ) {
				@Override
				protected void onClick() {
					energizeOne( item );
					hide();
				}
			};
			btnEnergize1.setRect( 0, pos + GAP, width, BTN_HEIGHT );
			btnEnergize1.icon(new ItemSprite(SpecificCurrencyDict.ENERGY_0));
			add( btnEnergize1 );
			RedButton btnEnergizeAll = new RedButton( Messages.get(this, "energize_all", energyAll ) ) {
				@Override
				protected void onClick() {
					energizeAll( item );
					hide();
				}
			};
			btnEnergizeAll.setRect( 0, btnEnergize1.bottom() + 1, width, BTN_HEIGHT );
			btnEnergizeAll.icon(new ItemSprite(SpecificCurrencyDict.ENERGY_0));
			add( btnEnergizeAll );

			pos = btnEnergizeAll.bottom();

		}

		resize( width, (int)pos );

	}

	@Override
	public void hide() {

		super.hide();

		if (owner != null) {
			owner.hide();
			openItemSelector();
		}
	}

	public static void energizeAll(Item item ) {

		if (item.isEquipped( Dungeon.hero ) && !((EquipableItem)item).doUnequip( Dungeon.hero, false )) {
			return;
		}
		item.detachAll( Dungeon.hero.belongings.backpack );
		energize(item);
	}

	public static void energizeOne( Item item ) {

		if (item.quantity() <= 1) {
			energizeAll( item );
		} else {
			energize(item.detach( Dungeon.hero.belongings.backpack ));
		}
	}

	private static void energize(Item item){
		Hero hero = Dungeon.hero;

		if (ShatteredPixelDungeon.scene() instanceof AlchemyScene){

			Dungeon.energy += item.energyVal();
			((AlchemyScene) ShatteredPixelDungeon.scene()).createEnergy();
			if (!item.isIdentified()){
				((AlchemyScene) ShatteredPixelDungeon.scene()).showIdentify(item);
			}

		} else {

			//energizing items doesn't spend time
			hero.spend(-hero.cooldown());
			new EnergyCrystal(item.energyVal()).doPickUp(hero);
			item.identify();
			GLog.h("You energized: " + item.name());

		}
	}

	public static WndBag openItemSelector(){
		if (ShatteredPixelDungeon.scene() instanceof GameScene) {
			return GameScene.selectItem( selector );
		} else {
			WndBag window = WndBag.getBag( selector );
			ShatteredPixelDungeon.scene().addToFront(window);
			return window;
		}
	}

	public static WndBag.ItemSelector selector = new WndBag.ItemSelector() {
		@Override
		public String textPrompt() {
			return Messages.get(WndEnergizeItem.class, "prompt");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.energyVal() > 0;
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				WndBag parentWnd = openItemSelector();
				if (ShatteredPixelDungeon.scene() instanceof GameScene) {
					GameScene.show(new WndEnergizeItem(item, parentWnd));
				} else {
					ShatteredPixelDungeon.scene().addToFront(new WndEnergizeItem(item, parentWnd));
				}
			}
		}
	};

}
