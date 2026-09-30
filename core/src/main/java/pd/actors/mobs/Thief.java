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
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Gold;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.food.vegetable.NutVegetable;
import pd.messages.Messages;
import pd.sprites.ThiefSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

public class Thief extends Mob {
	
	public Item item;
	
	{
		spriteClass = ThiefSprite.class;
		
		HP = HT = 80 + legacyDepthAdjustment(0) * Random.NormalIntRange(3, 5);
		defenseSkill = 8 + legacyDepthAdjustment(0);
		
		EXP = 5;
		maxLvl = 20;

		loot = NutVegetable.class;
		lootChance = 0.1f;

		WANDERING = new Wandering();
		FLEEING = new Fleeing();

		properties.add(Property.GOBLIN);
	}

	private static final String ITEM = "item";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEM, item );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		item = (Item)bundle.get( ITEM );
	}

	@Override
	public float speed() {
		if (item != null) return (5*super.speed())/6;
		else return super.speed();
	}

	@Override
	protected boolean act() {
		boolean result = super.act();
		if (state == FLEEING && buff(Terror.class) == null && enemy != null
				&& enemySeen && enemy.buff(Locked.class) == null) state = HUNTING;
		return result;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 7 + legacyDepthAdjustment(0));
	}

	@Override
	public float attackDelay() {
		return super.attackDelay()*0.5f;
	}

	@Override
	public void rollToDropLoot() {
		if (item != null) {
			Dungeon.level.drop( item, pos ).sprite.drop();
			//updates position
			if (item instanceof Honeypot.ShatteredPot) ((Honeypot.ShatteredPot)item).dropPot( this, pos );
			item = null;
		}
		super.rollToDropLoot();
		if (Dungeon.hero != null && Dungeon.level != null && legacyLootLevelEligible()
				&& Random.Float() < legacySecondaryLootChance(0.05f)) {
			Dungeon.level.drop(Generator.random(Generator.Category.BERRY), pos).sprite.drop();
		}
	}

	@Override
	public Item createLoot() {
		return super.createLoot();
	}

	@Override
	public Item SupercreateLoot() {
		return Generator.random(Generator.Category.RANGEWEAPON);
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(0, 3);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int(5) == 0) {
			Buff.affect(enemy, Locked.class, 20f);
			state = FLEEING;
		}
		return damage;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (state == FLEEING) {
			Dungeon.level.drop( new Gold(), pos ).sprite.drop();
		}

		return super.defenseProc(enemy, damage);
	}

	protected boolean steal( Hero hero ) {

		Item toSteal = hero.belongings.randomUnequipped();

		if (toSteal != null && !toSteal.unique && toSteal.level() < 1 ) {

			GLog.w( Messages.get(Thief.class, "stole", toSteal.name()) );
			if (!toSteal.stackable) {
				Dungeon.quickslot.convertToPlaceholder(toSteal);
			}
			Item.updateQuickslot();

			item = toSteal.detach( hero.belongings.backpack );
			if (item instanceof Honeypot){
				item = ((Honeypot)item).shatter(this, this.pos);
			} else if (item instanceof Honeypot.ShatteredPot) {
				((Honeypot.ShatteredPot)item).pickupPot(this);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public String description() {
		String desc = super.description();

		if (item != null) {
			desc += Messages.get(this, "carries", item.name() );
		}

		return desc;
	}
	
	private class Wandering extends Mob.Wandering {
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			super.act(enemyInFOV, justAlerted);
			
			//if an enemy is just noticed and the thief posses an item, run, don't fight.
			if (state == HUNTING && item != null){
				state = FLEEING;
			}
			
			return true;
		}
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			if (buff(Terror.class) == null) {
				if (sprite != null) sprite.showStatus(pd.sprites.CharSprite.NEGATIVE,
						Messages.get(Mob.class, "rage"));
				state = HUNTING;
			} else {
				super.nowhereToRun();
			}
		}

		@Override
		protected void escaped() {
			if (item != null
					&& !Dungeon.level.heroFOV[pos]
					&& Dungeon.level.distance(Dungeon.hero.pos, pos) >= 6) {

				int count = 32;
				int newPos;
				do {
					newPos = Dungeon.level.randomRespawnCell( Thief.this );
					if (count-- <= 0) {
						break;
					}
				} while (newPos == -1 || Dungeon.level.heroFOV[newPos] || Dungeon.level.distance(newPos, pos) < (count/3));

				if (newPos != -1) {

					pos = newPos;
					sprite.place( pos );
					sprite.visible = Dungeon.level.heroFOV[pos];
					if (Dungeon.level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

				}

				if (item != null) GLog.n( Messages.get(Thief.class, "escapes", item.name()));
				item = null;
				state = WANDERING;
			} else {
				state = WANDERING;
			}
		}
	}
}
