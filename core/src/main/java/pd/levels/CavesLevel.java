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
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Blacksmith;
import pd.actors.mobs.npcs.Blacksmith2;
import pd.actors.mobs.npcs.Tinkerer2;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.quest.Mushroom;
import pd.items.quest.Pickaxe;
import pd.levels.features.LevelTransition;
import pd.levels.builders.SpsBspLayout;
import pd.levels.painters.CavesPainter;
import pd.levels.painters.Painter;
import pd.levels.rooms.Room;
import pd.levels.traps.BurningTrap;
import pd.levels.traps.ConfusionTrap;
import pd.levels.traps.CorrosionTrap;
import pd.levels.traps.FrostTrap;
import pd.levels.traps.GatewayTrap;
import pd.levels.traps.GeyserTrap;
import pd.levels.traps.GrippingTrap;
import pd.levels.traps.GuardianTrap;
import pd.levels.traps.BoundTrap;
import pd.levels.traps.DewTrap;
import pd.levels.traps.ExplosiveTrap;
import pd.levels.traps.FlashingTrap;
import pd.levels.traps.FlockTrap;
import pd.levels.traps.KnowledgeTrap;
import pd.levels.traps.ParalyticTrap;
import pd.levels.traps.PitfallTrap;
import pd.levels.traps.PoisonDartTrap;
import pd.levels.traps.RockfallTrap;
import pd.levels.traps.StormTrap;
import pd.levels.traps.SummoningTrap;
import pd.levels.traps.WarpingTrap;
import pd.levels.traps.VenomTrap;
import pd.levels.traps.TeleportationTrap;
import pd.levels.traps.bufftrap.DarkBuff2Trap;
import pd.levels.traps.bufftrap.EarthBuff2Trap;
import pd.levels.traps.bufftrap.FireBuff2Trap;
import pd.levels.traps.bufftrap.IceBuff2Trap;
import pd.levels.traps.bufftrap.LightBuff2Trap;
import pd.levels.traps.bufftrap.ShockBuff2Trap;
import pd.levels.traps.damagetrap.FireDamageTrap;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.BlacksmithSprite;
import pd.tiles.DungeonTileSheet;
import pd.tiles.DungeonTilemap;
import pd.utils.GLog;
import pd.windows.WndOptions;
import pd.windows.WndTitledMessage;
import render.noosa.Game;
import render.noosa.Group;
import render.noosa.audio.Music;
import render.noosa.particles.PixelParticle;
import render.utils.Callback;
import render.utils.PointF;
import render.utils.Random;

import java.util.ArrayList;

public class CavesLevel extends SpsRegularLevel {
	private boolean legacyBlacksmithThisBuild;

	@Override
	protected boolean build() {
		legacyBlacksmithThisBuild = false;
		boolean built = super.build();
		if (built && legacyBlacksmithThisBuild) Blacksmith.Quest.beginLegacy();
		return built;
	}

	@Override
	protected void assignLegacyRoomTypes() {
		super.assignLegacyRoomTypes();
		if (Dungeon.branch != 0 || !Blacksmith.Quest.canSpawnLegacy()) return;
		for (SpsBspLayout.Room room : legacyLayout.rooms) {
			if (room.type == SpsBspLayout.Type.STANDARD
					&& room.width() > 4 && room.height() > 4) {
				room.type = SpsBspLayout.Type.BLACKSMITH;
				legacyBlacksmithThisBuild = true;
				break;
			}
		}
	}

