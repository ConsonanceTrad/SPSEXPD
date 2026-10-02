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

package pd.items.equipment.weapon.missiles;

import pd.atlas.items.ConsumThrowsDict;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.SnipersMark;
import pd.actors.hero.HeroSubClass;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.levels.traps.TenguDartTrap;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import pd.messages.InlineText;

public class ForceCube extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ForceCube.class)
			.t("name", "震爆方石")
			.t("ondeath", "你用震爆方石害死了自己...")
			.t("stats_desc", "这件武器释放出一阵小范围的能量，冲击邻近的所有单位。")
			.t("desc", "这些奇形怪状的魔力方块小到可以握在你的手里，但出乎意料地非常重。");
	}



	
	{
		image = ConsumThrowsDict.FORCE_CUBE_0;
		
		tier = 5;
		baseUses = 5;
		
		sticky = false;
	}

	@Override
	public int max(int lvl) {
		return  6 * tier +                  //30 base, up from 25
				(tier) * lvl;               //scaling unchanged
	}

	@Override
	public void hitSound(float pitch) {
		//no hitsound as it never hits enemies directly
	}

	@Override
	public float castDelay(Char user, int cell) {
		//special rules as throwing this onto empty space or yourself does trigger it
		if (!Dungeon.level.pit[cell] && Actor.findChar(cell) == null){
			return delayFactor( user );
		} else {
			return super.castDelay(user, cell);
		}
	}

	@Override
	protected void onThrow(int cell) {
		if ((Dungeon.level.pit[cell] && Actor.findChar(cell) == null)){
			super.onThrow(cell);
			return;
		}

		//keep the parent reference for things like IDing
		MissileWeapon parentTemp = parent;
		rangedHit( null, cell );
		parent = parentTemp;
		Dungeon.level.pressCell(cell);
		
		ArrayList<Char> targets = new ArrayList<>();
		Char primaryTarget;
		if (Actor.findChar(cell) != null) {
			primaryTarget = Actor.findChar(cell);
			targets.add(primaryTarget);
		} else {
			primaryTarget = null;
		}

		for (int i : PathFinder.NEIGHBOURS8){
			if (!(Dungeon.level.traps.get(cell+i) instanceof TenguDartTrap)) Dungeon.level.pressCell(cell+i);
			if (Actor.findChar(cell + i) != null) targets.add(Actor.findChar(cell + i));
		}

		//furthest to closest, mainly for elastic
		Collections.sort(targets, new Comparator<Char>() {
			@Override
			public int compare(Char a, Char b) {
				return Float.compare(Dungeon.level.trueDistance(b.pos, curUser.pos), Dungeon.level.trueDistance(a.pos, curUser.pos));
			}
		});
		
		for (Char target : targets){
			curUser.shoot(target, this);
			if (target == Dungeon.hero && !target.isAlive()){
				Badges.validateDeathFromFriendlyMagic();
				Dungeon.fail(this);
				GLog.n(Messages.get(this, "ondeath"));
			}
		}

		//if we're applying sniper's mark, prioritize giving it to the primary target of the attack
		if (curUser.subClass == HeroSubClass.SNIPER && primaryTarget != null && primaryTarget.isActive()){
			Actor.add(new Actor() {

				{
					actPriority = VFX_PRIO-1;
				}

				@Override
				protected boolean act() {
					SnipersMark mark = Dungeon.hero.buff(SnipersMark.class);
					if (mark != null && primaryTarget.isActive()){
						mark.object = primaryTarget.id();
					}
					Actor.remove(this);
					return true;
				}
			});
		}
		
		WandOfBlastWave.BlastWave.blast(cell);
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
	}
}
