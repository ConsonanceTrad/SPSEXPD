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
import pd.Statistics;
import pd.actors.Char;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Cripple;
import pd.effects.FloatingText;
import pd.items.Generator;
import pd.sprites.CharSprite;
import pd.sprites.RotLasherSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class RotLasher extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RotLasher.class)
			.t("name", "腐莓触手")
			.t("rankings_desc", "被腐莓触手鞭杀")
			.t("desc", "腐莓触手是成熟腐莓根系的一部分，也是其主要自卫手段。触手没有视力，且固扎土中不能移动。但是当周边出现威胁时，只需先花费片刻感知到目标，随后便会发动猛攻。当附近没有敌人时，它们将静止不动，试图将自己融入周遭植被中。")
			.t("discover_hint", "你可在某个任务中遇到该敌人。");
	}




	{
		spriteClass = RotLasherSprite.class;

		HP = HT = 80;
		defenseSkill = 0;

		EXP = 1;

		loot = Generator.Category.SEED;
		lootChance = 0.75f;

		state = WANDERING = new Waiting();
		viewDistance = 1;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
	}

	@Override
	protected boolean act() {
		if (HP < HT && (enemy == null || !Dungeon.level.adjacent(pos, enemy.pos))) {
			sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(Math.min(5, HT - HP)), FloatingText.HEALING);
			HP = Math.min(HT, HP + 5);
		}
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		if (src instanceof Burning) {
			destroy();
			sprite.die();
		} else {
			super.damage(dmg, src);
		}
	}

	@Override
	public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
		if (enemy == Dungeon.hero){
			Statistics.questScores[1] -= 100;
		}
		return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc( enemy, damage );
		Buff.affect( enemy, Cripple.class, 2f );
		return super.attackProc(enemy, damage);
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(10, 20);
	}

	@Override
	public int attackSkill( Char target ) {
		return 25;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 8);
	}
	
	{
		immunities.add( ToxicGas.class );
	}

	private class Waiting extends Mob.Wandering{

		@Override
		protected boolean noticeEnemy() {
			spend(TICK);
			return super.noticeEnemy();
		}
	}
}
