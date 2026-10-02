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
import pd.actors.blobs.Blob;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Burning;
import pd.journal.Bestiary;
import pd.mechanics.pathfind.PathFinder;
import pd.plants.Rotberry;
import pd.scenes.GameScene;
import pd.sprites.RotHeartSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class RotHeart extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(RotHeart.class)
			.t("name", "腐莓核心")
			.t("desc", "腐莓的果实与众不同。一般的果实会腐败并化作养分，但腐莓果实会生长、硬化，并包裹住种子。果实为长在其内的器官提供保护。这种巨大球体被视为成熟腐莓植株的核心。")
			.t("discover_hint", "你可在某个任务中遇到该敌人。");
	}


	{
		spriteClass = RotHeartSprite.class;

		HP = HT = 80;
		defenseSkill = 0;

		EXP = 4;

		state = PASSIVE;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
		properties.add(Property.STATIC);
	}

	@Override
	protected boolean act() {
		alerted = false;
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		//TODO: when effect properties are done, change this to FIRE
		if (src instanceof Burning) {
			destroy();
			sprite.die();
		} else {
			super.damage(dmg, src);
		}
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		//rot heart spreads less gas in enclosed spaces
		int openNearby = 0;
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[pos+i]){
				openNearby++;
			}
		}

		GameScene.add(Blob.seed(pos, 5 + 3*openNearby, ToxicGas.class));

		return super.defenseProc(enemy, damage);
	}

	@Override
	public void beckon(int cell) {
		//do nothing
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	public void destroy() {
		super.destroy();
		Bestiary.skipCountingEncounters = true;
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[Dungeon.level.mobs().size()])){
			if (mob instanceof RotLasher){
				mob.die(null);
			}
		}
		Bestiary.skipCountingEncounters = false;
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Dungeon.level.drop( new Rotberry.Seed(), pos ).sprite.drop();
		//assign score here as player may choose to keep the rotberry seed
		Statistics.questScores[1] += 2000;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public int damageRoll() {
		return 0;
	}

	@Override
	public int attackSkill( Char target ) {
		return 0;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 5);
	}
	
	{
		immunities.add( ToxicGas.class );
	}

}
