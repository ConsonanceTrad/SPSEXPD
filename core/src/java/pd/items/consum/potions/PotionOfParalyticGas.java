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
import pd.actors.blobs.ParalyticGas;
import pd.scenes.GameScene;
import pd.sprites.ItemIconSheet;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class PotionOfParalyticGas extends Potion {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfParalyticGas.class)
			.t("name", "麻痹药剂")
			.t("desc", "暴露于空气时，烧瓶里的液体会气化成一片令人失去知觉的黄色云雾。任何吸入气体的生物都可能立即麻痹，直到气体消散后一段时间才能行动。可以把该物件扔到远处的敌群中，让气体覆盖影响它们。");
	}




	{
		icon = ItemIconSheet.POTION_PARAGAS;
	}

	@Override
	public void shatter( int cell ) {

		splash( cell );
		if (Dungeon.level.heroFOV[cell]) {
			identify();

			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Sample.INSTANCE.play( Assets.Sounds.GAS );
		}

		GameScene.add( Blob.seed( cell, 1000, ParalyticGas.class ) );
	}
	
	@Override
	public int value() {
		return isKnown() ? 40 * quantity : super.value();
	}
}
