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
import pd.actors.blobs.TarGas;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Tar;
import pd.actors.buffs.Terror;
import pd.actors.mobs.npcs.Imp;
import pd.items.Item;
import pd.items.StoneOre;
import pd.items.equipment.weapon.guns.GunA;
import pd.items.equipment.weapon.guns.GunB;
import pd.items.equipment.weapon.guns.GunC;
import pd.items.equipment.weapon.guns.GunD;
import pd.items.equipment.weapon.guns.GunE;
import pd.scenes.GameScene;
import pd.sprites.GolemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Golem extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Golem.class)
			.t("name", "魔像")
			.t("def_verb", "格挡")
			.t("desc", "矮人们尝试将他们关于机械的知识与新发现的元素力量结合起来。土地之灵作为公认的最容易掌控的元素之灵，被用来当作机械的\"灵魂\"。尽管如此，仪式中最细微的失误都会造成严重的爆炸。");
	}



	
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
