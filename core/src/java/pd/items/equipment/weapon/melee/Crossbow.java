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

package pd.items.equipment.weapon.melee;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.equipment.wands.WandOfBlastWave;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.utils.GLog;
import pd.messages.InlineText;

public class Crossbow extends MeleeWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Crossbow.class)
			.t("name", "十字弩")
			.t("typical_stats_desc", "通常，这件武器在被装备时会强化飞镖以造成_%1$d~%2$d_点伤害，甚至能将其附魔赋予飞镖。")
			.t("stats_desc", "这件武器在被装备时会强化飞镖以造成_%1$d~%2$d_点伤害，甚至能将其附魔赋予飞镖。")
			.t("ability_name", "蓄势射击")
			.t("typical_ability_desc", "决斗家可以为十字弩_蓄势_。这个武技能使下一次攻击必定命中，并将视情况触发以下三种效果之一：近战攻击将会击退敌人；未涂药飞镖一般会造成_%1$d点额外伤害_，而涂药飞镖将会在7x7的范围内触发效果，还一般会增加_%2$d次可用次数_。")
			.t("ability_desc", "决斗家可以为十字弩_蓄势_。这个武技能使下一次攻击必定命中，并将视情况触发以下三种效果之一：近战攻击将会击退敌人；未涂药飞镖将会造成_%1$d点额外伤害_，而涂药飞镖将会在7x7的范围内触发效果，还会增加_%2$d次可用次数_。")
			.t("desc", "这是一件看起来相当精密复杂的装置，能够将飞镖一样的小型箭矢以极高的速度射出。这把十字弩掂在手里沉甸甸的，比想象中结实很多，虽然完全违背了设计初衷，不过也能够在肉搏战中起到作用。")
			.t("upgrade_ability_stat_name", "武技加成")
			.t("$chargedshot.name", "蓄势待发")
			.t("$chargedshot.desc", "决斗家正将能量集中于她的十字弩。这个武技能使她的下一次攻击必定命中，并将视情况触发以下三种效果之一：\n-近战攻击将会击退敌人数格。\n-未涂药飞镖将会造成额外伤害。\n-涂药飞镖将会增加可用次数，在7x7的范围内触发效果。正面效果只对盟友生效，负面效果也只对敌人生效。决斗家不能使用此武技使正面飞镖效果对她自己生效。");
	}



	
	{
		image = EquipmentEquipWeaponBasicWeaponDict.CROSSBOW_0;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1f;
		
		//check Dart.class for additional properties
		
		tier = 4;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)){
			if (hero.buff(ChargedShot.class) != null &&
					!(hero.belongings.weapon() instanceof Crossbow)
					&& !(hero.belongings.secondWep() instanceof Crossbow)){
				//clear charged shot if no crossbow is equipped
				hero.buff(ChargedShot.class).detach();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public float accuracyFactor(Char owner, Char target) {
		if (owner.buff(Crossbow.ChargedShot.class) != null){
			Actor.add(new Actor() {
				{ actPriority = VFX_PRIO; }
				@Override
				protected boolean act() {
					if (owner instanceof Hero && !target.isAlive()){
						onAbilityKill((Hero)owner, target);
					}
					Actor.remove(this);
					return true;
				}
			});
			return Float.POSITIVE_INFINITY;
		} else {
			return super.accuracyFactor(owner, target);
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int dmg = super.proc(attacker, defender, damage);

		//stronger elastic effect
		if (attacker == Dungeon.hero
				&& Dungeon.hero.buff(ChargedShot.class) != null
				//not proccing from a dart
				&& Dungeon.hero.belongings.attackingWeapon() == this){
			//trace a ballistica to our target (which will also extend past them
			Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, Ballistica.STOP_TARGET);
			//trim it to just be the part that goes past them
			trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
			//knock them back along that ballistica
			WandOfBlastWave.throwChar(defender,
					trajectory,
					4,
					true,
					true,
					this);
			attacker.buff(Crossbow.ChargedShot.class).detach();
		}
		return dmg;
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +    //20 base, down from 25
				lvl*(tier);     //+4 per level, down from +5
	}

	public int dartMin(){
		return dartMin(buffedLvl());
	}

	public int dartMin(int lvl){
		return  4 +     //4 base, up from dart base of 1
				lvl;    //+1 per level
	}

	public int dartMax(){
		return dartMax(buffedLvl());
	}

	public int dartMax(int lvl){
		return  12 +    //12 base, up from dart base of 2
				3*lvl;  //+3 per crossbow level
	}

	public String statsInfo(){
		if (isIdentified()){
			return Messages.get(this, "stats_desc", dartMin(), dartMax());
		} else {
			return Messages.get(this, "typical_stats_desc", dartMin(0), dartMax(0));
		}
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (hero.buff(ChargedShot.class) != null){
			GLog.w(Messages.get(this, "ability_cant_use"));
			return;
		}

		beforeAbilityUsed(hero, null);
		Buff.affect(hero, ChargedShot.class);
		hero.sprite.operate(hero.pos);
		hero.next();
		afterAbilityUsed(hero);
	}

	@Override
	public String abilityInfo() {
		if (levelKnown){
			return Messages.get(this, "ability_desc", 3+buffedLvl(), 3+buffedLvl());
		} else {
			return Messages.get(this, "typical_ability_desc", 3, 3);
		}
	}

	@Override
	public String upgradeAbilityStat(int level) {
		return Integer.toString(3 + level);
	}

	public static class ChargedShot extends Buff{

		{
			announced = true;
			type = buffType.POSITIVE;
		}

		@Override
		public int icon() {
			return BuffIndicator.DUEL_XBOW;
		}

	}

}
