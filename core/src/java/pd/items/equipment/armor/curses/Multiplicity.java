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

package pd.items.equipment.armor.curses;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.PinCushion;
import pd.actors.hero.Hero;
import pd.actors.hero.abilities.Ratmogrify;
import pd.actors.mobs.DwarfKing;
import pd.actors.mobs.Ghoul;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Statue;
import pd.actors.mobs.Thief;
import pd.actors.mobs.npcs.MirrorImage;
import pd.actors.mobs.npcs.NPC;
import pd.items.equipment.armor.Armor;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Multiplicity extends Armor.Glyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Multiplicity.class)
			.t("name", "分身%s")
			.t("desc", "带有分身诅咒的防具含有一种危险的复制魔法。有时候它会复制出穿戴者的镜像，但也有同等几率复制攻击者！");
	}


	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		float procChance = 1/20f * procChanceMultiplier(defender);
		if ( Random.Float() < procChance ) {
			ArrayList<Integer> spawnPoints = new ArrayList<>();

			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = defender.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
					spawnPoints.add( p );
				}
			}

			if (spawnPoints.size() > 0) {

				Mob m = null;
				if (Random.Int(2) == 0 && defender instanceof Hero){
					m = new MirrorImage();
					((MirrorImage)m).duplicate( (Hero)defender );

				} else {
					Char toDuplicate = attacker;

					if (toDuplicate instanceof Ratmogrify.TransmogRat){
						toDuplicate = ((Ratmogrify.TransmogRat)attacker).getOriginal();
					}

					//FIXME should probably have a mob property for this
					if (!(toDuplicate instanceof Mob)
							|| toDuplicate.properties().contains(Char.Property.BOSS) || toDuplicate.properties().contains(Char.Property.MINIBOSS)
							|| toDuplicate instanceof Mimic || toDuplicate instanceof Statue || toDuplicate instanceof NPC) {
						m = Dungeon.level.createMob();
					} else {
						m = duplicate((Mob)toDuplicate);
					}
				}

				if (m != null) {

					if (Char.hasProp(m, Char.Property.LARGE)){
						for ( int i : spawnPoints.toArray(new Integer[0])){
							if (!Dungeon.level.openSpace[i]){
								//remove the value, not at the index
								spawnPoints.remove((Integer) i);
							}
						}
					}

					if (!spawnPoints.isEmpty()) {
						m.pos = Random.element(spawnPoints);
						GameScene.add(m);
						ScrollOfTeleportation.appear(m, m.pos);
					}
				}

			}
		}

		return damage;
	}

	public static Mob duplicate( Mob toDuplicate ){

		if (toDuplicate instanceof Ratmogrify.TransmogRat){
			toDuplicate = ((Ratmogrify.TransmogRat)toDuplicate).getOriginal();
		}

		Actor.fixTime();

		Mob m = Reflection.newInstance(toDuplicate.getClass());

		if (m != null) {

			Bundle store = new Bundle();
			toDuplicate.storeInBundle(store);
			m.restoreFromBundle(store);
			m.pos = 0;
			m.HP = m.HT;

			//don't duplicate stuck projectiles
			m.remove(PinCushion.class);
			//don't duplicate pending damage to dwarf king
			m.remove(DwarfKing.KingDamager.class);
			//don't duplicate downed ghouls
			m.remove(Ghoul.GhoulLifeLink.class);

			//If a thief has stolen an item, that item is not duplicated.
			if (m instanceof Thief) {
				((Thief) m).item = null;
			}
		}

		return m;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
