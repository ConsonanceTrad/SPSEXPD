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

package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.Statistics;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.LostInventory;
import pd.actors.hero.Hero;
import pd.actors.mobs.GoldThief;
import pd.actors.mobs.npcs.Imp;
import pd.effects.particles.ElmoParticle;
import pd.items.equipment.armor.ClothArmor;
import pd.items.quest.EscapeCrystal;
import pd.levels.features.LevelTransition;
import pd.levels.painters.CityPainter;
import pd.levels.painters.Painter;
import pd.levels.rooms.Room;
import pd.levels.traps.BlazingTrap;
import pd.levels.traps.BoundTrap;
import pd.levels.traps.CorrosionTrap;
import pd.levels.traps.CursingTrap;
import pd.levels.traps.DewTrap;
import pd.levels.traps.DisarmingTrap;
import pd.levels.traps.DisintegrationTrap;
import pd.levels.traps.DistortionTrap;
import pd.levels.traps.ExplosiveTrap;
import pd.levels.traps.FlashingTrap;
import pd.levels.traps.FrostTrap;
import pd.levels.traps.GatewayTrap;
import pd.levels.traps.GeyserTrap;
import pd.levels.traps.GrippingTrap;
import pd.levels.traps.GuardianTrap;
import pd.levels.traps.KnowledgeTrap;
import pd.levels.traps.PitfallTrap;
import pd.levels.traps.RockfallTrap;
import pd.levels.traps.SpearTrap;
import pd.levels.traps.StormTrap;
import pd.levels.traps.SummoningTrap;
import pd.levels.traps.TeleportationTrap;
import pd.levels.traps.WarpingTrap;
import pd.levels.traps.WeakeningTrap;
import pd.levels.traps.damagetrap.DarkDamage2Trap;
import pd.levels.traps.damagetrap.EarthDamage2Trap;
import pd.levels.traps.damagetrap.FireDamage2Trap;
import pd.levels.traps.damagetrap.IceDamage2Trap;
import pd.levels.traps.damagetrap.LightDamage2Trap;
import pd.levels.traps.damagetrap.ShockDamage2Trap;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ImpSprite;
import pd.tiles.DungeonTilemap;
import pd.windows.WndOptions;
import render.noosa.Game;
import render.noosa.Group;
import render.noosa.audio.Music;
import render.noosa.particles.Emitter;
import render.noosa.particles.PixelParticle;
import render.utils.data.Callback;
import render.utils.geom.PointF;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

