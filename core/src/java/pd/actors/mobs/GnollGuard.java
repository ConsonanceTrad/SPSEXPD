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

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.equipment.weapon.melee.Spear;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.GnollGuardSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class GnollGuard extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GnollGuard.class)
			.t("name", "豺狼守卫")
			.t("def_verb", "格挡")
			.t("spear_warn", "豺狼人用矛尖打出残暴的一击！")
			.t("desc", "这只高大健壮的豺狼人操使着长矛和盾牌，但没有戴头盔。这些守卫似乎在为晋升成暴徒而训练，并为保护矿洞不受野兽侵扰在此执勤。\n\n豺狼守卫身强力壮，单手就能挥舞长矛，不过使得不算太好。_它需要相当开阔的施展空间才能进行长距离攻击。一旦被人欺身而上，长矛施展不开便会威力大减。_")
			.t("desc_armor", "_一旁的豺狼工兵正握持着一个看起来能赋予这个守卫大地护甲的装置，大幅减少它受到的伤害。_")
			.t("discover_hint", "你可在某个任务中遇到该敌人。");
	}




	{
		spriteClass = GnollGuardSprite.class;

		HP = HT = 35;
		defenseSkill = 15;

		EXP = 7;
		maxLvl = -2;

		loot = Spear.class;
		lootChance = 0.1f;

		WANDERING = new Wandering();
	}

	private int sapperID = -1;

	public void linkSapper( GnollSapper sapper){
		this.sapperID = sapper.id();
		if (sprite instanceof GnollGuardSprite){
			((GnollGuardSprite) sprite).setupArmor();
		}
	}

	public boolean hasSapper(){
		return sapperID != -1
				&& Actor.findById(sapperID) instanceof GnollSapper
				&& ((GnollSapper)Actor.findById(sapperID)).isAlive();
	}

	public void loseSapper(){
		if (sapperID != -1){
			sapperID = -1;
			if (sprite instanceof GnollGuardSprite){
				((GnollGuardSprite) sprite).loseArmor();
			}
		}
	}

	@Override
	public void damage(int dmg, Object src) {
		if (hasSapper()) dmg /= 4;
		super.damage(dmg, src);
	}

	@Override
	public int damageRoll() {
		if (enemy != null && !Dungeon.level.adjacent(pos, enemy.pos)){
			return Random.NormalIntRange( 16, 22 );
		} else {
			return Random.NormalIntRange( 6, 12 );
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		int dmg = super.attackProc(enemy, damage);
		if (enemy == Dungeon.hero && !Dungeon.level.adjacent(pos, enemy.pos) && dmg > 12){
			GLog.n(Messages.get(this, "spear_warn"));
		}
		return dmg;
	}

	@Override
	public int attackSkill( Char target ) {
		return 20;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 6);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		//cannot 'curve' spear hits like the hero, requires fairly open space to hit at a distance
		return Dungeon.level.distance(enemy.pos, pos) <= 2
				&& new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos
				&& new Ballistica( enemy.pos, pos, Ballistica.PROJECTILE).collisionPos == pos;
	}

	@Override
	public String description() {
		if (hasSapper()){
			return super.description() + "\n\n" + Messages.get(this, "desc_armor");
		} else {
			return super.description();
		}
	}

	private static final String SAPPER_ID = "sapper_id";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SAPPER_ID, sapperID);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		sapperID = bundle.getInt(SAPPER_ID);
	}

	public class Wandering extends Mob.Wandering {
		@Override
		protected int randomDestination() {
			if (hasSapper()){
				return ((GnollSapper)Actor.findById(sapperID)).pos;
			} else {
				return super.randomDestination();
			}
		}
	}

}
