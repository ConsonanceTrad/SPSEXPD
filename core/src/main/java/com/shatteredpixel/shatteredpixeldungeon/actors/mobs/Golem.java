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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.TarGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.StoneOre;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunA;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunB;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunC;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunD;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunE;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.watabou.utils.Random;

public class Golem extends Mob {
	
	{
		spriteClass = GolemSprite.class;
		
		HP = HT = 180 + legacyDepthAdjustment(0) * Random.NormalIntRange(4, 7);
		defenseSkill = 18 + legacyDepthAdjustment(1);
		
		EXP = 15;
		maxLvl = 30;

		loot = StoneOre.class;
		lootChance = 0.5f;

		properties.add(Property.MECH);

		immunities.add(Amok.class);
		immunities.add(Terror.class);
		immunities.add(Sleep.class);
		immunities.add(TarGas.class);
		immunities.add(Tar.class);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(20 + legacyDepthAdjustment(0), 60 + legacyDepthAdjustment(1));
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 28 + legacyDepthAdjustment(1);
	}

	@Override
	public float attackDelay() {
		return 1.5f;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(10, 15);
	}

	@Override
	public void rollToDropLoot() {
		Imp.Quest.oldProcess( this );
		super.rollToDropLoot();
	}

	@Override
	public void damage(int dmg, Object src) {
		if (legacyReleasesTar(dmg)) GameScene.add(Blob.seed(pos, 30, TarGas.class));
		super.damage(dmg, src);
	}

	static boolean legacyReleasesTar(int damage, int maxHealth) {
		return damage > maxHealth / 8;
	}

	private boolean legacyReleasesTar(int damage) { return legacyReleasesTar(damage, HT); }

	/** Compatibility callback for the retained Shattered sprite; SPS golems never initiate a zap. */
	public void onZapComplete() { next(); }

	@Override
	public Item SupercreateLoot() {
		return Random.oneOf(new GunA(), new GunB(), new GunC(), new GunD(), new GunE());
	}
}
