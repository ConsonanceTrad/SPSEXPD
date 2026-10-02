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

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.particles.ShadowParticle;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class HolyDart extends TippedDart {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HolyDart.class)
			.t("name", "神圣飞镖")
			.t("desc", "这些飞镖上涂着一种由星陨花制成的药物，能向目标体内注入神圣能量。友方或常规敌人将因此进入赐福状态，而亡灵或恶魔类敌人则会受到大量伤害。这只飞镖仍能对敌人造成伤害，但不会伤及盟友。");
	}




	{
		image = ConsumThrowsDict.HOLY_DART_0;
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
	public int proc(Char attacker, Char defender, int damage) {

		//do nothing to the hero when processing charged shot
		if (processingChargedShot && defender == attacker){
			return super.proc(attacker, defender, damage);
		}

		if (attacker.alignment == defender.alignment){
			Buff.affect(defender, Bless.class, Math.round(Bless.DURATION));
			return 0; //also skips on-hit fx like enchants for allies
		}

		if (Char.hasProp(defender, Char.Property.UNDEAD) || Char.hasProp(defender, Char.Property.DEMONIC)){
			defender.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10+buffedLvl() );
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			defender.damage(Random.NormalIntRange(10 + Dungeon.scalingDepth()/3, 20 + Dungeon.scalingDepth()/3), this);
		//also do not bless enemies if processing charged shot
		} else if (!processingChargedShot){
			Buff.affect(defender, Bless.class, Math.round(Bless.DURATION));
		}
		
		return super.proc(attacker, defender, damage);
	}
}
