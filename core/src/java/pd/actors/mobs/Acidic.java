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

package pd.actors.mobs;

import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.StenchGas;
import pd.effects.Wound;
import pd.items.Item;
import pd.items.consum.potions.PotionOfToxicGas;
import pd.items.equipment.wands.WandOfAcid;
import pd.scenes.GameScene;
import pd.sprites.AcidicSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Acidic extends Scorpio {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Acidic.class)
			.t("name", "酸液蝎子")
			.t("desc", "酸液蝎子较普通蝎子更强，也更加危险。它体内的酸液会在遇到危险时释放，从而保护自己免受天敌的袭击。");
	}


	{
		spriteClass = AcidicSprite.class;
		
		properties.add(Property.BEAST);
		properties.add(Property.DEMONIC);
	}

	@Override
	public boolean act() {
		GameScene.add(Blob.seed(pos, 30, StenchGas.class));
		return super.act();
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {
		int reflected = Random.IntRange(0, damage / 2) - enemy.drRoll();
		if (reflected > 0) {
			enemy.damage(reflected, this);
			if (enemy.sprite != null) Wound.hit(enemy);
		}
		return super.defenseProc( enemy, damage );
	}

	{
		immunities.add(StenchGas.class);
	}

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new PotionOfToxicGas(), new WandOfAcid());
	}

	public static Class<?>[] specialLootTypes() {
		return new Class<?>[]{PotionOfToxicGas.class, WandOfAcid.class};
	}
}
