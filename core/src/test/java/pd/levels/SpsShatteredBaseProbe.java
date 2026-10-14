package pd.levels;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.Heap;
import pd.items.consum.potions.Potion;
import pd.items.consum.scrolls.Scroll;
import pd.items.equipment.rings.Ring;
import pd.levels.painters.Painter;
import pd.levels.painters.SewerPainter;
import pd.levels.rooms.Room;
import pd.levels.rooms.standard.StandardRoom;
import pd.levels.rooms.standard.entrance.EntranceRoom;
import pd.levels.rooms.standard.exit.ExitRoom;
import pd.plants.Plant;
import render.noosa.Game;
import render.utils.data.SparseArray;
import render.utils.math.Random;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SPSEXPD 诊断用：用【破碎原生生成机制】(RegularLevel.build + builder + RegularPainter + 破碎房间池)
 * 生成普通层并导出地形 PNG，看破碎基底长什么样。临时工具，用完即删。
 */
public final class SpsShatteredBaseProbe {

	private static final int MAPS = 10;
	private static final int SCALE = 6;

	public static void main(String[] args) throws Exception {
		try {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(
				new ApplicationAdapter() { @Override public void create() { } },
				new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Dungeon.hero = new Hero();
		Scroll.initLabels();
		Potion.initColors();
		Ring.initGems();

		File out = new File(System.getProperty("previewDir",
				System.getProperty("java.io.tmpdir") + File.separator + "sps-shattered-base"));
		out.mkdirs();

		int ok = 0, failed = 0;
		for (int k = 0; k < MAPS; k++) {
			Dungeon.depth = 2;
			Dungeon.branch = 0;
			Random.pushGenerator(0x5348415454455244L + k);
			try {
				BaseLevel level = new BaseLevel();
				boolean built = false;
				for (int attempt = 0; attempt < 50 && !built; attempt++) {
					prepare(level);
					built = level.build();
				}
				if (!built) { failed++; System.out.println("build FAILED k=" + k); continue; }
				level.buildFlagMaps();
				BufferedImage img = render(level);
				File f = new File(out, String.format("base_%02d.png", k));
				ImageIO.write(img, "png", f);
				System.out.println("[" + k + "] " + f.getAbsolutePath()
						+ "  size=" + level.width() + "x" + level.height()
						+ "  rooms=" + level.rooms().size());
				ok++;
			} finally {
				Random.popGenerator();
			}
		}
		System.out.println("summary: built=" + ok + " failed=" + failed + " -> " + out.getAbsolutePath());
		app.exit();
		System.exit(0);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/** 用破碎原生生成机制的正常层（照 SewerLevel 的破碎钩子）。 */
	static final class BaseLevel extends RegularLevel {
		@Override
		protected Painter painter() {
			return new SewerPainter()
					.setWater(0.30f, 5)
					.setGrass(0.20f, 4)
					.setTraps(3, new Class<?>[]{pd.levels.traps.WornDartTrap.class}, new float[]{1});
		}
		@Override protected int standardRooms(boolean forceMax) {
			return forceMax ? 7 : 5 + Random.chances(new float[]{1, 3, 1});
		}
		@Override protected int specialRooms(boolean forceMax) {
			return forceMax ? 2 : 1 + Random.chances(new float[]{1, 4});
		}
		// 纯基底：只放标准房间（仓库里破碎特殊/秘室房间池已被移植掏空）
		@Override
		protected ArrayList<Room> initRooms() {
			ArrayList<Room> initRooms = new ArrayList<>();
			initRooms.add(roomEntrance = EntranceRoom.createEntrance());
			initRooms.add(roomExit = ExitRoom.createExit());
			int standards = standardRooms(feeling == Feeling.LARGE);
			if (feeling == Feeling.LARGE) {
				standards = (int) Math.ceil(standards * 1.5f);
			}
			for (int i = 0; i < standards; i++) {
				StandardRoom s;
				do {
					s = StandardRoom.createRoom();
				} while (!s.setSizeCat(standards - i));
				i += s.sizeFactor() - 1;
				initRooms.add(s);
			}
			return initRooms;
		}
		@Override public String tilesTex() { return null; }
		@Override public String waterTex() { return null; }
	}

	private static void prepare(Level level) {
		level.transitions = new ArrayList<>();
		level.mobs().clear();
		level.heaps = new SparseArray<>();
		level.blobs = new HashMap<>();
		level.plants = new SparseArray<>();
		level.traps = new SparseArray<>();
		level.customTiles = new ArrayList<>();
		level.customTerrain = new ArrayList<>();
		level.customWalls = new ArrayList<>();
	}

	private static BufferedImage render(Level level) {
		int w = level.width();
		int h = level.height();
		BufferedImage img = new BufferedImage(w * SCALE, h * SCALE, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < level.length(); i++) {
			int color = colorOf(level.map[i]);
			int x = (i % w) * SCALE;
			int y = (i / w) * SCALE;
			for (int dy = 0; dy < SCALE; dy++) {
				for (int dx = 0; dx < SCALE; dx++) {
					img.setRGB(x + dx, y + dy, color);
				}
			}
		}
		mark(img, level.entrance(), 0x00FF00, w);
		mark(img, level.exit(), 0xFF0000, w);
		return img;
	}

	private static void mark(BufferedImage img, int cell, int color, int w) {
		if (cell < 0) return;
		int x = (cell % w) * SCALE;
		int y = (cell / w) * SCALE;
		for (int dy = 0; dy < SCALE; dy++) {
			for (int dx = 0; dx < SCALE; dx++) {
				img.setRGB(x + dx, y + dy, color);
			}
		}
	}

	private static int colorOf(int t) {
		if (t == Terrain.ENTRANCE) return 0x00B000;
		if (t == Terrain.EXIT) return 0xC00000;
		if (t == Terrain.DOOR || t == Terrain.OPEN_DOOR) return 0x2040FF;
		if (t == Terrain.SECRET_DOOR) return 0x208080;
		if (t == Terrain.LOCKED_DOOR || t == Terrain.CRYSTAL_DOOR || t == Terrain.HERO_LKD_DR
				|| t == Terrain.BROKEN_DOOR || t == Terrain.BARRICADE || t == Terrain.BOOKSHELF) return 0xA020A0;
		if (t == Terrain.WATER) return 0x2050A0;
		if (t == Terrain.CHASM) return 0x080810;
		if (t == Terrain.TRAP || t == Terrain.SECRET_TRAP || t == Terrain.INACTIVE_TRAP) return 0xD00000;
		if (t == Terrain.HIGH_GRASS || t == Terrain.GRASS || t == Terrain.FURROWED_GRASS
				|| t == Terrain.OLD_HIGH_GRASS) return 0x30A030;
		int fl = Terrain.flags[t];
		if ((fl & Terrain.SOLID) != 0) return 0x181818;
		if (t == Terrain.EMPTY_SP) return 0xB0B0B0;
		return 0xE8E8E8;
	}

	private SpsShatteredBaseProbe() { }
}
