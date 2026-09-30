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
import pd.items.Torch;
import pd.items.keys.SpsSkeletonKey;
import pd.levels.painters.HallsPainter;
import pd.levels.painters.Painter;
import pd.levels.rooms.Room;
import pd.levels.rooms.special.DemonSpawnerRoom;
import pd.levels.traps.BlazingTrap;
import pd.levels.traps.BoundTrap;
import pd.levels.traps.CorrosionTrap;
import pd.levels.traps.CursingTrap;
import pd.levels.traps.DewTrap;
import pd.levels.traps.DisarmingTrap;
import pd.levels.traps.DisintegrationTrap;
import pd.levels.traps.DistortionTrap;
import pd.levels.traps.FlashingTrap;
import pd.levels.traps.FlockTrap;
import pd.levels.traps.FrostTrap;
import pd.levels.traps.GatewayTrap;
import pd.levels.traps.GeyserTrap;
import pd.levels.traps.GrimTrap;
import pd.levels.traps.GrippingTrap;
import pd.levels.traps.GuardianTrap;
import pd.levels.traps.KnowledgeTrap;
import pd.levels.traps.PitfallTrap;
import pd.levels.traps.RockfallTrap;
import pd.levels.traps.StormTrap;
import pd.levels.traps.SummoningTrap;
import pd.levels.traps.TeleportationTrap;
import pd.levels.traps.VenomTrap;
import pd.levels.traps.WarpingTrap;
import pd.levels.traps.WeakeningTrap;
import pd.levels.traps.bufftrap.DarkBuff3Trap;
import pd.levels.traps.bufftrap.EarthBuff3Trap;
import pd.levels.traps.bufftrap.FireBuff3Trap;
import pd.levels.traps.bufftrap.IceBuff3Trap;
import pd.levels.traps.bufftrap.LightBuff3Trap;
import pd.levels.traps.bufftrap.ShockBuff3Trap;
import pd.messages.Messages;
import pd.tiles.DungeonTilemap;
import render.glwrap.Blending;
import render.noosa.Game;
import render.noosa.Group;
import render.noosa.audio.Music;
import render.noosa.particles.PixelParticle;
import render.utils.geom.PointF;
import render.utils.math.Random;

import java.util.ArrayList;

public class HallsLevel extends SpsRegularLevel {

	@Override
	protected float legacyWaterFill() {
		return feeling == Feeling.WATER ? 0.55f : 0.40f;
	}

	@Override
	protected int legacyWaterClustering() {
		return 6;
	}

	@Override
	protected float legacyGrassFill() {
		return feeling == Feeling.GRASS ? 0.55f : 0.30f;
	}

	@Override
	protected int legacyGrassClustering() {
		return 3;
	}

	{
		
		viewDistance = Math.min( 26 - Dungeon.depth, viewDistance );
		
		color1 = 0x801500;
		color2 = 0xa68521;
	}

	public static final String[] HALLS_TRACK_LIST
			= new String[]{Assets.Music.HALLS_1, Assets.Music.HALLS_2, Assets.Music.HALLS_2,
			Assets.Music.HALLS_1, Assets.Music.HALLS_3, Assets.Music.HALLS_3};
	public static final float[] HALLS_TRACK_CHANCES = new float[]{1f, 1f, 0.5f, 0.25f, 1f, 0.5f};


	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
	}

	@Override
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> rooms = super.initRooms();

		rooms.add(new DemonSpawnerRoom());

		return rooms;
	}

	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 10;
		//9 to 10, average 9.33
		return 9+Random.chances(new float[]{2, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 3;
		//2 to 3, average 2.5
		return 2 + Random.chances(new float[]{1, 1});
	}
	
	@Override
	protected Painter painter() {
		return new HallsPainter()
				.setWater(feeling == Feeling.WATER ? 0.70f : 0.15f, 6)
				.setGrass(feeling == Feeling.GRASS ? 0.65f : 0.10f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public void create() {
		addItemToSpawn( new Torch() );
		super.create();
	}

	@Override
	protected void createItems() {
		addLegacyExitKeyToSpawn();
		super.createItems();
	}

	void addLegacyExitKeyToSpawn() {
		if (Dungeon.depth != 25) addItemToSpawn(new SpsSkeletonKey(Dungeon.depth));
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.SPS_TILES_HALLS_LEGACY;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_HALLS;
	}
	
	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				DisintegrationTrap.class, VenomTrap.class, BoundTrap.class, DewTrap.class,
				GrippingTrap.class, WeakeningTrap.class, CursingTrap.class,
				FlockTrap.class, GrimTrap.class, GuardianTrap.class, KnowledgeTrap.class,
				SummoningTrap.class, TeleportationTrap.class, DisarmingTrap.class,
				FireBuff3Trap.class, IceBuff3Trap.class, ShockBuff3Trap.class, EarthBuff3Trap.class,
				LightBuff3Trap.class, DarkBuff3Trap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				4, 4, 3, 2,
				4, 1, 1,
				1, 4, 4, 1,
				4, 2, 3,
				3, 3, 3, 3, 3, 2 };
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(HallsLevel.class, "water_name");
			case Terrain.GRASS:
				return Messages.get(HallsLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(HallsLevel.class, "high_grass_name");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(HallsLevel.class, "statue_name");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(HallsLevel.class, "region_deco_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(HallsLevel.class, "water_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(HallsLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(HallsLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(HallsLevel.class, "region_deco_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addHallsVisuals( this, visuals );
		return visuals;
	}
	
	public static void addHallsVisuals( Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WATER) {
				group.add( new Stream( i ) );
			}
		}
	}
	
	private static class Stream extends Group {
		
		private int pos;
		
		private float delay;
		
		public Stream( int pos ) {
			super();
			
			this.pos = pos;
			
			delay = Random.Float( 2 );
		}
		
		@Override
		public void update() {

			if (!Dungeon.level.water[pos]){
				killAndErase();
				return;
			}
			
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				
				super.update();
				
				if ((delay -= Game.elapsed) <= 0) {
					
					delay = Random.Float( 2 );
					
					PointF p = DungeonTilemap.tileToWorld( pos );
					((FireParticle)recycle( FireParticle.class )).reset(
						p.x + Random.Float( DungeonTilemap.SIZE ),
						p.y + Random.Float( DungeonTilemap.SIZE ) );
				}
			}
		}
		
		@Override
		public void draw() {
			Blending.setLightMode();
			super.draw();
			Blending.setNormalMode();
		}
	}
	
	public static class FireParticle extends PixelParticle.Shrinking {
		
		public FireParticle() {
			super();
			
			color( 0xEE7722 );
			lifespan = 1f;
			
			acc.set( 0, +80 );
		}
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan;
			
			speed.set( 0, -40 );
			size = 4;
		}
		
		@Override
		public void update() {
			super.update();
			float p = left / lifespan;
			am = p > 0.8f ? (1 - p) * 5 : 1;
		}
	}
}
