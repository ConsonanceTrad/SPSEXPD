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

package pd.actors.buffs;

import pd.Dungeon;
import pd.items.Item;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.items.equipment.weapon.missiles.darts.Dart;
import pd.items.equipment.weapon.missiles.darts.TippedDart;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import pd.messages.InlineText;

public class PinCushion extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(PinCushion.class)
			.t("name", "中矢")
			.t("desc", "你击中这个角色的投掷武器正卡在他们身上，打败他们后投掷武器将会掉在地上。\n\n被卡住的物品如下：");
	}




	private ArrayList<MissileWeapon> items = new ArrayList<>();

	public void stick(MissileWeapon projectile){
		for (int i = 0; i < items.size(); i++) {
			if (projectile.isSimilar(items.get(i))) {
				projectile.merge(items.get(i));
				items.set(i, projectile);
				if (TippedDart.lostDarts > 0) {
					Dart d = new Dart();
					d.quantity(TippedDart.lostDarts);
					TippedDart.lostDarts = 0;
					stick(d);
				}
				return;
			}
		}
		items.add(projectile);
	}

	public Item grabOne(){
		Item item = items.remove(0);
		if (items.isEmpty()){
			detach();
		}
		return item;
	}

	public ArrayList<MissileWeapon> getStuckItems(){
		return new ArrayList<>(items);
	}

	@Override
	public void detach() {
		for (Item item : items)
			Dungeon.level.drop( item, target.pos).sprite.drop();
		super.detach();
	}

	private static final String ITEMS = "items";

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put( ITEMS , items );
		super.storeInBundle(bundle);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		items = new ArrayList<>((Collection<MissileWeapon>) ((Collection<?>) bundle.getCollection(ITEMS)));
		super.restoreFromBundle( bundle );
	}

	@Override
	public int icon() {
		return BuffIndicator.PINCUSHION;
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		for (Item i : items){
			desc += "\n" + i.title();
		}
		return desc;
	}

}