public class CityLevel extends SpsRegularLevel {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(CityLevel.class)
			.t("water_name", "异色水潭")
			.t("high_grass_name", "茂盛花朵")
			.t("entrance_desc", "通向上一层的斜坡。")
			.t("exit_desc", "通向下一层的斜坡。")
			.t("deco_desc", "这里少了一些地砖。")
			.t("statue_desc", "这尊雕像刻画出了一位摆出英勇姿态的矮人。")
			.t("bookshelf_desc", "不同学科的书排满了书架。")
			.t("region_deco_name", "长明基座")
			.t("region_deco_desc", "一个在其上燃有明亮的绿色魔法火焰的凸起基座。火焰是如此致密，好似固化了一般。");
	}




	@Override
	protected float legacyWaterFill() {
		return feeling == Feeling.WATER ? 0.65f : 0.45f;
	}

	@Override
	protected int legacyWaterClustering() {
		return 4;
	}

	@Override
	protected int legacyGrassClustering() {
		return 3;
	}

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	public static final String[] CITY_TRACK_LIST
			= new String[]{Assets.Music.CITY_1, Assets.Music.CITY_2, Assets.Music.CITY_2,
			Assets.Music.CITY_1, Assets.Music.CITY_3, Assets.Music.CITY_3};
	public static final float[] CITY_TRACK_CHANCES = new float[]{1f, 1f, 0.5f, 0.25f, 1f, 0.5f};

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
	}

	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 9;
		//7 to 9, average 8
		return 7+Random.chances(new float[]{1, 3, 1});
	}

	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 3;
		//2 to 3, average 2.33
		return 2 + Random.chances(new float[]{2, 1});
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.SPS_TILES_CITY_LEGACY;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_CITY;
	}

	@Override
	protected Painter painter() {
		return new CityPainter()
				.setWater(feeling == Feeling.WATER ? 0.90f : 0.30f, 4)
				.setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}

	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				SpearTrap.class, ExplosiveTrap.class, GrippingTrap.class,
				RockfallTrap.class, WeakeningTrap.class, BoundTrap.class, DewTrap.class,
				CursingTrap.class, GuardianTrap.class, KnowledgeTrap.class,
				SummoningTrap.class, TeleportationTrap.class, DisarmingTrap.class, WarpingTrap.class,
				FireDamage2Trap.class, IceDamage2Trap.class, ShockDamage2Trap.class, EarthDamage2Trap.class,
				LightDamage2Trap.class, DarkDamage2Trap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				6, 4, 3,
				6, 3, 4, 2,
				4, 4, 1,
				4, 1, 2, 3,
				3, 3, 3, 3, 3, 3 };
	}

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.BRANCH_EXIT) {

			if ( Imp.Quest.isOld() || Imp.Quest.isCompleted() || !Imp.Quest.given()
					|| hero.buff(AscensionChallenge.class) != null
					|| hero.buff(LostInventory.class) != null){
				return false;
			}

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show( new WndOptions( new ImpSprite(),
							Messages.titleCase(Messages.get(Imp.class, "name")),
							Messages.get(Imp.class, "enter_text"),
							Messages.get(Imp.class, "enter_yes"),
							Messages.get(Imp.class, "enter_no")){
						@Override
						protected void onSelect(int index) {
							if (index == 0){

								Dungeon.hero.live(); //clears all non-persist buffs, resets hunger/regen
								hero.HP = hero.HT; //full heal

								EscapeCrystal crystal = hero.belongings.getItem(EscapeCrystal.class);
								if (crystal == null) {
									crystal = new EscapeCrystal();
								} else {
									crystal.detachAll(Dungeon.hero.belongings.backpack);
								}
								if (crystal.storedItems == null){
									crystal.storeHeroBelongings(Dungeon.hero);
								}
								crystal.collect();
								hero.belongings.armor = new ClothArmor();
								hero.belongings.armor.identify();
								hero.updateHT( false );
								CityLevel.super.activateTransition(hero, transition);
							}
						}
					} );
				}
			});
			return false;

		} else {
			return super.activateTransition(hero, transition);
		}
	}

	@Override
	protected ArrayList<Room> initRooms() {
		// Keep the Shattered vault quest code available without inserting its room
		// into SPS runs. createLegacyQuestActors() handles the 0.9.8 imp instead.
		return super.initRooms();
	}

	@Override
	protected void createItems() {
		super.createItems();
		createLegacyQuestActors();
	}

	void createLegacyQuestActors() {
		Imp.Quest.spawnLegacy(this);
		if (Dungeon.depth == 19) spawnLegacyGoldThief();
	}

	private void spawnLegacyGoldThief() {
		GoldThief thief = new GoldThief();
		int cell = -1;
		for (int attempt = 0; attempt < 30 && cell == -1; attempt++) {
			int candidate = randomRespawnCell(thief);
			if (candidate >= 0 && heaps.get(candidate) == null) cell = candidate;
		}
		if (cell != -1) {
			thief.pos = cell;
			mobs().add(thief);
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(CityLevel.class, "water_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(CityLevel.class, "region_deco_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return Messages.get(CityLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(CityLevel.class, "exit_desc");
			case Terrain.WALL_DECO:
			case Terrain.EMPTY_DECO:
				return Messages.get(CityLevel.class, "deco_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(CityLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CityLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(CityLevel.class, "region_deco_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addCityVisuals( this, visuals );
		return visuals;
	}

	public static void addCityVisuals( Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Smoke( i ) );
			}
		}
	}

	@Override
	public Group addWallVisuals() {
		super.addWallVisuals();
		addCityWallVisuals( this, wallVisuals );
		return wallVisuals;
	}

	public static void addCityWallVisuals( Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.REGION_DECO || level.map[i] == Terrain.REGION_DECO_ALT) {
				group.add( new GreenFlame( i ) );
			}
		}
	}

	public static class GreenFlame extends Emitter {

		private int pos;

		public static final Emitter.Factory factory = new Factory() {
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				GreenFlameParticle p = (GreenFlameParticle)emitter.recycle( GreenFlameParticle.class );
				p.reset( x, y );
			}
			@Override
			public boolean lightMode() {
				return true;
			}
		};

		public GreenFlame( int pos ) {
			super();

			this.pos = pos;

			PointF p = DungeonTilemap.raisedTileCenterToWorld( pos );
			pos( p.x - 2, p.y - 5, 4, 4 );

			pour( factory, 0.1f );
		}

		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				super.update();
			}
		}

	}

	public static class GreenFlameParticle extends ElmoParticle {

		public GreenFlameParticle(){
			super();
			acc.set( 0, -40 );
		}

	}

	
	public static class Smoke extends Emitter {
		
		private int pos;

		public static final Emitter.Factory factory = new Factory() {
			
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				SmokeParticle p = (SmokeParticle)emitter.recycle( SmokeParticle.class );
				p.reset( x, y );
			}
		};
		
		public Smoke( int pos ) {
			super();
			
			this.pos = pos;
			
			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 6, p.y - 4, 12, 12 );
			
			pour( factory, 0.2f );
		}
		
		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				super.update();
			}
		}
	}
	
	public static final class SmokeParticle extends PixelParticle {
		
		public SmokeParticle() {
			super();
			
			color( 0x000000 );
			speed.set( Random.Float( -2, 4 ), -Random.Float( 3, 6 ) );
		}
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan = 2f;
		}
		
		@Override
		public void update() {
			super.update();
			float p = left / lifespan;
			am = p > 0.8f ? 1 - p : p * 0.25f;
			size( 6 - p * 3 );
		}
	}
}
