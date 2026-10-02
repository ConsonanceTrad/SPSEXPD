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

package pd.items.consum.spells;


import pd.actors.buffs.MagicImmune;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;
import pd.messages.InlineText;

public abstract class Spell extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Spell.class)
			.t("ac_cast", "施放")
			.t("no_magic", "你无法在魔法免疫的情况下使用法术结晶。")
			.t("discover_hint", "你可通过炼金合成该物品。");
	}



	
	public static final String AC_CAST = "CAST";

	//affects how strongly on-scroll talents trigger from this scroll
	protected float talentFactor = 1;
	//the chance (0-1) of whether on-scroll talents trigger from this potion
	protected float talentChance = 1;
	
	{
		stackable = true;
		defaultAction = AC_CAST;
	}
	
	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_CAST );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {
		
		super.execute( hero, action );
		
		if (action.equals( AC_CAST )) {
			
			if (curUser.buff(MagicImmune.class) != null){
				GLog.w( Messages.get(this, "no_magic") );
				return;
			}
			
			onCast( hero );
			
		}
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	protected abstract void onCast(Hero hero );
	
}
