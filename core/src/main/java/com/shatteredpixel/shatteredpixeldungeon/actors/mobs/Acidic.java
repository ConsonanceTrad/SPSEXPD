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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfAcid;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AcidicSprite;
import com.watabou.utils.Random;

public class Acidic extends Scorpio {

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
