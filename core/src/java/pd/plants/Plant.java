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

package pd.plants;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Challenges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Barkskin;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.effects.CellEmitter;
import pd.effects.particles.LeafParticle;
import pd.items.Item;
import pd.items.equipment.wands.WandOfRegrowth;
import pd.journal.Bestiary;
import pd.journal.Catalog;
import pd.levels.GroundItems;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import pd.messages.InlineText;

public abstract class Plant implements Bundlable {
	//SPSEXPD: inline Chinese text (generated from messages/plants/zh)
	static {
		InlineText.of(Plant.class)
			.t("warden_desc", "守望者踩踏这株植物不会获得额外效果。")
			.t("discover_hint", "你可在地牢各处直接找到该植物，使用对应的种子进行种植亦可。")
			.t("$seed.seed_of", "%s之种")
			.t("$seed.ac_plant", "种植")
			.t("$seed.info", "把这粒种子丢到你想长出一株植物的地方。\n\n%s")
			.t("$seed$placeholder.name", "种子");
	}



	
	public int image;
	public int pos;

	protected Class<? extends Plant.Seed> seedClass;

	public void trigger(){

		Char ch = Actor.findChar(pos);

		if (ch instanceof Hero){
			((Hero) ch).interrupt();
			if(((Hero) ch).hasTalent(Talent.BARKSKIN)){
				Barkskin.conditionallyAppend(ch, (((Hero) ch).lvl* ((Hero) ch).pointsInTalent(Talent.BARKSKIN))/3, 1 );
			}
		}

		if (Dungeon.level.heroFOV[pos] && Dungeon.hero.hasTalent(Talent.NATURES_AID)){
			// 3/5 turns based on talent points spent
			Barkskin.conditionallyAppend(Dungeon.hero, 2, 1 + 2*(Dungeon.hero.pointsInTalent(Talent.NATURES_AID)));
		}

		wither();
		activate( ch );

		//SPSEXPD: 野生植物触发原生踩踏效果后，还会在附近散落 1 枚对应的投掷果实
		if (!(this instanceof SpsFruitBush) && Dungeon.level != null) {
			Class<? extends Item> fruit = PlantHarvest.fruitFor(getClass());
			if (fruit != null) PlantHarvest.scatter(Dungeon.level, pos, fruit, null, 1, 0f);
		}

		Bestiary.setSeen(getClass());
		Bestiary.countEncounter(getClass());
	}
	
	public abstract void activate( Char ch );
	
	public void wither() {
		GroundItems.uproot( Dungeon.level,  pos );

		if (Dungeon.level.heroFOV[pos]) {
			CellEmitter.get( pos ).burst( LeafParticle.GENERAL, 6 );
		}

		float seedChance = 0f;
		for (Char c : Actor.chars()){
			if (c instanceof WandOfRegrowth.Lotus){
				WandOfRegrowth.Lotus l = (WandOfRegrowth.Lotus) c;
				if (l.inRange(pos)){
					seedChance = Math.max(seedChance, l.seedPreservation());
				}
			}
		}

		if (Random.Float() < seedChance){
			if (seedClass != null && seedClass != Rotberry.Seed.class) {
				Dungeon.level.drop(Reflection.newInstance(seedClass), pos).sprite.drop();
			}
		}
		
	}
	
	private static final String POS	= "pos";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
	}

	public String name(){
		return Messages.get(this, "name");
	}

	public String desc() {
		String desc = Messages.get(this, "desc");
		if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.WARDEN){
			desc += "\n\n" + Messages.get(this, "warden_desc");
		}
		return desc;
	}
	
	public static class Seed extends Item {

		public static final String AC_PLANT	= "PLANT";
		
		private static final float TIME_TO_PLANT = 1f;
		
		{
			stackable = true;
			defaultAction = AC_THROW;
		}
		
		protected Class<? extends Plant> plantClass;
		protected Class<? extends Plant> explantClass;
		
		@Override
		public ArrayList<String> actions( Hero hero ) {
			ArrayList<String> actions = super.actions( hero );
			actions.add( AC_PLANT );
			return actions;
		}
		
		@Override
		protected void onThrow( int cell ) {
			if (Dungeon.level.map[cell] == Terrain.ALCHEMY
					|| Dungeon.level.pit[cell]
					|| Dungeon.level.traps.get(cell) != null
					|| Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
				super.onThrow( cell );
			} else if (Dungeon.level.map[cell] == Terrain.FLOWER_POT) {
				//SPSEXPD: 手动把种子种进花盆 = 精心种植（果丛形态 + 花盆产出规则）
				Catalog.countUse(getClass());
				GroundItems.explantPot( Dungeon.level, this, cell );
			} else {
				Catalog.countUse(getClass());
				GroundItems.plant( Dungeon.level,  this, cell );
				if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
					for (int i : PathFinder.NEIGHBOURS8) {
						int c = Dungeon.level.map[cell + i];
						if ( c == Terrain.EMPTY || c == Terrain.EMPTY_DECO
								|| c == Terrain.EMBERS || c == Terrain.GRASS){
							Level.set(cell + i, Terrain.FURROWED_GRASS);
							GameScene.updateMap(cell + i);
							CellEmitter.get( cell + i ).burst( LeafParticle.LEVEL_SPECIFIC, 4 );
						}
					}
				}
			}
		}
		
		@Override
		public void execute( Hero hero, String action ) {

			super.execute (hero, action );

			if (action.equals( AC_PLANT )) {

				hero.busy();
				((Seed)detach( hero.belongings.backpack )).onThrow( hero.pos );
				hero.spend( TIME_TO_PLANT );

				hero.sprite.operate( hero.pos );
				
			}
		}
		
		public Plant couch( int pos, Level level ) {
			if (level != null && level.heroFOV != null && level.heroFOV[pos]) {
				Sample.INSTANCE.play(Assets.Sounds.PLANT);
			}
			Plant plant = Reflection.newInstance(plantClass);
			plant.pos = pos;
			//SPSEXPD: 记录植物来自哪种种子，供花盆种植等场景校验/回推
			plant.seedClass = getClass();
			return plant;
		}

		public Plant excouch( int pos, Level level ) {
			if (level != null && level.heroFOV != null && level.heroFOV[pos]) {
				Sample.INSTANCE.play(Assets.Sounds.PLANT);
			}
			Class<? extends Plant> type = explantClass == null ? plantClass : explantClass;
			Plant plant = Reflection.newInstance(type);
			plant.pos = pos;
			//SPSEXPD: 记录植物来自哪种种子，供花盆种植等场景校验/回推
			plant.seedClass = getClass();
			return plant;
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
			return 10 * quantity;
		}

		@Override
		public int energyVal() {
			return 2 * quantity;
		}

		@Override
		public String desc() {
			String desc = Messages.get(plantClass, "desc");
			if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.WARDEN){
				desc += "\n\n" + Messages.get(plantClass, "warden_desc");
			}
			return desc;
		}

		@Override
		public String info() {
			return Messages.get( Seed.class, "info", super.info() );
		}
		
		public static class PlaceHolder extends Seed {
			
			{
				image = SpecificPlaceHolderDict.SEED_HOLDER_0;
			}
			
			@Override
			public boolean isSimilar(Item item) {
				return item instanceof Plant.Seed;
			}
			
			@Override
			public String info() {
				return "";
			}
		}
	}
}
