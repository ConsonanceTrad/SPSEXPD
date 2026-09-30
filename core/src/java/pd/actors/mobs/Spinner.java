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
import pd.actors.blobs.Blob;
import pd.actors.blobs.Web;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Terror;
import pd.effects.particles.ShadowParticle;
import pd.items.Item;
import pd.items.food.MysteryMeat;
import pd.items.weapon.melee.normalweapon.Whip;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.SpinnerSprite;
import render.noosa.tweeners.AlphaTweener;
import render.utils.math.Random;

public class Spinner extends Mob {

	{
		spriteClass = SpinnerSprite.class;

		HP = HT = 120 + legacyDepthAdjustment(0) * Random.NormalIntRange(5, 7);
		defenseSkill = 14 + legacyDepthAdjustment(1);

		EXP = 9;
		maxLvl = 25;

		loot = MysteryMeat.class;
		lootChance = 0.15f;
		properties.add(Property.BEAST);

		FLEEING = new Fleeing();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(12, 26 + legacyDepthAdjustment(0));
	}

	@Override
	public int attackSkill(Char target) {
		return 20 + legacyDepthAdjustment(0);
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(6, 10);
	}

	@Override
	protected boolean act() {
		boolean result = super.act();
		if (state == FLEEING && buff(Terror.class) == null && enemy != null
				&& enemySeen && enemy.buff(Poison.class) == null) {
			state = HUNTING;
		}
		return result;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) {
			Buff.affect(enemy, Poison.class).set(Random.IntRange(7, 8));
			state = FLEEING;
		}
		return damage;
	}

	@Override
	public void move(int step, boolean travelling) {
		if (travelling && state == FLEEING) leaveLegacyWeb(pos);
		super.move(step, travelling);
	}

	void leaveLegacyWeb(int cell) { GameScene.add(Blob.seed(cell, Random.IntRange(5, 6), Web.class)); }

	/** Compatibility hook for Shattered-only spinner variants, inactive in the SPS AI. */
	protected void applyWebToCell(int cell) { GameScene.add(Blob.seed(cell, 20, Web.class)); }

	/** Compatibility callback for sprites from hidden Shattered-only spinner variants. */
	public void shootWeb() { next(); }

	@Override public Item SupercreateLoot() { return new Whip(); }

	public static void spawnAround(int pos) {
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell] && Actor.findChar(cell) == null) spawnAt(cell);
		}
	}

	public static Spinner spawnAt(int pos) {
		if (!Dungeon.level.insideMap(pos) || !Dungeon.level.passable[pos] || Actor.findChar(pos) != null) return null;
		Spinner spinner = new Spinner();
		spinner.pos = pos;
		spinner.state = spinner.HUNTING;
		GameScene.add(spinner, 1f);
		if (spinner.sprite != null) {
			spinner.sprite.alpha(0);
			if (spinner.sprite.parent != null) spinner.sprite.parent.add(new AlphaTweener(spinner.sprite, 1, 0.5f));
			spinner.sprite.emitter().burst(ShadowParticle.CURSE, 5);
		}
		return spinner;
	}

	{
		resistances.add(Poison.class);
		immunities.add(Roots.class);
	}

	private class Fleeing extends Mob.Fleeing {

		@Override
		protected void nowhereToRun() {
			if (buff(Terror.class) == null) state = HUNTING;
			else super.nowhereToRun();
		}
	}
}
