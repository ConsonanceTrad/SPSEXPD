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

package pd.items.consum.potions;

import pd.Assets;
import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Levitation;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class PotionOfLevitation extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfLevitation.class)
			.t("name", "浮空药剂")
			.t("float", "你漂浮到了空中！")
			.t("desc", "饮用这种古怪的药剂能让你漂浮到空中，从而轻易地越过陷阱和深渊。扔出它会产生一片未被提纯的气体云雾，让其中的所有生物迷失方向。");
	}




	{
		icon = ItemIconSheet.POTION_LEVITATE;
	}

	@Override
	public void shatter( int cell ) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			identify();

			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Sample.INSTANCE.play( Assets.Sounds.GAS );
		}

		GameScene.add( Blob.seed( cell, 1000, ConfusionGas.class ) );
	}
	
	@Override
	public void apply( Hero hero ) {
		identify();
		Buff.prolong( hero, Levitation.class, Levitation.DURATION );
		GLog.i( Messages.get(this, "float") );
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