	@Override
	protected boolean paintLegacyQuestRoom(SpsBspLayout.Room room) {
		if (room.type != SpsBspLayout.Type.BLACKSMITH) return false;

		fill(room, Terrain.WALL);
		fill(room.left + 1, room.top + 1, room.width() - 1, room.height() - 1, Terrain.TRAP);
		fill(room.left + 2, room.top + 2, room.width() - 3, room.height() - 3, Terrain.EMPTY_SP);

		ArrayList<Integer> trapCells = new ArrayList<>();
		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) {
				int cell = x + y * width();
				if (map[cell] == Terrain.TRAP) trapCells.add(cell);
			}
		}
		for (int i = 0; i < 2 && !trapCells.isEmpty(); i++) {
			int cell = trapCells.remove(Random.Int(trapCells.size()));
			Item item = Generator.random(Random.oneOf(Generator.Category.ARMOR,
					Generator.Category.MELEEWEAPON));
			Heap heap = new Heap();
			heap.pos = cell;
			heap.drop(item);
			heaps.put(cell, heap);
		}

		for (SpsBspLayout.Door door : room.connected.values()) {
			if (door == null) continue;
			door.set(SpsBspLayout.Door.Type.UNLOCKED);
			int x = door.x;
			int y = door.y;
			if (x == room.left) x++;
			else if (x == room.right) x--;
			else if (y == room.top) y++;
			else if (y == room.bottom) y--;
			map[x + y * width()] = Terrain.EMPTY;
		}

		ArrayList<Integer> npcCells = new ArrayList<>();
		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) {
				int cell = x + y * width();
				if (heaps.get(cell) == null) npcCells.add(cell);
			}
		}
		if (!npcCells.isEmpty()) {
			Blacksmith smith = new Blacksmith();
			smith.pos = npcCells.remove(Random.Int(npcCells.size()));
			mobs.add(smith);
		}
		if (!npcCells.isEmpty()) {
			Blacksmith2 welder = new Blacksmith2();
			welder.pos = npcCells.remove(Random.Int(npcCells.size()));
			mobs.add(welder);
		}

		for (int y = room.top + 1; y < room.bottom; y++) {
			for (int x = room.left + 1; x < room.right; x++) {
				int cell = x + y * width();
				if (map[cell] == Terrain.TRAP && traps.get(cell) == null) {
					setTrap(new FireDamageTrap().reveal(), cell);
				}
			}
		}
		return true;
	}

	@Override
	protected int legacyWaterClustering() {
		return 6;
	}

	@Override
	protected float legacyGrassFill() {
		return feeling == Feeling.GRASS ? 0.55f : 0.35f;
	}

	@Override
	protected int legacyGrassClustering() {
		return 3;
	}

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	public static final String[] CAVES_TRACK_LIST
			= new String[]{Assets.Music.CAVES_1, Assets.Music.CAVES_2, Assets.Music.CAVES_2,
			Assets.Music.CAVES_1, Assets.Music.CAVES_3, Assets.Music.CAVES_3};
	public static final float[] CAVES_TRACK_CHANCES = new float[]{1f, 1f, 0.5f, 0.25f, 1f, 0.5f};

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
	}

	@Override
	protected ArrayList<Room> initRooms() {
		// Retain Shattered's mining quest implementation, but do not expose it in
		// the normal SPS route. The legacy BSP builder injects the 0.9.8 smithy.
		return super.initRooms();
	}

	@Override
	protected void createItems() {
		if (Dungeon.depth == 12) {
			addItemToSpawn(new Mushroom());
			int cell;
			do {
				cell = randomRespawnCell(null);
			} while (cell != -1 && (heaps.get(cell) != null || findMob(cell) != null));
			if (cell != -1) {
				Tinkerer2 tinkerer = new Tinkerer2();
				tinkerer.pos = cell;
				mobs.add(tinkerer);
			}
		}
		super.createItems();
	}
	
	@Override
	protected int standardRooms(boolean forceMax) {
		if (forceMax) return 8;
		//7 to 8, average 7.333
		return 7+Random.chances(new float[]{2, 1});
	}
	
	@Override
	protected int specialRooms(boolean forceMax) {
		if (forceMax) return 3;
		//2 to 3, average 2.2
		return 2+Random.chances(new float[]{4, 1});
	}
	
	@Override
	protected Painter painter() {
		return new CavesPainter()
				.setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 6)
				.setGrass(feeling == Feeling.GRASS ? 0.65f : 0.15f, 3)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}
	
	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.BRANCH_EXIT
				&& (!Blacksmith.Quest.given() || Blacksmith.Quest.completed() || !Blacksmith.Quest.started())) {

			Blacksmith smith = null;
			for (Char c : Actor.chars()){
				if (c instanceof Blacksmith){
					smith = (Blacksmith) c;
					break;
				}
			}

			if (smith == null || !Blacksmith.Quest.given() || Blacksmith.Quest.completed()) {
				GLog.w(Messages.get(Blacksmith.class, "entrance_blocked"));
			} else {
				final Pickaxe pick = hero.belongings.getItem(Pickaxe.class);
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						if (pick == null){
							GameScene.show( new WndTitledMessage(new BlacksmithSprite(),
									Messages.titleCase(Messages.get(Blacksmith.class, "name")),
									Messages.get(Blacksmith.class, "lost_pick"))
							);
						} else {
							GameScene.show( new WndOptions( new BlacksmithSprite(),
									Messages.titleCase(Messages.get(Blacksmith.class, "name")),
									Messages.get(Blacksmith.class, "quest_start_prompt"),
									Messages.get(Blacksmith.class, "enter_yes"),
									Messages.get(Blacksmith.class, "enter_no")){
								@Override
								protected void onSelect(int index) {
									if (index == 0){
										Blacksmith.Quest.start();
										CavesLevel.super.activateTransition(hero, transition);
									}
								}
							} );
						}

					}
				});
			}
			return false;

		} else {
			return super.activateTransition(hero, transition);
		}
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.SPS_TILES_CAVES_LEGACY;
	}
	
	@Override
	public String waterTex() {
		return Assets.Environment.SPS_WATER_CAVES;
	}
	
	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{
				VenomTrap.class, ExplosiveTrap.class, FlashingTrap.class, KnowledgeTrap.class,
				GrippingTrap.class, ParalyticTrap.class, RockfallTrap.class,
				ConfusionTrap.class, FlockTrap.class, GuardianTrap.class, SummoningTrap.class,
				TeleportationTrap.class, WarpingTrap.class, BoundTrap.class, DewTrap.class,
				FireBuff2Trap.class, IceBuff2Trap.class, ShockBuff2Trap.class, EarthBuff2Trap.class,
				LightBuff2Trap.class, DarkBuff2Trap.class };
	}

	@Override
	protected float[] trapChances() {
		return new float[]{
				4, 4, 4, 1,
				2, 3, 4,
				1, 1, 2, 2,
				1, 1, 4, 3,
				3, 3, 3, 3, 3, 3 };
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.GRASS:
				return Messages.get(CavesLevel.class, "grass_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CavesLevel.class, "high_grass_name");
			case Terrain.WATER:
				return Messages.get(CavesLevel.class, "water_name");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(CavesLevel.class, "region_deco_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc( int tile ) {
		switch (tile) {
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return Messages.get(CavesLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(CavesLevel.class, "exit_desc");
			case Terrain.HIGH_GRASS:
				return Messages.get(CavesLevel.class, "high_grass_desc");
			case Terrain.WALL_DECO:
				return Messages.get(CavesLevel.class, "wall_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CavesLevel.class, "bookshelf_desc");
			case Terrain.REGION_DECO:
			case Terrain.REGION_DECO_ALT:
				return Messages.get(CavesLevel.class, "region_deco_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals() {
		super.addVisuals();
		addCavesVisuals( this, visuals );
		return visuals;
	}

	public static void addCavesVisuals( Level level, Group group ) {
		addCavesVisuals(level, group, false);
	}
	
	public static void addCavesVisuals( Level level, Group group, boolean overHang ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Vein( i, overHang ) );
			}
		}
	}
	
	private static class Vein extends Group {
		
		private int pos;

		private boolean includeOverhang;
		
		private float delay;

		public Vein( int pos ) {
			this(pos, false);
		}

		public Vein( int pos, boolean includeOverhang ) {
			super();
			
			this.pos = pos;
			this.includeOverhang = includeOverhang;
			
			delay = Random.Float( 2 );
		}
		
		@Override
		public void update() {
			
			if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {
				
				super.update();

				if ((delay -= Game.elapsed) <= 0) {

					//pickaxe can remove the ore, should remove the sparkling too.
					if (Dungeon.level.map[pos] != Terrain.WALL_DECO){
						kill();
						return;
					}
					
					delay = Random.Float();

					PointF p = DungeonTilemap.tileToWorld( pos );
					if (includeOverhang && !DungeonTileSheet.wallStitcheable(Dungeon.level.map[pos-Dungeon.level.width()])){
						//also sparkles in the bottom 1/2 of the upper tile. Increases particle frequency by 50% accordingly.
						delay *= 0.67f;
						p.y -= DungeonTilemap.SIZE/2f;
						((Sparkle)recycle( Sparkle.class )).reset(
								p.x + Random.Float( DungeonTilemap.SIZE ),
								p.y + Random.Float( DungeonTilemap.SIZE*1.5f ) );
					} else {
						((Sparkle)recycle( Sparkle.class )).reset(
								p.x + Random.Float( DungeonTilemap.SIZE ),
								p.y + Random.Float( DungeonTilemap.SIZE ) );
					}
				}
			}
		}
	}
	
	public static final class Sparkle extends PixelParticle {
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			left = lifespan = 0.5f;
		}
		
		@Override
		public void update() {
			super.update();
			
			float p = left / lifespan;
			size( (am = p < 0.5f ? p * 2 : (1 - p) * 2) * 2 );
		}
	}
}
