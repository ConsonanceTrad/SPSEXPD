/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.mobs.TestMob;
import pd.actors.mobs.npcs.Leadercn;
import pd.items.Gold;
import pd.items.Heap;
import pd.items.KnowledgeBook;
import pd.items.VioletDewdrop;
import pd.items.armor.specialarmor.TestArmor;
import pd.items.food.SmallMeat;
import pd.items.keys.IronKey;
import pd.items.potions.PotionOfMending;
import pd.items.potions.PotionOfMindVision;
import pd.items.scrolls.ScrollOfMagicMapping;
import pd.items.weapon.melee.special.TestWeapon;
import pd.levels.traps.bufftrap.FireBuffTrap;
import pd.messages.Messages;
import pd.tiles.custom.SpsLegacyLevelVisual;

/** The title-screen tutorial from SPS-PD 0.9.8. */
public class LearnLevel extends Level {

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
		map = LearnRoomLayouts.LEARN_ROOM.clone();
		customTiles.add(SpsLegacyLevelVisual.fromTerrainMap(
				Assets.Environment.SPS_TILES_PUZZLE, width(), height(), map));
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.SECRET_TRAP) GroundItems.setTrap( this, new FireBuffTrap().hide(), cell);
		}
		return map.length == length();
	}

	@Override
	protected void createMobs() {
		for (int cell = 0; cell < length(); cell++) {
			if (map[cell] == Terrain.GROUND_A) {
				Leadercn guide = new Leadercn();
				guide.pos = cell;
				mobs().add(guide);
			} else if (map[cell] == Terrain.EMPTY_SP) {
				TestMob mob = new TestMob();
				mob.pos = cell;
				mob.HP = 100;
				mobs().add(mob);
			}
		}
	}

	@Override
	protected void createItems() {
		drop(new TestWeapon().identify(), 15 + WIDTH * 2).type = Heap.Type.HEAP;
		drop(new TestArmor().identify(), 16 + WIDTH * 2).type = Heap.Type.HEAP;
		drop(new PotionOfMending(), 40 + WIDTH * 3).type = Heap.Type.CHEST;
		drop(new IronKey(Dungeon.depth).identify(), 41 + WIDTH * 3).type = Heap.Type.E_DUST;
		drop(new VioletDewdrop().identify(), 42 + WIDTH * 4).type = Heap.Type.E_DUST;
		drop(new Gold(1000), 42 + WIDTH * 5).type = Heap.Type.CHEST;

		drop(new SmallMeat(), 37 + WIDTH * 35).type = Heap.Type.M_WEB;
		drop(new SmallMeat(), 38 + WIDTH * 34).type = Heap.Type.M_WEB;
		drop(new SmallMeat(), 38 + WIDTH * 36).type = Heap.Type.M_WEB;
		drop(new SmallMeat(), 39 + WIDTH * 35).type = Heap.Type.M_WEB;

		drop(new ScrollOfMagicMapping(), 14 + WIDTH * 18).type = Heap.Type.FOR_SALE;
		drop(new PotionOfMindVision(), 15 + WIDTH * 18).type = Heap.Type.FOR_LIFE;
		drop(new KnowledgeBook(), 7 + WIDTH * 39).type = Heap.Type.HEAP;
	}

	@Override public int entrance() { return ENTRANCE; }
	@Override public int exit() { return LEGACY_EXIT; }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }
	@Override public String tilesTex() { return Assets.Environment.TILES_PRISON; }
	@Override public String waterTex() { return Assets.Environment.SPS_WATER_PRISON; }

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
