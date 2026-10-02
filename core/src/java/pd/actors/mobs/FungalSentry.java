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
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.mechanics.Ballistica;
import pd.sprites.FungalSentrySprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FungalSentry extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FungalSentry.class)
			.t("name", "蘑菇哨卫")
			.t("desc", "屹立于此的高大蘑菇是整个洞穴中更大的菌丝网络的防御节孢。\n\n它无法移动，但会精准地向任何进入视线的物体喷射毒液。虽然毒液喷射的力道相当弱，_但却可以快速地叠加毒量_。虽然可以被杀死，但最好还是远离它。");
	}




	{
		spriteClass = FungalSentrySprite.class;

		HP = HT = 200;
		defenseSkill = 12;

		EXP = 10;
		maxLvl = -2;

		state = WANDERING = new Waiting();

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public float spawningWeight() {
		return 0;
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
		return Random.NormalIntRange(5, 10);
	}

	@Override
	//TODO attack is a little permissive atm?
	protected boolean canAttack( Char enemy ) {
		return super.canAttack(enemy)
				|| new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	//TODO if we want to allow them to be literally killed, probably should give them a heal if hero is out of FOV, or similar

	@Override
	public int attackProc(Char enemy, int damage) {
		Buff.affect(enemy, Poison.class).extend(6);
		return super.attackProc(enemy, damage);
	}

	@Override
	public int attackSkill( Char target ) {
		return 50;
	}

	{
		immunities.add( ToxicGas.class );
		immunities.add( Poison.class );
	}

	private class Waiting extends Mob.Wandering{

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			//always notices the hero
			if (enemyInFOV) {

				return noticeEnemy();

			} else {

				return continueWandering();

			}
		}

		@Override
		protected boolean noticeEnemy() {
			spend(TICK);
			return super.noticeEnemy();
		}
	}

}
