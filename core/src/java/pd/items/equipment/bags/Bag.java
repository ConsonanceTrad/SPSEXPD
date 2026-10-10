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

package pd.items.equipment.bags;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.LostInventory;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.quest.DarkGold;
import pd.scenes.GameScene;
import pd.windows.WndBag;
import pd.windows.WndQuickBag;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Iterator;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.atlas.items.EquipmentBagsDict;

public class Bag extends Item implements Iterable<Item> {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Bag.class)
			.t("name", "背包")
			.t("ac_open", "打开")
			.t("discover_hint", "你可在商店中购买该物品。");
	}




	public static final String AC_OPEN	= "OPEN";
	
	{
		image = EquipmentBagsDict.BACKPACK_0;
		
		defaultAction = AC_OPEN;

		unique = true;
	}
	
	public Char owner;

	public ArrayList<Item> items = new ArrayList<>();

	public int capacity(){
		return 20; // default container size
	}

	/**
	 * SPSEXPD: 包裹标签页的固定排序位（越小越靠前，未指认的排最后）。
	 * 固定顺序：绒布包-卷轴筒-药水箱-购物车-竹背篓-魔法套筒-暗器袋-草靶子-钥匙串。
	 */
	public int bagOrder(){
		return 100;
	}

	//SPS: 包裹袋不允许被售卖（商店以 item.value() 定价）
	@Override
	public int value() {
		return 0;
	}

	//if an item is being quick-used from the bag, the bag should take on its targeting properties
	public Item quickUseItem;

	@Override
	public int targetingPos(Hero user, int dst) {
		if (quickUseItem != null){
			return quickUseItem.targetingPos(user, dst);
		} else {
			return super.targetingPos(user, dst);
		}
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		//SPSEXPD: 包裹的快捷行为是「打开」——打开背包窗口并跳转到本包裹的标签页（可在快捷栏中使用）
		actions.add( AC_OPEN );
		return actions;
	}

	@Override
	public String actionName( String action, Hero hero ) {
		//SPSEXPD: 统一用 Bag 的文本键，避免各包裹子类缺少 ac_open 而显示 TEXT NOT FOUND
		if (AC_OPEN.equals(action)) {
			return Messages.get( Bag.class, "ac_open" );
		}
		return super.actionName( action, hero );
	}

	@Override
	public void execute( Hero hero, String action ) {
		quickUseItem = null;

		super.execute( hero, action );

		if (action.equals( AC_OPEN )) {
			//SPSEXPD: 打开背包窗口并跳转到本包裹的标签页（WndBag 构造时选中 bag 对应的标签）
			GameScene.show( new WndBag( this ) );
		}
	}
	
	@Override
	public boolean collect( Bag container ) {

		//SPS: 英雄已拥有同款包裹袋时，本次捡起的袋子转换为 5 个暗金块掉在地上（本体不进入背包）
		if (container != null && container.owner instanceof Hero){
			Hero hero = (Hero) container.owner;
			for (Bag owned : hero.belongings.getBags()){
				if (owned != null && owned != this && owned.getClass() == this.getClass()){
					DarkGold gold = new DarkGold();
					gold.quantity( 5 );
					Dungeon.level.drop( gold, hero.pos );
					return true;   //视为已处理：本体消失、不占背包格
				}
			}
		}

		grabItems(container);

		//if there are any quickslot placeholders that match items in this bag, assign them
		for (Item item : items) {
			Dungeon.quickslot.replacePlaceholder(item);
		}

		if (super.collect( container )) {
			
			owner = container.owner;
			
			Badges.validateAllBagsBought( this );
			
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDetach( ) {
		this.owner = null;
		for (Item item : items) {
			Dungeon.quickslot.clearItem(item);
		}
		updateQuickslot();
	}

	public void grabItems(){
		if (owner != null && owner instanceof Hero && this != ((Hero) owner).belongings.backpack) {
			grabItems(((Hero) owner).belongings.backpack);
		}
	}

	public void grabItems( Bag container ){
		for (Item item : container.items.toArray( new Item[0] )) {
			if (canHold( item )) {
				int slot = Dungeon.quickslot.getSlot(item);
				item.detachAll(container);
				if (!item.collect(this)) {
					item.collect(container);
				}
				if (slot != -1) {
					Dungeon.quickslot.setSlot(slot, item);
				}
			}
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public void clear() {
		items.clear();
	}
	
	public void resurrect() {
		for (Item item : items.toArray(new Item[0])){
			if (!item.unique) items.remove(item);
		}
	}
	
	private static final String ITEMS	= "inventory";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEMS, items );
	}

	//temp variable so that bags can load contents even with lost inventory debuff
	private boolean loading;

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );

		loading = true;
		for (Bundlable item : bundle.getCollection( ITEMS )) {
			if (item != null){
				if (!((Item)item).collect( this )){
					//force-add the item if necessary, such as if its item category changed after an update
					items.add((Item) item);
				}
			}
		}
		loading = false;
	}
	
	public boolean contains( Item item ) {
		for (Item i : items) {
			if (i == item) {
				return true;
			} else if (i instanceof Bag && ((Bag)i).contains( item )) {
				return true;
			}
		}
		return false;
	}

	/** SPSEXPD: 已占用的格数——包裹袋（Bag）不占格，与 WndBag 的显示口径一致。
	 *  包裹数量会随玩家增减而变动，所以每次动态统计，不写死常量。 */
	public int usedSlots(){
		int n = 0;
		for (Item i : items) if (!(i instanceof Bag)) n++;
		return n;
	}

	/** SPSEXPD: 非本物品占用的预留格数（主背包最后一格恒留给露珠瓶，见 Belongings.Backpack）。 */
	protected int reservedSlotsFor( Item item ){
		return 0;
	}

	public boolean canHold( Item item ){
		if (!loading && owner != null && owner.buff(LostInventory.class) != null
			&& !item.keptThroughLostInventory()){
			return false;
		}

		//SPSEXPD: 用 usedSlots() 而不是 items.size()——包裹袋不占格，不该挤掉普通物品的名额
		if (items.contains(item) || item instanceof Bag || usedSlots() < capacity() - reservedSlotsFor(item)){
			return true;
		} else if (item.stackable) {
			for (Item i : items) {
				if (item.isSimilar( i )) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Iterator<Item> iterator() {
		return new ItemIterator();
	}
	
	private class ItemIterator implements Iterator<Item> {

		private int index = 0;
		private Iterator<Item> nested = null;
		
		@Override
		public boolean hasNext() {
			if (nested != null) {
				return nested.hasNext() || index < items.size();
			} else {
				return index < items.size();
			}
		}

		@Override
		public Item next() {
			if (nested != null && nested.hasNext()) {
				
				return nested.next();
				
			} else {
				
				nested = null;
				
				Item item = items.get( index++ );
				if (item instanceof Bag) {
					nested = ((Bag)item).iterator();
				}
				
				return item;
			}
		}

		@Override
		public void remove() {
			if (nested != null) {
				nested.remove();
			} else {
				items.remove( index );
			}
		}
	}
}
