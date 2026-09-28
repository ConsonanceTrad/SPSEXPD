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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Needling;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class CapeOfThorns extends Artifact {

	public static final String AC_NEEDLING = "NEEDLING";

	{
		image = ItemSpriteSheet.ARTIFACT_CAPE;

		levelCap = 10;

		charge = 0;
		chargeCap = 100;
		cooldown = 0;

		defaultAction = AC_NEEDLING;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && level() > 1 && !cursed) actions.add(AC_NEEDLING);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (!AC_NEEDLING.equals(action)) {
			super.execute(hero, action);
			return;
		}

		if (!isEquipped(hero)) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
		} else if (cursed) {
			GLog.i(Messages.get(Artifact.class, "cursed"));
		} else if (level() > 1) {
			int duration = level() * 10;
			level(level() - 1);
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
			if (hero.sprite != null) {
				hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
				hero.sprite.operate(hero.pos);
			}
			Buff.affect(hero, Needling.class, duration);
			hero.spend(1f);
			hero.busy();
			updateQuickslot();
		} else {
			GLog.i(Messages.get(Artifact.class, "cursed"));
		}
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Thorns();
	}
	
	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		if (isEquipped( Dungeon.hero )) {
			desc += "\n\n";
			if (cooldown == 0)
				desc += Messages.get(this, "desc_inactive");
			else
				desc += Messages.get(this, "desc_active");
		}

		return desc;
	}

	public class Thorns extends ArtifactBuff{

		@Override
		public boolean act(){
			if (cooldown > 0) {
				cooldown--;
				if (cooldown == 0) {
					BuffIndicator.refreshHero();
					GLog.w( Messages.get(this, "inert") );
				}
				updateQuickslot();
			}
			spend(TICK);
			return true;
		}

		public int proc(int damage, Char attacker, Char defender){
			if (cooldown == 0){
				charge += damage*(0.7+level()*0.1);
				if (charge >= chargeCap){
					charge = 0;
					cooldown = 10+level();
					GLog.p( Messages.get(this, "radiating") );
					Char shieldTarget = defender != null ? defender : target;
					if (shieldTarget != null) {
						Buff.affect(shieldTarget, ShieldArmor.class).level(level()*10);
					}
					BuffIndicator.refreshHero();
				}
			}

			if (cooldown != 0){
				int deflected = Random.NormalIntRange(0, damage);
				damage -= deflected;

				if (attacker != null) attacker.damage(deflected, this);

				exp+= deflected;

				if (exp >= (level()+1)*5 && level() < levelCap){
					exp -= (level()+1)*5;
					upgrade();
					Catalog.countUse(CapeOfThorns.class);
					GLog.p( Messages.get(this, "levelup") );
				}

			}
			updateQuickslot();
			return damage;
		}

		@Override
		public String toString() {
			return Messages.get(this, "name");
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(cooldown));
		}

		@Override
		public int icon() {
			if (cooldown == 0)
				return BuffIndicator.NONE;
			else
				return BuffIndicator.THORNS;
		}

		@Override
		public void detach(){
			cooldown = 0;
			charge = 0;
			super.detach();
		}

	}


}
