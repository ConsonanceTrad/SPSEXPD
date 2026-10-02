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

package pd.items.equipment.weapon.missiles.darts;

import pd.atlas.items.ConsumThrowsDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ChampionEnemy;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.items.consum.potions.exotic.PotionOfCleansing;
import pd.items.equipment.weapon.melee.Crossbow;
import pd.messages.InlineText;

public class CleansingDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CleansingDart.class)
			.t("name", "净化飞镖")
			.t("desc", "这些飞镖上涂着一种由魔皇草制成的药物，能使友军暂时对所有负面效果免疫，也可以清除敌人身上的增益效果。敌人甚至会暂时忘记它正在攻击或逃离你。这只飞镖仍能对敌人造成伤害，但不会伤及盟友。");
	}

	
	{
		image = ConsumThrowsDict.CLEANSING_DART_0;
	}

	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			if (((Hero) owner).attackTarget().alignment == owner.alignment){
				return 0; //does not deal damage to allies
			}
		}
		return super.damageRoll(owner);
	}

	@Override
	public int proc(Char attacker, final Char defender, int damage) {

		if (processingChargedShot && defender == attacker) {
			//do nothing to the hero when processing charged shot
		} else if (attacker.alignment == defender.alignment){
			PotionOfCleansing.cleanse(defender, PotionOfCleansing.Cleanse.DURATION*2f);
			return 0; //also skips on-hit fx like enchants for allies
		} else {
			for (Buff b : defender.buffs()){
				if (!(b instanceof ChampionEnemy)
						&& b.type == Buff.buffType.POSITIVE
						&& !(b instanceof Crossbow.ChargedShot)){
					b.detach();
				}
			}
			//for when cleansed effects were keeping defender alive (e.g. raging brutes)
			if (!defender.isAlive()){
				defender.die(attacker);
				return super.proc(attacker, defender, damage);
			}
			if (defender instanceof Mob) {
				//need to delay this so damage from the dart doesn't break wandering
				new FlavourBuff(){
					{actPriority = VFX_PRIO;}
					public boolean act() {
						if (((Mob) defender).state == ((Mob) defender).HUNTING || ((Mob) defender).state == ((Mob) defender).FLEEING){
							((Mob) defender).state = ((Mob) defender).WANDERING;
						}
						((Mob) defender).beckon(Dungeon.level.randomDestination(defender));
						defender.sprite.showLost();
						return super.act();
					}
				}.attachTo(defender);
			}
		}

		return super.proc(attacker, defender, damage);
	}
}
