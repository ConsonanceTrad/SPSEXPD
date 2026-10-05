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
import pd.Challenges;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SacrificialFire;
import pd.actors.blobs.SmokeScreen;
import pd.actors.blobs.Web;
import pd.actors.blobs.WellWater;
import pd.actors.buffs.Awareness;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.ChampionEnemy;
import pd.actors.buffs.Dewcharge;
import pd.actors.buffs.LockedFloor;
import pd.actors.buffs.MagicalSight;
import pd.actors.buffs.MindVision;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.PinCushion;
import pd.actors.buffs.Regeneration;
import pd.actors.buffs.RevealedArea;
import pd.actors.buffs.Shadows;
import pd.actors.buffs.TentSleep;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.actors.hero.abilities.huntress.SpiritHawk;
import pd.actors.hero.spells.DivineSense;
import pd.actors.hero.spells.Stasis;
import pd.actors.mobs.GnollGeomancer;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Mob;
import pd.actors.mobs.MobSpawner;
import pd.actors.mobs.Piranha;
import pd.actors.mobs.YogFist;
import pd.actors.mobs.npcs.Blacksmith;
import pd.actors.mobs.npcs.Sheep;
import pd.effects.CellEmitter;
import pd.effects.TargetedCell;
import pd.effects.particles.FlowParticle;
import pd.effects.particles.SacrificialParticle;
import pd.effects.particles.WindParticle;
import pd.items.Dewdrop;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.StrBottle;
import pd.items.Stylus;
import pd.items.Torch;
import pd.items.Weightstone;
import pd.items.equipment.artifacts.TalismanOfForesight;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.items.equipment.bombs.Bomb;
import pd.items.misc.LuckyBadge;
import pd.items.consum.potions.PotionOfStrength;
import pd.items.consum.scrolls.ScrollOfMagicalInfusion;
import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.items.consum.scrolls.exotic.ScrollOfChallenge;
import pd.items.consum.stones.StoneOfEnchantment;
import pd.items.consum.stones.StoneOfIntuition;
import pd.items.equipment.trinkets.DimensionalSundial;
import pd.items.equipment.trinkets.EyeOfNewt;
import pd.items.equipment.trinkets.MossyClump;
import pd.items.equipment.trinkets.TrapMechanism;
import pd.items.equipment.trinkets.TrinketCatalyst;
import pd.items.equipment.wands.WandOfRegrowth;
import pd.items.equipment.wands.WandOfWarding;
import pd.items.equipment.weapon.missiles.HeavyBoomerang;
import pd.levels.Transitions;
import pd.levels.features.Chasm;
import pd.levels.features.DewBlessRoom;
import pd.levels.features.Door;
import pd.levels.features.HighGrass;
import pd.levels.features.LevelTransition;
import pd.levels.features.OldHighGrass;
import pd.levels.mobs.LevelMobs;
import pd.levels.painters.Painter;
import pd.levels.traps.Trap;
import pd.mechanics.ShadowCaster;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.plants.Swiftthistle;
import pd.scenes.GameScene;
import pd.scenes.InterlevelScene;
import pd.sprites.ItemSprite;
import pd.tiles.CustomTilemap;
import pd.tiles.custom.SpsFeatureVisual;
import pd.tiles.custom.SpsLegacyLevelVisual;
import pd.utils.GLog;
import pd.windows.WndMessage;
import render.noosa.Game;
import render.noosa.Group;
import render.noosa.audio.Sample;
import render.utils.data.BArray;
import render.utils.data.Callback;
import render.utils.data.SparseArray;
import render.utils.geom.Point;
import render.utils.math.GameMath;
import render.utils.math.Random;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import pd.messages.InlineText;

