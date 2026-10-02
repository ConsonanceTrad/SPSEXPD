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

package pd.items;

import pd.atlas.items.ConsumUsefulUsefulDict;

import pd.Assets;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.messages.Messages;
import pd.sprites.ItemSprite.Glowing;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Ankh extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Ankh.class)
			.t("name", "重生十字架")
			.t("ac_bless", "祝福")
			.t("bless", "你用散发着治愈魔力的清水祝福了这枚重生十字架。")
			.t("desc", "这枚象征不朽的古老饰物能使人起死回生。不过大部分物品将会被遗落在死亡的地点，等待你去拾回。装满的水袋可用于对重生十字架进行赐福，赋予它更强的力量。")
			.t("desc_blessed", "这枚象征不朽的古老饰品能够让人起死回生。这枚十字架已被祝福而变得更加强大。它会在危急关头牺牲自己来救你一命。")
			.t("discover_hint", "你可在商店中购买该物品。");
	}


	public static final String AC_BLESS = "BLESS";

	{
		image = ConsumUsefulUsefulDict.ANKH;

		//You tell the ankh no, don't revive me, and then it comes back to revive you again in another run.
		//I'm not sure if that's enthusiasm or passive-aggression.
		bones = true;
	}

	private boolean blessed = false;
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions(hero);
		Waterskin waterskin = hero.belongings.getItem(Waterskin.class);
		if (waterskin != null && waterskin.isFull() && !blessed)
			actions.add( AC_BLESS );
		return actions;
	}

	@Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_BLESS )) {

			Waterskin waterskin = hero.belongings.getItem(Waterskin.class);
			if (waterskin != null){
				blessed = true;
				waterskin.empty();
				GLog.p( Messages.get(this, "bless") );
				hero.spend( 1f );
				hero.busy();


				Sample.INSTANCE.play( Assets.Sounds.DRINK );
				CellEmitter.get(hero.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
				hero.sprite.operate( hero.pos );
			}
		}
	}
	
	@Override
	public String desc() {
		if (blessed)
			return Messages.get(this, "desc_blessed");
		else
			return super.desc();
	}

	public boolean isBlessed(){
		return blessed;
	}

	public void bless(){
		blessed = true;
	}

	private static final Glowing WHITE = new Glowing( 0xFFFFCC );

	@Override
	public Glowing glowing() {
		return isBlessed() ? WHITE : null;
	}

	private static final String BLESSED = "blessed";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( BLESSED, blessed );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		blessed	= bundle.getBoolean( BLESSED );
	}
	
	@Override
	public int value() {
		return 50 * quantity;
	}
}
