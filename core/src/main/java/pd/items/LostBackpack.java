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

import pd.Assets;
import pd.actors.buffs.LostInventory;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.items.artifacts.CloakOfShadows;
import pd.items.artifacts.HolyTome;
import pd.items.bags.MagicalHolster;
import pd.items.wands.Wand;
import pd.items.weapon.melee.MagesStaff;
import pd.journal.Notes;
import pd.scenes.GameScene;
import pd.sprites.HeroSprite;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.audio.Sample;

public class LostBackpack extends Item {

	{
		image = ItemSpriteSheet.BACKPACK;

		unique = true;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (hero.buff(LostInventory.class) != null){
			hero.buff(LostInventory.class).detach();
		}

		MagicalHolster holster = hero.belongings.getItem(MagicalHolster.class);
		for (Item i : hero.belongings){
			if (i.keptThroughLostInventory()){
				i.keptThoughLostInvent = false; //don't reactivate, was previously activated
			} else {
				if (i instanceof EquipableItem && i.isEquipped(hero)){
					((EquipableItem) i).activate(hero);
				} else if ( i instanceof CloakOfShadows && hero.hasTalent(Talent.LIGHT_CLOAK)){
					((CloakOfShadows) i).activate(hero);
				} else if ( i instanceof HolyTome && hero.hasTalent(Talent.LIGHT_READING)){
					((HolyTome) i).activate(hero);
				} else if (i instanceof Wand){
					if (holster != null && holster.contains(i)){
						((Wand) i).charge(hero, MagicalHolster.HOLSTER_SCALE_FACTOR);
					} else {
						((Wand) i).charge(hero);
					}
				} else if (i instanceof MagesStaff){
					((MagesStaff) i).applyWandChargeBuff(hero);
				}
			}
		}

		hero.updateHT(false);

		Item.updateQuickslot();
		Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
		hero.spendAndNext(pickupDelay());
		GameScene.pickUp( this, pos );
		((HeroSprite)hero.sprite).updateArmor();

		Notes.remove(Notes.Landmark.LOST_PACK);
		return true;
	}
}