public abstract class Level implements Bundlable {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(Level.class)
			.t("hidden_trap", "一个隐藏的%s被触发了！")
			.t("chasm_name", "深渊")
			.t("floor_name", "地板")
			.t("grass_name", "草")
			.t("water_name", "水潭")
			.t("wall_name", "墙壁")
			.t("closed_door_name", "关着的门")
			.t("open_door_name", "开着的门")
			.t("entrace_name", "楼层入口")
			.t("exit_name", "楼层出口")
			.t("embers_name", "余烬")
			.t("furrowed_grass_name", "枯萎的植被")
			.t("locked_door_name", "锁着的门")
			.t("crystal_door_name", "水晶之门")
			.t("pedestal_name", "基座")
			.t("dew_bless_name", "露珠神像")
			.t("tent_name", "休息帐篷")
			.t("iron_maker_name", "铁砧")
			.t("sign_name", "告示牌")
			.t("wool_rug_name", "白色碎屑")
			.t("fleecing_trap_name", "破坏陷阱")
			.t("change_sheep_trap_name", "转换陷阱")
			.t("sokoban_item_reveal_name", "奖励开关")
			.t("sokoban_port_switch_name", "传送开关")
			.t("port_well_name", "单向传送门")
			.t("barricade_name", "路障")
			.t("high_grass_name", "高草")
			.t("locked_exit_name", "锁着的楼层出口")
			.t("unlocked_exit_name", "开放的楼层出口")
			.t("well_name", "井")
			.t("empty_well_name", "干涸的井")
			.t("statue_name", "雕像")
			.t("inactive_trap_name", "已触发的陷阱")
			.t("bookshelf_name", "书架")
			.t("alchemy_name", "炼金釜")
			.t("default_name", "???")
			.t("chasm_desc", "你看不到底。")
			.t("water_desc", "进入水潭中可熄灭身上的火焰。")
			.t("entrance_desc", "通向上一层的楼梯。")
			.t("exit_desc", "通向下一层的楼梯。")
			.t("embers_desc", "地上散落着余烬。")
			.t("high_grass_desc", "茂密的植被遮挡了你的视线。")
			.t("locked_door_desc", "这扇门锁着，而你需要匹配的钥匙才能打开它。")
			.t("crystal_door_desc", "这扇锁着的门由魔力水晶铸造而成。你的视线可以穿过它，但你的身体不能，还是找找对应的水晶钥匙吧。")
			.t("locked_exit_desc", "沉重的铁栏杆封锁住了通往下一层的楼梯。")
			.t("barricade_desc", "木栅栏依然坚固，但早已风干多年。或许可以点燃烧掉？")
			.t("dew_bless_desc", "作为自然派系的神明之一，露珠女神很乐意为冒险者提供露珠相关的帮助。")
			.t("tent_desc", "一个安静的休息处。投入食物后，你会休息与其饱食值相同的时间。")
			.t("iron_maker_desc", "这座铁砧已经冷却废弃了，它的锻造公式都转移到了炼金釜。")
			.t("sign_desc", "你从这里看不清楚上面写的是什么。")
			.t("wool_rug_desc", "这东西被摧毁到只剩下一些碎屑了。")
			.t("fleecing_trap_desc", "这个陷阱破坏能力很强，推个箱子进去试试。")
			.t("change_sheep_trap_desc", "这个陷阱能完全转换一个箱子。注意，该过程不可逆！")
			.t("sokoban_item_reveal_desc", "用箱子压一下就会有东西掉出来，但是你没法知道那东西掉在哪里。")
			.t("sokoban_port_switch_desc", "只有触发这个开关传送门才会启动。但是这个开关能用几次？它又对应着哪个传送门？")
			.t("port_well_desc", "只能传送到一个地方的传送门。")
			.t("inactive_trap_desc", "这个陷阱已经被触发过了，现在一点也不危险。")
			.t("statue_desc", "有人想装饰一下这里，很显然，它没能成功...")
			.t("alchemy_desc", "这口炼金釜中充满了魔力之水。物品可以在里面共炼以制造新的物品。")
			.t("empty_well_desc", "这座井已经枯竭了。")
			.t("$feeling.chasm_title", "深渊层")
			.t("$feeling.water_title", "流水层")
			.t("$feeling.grass_title", "草木层")
			.t("$feeling.dark_title", "黑暗层")
			.t("$feeling.large_title", "广阔层")
			.t("$feeling.traps_title", "陷阱层")
			.t("$feeling.secrets_title", "隐秘层")
			.t("$feeling.trap_title", "陷阱层")
			.t("$feeling.special_floor_title", "琉璃层")
			.t("$feeling.chasm_desc", "你的脚步声在地牢中回荡不绝。")
			.t("$feeling.water_desc", "你听到周围水花四处飞溅的声音。")
			.t("$feeling.grass_desc", "浓郁的草木气息弥漫在空气中。")
			.t("$feeling.dark_desc", "你能听到敌人在暗中潜伏的声音。")
			.t("$feeling.large_desc", "这层地牢的规模似乎不同寻常的广阔。")
			.t("$feeling.traps_desc", "这层地牢的地面陷阱密布，暗藏杀机。")
			.t("$feeling.secrets_desc", "这层地牢的气氛暗示着潜藏其中的秘密。")
			.t("$feeling.trap_desc", "脚下的地面本身就显得危险而不稳定。")
			.t("$feeling.special_floor_desc", "四周的墙壁变成了诡异的琉璃。");
	}



	
	public static enum Feeling {
		NONE,
		CHASM,
		WATER,
		GRASS,
		DARK,
		LARGE,
		TRAPS,
		SECRETS,
		TRAP,
		SPECIAL_FLOOR;

		public String title(){
			return Messages.get(this, name()+"_title");
		}

		public String desc() {
			return Messages.get(this, name()+"_desc");
		}
	}

	protected int width;
	protected int height;
	protected int length;
	
	protected static final float TIME_TO_RESPAWN	= 50;

	public int version;
	
	public int[] map;
	public boolean[] visited;
	public boolean[] mapped;
	public boolean[] discoverable;

	public int viewDistance = Dungeon.isChallenged( Challenges.DARKNESS ) ? 2 : 8;
	
	public boolean[] heroFOV;
	
	public boolean[] passable;
	public boolean[] losBlocking;
	public boolean[] flamable;
	public boolean[] secret;
	public boolean[] solid;
	public boolean[] avoid;
	public boolean[] water;
	public boolean[] pit;

	public boolean[] openSpace;
	
	public Feeling feeling = Feeling.NONE;
	
	public int entrance;
	public int exit;

	public ArrayList<LevelTransition> transitions;

	//when a boss level has become locked.
	public boolean locked = false;

	// SPS dew-route progress belongs to the floor, not the global run.
	public int currentMoves = 0;
	public boolean cleared = false;
	public boolean forceDone = false;
	public int pitSign = -1;
	
	//怪物集合与驻留机制见 pd.levels.mobs.LevelMobs；本类只保留各关卡不同的刷怪钩子
	private final LevelMobs levelMobs = new LevelMobs( this );
	public SparseArray<Heap> heaps;
	public HashMap<Class<? extends Blob>,Blob> blobs;
	public SparseArray<Plant> plants;
	public SparseArray<Trap> traps;
	public ArrayList<CustomTilemap> customTiles;
	public ArrayList<CustomTilemap> customTerrain;
	public ArrayList<CustomTilemap> customWalls;
	
	protected ArrayList<Item> itemsToSpawn = new ArrayList<>();

	protected Group visuals;
	protected Group wallVisuals;
	
	public int color1 = 0x004400;
	public int color2 = 0x88CC44;


	public void create() {

		LevelGeneration.generate( this );
	}
	
	public void setSize(int w, int h){
		
		width = w;
		height = h;
		length = w * h;
		
		map = new int[length];
		Arrays.fill(map, feeling == Level.Feeling.CHASM || feeling == Level.Feeling.TRAP
				? Terrain.CHASM : Terrain.WALL);
		
		visited     = new boolean[length];
		mapped      = new boolean[length];
		
		heroFOV     = new boolean[length];
		
		passable	= new boolean[length];
		losBlocking	= new boolean[length];
		flamable	= new boolean[length];
		secret		= new boolean[length];
		solid		= new boolean[length];
		avoid		= new boolean[length];
		water		= new boolean[length];
		pit			= new boolean[length];

		openSpace   = new boolean[length];
		
		PathFinder.setMapSize(w, h);
	}
	
	public void reset() {
		
		for (Mob mob : mobs().toArray( new Mob[0] )) {
			if (!mob.reset()) {
				mobs().remove( mob );
			}
		}
		createMobs();
	}

	public void playLevelMusic(){
		//do nothing by default
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		LevelPersistence.restore( this, bundle );
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		LevelPersistence.store( this, bundle );
	}
	
	public int tunnelTile() {
		return feeling == Feeling.CHASM ? Terrain.EMPTY_SP : Terrain.EMPTY;
	}

	public int width() {
		return width;
	}

	//在场怪物的集合与驻留机制
	public LevelMobs mobs() {
		return levelMobs;
	}

	//各关卡可覆写驻留者的创建（21 个子类覆写）；默认交由 LevelMobs 驱动
	public Actor addRespawner() {
		return mobs().addRespawner();
	}

	//各关卡可覆写刷怪节流（MiningLevel 等会放大）；默认由 LevelMobs 按在场权重计算
	public float respawnCooldown() {
		return mobs().respawnCooldown();
	}

	//各关卡可覆写补充怪物的方式（SpsRegionChallengeLevel 等会改写落点规则）
	public boolean spawnMob(int disLimit) {
		return spawnMob( disLimit );
	}

	//各关卡可覆写的刷怪节流基准（MiningLevel 等会放大它）
	public static float timeToRespawn() {
		return TIME_TO_RESPAWN;
	}

	public int height() {
		return height;
	}

	public int length() {
		return length;
	}
	
	public String tilesTex() {
		return null;
	}
	
	public String waterTex() {
		return null;
	}
	
	abstract protected boolean build();
	
	//刷怪队列与轮换在 LevelMobs；这里保留可覆写入口，默认走轮换队列
	public Mob createMob() {
		return mobs().createMob();
	}

	abstract protected void createMobs();

	abstract protected void createItems();

	public int entrance(){
		LevelTransition l = Transitions.get( this, null);
		if (l != null){
			return l.cell();
		}
		return 0;
	}

	public int exit(){
		LevelTransition l = Transitions.get( this, LevelTransition.Type.REGULAR_EXIT);
		if (l != null){
			return l.cell();
		}
		return 0;
	}


	//returns true if we immediately transition, false otherwise
	public boolean activateTransition(Hero hero, LevelTransition transition){
		if (locked){
			return false;
		}
		//SPS: 0 层向上会进入负数层，禁止楼层移动（弹 SPS 弹窗文案并停住）
		//注意：WndMessage 构造会测量文字，必须切回渲染线程（actor 线程直接 new 会崩）
		if (transition.destDepth < 0) {
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show( new WndMessage( Messages.get(hero, "leave") ) );
				}
			});
			return false;
		}

		//SPS: 露珠清层——直接下楼即标记本层已清（已按用户裁决移除下楼前的露珠状态确认框）
		if (transition.type == LevelTransition.Type.REGULAR_EXIT
				&& SpsDew.isClearable( this ) && !cleared) {
			cleared = true;
		}

		Transitions.beforeTransition();
		InterlevelScene.curTransition = transition;
		if (transition.type == LevelTransition.Type.REGULAR_EXIT
				|| transition.type == LevelTransition.Type.BRANCH_EXIT) {
			InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
		} else {
			InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
		}
		Game.switchScene(InterlevelScene.class);
		return true;
	}

	//some buff effects have special logic or are cancelled from the hero before transitioning levels

	public void seal(){
		if (!locked) {
			locked = true;
			Buff.affect(Dungeon.hero, LockedFloor.class);
		}
	}

	public void unseal(){
		if (locked) {
			locked = false;
			if (Dungeon.hero.buff(LockedFloor.class) != null){
				Dungeon.hero.buff(LockedFloor.class).detach();
			}
		}
	}

	public ArrayList<Item> getItemsToPreserveFromSealedResurrect(){
		ArrayList<Item> items = new ArrayList<>();
		for (Heap h : heaps.valueList()){
			if (h.type == Heap.Type.HEAP) {
				for (Item i : h.items){
					if (i instanceof Bomb){
						((Bomb) i).fuse = null;
					}
					items.add(i);
				}
			}
		}
		for (Mob m : mobs()){
			for (PinCushion b : m.buffs(PinCushion.class)){
				items.addAll(b.getStuckItems());
			}
		}
		for (HeavyBoomerang.CircleBack b : Dungeon.hero.buffs(HeavyBoomerang.CircleBack.class)){
			if (b.activeDepth() == Dungeon.depth) items.add(b.cancel());
		}
		return items;
	}

	public Group addVisuals() {
		if (visuals == null || visuals.parent == null){
			visuals = new Group();
		} else {
			visuals.clear();
			visuals.camera = null;
		}
		for (int i=0; i < length(); i++) {
			if (pit[i]) {
				visuals.add( new WindParticle.Wind( i ) );
				if (i >= width() && water[i-width()]) {
					visuals.add( new FlowParticle.Flow( i - width() ) );
				}
			}
		}
		return visuals;
	}

	//for visual effects that should render above wall overhang tiles
	public Group addWallVisuals(){
		if (wallVisuals == null || wallVisuals.parent == null){
			wallVisuals = new Group();
		} else {
			wallVisuals.clear();
			wallVisuals.camera = null;
		}
		return wallVisuals;
	}

	
	public int mobLimit() {
		return 0;
	}


	protected void markSpsOriginalMobs() {
		if (Dungeon.branch != 0 || Dungeon.depth <= 1 || Dungeon.depth >= 25 || Dungeon.bossLevel()) {
			return;
		}
		boolean dewMode = Dungeon.dewDraw || Dungeon.dewWater;
		for (Mob mob : mobs()) {
			if (mob.alignment == Char.Alignment.ENEMY) {
				mob.spsOriginalGeneration = true;
				//SPSEXPD: 本层初始怪物自带「露珠爆破」，死亡时爆出露珠；后续刷出的怪物不再带
				if (dewMode && mob.buff(Dewcharge.class) == null) {
					Buff.affect(mob, Dewcharge.class, Dewcharge.DURATION);
				}
			}
		}
	}

	public int randomRespawnCell( Char ch ) {
		int cell;
		int count = 0;
		do {

			if (++count > 30) {
				return -1;
			}

			cell = Random.Int( length() );

		} while ((Dungeon.level == this && heroFOV[cell])
				|| !passable[cell]
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
				|| Actor.findChar( cell ) != null);
		return cell;
	}
	
	public int randomDestination( Char ch ) {
		int cell;
		do {
			cell = Random.Int( length() );
		} while (!passable[cell]
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell]));
		return cell;
	}
	

	public void buildFlagMaps() {
		CellFlags.build( this );
	}



	public void destroy( int pos ) {
		CellFlags.destroy( this, pos );
	}
	
	public static void set( int cell, int terrain ){
		set( cell, terrain, Dungeon.level );
	}
	
	public static void set( int cell, int terrain, Level level ) {
		Painter.set(level, cell, terrain);
		if (level.customTiles != null) {
			for (CustomTilemap visual : level.customTiles) {
				if (visual instanceof SpsLegacyLevelVisual) {
					((SpsLegacyLevelVisual) visual).updateTerrainCell(cell, terrain);
				}
			}
		}

		if (terrain != Terrain.TRAP && terrain != Terrain.SECRET_TRAP && terrain != Terrain.INACTIVE_TRAP) {
			level.traps.remove(cell);
		}

		level.updateCellFlags(cell);
	}

	public void updateCellFlags( int cell ){
		CellFlags.updateCellFlags( this, cell );
	}
	
	public Heap drop( Item item, int cell ) {

		if (item == null || Challenges.isItemBlocked(item)){

			//create a dummy heap, give it a dummy sprite, don't add it to the game, and return it.
			//effectively nullifies whatever the logic calling this wants to do, including dropping items.
			Heap heap = new Heap();
			ItemSprite sprite = heap.sprite = new ItemSprite();
			sprite.link(heap);
			return heap;

		}
		
		Heap heap = heaps.get( cell );
		if (heap == null) {
			
			heap = new Heap();
			heap.seen = Dungeon.level == this && heroFOV[cell];
			heap.pos = cell;
			heap.drop(item);
			if (map[cell] == Terrain.CHASM || (Dungeon.level != null && pit[cell])) {
				Dungeon.dropToChasm( item );
				GameScene.discard( heap );
			} else {
				heaps.put( cell, heap );
				GameScene.add( heap );
			}
			
		} else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {
			
			int n;
			do {
				n = cell + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			} while (!passable[n] && !avoid[n]);
			return drop( item, n );
			
		} else {
			heap.drop(item);
		}
		
		if (Dungeon.level != null && Game.instance != null
				&& ShatteredPixelDungeon.scene() instanceof GameScene) {
			pressCell( cell );
		}
		
		return heap;
	}
	


	public boolean setCellToWater( boolean includeTraps, int cell ){
		return CellFlags.setCellToWater( this, includeTraps, cell );
	}
	
	public int fallCell( boolean fallIntoPit ) {
		int result;
		do {
			result = randomRespawnCell( null );
			if (result == -1) return -1;
		} while (traps.get(result) != null
				|| mobs().findMob(result) != null);
		return result;
	}
	
	public void occupyCell( Char ch ){
		CellTriggers.occupy( this, ch );
	}
	
	//public method for forcing the hard press of a cell. e.g. when an item lands on it
	public void pressCell( int cell ){
		CellTriggers.press( this, cell, true );
	}
	
	//a 'soft' press ignores hidden traps
	//a 'hard' press triggers all things


	public float levelExplorePercent( int depth ){
		return 0;
	}
	
	public int distance( int a, int b ) {
		int ax = a % width();
		int ay = a / width();
		int bx = b % width();
		int by = b / width();
		return Math.max( Math.abs( ax - bx ), Math.abs( ay - by ) );
	}
	
	public boolean adjacent( int a, int b ) {
		return distance( a, b ) == 1;
	}
	
	//uses pythagorean theorum for true distance, as if there was no movement grid
	public float trueDistance(int a, int b){
		int ax = a % width();
		int ay = a / width();
		int bx = b % width();
		int by = b / width();
		return (float)Math.sqrt(Math.pow(Math.abs( ax - bx ), 2) + Math.pow(Math.abs( ay - by ), 2));
	}

	//usually just if the base terrain of a cell is solid, but other cases exist too
	//only check on base terrain, we want to ignore temporary changes from blobs (e.g. light wall)
	public boolean invalidHeroPos( int tile ){
		int flags = Terrain.flags[map[tile]];
		return (flags & Terrain.PASSABLE) != 0 && (flags & Terrain.AVOID) != 0;
	}

	//returns true if the input is a valid tile within the level
	public boolean insideMap( int tile ){
				//top and bottom row and beyond
		return !((tile < width || tile >= length - width) ||
				//left and right column
				(tile % width == 0 || tile % width == width-1));
	}

	public Point cellToPoint( int cell ){
		return new Point(cell % width(), cell / width());
	}

	public int pointToCell( Point p ){
		return p.x + p.y*width();
	}
	
	public String tileName( int tile ) {
		return TileTexts.tileName( tile );
	}
	
	public String tileDesc( int tile ) {
		return TileTexts.tileDesc( tile );
	}
}
