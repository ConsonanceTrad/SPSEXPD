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
import pd.actors.Char;
import pd.actors.mobs.npcs.Wandmaker;
import pd.effects.particles.FlameParticle;
import pd.effects.particles.WindParticle;
import pd.levels.painters.Painter;
import pd.levels.painters.PrisonPainter;
import pd.levels.rooms.Room;
import pd.levels.traps.AlarmTrap;
import pd.levels.traps.BurningTrap;
import pd.levels.traps.ChillingTrap;
import pd.levels.traps.ConfusionTrap;
import pd.levels.traps.FlockTrap;
import pd.levels.traps.FlashingTrap;
import pd.levels.traps.GatewayTrap;
import pd.levels.traps.GeyserTrap;
import pd.levels.traps.GrippingTrap;
import pd.levels.traps.OozeTrap;
import pd.levels.traps.ParalyticTrap;
import pd.levels.traps.PoisonTrap;
import pd.levels.traps.PoisonDartTrap;
import pd.levels.traps.SpearTrap;
import pd.levels.traps.ShockingTrap;
import pd.levels.traps.SummoningTrap;
import pd.levels.traps.TeleportationTrap;
import pd.levels.traps.ToxicTrap;
import pd.levels.traps.BoundTrap;
import pd.levels.traps.DewTrap;
import pd.levels.traps.KnowledgeTrap;
import pd.levels.traps.damagetrap.DarkDamageTrap;
import pd.levels.traps.damagetrap.EarthDamageTrap;
import pd.levels.traps.damagetrap.FireDamageTrap;
import pd.levels.traps.damagetrap.IceDamageTrap;
import pd.levels.traps.damagetrap.LightDamageTrap;
import pd.levels.traps.damagetrap.ShockDamageTrap;
import pd.messages.Messages;
import pd.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Halo;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PrisonLevel extends SpsRegularLevel {

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
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}

	public static final String[] PRISON_TRACK_LIST
			= new String[]{Assets.Music.PRISON_1, Assets.Music.PRISON_2, Assets.Music.PRISON_2,
			Assets.Music.PRISON_1, Assets.Music.PRISON_3, Assets.Music.PRISON_3};
	public static final float[] PRISON_TRACK_CHANCES = new float[]{1f, 1f, 0.5f, 0.25f, 1f, 0.5f};

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
		wandmakerQuestWasActive = Wandmaker.Quest.active();
	}

	@Override
	protected ArrayList<Room> initRooms() {
		return super.initRooms();
	}

	@Override
	protected void createMobs() {
		Wandmaker.Quest.spawn(this, roomEntrance);
		super.createMobs();
	}

	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 7;
		//6 to 7, average 6.5
		return 6+Random.chances(new float[]{1, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 3;
		//1 to 3, average 2.0
		return 1+Random.chances(new float[]{1, 3, 1});
	}
	
	@Override
	protected Painter painter() {
		return new PrisonPainter()
				.setWater(feeling == Feeling.WATER ? 0.90f : 0.30f, 4)
				.setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.SPS_TILES_PRISON_LEGACY;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_PRISON;
	}
	
	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				PoisonTrap.class, SpearTrap.class, ToxicTrap.class,
				AlarmTrap.class, FlashingTrap.class, GrippingTrap.class,
				ParalyticTrap.class, ConfusionTrap.class, FlockTrap.class,
				SummoningTrap.class, TeleportationTrap.class, BoundTrap.class, DewTrap.class, KnowledgeTrap.class,
				FireDamageTrap.class, IceDamageTrap.class, ShockDamageTrap.class, EarthDamageTrap.class,
				LightDamageTrap.class, DarkDamageTrap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				4, 4, 4,
				3, 4, 3,
				2, 2, 1,
				2, 1, 5, 3, 1,
				3, 3, 3, 3, 3, 3 };
	}

	@Override
	public void occupyCell(Char ch) {
		super.occupyCell(ch);
		if (ch == Dungeon.hero) {
			updateWandmakerQuestMusic();
		}
	}

	private Boolean wandmakerQuestWasActive = null;

	public void updateWandmakerQuestMusic(){
		if (wandmakerQuestWasActive == null) {
			wandmakerQuestWasActive = Wandmaker.Quest.active();
			return;
		}
		if (Wandmaker.Quest.active() != wandmakerQuestWasActive) {
			wandmakerQuestWasActive = Wandmaker.Quest.active();

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Music.INSTANCE.fadeOut(1f, new Callback() {
						@Override
						public void call() {
							if (Dungeon.level != null) {
								Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
							}
						}
					});
				}
			});
		}
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(PrisonLevel.class, "water_name");
			case Terrain.REGION_DECO:
				return Messages.get(PrisonLevel.class, "region_deco_name");
			case Terrain.REGION_DECO_ALT:
				return Messages.get(PrisonLevel.class, "region_deco_alt_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(PrisonLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(PrisonLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
				return Messages.get(PrisonLevel.class, "region_deco_desc");
			case Terrain.REGION_DECO_ALT:
				return Messages.get(PrisonLevel.class, "region_deco_alt_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addPrisonVisuals(this, visuals);
		return visuals;
	}

	public static void addPrisonVisuals(Level level, Group group){
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Torch( i ) );
			}
			//alt deco is a chasm visual in the prison
			if (level.map[i] == Terrain.REGION_DECO_ALT) {
				group.add( new WindParticle.Wind( i ) );
			}
		}
	}
	
	public static class Torch extends Emitter {
		
		private int pos;
		
		public Torch( int pos ) {
			super();
			
			this.pos = pos;
			
			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 1, p.y + 2, 2, 0 );
			
			pour( FlameParticle.FACTORY, 0.15f );
			
			add( new Halo( 12, 0xFFFFCC, 0.4f ).point( p.x, p.y + 1 ) );
		}
		
		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				super.update();
			}
		}
	}
}
