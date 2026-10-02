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

package pd.items;

import pd.atlas.items.GroundFunctionalFallingDict;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.Barrier;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.effects.FloatingText;
import pd.items.equipment.trinkets.VialOfBlood;
import pd.journal.Catalog;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;

public class Dewdrop extends Item {
	
	{
		image = GroundFunctionalFallingDict.DEWDROP_0;
		
		stackable = true;
		dropsDownHeap = true;
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {
		
		Waterskin flask = hero.belongings.getItem( Waterskin.class );
		Catalog.setSeen(getClass());
		Statistics.itemTypesDiscovered.add(getClass());
		
		if (flask != null){

			flask.collectDew( this );
			GameScene.pickUp( this, pos );

		} else {

			int terr = Dungeon.level.map[pos];
			if (!consumeDew(1, hero, terr == Terrain.ENTRANCE || terr == Terrain.ENTRANCE_SP
					|| terr == Terrain.EXIT || terr == Terrain.UNLOCKED_EXIT)){
				return false;
			} else {
				Catalog.countUse(getClass());
			}
			
		}
		
		Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
		hero.spendAndNext( pickupDelay() );
		
		return true;
	}

	public static boolean consumeDew(int quantity, Hero hero, boolean force){
		return consumeDew(quantity, hero, force, 0.025f);
	}

	public static boolean consumeDew(int quantity, Hero hero, boolean force, float dropHealPercent){
		//SPS uses 40 ordinary drops for a full heal.
		int effect = Math.round( hero.HT * dropHealPercent * quantity );

		int heal = Math.min( hero.HT - hero.HP, effect );

		int shield = 0;
		if (hero.hasTalent(Talent.SHIELDING_DEW)){

			//When vial is present, this allocates exactly as much of the effect as is needed
			// to get to 100% HP, and the rest is then given as shielding (without the vial boost)
			if (quantity > 1 && heal < effect && VialOfBlood.delayBurstHealing()){
				heal = Math.round(heal/VialOfBlood.totalHealMultiplier());
			}

			shield = effect - heal;

			int maxShield = Math.round(hero.HT *0.2f*hero.pointsInTalent(Talent.SHIELDING_DEW));
			int curShield = 0;
			if (hero.buff(Barrier.class) != null) curShield = hero.buff(Barrier.class).shielding();
			shield = Math.min(shield, maxShield-curShield);
		}

		if (heal > 0 || shield > 0) {

			if (heal > 0 && quantity > 1 && VialOfBlood.delayBurstHealing()){
				Healing healing = Buff.affect(hero, Healing.class);
				healing.setHeal(heal, 0, VialOfBlood.maxHealPerTurn(), true);
			} else {
				hero.HP += heal;
				if (heal > 0){
					if (hero.sprite != null) {
						hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
					}
				}
			}

			if (shield > 0) {
				Buff.affect(hero, Barrier.class).incShield(shield);
				if (hero.sprite != null) {
					hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(shield), FloatingText.SHIELDING );
				}
			}

		} else if (!force) {
			GLog.i( Messages.get(Dewdrop.class, "already_full") );
			return false;
		}

		return true;
	}

	public int dewValue() {
		return quantity;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

}
