/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.Tinkerer1;
import pd.levels.traps.bufftrap.FireBuffTrap;
import pd.messages.Messages;
import pd.tiles.custom.SpsLegacyLevelVisual;

/** Unused SPS-PD 0.9.8 start-map class, retained for source and save compatibility. */
public class StartLevel extends Level {

	public static final int WIDTH = 48;
	public static final int HEIGHT = 48;
	public static final int ENTRANCE = 3 + WIDTH * 3;
	public static final int LEGACY_EXIT = WIDTH * 47;

	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		map = LearnRoomLayouts.START_ROOM.clone();
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_TOWN, width(), height(), map));
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.SECRET_TRAP) GroundItems.setTrap( this, new FireBuffTrap().hide(), cell);
		}
		return map.length == length();
	}

	@Override
	protected void createMobs() {
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.GROUND_A) {
				Tinkerer1 tinkerer = new Tinkerer1();
				tinkerer.pos = cell;
				mobs().add(tinkerer);
			}
		}
	}

	@Override protected void createItems() { }
	@Override public int entrance() { return ENTRANCE; }
	@Override public int exit() { return LEGACY_EXIT; }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }
	@Override public String tilesTex() { return Assets.Environment.TILES_CITY; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_SEWERS; }

	@Override
	public String tileName(int tile) {
		if (tile == Terrain.WATER) return Messages.get(PrisonLevel.class, "water_name");
		return super.tileName(tile);
	}

	@Override
	public String tileDesc(int tile) {
		if (tile == Terrain.EMPTY_DECO) return Messages.get(PrisonLevel.class, "empty_deco_desc");
		if (tile == Terrain.BOOKSHELF) return Messages.get(PrisonLevel.class, "bookshelf_desc");
		return super.tileDesc(tile);
	}
}
