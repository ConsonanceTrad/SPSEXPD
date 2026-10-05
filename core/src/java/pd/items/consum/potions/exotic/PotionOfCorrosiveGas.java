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

package pd.items.consum.potions.exotic;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Roots;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

/**
 * SPSEXPD: 由剧毒气体合剂炼制的合剂，效果已改为范围的短暂束缚。
 * 饮用时以自身为中心、投掷时以落点为中心，束缚九宫格内的生物。
 */
public class PotionOfCorrosiveGas extends ExoticPotion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfCorrosiveGas.class)
			.t("name", "束缚合剂")
			.t("desc", "与剧毒气体合剂不同的是，砸碎这瓶合剂会在地面蔓生出魔法根须，短暂束缚范围内的所有生物。");
	}



	
	{
		icon = ItemIconSheet.POTION_CORROGAS;
	}
	
	@Override
	public void apply( Hero hero ) {
		identify();
		bindAround( hero.pos, hero );
		hero.sprite.emitter().start( Speck.factory(Speck.PARALYSIS), 0.4f, 4 );
	}

	@Override
	public void shatter( int cell ) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			identify();

			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Sample.INSTANCE.play( Assets.Sounds.PLANT );
		}

		bindAround( cell, null );
		CellEmitter.get( cell ).burst( Speck.factory(Speck.WOOL), 6 );
	}

	/** SPSEXPD: 以 center 为中心的九宫格内束缚生物，skip 用于避免束缚使用者本人。 */
	private static void bindAround( int center, Char skip ) {
		if (Dungeon.level == null) return;
		bind( center, skip );
		for (int offset : PathFinder.NEIGHBOURS8){
			int cell = center + offset;
			if (Dungeon.level.insideMap(cell)) bind( cell, skip );
		}
	}

	private static void bind( int cell, Char skip ) {
		Char ch = Actor.findChar( cell );
		if (ch != null && ch != skip) Buff.prolong( ch, Roots.class, Roots.DURATION );
	}
}
