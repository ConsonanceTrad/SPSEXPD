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
import pd.Dungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Bee;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.NPC;
import pd.effects.Pushing;
import pd.effects.Splash;
import pd.journal.Catalog;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.sprites.SteelBeeSprite;
import watabou.noosa.audio.Sample;
import watabou.noosa.tweeners.AlphaTweener;
import watabou.utils.PathFinder;
import watabou.utils.Bundle;
import watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Honeypot extends Item {
	
	public static final String AC_SHATTER	= "SHATTER";
	
	{
		image = ItemSpriteSheet.HONEYPOT;

		defaultAction = AC_THROW;
		usesTargeting = true;

		stackable = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_SHATTER );
		return actions;
	}
	
	@Override
	public void execute( final Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_SHATTER )) {
			
			hero.sprite.zap( hero.pos );
			
			detach( hero.belongings.backpack );
			Catalog.countUse(getClass());

			Item item = shatter( hero, hero.pos );
			if (!item.collect()){
				Dungeon.level.drop(item, hero.pos);
				if (item instanceof ShatteredPot){
					((ShatteredPot) item).dropPot(hero, hero.pos);
				}
			}

			hero.next();

		}
	}
	
	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.level.pit[cell]) {
			super.onThrow( cell );
		} else {
			Catalog.countUse(getClass());
			Dungeon.level.drop(shatter( null, cell ), cell);
		}
	}
	
	public Item shatter( Char owner, int pos ) {
		
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.Sounds.SHATTER );
			Splash.at( pos, 0xffd500, 5 );
		}
		
		int newPos = pos;
		if (Actor.findChar( pos ) != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			
			for (int n : PathFinder.NEIGHBOURS4) {
				int c = pos + n;
				if (!Dungeon.level.solid[c] && Actor.findChar( c ) == null) {
					candidates.add( c );
				}
			}
	
			newPos = candidates.size() > 0 ? Random.element( candidates ) : -1;
		}
		
		if (newPos != -1) {
			Mob bee;
			if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.LEADER) {
				SteelBee steelBee = new SteelBee();
				steelBee.spawn(Dungeon.legacyDepth());
				bee = steelBee;
			} else {
				Bee normalBee = new Bee();
				normalBee.spawn(Dungeon.legacyDepth());
				normalBee.setPotInfo(pos, owner);
				bee = normalBee;
			}
			bee.HP = bee.HT;
			bee.pos = newPos;

			GameScene.add(bee);
			if (newPos != pos) Actor.add(new Pushing(bee, pos, newPos));

			if (bee.sprite != null) {
				bee.sprite.alpha(0);
				if (bee.sprite.parent != null) bee.sprite.parent.add(new AlphaTweener(bee.sprite, 1, 0.15f));
			}
			
			Sample.INSTANCE.play( Assets.Sounds.BEE );
			return new ShatteredPot();
		} else {
			return this;
		}
	}

	/** Leader-only allied bee used by honeypots and HoneyArrow. */
	public static class SteelBee extends NPC {

		private static final String LEVEL = "level";
		private int level;

		{
			spriteClass = SteelBeeSprite.class;
			viewDistance = 6;
			alignment = Alignment.ALLY;
			flying = true;
			state = WANDERING;
			intelligentAlly = true;
			immunities.add(Poison.class);
		}

		public void spawn(int requestedLevel) {
			level = Math.max(0, Math.min(requestedLevel, Statistics.deepestFloor));
			HT = (50 + requestedLevel) * 4;
			HP = HT;
			defenseSkill = 15 + requestedLevel;
		}

		public int beeLevel() { return level; }
		@Override public int attackSkill(Char target) { return defenseSkill * 2; }
		@Override public int damageRoll() { return Random.NormalIntRange(HT / 8, HT / 2); }

		@Override
		protected boolean getCloser(int target) {
			if (Dungeon.hero != null && (state == WANDERING
					|| Dungeon.level.distance(target, Dungeon.hero.pos) > 6)) {
				this.target = target = Dungeon.hero.pos;
			}
			return super.getCloser(target);
		}

		@Override
		protected Char chooseEnemy() {
			if (enemy == null || !enemy.isAlive() || state == WANDERING) {
				HashSet<Mob> enemies = new HashSet<>();
				for (Mob mob : Dungeon.level.mobs) {
					if (mob.alignment == Alignment.ENEMY && mob.state != PASSIVE
							&& fieldOfView != null && fieldOfView[mob.pos]) enemies.add(mob);
				}
				enemy = enemies.isEmpty() ? null : Random.element(enemies);
			}
			return enemy;
		}

		@Override
		public boolean interact(Char ch) {
			if (!(ch instanceof Hero) || !Dungeon.level.adjacent(pos, ch.pos)) return false;
			int beeFrom = pos;
			int heroFrom = ch.pos;
			move(heroFrom);
			ch.move(beeFrom);
			if (sprite != null) sprite.move(beeFrom, heroFrom);
			if (ch.sprite != null) ch.sprite.move(heroFrom, beeFrom);
			((Hero)ch).spendAndNext(1f / ch.speed());
			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LEVEL, level);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			spawn(bundle.getInt(LEVEL));
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}

	//The bee's broken 'home', all this item does is let its bee know where it is, and who owns it (if anyone).
	public static class ShatteredPot extends Item {

		{
			image = ItemSpriteSheet.SHATTPOT;
			stackable = true;
		}

		@Override
		public boolean doPickUp(Hero hero, int pos) {
			if ( super.doPickUp(hero, pos) ){
				pickupPot( hero );
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void doDrop(Hero hero) {
			super.doDrop(hero);
			dropPot(hero, hero.pos);
		}

		@Override
		protected void onThrow(int cell) {
			super.onThrow(cell);
			dropPot(curUser, cell);
		}

		public void pickupPot(Char holder){
			for (Bee bee : findBees(holder.pos)){
				updateBee(bee, -1, holder);
			}
		}
		
		public void dropPot( Char holder, int dropPos ){
			for (Bee bee : findBees(holder)){
				updateBee(bee, dropPos, null);
			}
		}

		public void movePot( int oldpos, int movePos){
			for (Bee bee : findBees(oldpos)){
				updateBee(bee, movePos, null);
			}
		}

		public void destroyPot( int potPos ){
			for (Bee bee : findBees(potPos)){
				updateBee(bee, -1, null);
			}
		}

		private void updateBee( Bee bee, int cell, Char holder ){
			if (bee != null && bee.alignment == Char.Alignment.ENEMY)
				bee.setPotInfo( cell, holder );
		}
		
		//returns up to quantity bees which match the current pot Pos
		private ArrayList<Bee> findBees( int potPos ){
			ArrayList<Bee> bees = new ArrayList<>();
			for (Char c : Actor.chars()){
				if (c instanceof Bee && ((Bee) c).potPos() == potPos){
					bees.add((Bee) c);
					if (bees.size() >= quantity) {
						break;
					}
				}
			}
			
			return bees;
		}
		
		//returns up to quantity bees which match the current pot holder
		private ArrayList<Bee> findBees( Char potHolder ){
			ArrayList<Bee> bees = new ArrayList<>();
			for (Char c : Actor.chars()){
				if (c instanceof Bee && ((Bee) c).potHolderID() == potHolder.id()){
					bees.add((Bee) c);
					if (bees.size() >= quantity) {
						break;
					}
				}
			}
			
			return bees;
		}

		@Override
		public boolean isUpgradable() {
			return false;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}
		
		@Override
		public int value() {
			return 5 * quantity;
		}
	}
}
