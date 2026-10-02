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

package pd.actors.hero.spells;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.weapon.Weapon;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.ui.AttackIndicator;
import pd.ui.HeroIcon;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;

public class Smite extends TargetedClericSpell {

	public static Smite INSTANCE = new Smite();

	@Override
	public int icon() {
		return HeroIcon.SMITE;
	}

	@Override
	public int targetingFlags() {
		return Ballistica.STOP_TARGET; //no auto-aim
	}

	@Override
	public String desc() {
		int min = 5 + Dungeon.hero.lvl/2;
		int max = 10 + Dungeon.hero.lvl;
		return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2f;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.subClass == HeroSubClass.PALADIN;
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero){
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		//we apply here because of projecting
		SmiteTracker tracker = Buff.affect(hero, SmiteTracker.class);
		if (hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target] || !hero.canAttack(enemy)) {
			GLog.w(Messages.get(this, "invalid_enemy"));
			tracker.detach();
			return;
		}

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				AttackIndicator.target(enemy);

				float accMult = 1;
				if (!(hero.belongings.attackingWeapon() instanceof Weapon)
						|| ((Weapon) hero.belongings.attackingWeapon()).STRReq() <= hero.STR()){
					accMult = Char.INFINITE_ACCURACY;
				}
				if (hero.attack(enemy, 1, 0, accMult)){
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
					enemy.sprite.burst(0xFFFFFFFF, 10);
				}
				tracker.detach();

				Invisibility.dispel();

				hero.spendAndNext(hero.attackDelay());
				onSpellCast(tome, hero);
			}
		});

	}

	public static int bonusDmg( Hero attacker, Char defender){
		int min = 5 + attacker.lvl/2;
		int max = 10 + attacker.lvl;
		if (Char.hasProp(defender, Char.Property.UNDEAD) || Char.hasProp(defender, Char.Property.DEMONIC)){
			return max;
		} else {
			return Hero.heroDamageIntRange(min, max);
		}
	}

	public static class SmiteTracker extends FlavourBuff {};

}
