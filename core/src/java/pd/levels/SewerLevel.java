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
import pd.actors.hero.Hero;
import pd.actors.mobs.GnollArcher;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.Ghost;
import pd.effects.Ripple;
import pd.effects.Splash;
import pd.levels.features.LevelTransition;
import pd.levels.painters.Painter;
import pd.levels.painters.SewerPainter;
import pd.levels.traps.AlarmTrap;
import pd.levels.traps.BoundTrap;
import pd.levels.traps.ChillingTrap;
import pd.levels.traps.ConfusionTrap;
import pd.levels.traps.DewTrap;
import pd.levels.traps.FlockTrap;
import pd.levels.traps.GatewayTrap;
import pd.levels.traps.KnowledgeTrap;
import pd.levels.traps.OozeTrap;
import pd.levels.traps.ShockingTrap;
import pd.levels.traps.SummoningTrap;
import pd.levels.traps.TeleportationTrap;
import pd.levels.traps.ToxicTrap;
import pd.levels.traps.WornDartTrap;
import pd.levels.traps.bufftrap.DarkBuffTrap;
import pd.levels.traps.bufftrap.EarthBuffTrap;
import pd.levels.traps.bufftrap.FireBuffTrap;
import pd.levels.traps.bufftrap.IceBuffTrap;
import pd.levels.traps.bufftrap.LightBuffTrap;
import pd.levels.traps.bufftrap.ShockBuffTrap;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.tiles.DungeonTilemap;
import render.noosa.Game;
import render.noosa.Group;
import render.noosa.audio.Music;
import render.noosa.particles.Emitter;
import render.noosa.particles.PixelParticle;
import render.utils.geom.PointF;
import render.utils.math.ColorMath;
import render.utils.math.Random;
import pd.messages.InlineText;

public class SewerLevel extends SpsRegularLevel {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(SewerLevel.class)
			.t("water_name", "浑浊水潭")
			.t("empty_deco_desc", "潮湿且发黄的苔藓覆盖其上。")
			.t("bookshelf_desc", "这个书架塞满了没用的成功学书籍。烧掉怎么样？")
			.t("region_deco_name", "储物木桶")
			.t("region_deco_desc", "一个几乎和你差不多大的木桶。里面肯定装满了什么东西，重得搬不动。");
	}


	{
		color1 = 0x48763c;
		color2 = 0x59994a;
	}

	public static final String[] SEWER_TRACK_LIST
			= new String[]{Assets.Music.SEWERS_1, Assets.Music.SEWERS_2, Assets.Music.SEWERS_2,
			Assets.Music.SEWERS_1, Assets.Music.SEWERS_3, Assets.Music.SEWERS_3};
	public static final float[] SEWER_TRACK_CHANCES = new float[]{1f, 1f, 0.5f, 0.25f, 1f, 0.5f};

	public void playLevelMusic(){
		Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
	}
	
	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 7;
		//5 to 7, average 6; one extra room extends exploration without adding loot
		return 5+Random.chances(new float[]{1, 3, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 2;
		//1 to 2, average 1.8
		return 1+Random.chances(new float[]{1, 4});
	}
	
	@Override
	protected Painter painter() {
		return new SewerPainter()
				.setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 5)
				.setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 4)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public String tilesTex() {
		return Assets.Environment.SPS_TILES_SEWERS_LEGACY;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_SEWERS;
	}
	
	@Override
	protected Class<?>[] trapClasses() {
		return Dungeon.depth == 1 ?
				new Class<?>[]{ KnowledgeTrap.class } :
				new Class<?>[]{
						ToxicTrap.class, AlarmTrap.class, FlockTrap.class, BoundTrap.class,
						DewTrap.class, KnowledgeTrap.class, FireBuffTrap.class, IceBuffTrap.class,
						ShockBuffTrap.class, EarthBuffTrap.class, LightBuffTrap.class, DarkBuffTrap.class };
}

	@Override
	protected float[] trapChances() {
		return Dungeon.depth == 1 ?
				new float[]{1} :
				new float[]{3, 3, 3, 6, 3, 1, 2, 2, 2, 2, 2, 2};
	}

	@Override
	protected void createMobs() {
		Ghost.Quest.spawn(this);
		super.createMobs();
		if (Dungeon.depth == 4) {
			GnollArcher archer = new GnollArcher();
			int pos = randomRespawnCell(archer);
			if (pos >= 0 && mobs().findMob(pos) == null) {
				archer.pos = pos;
				mobs().add(archer);
			}
		}
	}

	@Override
	public Mob createMob() {
		if (Dungeon.depth == 4 && Random.Int(5) == 0) return new GnollArcher();
		return super.createMob();
	}
	
	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		//SPS: 本项目 1 层之上还有 0 层，SURFACE 只是回到 0 层的入口；
		//通关判定在 0 层出门时进行（见 BetweenLevel.activateTransition）
		return super.activateTransition(hero, transition);
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		addSewerVisuals(this, visuals);
		return visuals;
	}

	@Override
	public void buildFlagMaps() {
		super.buildFlagMaps();
		for (int i=0; i < length(); i++) {
			if (map[i] == Terrain.REGION_DECO || map[i] == Terrain.REGION_DECO_ALT){
				flamable[i] = true;
			}
		}
	}

	@Override
	public void destroy(int pos) {
		//if we're burning  sewers barrels
		int terr = map[pos];
		if (terr == Terrain.REGION_DECO){
			set(pos, Terrain.WATER);
			Splash.at(pos, 0xFF507B5D, 10);
		} else if (terr == Terrain.REGION_DECO_ALT){
			set(pos, Terrain.EMPTY_SP);
			Splash.at(pos, 0xFF507B5D, 10);
		}
		super.destroy(pos);
	}

	public static void addSewerVisuals(Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Sink( i ) );
			}
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(SewerLevel.class, "water_name");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(SewerLevel.class, "region_deco_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(SewerLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(SewerLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(SewerLevel.class, "region_deco_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	private static class Sink extends Emitter {
		
		private int pos;
		private float rippleDelay = 0;
		
		private static final Emitter.Factory factory = new Factory() {
			
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				WaterParticle p = (WaterParticle)emitter.recycle( WaterParticle.class );
				p.reset( x, y );
			}
		};
		
		public Sink( int pos ) {
			super();
			
			this.pos = pos;
			
			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 2, p.y + 3, 4, 0 );
			
			pour( factory, 0.1f );
		}
		
		@Override
		public void update() {
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				
				super.update();
				
				if (!isFrozen() && (rippleDelay -= Game.elapsed) <= 0) {
					Ripple ripple = GameScene.ripple( pos + Dungeon.level.width() );
					if (ripple != null) {
						ripple.y -= DungeonTilemap.SIZE / 2;
						rippleDelay = Random.Float(0.4f, 0.6f);
					}
				}
			}
		}
	}
	
	public static final class WaterParticle extends PixelParticle {
		
		public WaterParticle() {
			super();
			
			acc.y = 50;
			am = 0.5f;
			
			color( ColorMath.random( 0xb6ccc2, 0x3b6653 ) );
			size( 2 );
		}
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			speed.set( Random.Float( -2, +2 ), 0 );
			
			left = lifespan = 0.4f;
		}
	}
}
