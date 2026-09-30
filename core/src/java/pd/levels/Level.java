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
import pd.items.artifacts.TalismanOfForesight;
import pd.items.artifacts.TimekeepersHourglass;
import pd.items.bombs.Bomb;
import pd.items.misc.LuckyBadge;
import pd.items.potions.PotionOfOverHealing;
import pd.items.potions.PotionOfStrength;
import pd.items.scrolls.ScrollOfMagicalInfusion;
import pd.items.scrolls.ScrollOfUpgrade;
import pd.items.scrolls.exotic.ScrollOfChallenge;
import pd.items.stones.StoneOfEnchantment;
import pd.items.stones.StoneOfIntuition;
import pd.items.trinkets.DimensionalSundial;
import pd.items.trinkets.EyeOfNewt;
import pd.items.trinkets.MossyClump;
import pd.items.trinkets.TrapMechanism;
import pd.items.trinkets.TrinketCatalyst;
import pd.items.wands.WandOfRegrowth;
import pd.items.wands.WandOfWarding;
import pd.items.weapon.missiles.HeavyBoomerang;
import pd.levels.features.Chasm;
import pd.levels.features.DewBlessRoom;
import pd.levels.features.Door;
import pd.levels.features.HighGrass;
import pd.levels.features.LevelTransition;
import pd.levels.features.OldHighGrass;
import pd.levels.painters.Painter;
import pd.levels.traps.Trap;
import pd.mechanics.ShadowCaster;
import pd.mechanics.pathfind.PathFinder;
import pd.levels.mobs.LevelMobs;
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

public abstract class Level implements Bundlable {
	
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

	private static final String VERSION     = "version";
	private static final String WIDTH       = "width";
	private static final String HEIGHT      = "height";
	private static final String MAP			= "map";
	private static final String VISITED		= "visited";
	private static final String MAPPED		= "mapped";
	private static final String TRANSITIONS	= "transitions";
	private static final String LOCKED      = "locked";
	private static final String HEAPS		= "heaps";
	private static final String PLANTS		= "plants";
	private static final String TRAPS       = "traps";
	private static final String CUSTOM_TILES= "customTiles";
	private static final String CUSTOM_TERRAIN= "customTerrain";
	private static final String CUSTOM_WALLS= "customWalls";
	private static final String BLOBS		= "blobs";
	private static final String FEELING		= "feeling";
	private static final String CURRENT_MOVES = "currentmoves";
	private static final String CLEARED		= "cleared";
	private static final String FORCE_DONE	= "forcedone";
	private static final String PIT_SIGN     = "pit_sign";

	public void create() {

		TargetedCell.cells.clear();
		Random.pushGenerator( Dungeon.seedCurDepth() );
		if (this instanceof SpsTriangleLevel) {
			((SpsTriangleLevel)this).prepareLegacyTrial();
		}

		//TODO maybe just make this part of RegularLevel?
		if (!Dungeon.bossLevel() && Dungeon.branch == 0) {

			if (this instanceof SpsRegularLevel) {
				// SPS-PD queued these supplies on every ordinary floor before painting it.
				addItemToSpawn(Generator.random(Generator.Category.FOOD));
				addItemToSpawn(Generator.random(Generator.Category.FOOD));
				addItemToSpawn(new ScrollOfUpgrade());
				if (Random.Int(2) == 0) {
					addItemToSpawn(new Stylus());
					addItemToSpawn(new Weightstone());
				}
				if (Dungeon.posNeeded() && !Dungeon.shopOnLevel()) {
					Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
					addItemToSpawn(new StrBottle());
				}
				if (Random.Float() < LuckyBadge.rareRewardChance(LuckyBadge.luckBonus(Dungeon.hero))) {
					addItemToSpawn(Random.Int(2) == 0
							? new ScrollOfMagicalInfusion()
							: new PotionOfOverHealing());
				}
			} else {
				addItemToSpawn(Generator.random(Generator.Category.FOOD));
				if (Random.Float() < LuckyBadge.rareRewardChance(LuckyBadge.luckBonus(Dungeon.hero))) {
					addItemToSpawn(Random.Int(2) == 0
							? new ScrollOfMagicalInfusion()
							: new PotionOfOverHealing());
				}

				if (Dungeon.posNeeded()) {
					Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
					addItemToSpawn( new PotionOfStrength() );
				}
				if (Dungeon.souNeeded()) {
					Dungeon.LimitedDrops.UPGRADE_SCROLLS.count++;
					//every 2nd scroll of upgrade is removed with forbidden runes challenge on
					if (!Dungeon.isChallenged(Challenges.NO_SCROLLS) || Dungeon.LimitedDrops.UPGRADE_SCROLLS.count%2 != 0){
						addItemToSpawn(new ScrollOfUpgrade());
					}
				}
				if (Dungeon.asNeeded()) {
					Dungeon.LimitedDrops.ARCANE_STYLI.count++;
					addItemToSpawn( new Stylus() );
				}
				if ( Dungeon.enchStoneNeeded() ){
					Dungeon.LimitedDrops.ENCH_STONE.drop();
					addItemToSpawn( new StoneOfEnchantment() );
				}
				if ( Dungeon.intStoneNeeded() ){
					Dungeon.LimitedDrops.INT_STONE.drop();
					addItemToSpawn( new StoneOfIntuition() );
				}
				if ( Dungeon.trinketCataNeeded() ){
					Dungeon.LimitedDrops.TRINKET_CATA.drop();
					addItemToSpawn( new TrinketCatalyst());
				}
			}
			
			if (this instanceof SpsRegularLevel && Dungeon.depth > 1 && Dungeon.depth < 25) {
				int roll = Random.Int(10);
				if (Dungeon.depth <= 20) {
					switch (roll) {
						case 0: feeling = Feeling.CHASM; break;
						case 1: feeling = Feeling.WATER; break;
						case 2: feeling = Feeling.GRASS; break;
						case 3:
							feeling = Feeling.DARK;
							addItemToSpawn(new Torch());
							addItemToSpawn(new Torch());
							addItemToSpawn(new Torch());
							viewDistance = (int)Math.ceil(viewDistance / 3f);
							break;
						case 4: feeling = Feeling.SPECIAL_FLOOR; break;
						default: feeling = Feeling.NONE; break;
					}
				} else {
					switch (roll) {
						case 0:
							feeling = Feeling.DARK;
							addItemToSpawn(new Torch());
							addItemToSpawn(new Torch());
							addItemToSpawn(new Torch());
							viewDistance = (int)Math.ceil(viewDistance / 3f);
							break;
						case 1: feeling = Feeling.WATER; break;
						case 2: feeling = Feeling.GRASS; break;
						case 3: feeling = Feeling.SPECIAL_FLOOR; break;
						default: feeling = Feeling.NONE; break;
					}
				}
			} else if (Dungeon.depth > 1) {
				//50% chance of getting a level feeling
				//~7.15% chance for each feeling
				switch (Random.Int( 14 )) {
					case 0:
						feeling = Feeling.CHASM;
						break;
					case 1:
						feeling = Feeling.WATER;
						break;
					case 2:
						feeling = Feeling.GRASS;
						break;
					case 3:
						feeling = Feeling.DARK;
						viewDistance = Math.round(5*viewDistance/8f);
						break;
					case 4:
						feeling = Feeling.LARGE;
						addItemToSpawn(Generator.random(Generator.Category.FOOD));
						break;
					case 5:
						feeling = Feeling.TRAPS;
						break;
					case 6:
						feeling = Feeling.SECRETS;
						break;
					default:
						//if-else statements are fine here as only one chance can be above 0 at a time
						// we pre-generate the floats to ensure Random is called consistently
						float mossyChance = Random.Float();
						float trapMechChance = Random.Float();
						if (mossyChance < MossyClump.overrideNormalLevelChance()){
							feeling = MossyClump.getNextFeeling();
						} else if (trapMechChance < TrapMechanism.overrideNormalLevelChance()) {
							feeling = TrapMechanism.getNextFeeling();
						} else {
							feeling = Feeling.NONE;
						}
				}
			}
		}
		
		int buildAttempts = 0;
		do {
			width = height = length = 0;

			transitions = new ArrayList<>();

			mobs().clear();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<>();
			traps = new SparseArray<>();
			customTiles = new ArrayList<>();
			customTerrain = new ArrayList<>();
			customWalls = new ArrayList<>();
			if (++buildAttempts > 100) {
				Random.popGenerator();
				throw new IllegalStateException("level generation failed after 100 attempts: "
						+ getClass().getName() + ", depth=" + Dungeon.depth);
			}
		} while (!build());

		placeSpsDewBless();
		
		buildFlagMaps();
		cleanWalls();
		
		createMobs();
		markSpsOriginalMobs();
		createItems();

		Random.popGenerator();
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

		version = bundle.getInt( VERSION );
		
		//saves from before v3.1.1 are not supported
		if (version < ShatteredPixelDungeon.v3_1_1){
			throw new RuntimeException("old save");
		}

		setSize( bundle.getInt(WIDTH), bundle.getInt(HEIGHT));
		
		mobs().clear();
		heaps = new SparseArray<>();
		blobs = new HashMap<>();
		plants = new SparseArray<>();
		traps = new SparseArray<>();
		customTiles = new ArrayList<>();
		customTerrain = new ArrayList<>();
		customWalls = new ArrayList<>();
		
		map		= bundle.getIntArray( MAP );

		visited	= bundle.getBooleanArray( VISITED );
		mapped	= bundle.getBooleanArray( MAPPED );

		transitions = new ArrayList<>();
		for (Bundlable b : bundle.getCollection( TRANSITIONS )){
			transitions.add((LevelTransition) b);
		}

		locked      = bundle.getBoolean( LOCKED );
		currentMoves = bundle.getInt( CURRENT_MOVES );
		cleared = bundle.getBoolean( CLEARED );
		forceDone = bundle.getBoolean( FORCE_DONE );
		pitSign = bundle.contains(PIT_SIGN) ? bundle.getInt(PIT_SIGN) : -1;
		
		Collection<Bundlable> collection = bundle.getCollection( HEAPS );
		for (Bundlable h : collection) {
			Heap heap = (Heap)h;
			if (!heap.isEmpty())
				heaps.put( heap.pos, heap );
		}
		
		collection = bundle.getCollection( PLANTS );
		for (Bundlable p : collection) {
			Plant plant = (Plant)p;
			plants.put( plant.pos, plant );
		}

		collection = bundle.getCollection( TRAPS );
		for (Bundlable p : collection) {
			Trap trap = (Trap)p;
			traps.put( trap.pos, trap );
		}

		collection = bundle.getCollection( CUSTOM_TILES );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			customTiles.add(vis);
		}

		collection = bundle.getCollection( CUSTOM_TERRAIN );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			customTerrain.add(vis);
		}

		collection = bundle.getCollection( CUSTOM_WALLS );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			customWalls.add(vis);
		}
		
		mobs().restoreFromBundle( bundle );

		feeling = bundle.getEnum( FEELING, Feeling.class );
		if (feeling == Feeling.DARK) {
			viewDistance = Math.round(5 * viewDistance / 8f);
		}
		TargetedCell.cells.clear();
		if (bundle.contains( "targeted_cells" )){
			collection = bundle.getCollection( "targeted_cells" );
			for (Bundlable c : collection) {
				TargetedCell cell = (TargetedCell)c;
				if (cell != null) {
					TargetedCell.cells.put(cell.pos, cell);
				}
			}
		}

		buildFlagMaps();
		cleanWalls();

	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( VERSION, Game.versionCode );
		bundle.put( WIDTH, width );
		bundle.put( HEIGHT, height );
		bundle.put( MAP, map );
		bundle.put( VISITED, visited );
		bundle.put( MAPPED, mapped );
		bundle.put( TRANSITIONS, transitions );
		bundle.put( LOCKED, locked );
		bundle.put( CURRENT_MOVES, currentMoves );
		bundle.put( CLEARED, cleared );
		bundle.put( FORCE_DONE, forceDone );
		bundle.put( PIT_SIGN, pitSign );
		bundle.put( HEAPS, heaps.valueList() );
		bundle.put( PLANTS, plants.valueList() );
		bundle.put( TRAPS, traps.valueList() );
		bundle.put( CUSTOM_TILES, customTiles );
		bundle.put( CUSTOM_TERRAIN, customTerrain);
		bundle.put( CUSTOM_WALLS, customWalls );
		mobs().storeInBundle( bundle );
		bundle.put( "targeted_cells", TargetedCell.cells.valueList() );
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
		LevelTransition l = getTransition(null);
		if (l != null){
			return l.cell();
		}
		return 0;
	}

	public int exit(){
		LevelTransition l = getTransition(LevelTransition.Type.REGULAR_EXIT);
		if (l != null){
			return l.cell();
		}
		return 0;
	}

	public LevelTransition getTransition(LevelTransition.Type type){
		if (transitions.isEmpty()){
			return null;
		}
		for (LevelTransition transition : transitions){
			//if we don't specify a type, prefer to return any entrance
			if (type == null &&
					(transition.type == LevelTransition.Type.REGULAR_ENTRANCE
							|| transition.type == LevelTransition.Type.BRANCH_ENTRANCE
							|| transition.type == LevelTransition.Type.SURFACE)){
				return transition;
			} else if (transition.type == type){
				return transition;
			}
		}
		return type != null ? getTransition(null) : transitions.get(0);
	}

	public LevelTransition getTransition(int cell){
		for (LevelTransition transition : transitions){
			if (transition.inside(cell)){
				return transition;
			}
		}
		return null;
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
				&& isSpsClearable() && !cleared) {
			cleared = true;
			Statistics.previousFloorMoves = 0;
		}

		beforeTransition();
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
	public static void beforeTransition(){

		//time freeze effects need to resolve their pressed cells before transitioning
		TimekeepersHourglass.timeFreeze timeFreeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
		if (timeFreeze != null) timeFreeze.disarmPresses();
		Swiftthistle.TimeBubble timeBubble = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
		if (timeBubble != null) timeBubble.disarmPresses();

		//iron stomach and challenge arena do not persist between floors
		Talent.WarriorFoodImmunity foodImmune = Dungeon.hero.buff(Talent.WarriorFoodImmunity.class);
		if (foodImmune != null) foodImmune.detach();
		ScrollOfChallenge.ChallengeArena arena = Dungeon.hero.buff(ScrollOfChallenge.ChallengeArena.class);
		if (arena != null) arena.detach();
		//awareness also doesn't, honestly it's weird that it's a buff
		Awareness awareness = Dungeon.hero.buff(Awareness.class);
		if (awareness != null) awareness.detach();

		Char ally = Stasis.getStasisAlly();
		if (Char.hasProp(ally, Char.Property.IMMOVABLE)){
			Dungeon.hero.buff(Stasis.StasisBuff.class).act();
			GLog.w(Messages.get(Stasis.StasisBuff.class, "left_behind"));
		}

		//spend the hero's partial turns,  so the hero cannot take partial turns between floors
		Dungeon.hero.spendToWhole();
		for (Actor a : Actor.all()){
			//also adjust any other actors that are now ahead of the hero due to this
			if (a.cooldown() < Dungeon.hero.cooldown()){
				a.spendToWhole();
			}
		}
	}

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
		for (Mob mob : mobs()) {
			if (mob.alignment == Char.Alignment.ENEMY) mob.spsOriginalGeneration = true;
		}
	}

	private void placeSpsDewBless() {
		if (!(Dungeon.dewDraw || Dungeon.dewWater) || Dungeon.branch != 0
				|| Dungeon.depth <= 1 || Dungeon.depth >= 25 || Dungeon.bossLevel()
				|| Dungeon.shopOnLevel() || this instanceof BetweenLevel) {
			return;
		}
		int entrance = entrance();
		if (!insideMap(entrance)) return;
		ArrayList<Integer> candidates = new ArrayList<>();
		int ex = entrance % width();
		int ey = entrance / width();
		for (int radius = 1; radius <= 4 && candidates.isEmpty(); radius++) {
			for (int y = Math.max(1, ey - radius); y <= Math.min(height() - 2, ey + radius); y++) {
				for (int x = Math.max(1, ex - radius); x <= Math.min(width() - 2, ex + radius); x++) {
					if (Math.max(Math.abs(x - ex), Math.abs(y - ey)) != radius) continue;
					int cell = x + y * width();
					int terrain = map[cell];
					if ((terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO
							|| terrain == Terrain.GRASS || terrain == Terrain.EMBERS)
							&& traps.get(cell) == null && plants.get(cell) == null
							&& !insideTransition(cell)) {
						candidates.add(cell);
					}
				}
			}
		}
		if (!candidates.isEmpty()) {
			int cell = Random.element(candidates);
			map[cell] = Terrain.DEW_BLESS;
			SpsFeatureVisual visual = new SpsFeatureVisual(SpsFeatureVisual.DEW_BLESS);
			visual.pos(cell, this);
			customTiles.add(visual);
		}
	}

	//本关卡内的地形查询不能走 getTransition(cell)/LevelTransition.inside(int)：
	//二者按 Dungeon.level 换算坐标，而本方法在 Level.create() 期间执行，此时 Dungeon.level 尚未指向本关卡
	private boolean insideTransition(int cell) {
		Point p = cellToPoint(cell);
		for (LevelTransition transition : transitions) {
			if (transition.inside(p)) return true;
		}
		return false;
	}

	private boolean isSpsClearable() {
		return Dungeon.branch == 0 && Dungeon.depth > 1 && Dungeon.depth < 25
				&& !Dungeon.bossLevel() && !(this instanceof BetweenLevel);
	}

	public boolean hasSpsDew() {
		for (Heap heap : heaps.valueList()) {
			for (Item item : heap.items) {
				if (item instanceof Dewdrop) return true;
			}
		}
		return false;
	}


	public int spsDewPar() {
		int base;
		switch ((Dungeon.depth - 1) / 5) {
			case 0: default: base = 500; break;
			case 1: base = 400; break;
			case 2: base = 300; break;
			case 3: base = 250; break;
			case 4: base = 200; break;
		}
		int secretDoors = 0;
		for (int terrain : map) if (terrain == Terrain.SECRET_DOOR) secretDoors++;
		return base + Dungeon.depth * 50 + secretDoors * 20;
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
	
	public void addItemToSpawn( Item item ) {
		if (item != null) {
			itemsToSpawn.add( item );
		}
	}

	public Item findPrizeItem(){ return findPrizeItem(null); }

	public Item findPrizeItem(Class<?extends Item> match){
		if (itemsToSpawn.size() == 0)
			return null;

		if (match == null){
			//if we have a trinket catalyst, always return that first
			for (Item item : itemsToSpawn){
				if (item instanceof TrinketCatalyst){
					itemsToSpawn.remove(item);
					return item;
				}
			}

			Item item = Random.element(itemsToSpawn);
			itemsToSpawn.remove(item);
			return item;
		}

		for (Item item : itemsToSpawn){
			if (match.isInstance(item)){
				itemsToSpawn.remove( item );
				return item;
			}
		}

		return null;
	}

	public void buildFlagMaps() {
		
		for (int i=0; i < length(); i++) {
			int flags = Terrain.flags[map[i]];
			passable[i]     = (flags & Terrain.PASSABLE) != 0;
			losBlocking[i]  = (flags & Terrain.LOS_BLOCKING) != 0;
			flamable[i]     = (flags & Terrain.FLAMABLE) != 0;
			secret[i]       = (flags & Terrain.SECRET) != 0;
			solid[i]        = (flags & Terrain.SOLID) != 0;
			avoid[i]        = (flags & Terrain.AVOID) != 0;
			water[i]        = (flags & Terrain.LIQUID) != 0;
			pit[i]          = (flags & Terrain.PIT) != 0;
		}

		for (Blob b : blobs.values()){
			b.onBuildFlagMaps(this);
		}
		
		int lastRow = length() - width();
		for (int i=0; i < width(); i++) {
			passable[i] = avoid[i] = false;
			losBlocking[i] = solid[i] = true;
			passable[lastRow + i] = avoid[lastRow + i] = false;
			losBlocking[lastRow + i] = solid[lastRow + i] = true;
		}
		for (int i=width(); i < lastRow; i += width()) {
			passable[i] = avoid[i] = false;
			losBlocking[i] = solid[i] = true;
			passable[i + width()-1] = avoid[i + width()-1] = false;
			losBlocking[i + width()-1] = solid[i + width()-1] = true;
		}

		//an open space is large enough to fit large mobs. A space is open when it is not solid
		// and there is an open corner with both adjacent cells opens
		for (int i=0; i < length(); i++) {
			if (solid[i]){
				openSpace[i] = false;
			} else {
				for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2){
					if (solid[i+PathFinder.CIRCLE8[j]]) {
						openSpace[i] = false;
					} else if (!solid[i+PathFinder.CIRCLE8[(j+1)%8]]
							&& !solid[i+PathFinder.CIRCLE8[(j+2)%8]]){
						openSpace[i] = true;
						break;
					}
				}
			}
		}

	}

	//updates open space both on the cell itself and adjacent cells
	public void updateOpenSpace(int cell){
		int centerX = cell % width();
		int centerY = cell / width();
		for (int dy = -1; dy <= 1; dy++) {
			for (int dx = -1; dx <= 1; dx++) {
				int x = centerX + dx;
				int y = centerY + dy;
				if (x < 0 || y < 0 || x >= width() || y >= height()) continue;
				int target = x + y * width();
				if (solid[target] || x == 0 || y == 0 || x == width() - 1 || y == height() - 1){
					openSpace[target] = false;
					continue;
				}
				openSpace[target] = false;
				for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2){
					if (solid[target + PathFinder.CIRCLE8[j]]) {
						openSpace[target] = false;
					} else if (!solid[target + PathFinder.CIRCLE8[(j+1)%8]]
							&& !solid[target + PathFinder.CIRCLE8[(j+2)%8]]){
						openSpace[target] = true;
						break;
					}
				}
			}
		}
	}

	public void destroy( int pos ) {
		//if raw tile type is flammable or empty
		int terr = map[pos];
		if (terr == Terrain.EMPTY || terr == Terrain.EMPTY_DECO
				|| (Terrain.flags[map[pos]] & Terrain.FLAMABLE) != 0) {
			set(pos, Terrain.EMBERS);
		}
		Blob web = blobs.get(Web.class);
		if (web != null){
			web.clear(pos);
		}
	}

	public void cleanWalls() {
		if (discoverable == null || discoverable.length != length) {
			discoverable = new boolean[length()];
		}

		for (int i=0; i < length(); i++) {
			
			boolean d = false;
			
			for (int j=0; j < PathFinder.NEIGHBOURS9.length; j++) {
				int n = i + PathFinder.NEIGHBOURS9[j];
				if (n >= 0 && n < length() && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
					d = true;
					break;
				}
			}
			
			discoverable[i] = d;
		}
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
		int terrain = map[cell];

		int flags = Terrain.flags[terrain];
		passable[cell]      = (flags & Terrain.PASSABLE) != 0;
		losBlocking[cell]   = (flags & Terrain.LOS_BLOCKING) != 0;
		flamable[cell]      = (flags & Terrain.FLAMABLE) != 0;
		secret[cell]        = (flags & Terrain.SECRET) != 0;
		solid[cell]         = (flags & Terrain.SOLID) != 0;
		avoid[cell]         = (flags & Terrain.AVOID) != 0;
		pit[cell]           = (flags & Terrain.PIT) != 0;
		water[cell]         = terrain == Terrain.WATER;

		if (this instanceof SewerLevel){
			if (map[cell] == Terrain.REGION_DECO || map[cell] == Terrain.REGION_DECO_ALT){
				flamable[cell] = true;
			}
		}

		for (Blob b : blobs.values()){
			b.onUpdateCellFlags(this, cell);
		}

		updateOpenSpace(cell);
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
	
	public Plant plant( Plant.Seed seed, int pos ) {

		Plant plant = plants.get( pos );
		if (plant != null) {
			plant.wither();
		}

		if (map[pos] == Terrain.HIGH_GRASS ||
				map[pos] == Terrain.FURROWED_GRASS ||
				map[pos] == Terrain.EMPTY ||
				map[pos] == Terrain.EMBERS ||
				map[pos] == Terrain.EMPTY_DECO) {
			set(pos, Terrain.GRASS, this);
			GameScene.updateMap(pos);
		}

		//we have to get this far as grass placement has RNG implications in levelgen
		if (Dungeon.isChallenged(Challenges.NO_HERBALISM)){
			return null;
		}
		
		plant = seed.couch( pos, this );
		plants.put( pos, plant );
		
		GameScene.plantSeed( pos );

		for (Char ch : Actor.chars()){
			if (ch instanceof WandOfRegrowth.Lotus
					&& ((WandOfRegrowth.Lotus) ch).inRange(pos)
					&& Actor.findChar(pos) != null){
				plant.trigger();
				return null;
			}
		}
		
		return plant;
	}

	public Plant explant( Plant.Seed seed, int pos ) {
		Plant plant = plants.get(pos);
		if (plant != null) plant.wither();

		if (map[pos] == Terrain.HIGH_GRASS || map[pos] == Terrain.FURROWED_GRASS
				|| map[pos] == Terrain.EMPTY || map[pos] == Terrain.EMBERS
				|| map[pos] == Terrain.EMPTY_DECO) {
			set(pos, Terrain.GRASS, this);
			GameScene.updateMap(pos);
		}

		plant = seed.excouch(pos, this);
		plants.put(pos, plant);
		GameScene.plantSeed(pos);
		return plant;
	}
	
	public void uproot( int pos ) {
		plants.remove(pos);
		GameScene.updateMap( pos );
	}

	public Trap setTrap( Trap trap, int pos ){
		Trap existingTrap = traps.get(pos);
		if (existingTrap != null){
			traps.remove( pos );
		}
		trap.set( pos );
		traps.put( pos, trap );
		GameScene.updateMap( pos );
		return trap;
	}

	public void disarmTrap( int pos ) {
		set(pos, Terrain.INACTIVE_TRAP);
		GameScene.updateMap(pos);
	}

	public void discover( int cell ) {
		set( cell, Terrain.discover( map[cell] ) );
		Trap trap = traps.get( cell );
		if (trap != null)
			trap.reveal();
		GameScene.updateMap( cell );
	}

	public boolean setCellToWater( boolean includeTraps, int cell ){
		Point p = cellToPoint(cell);

		//if a custom tilemap is over that cell, check if it allows water
		for (CustomTilemap cust : customTiles){
			Point custPoint = new Point(p);
			custPoint.x -= cust.tileX;
			custPoint.y -= cust.tileY;
			if (custPoint.x >= 0 && custPoint.y >= 0
					&& custPoint.x < cust.tileW && custPoint.y < cust.tileH){
				if (!cust.allowWater(custPoint.x, custPoint.y)){
					return false;
				}
			}
		}

		int terr = map[cell];
		if (terr == Terrain.EMPTY || terr == Terrain.GRASS ||
				terr == Terrain.EMBERS || terr == Terrain.EMPTY_SP ||
				terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS
				|| terr == Terrain.EMPTY_DECO){
			set(cell, Terrain.WATER);
			GameScene.updateMap(cell);
			return true;
		} else if (includeTraps && (terr == Terrain.SECRET_TRAP ||
				terr == Terrain.TRAP || terr == Terrain.INACTIVE_TRAP)){
			set(cell, Terrain.WATER);
			Dungeon.level.traps.remove(cell);
			GameScene.updateMap(cell);
			return true;
		}

		return false;
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
		if (!ch.isImmune(Web.class) && Blob.volumeAt(ch.pos, Web.class) > 0){
			blobs.get(Web.class).clear(ch.pos);
			Web.affectChar( ch );
		}

		if (Blob.volumeAt(ch.pos, SacrificialFire.class) > 0 && ch.buff( SacrificialFire.Marked.class ) == null){
			if (Dungeon.level.heroFOV[ch.pos]) {
				CellEmitter.get(ch.pos).burst( SacrificialParticle.FACTORY, 5 );
			}
			Buff.prolong( ch, SacrificialFire.Marked.class, SacrificialFire.Marked.DURATION );
		}

		if (!ch.flying){

			//we call act here instead of detach in case the debuffs haven't managed to deal dmg once yet
			if (map[ch.pos] == Terrain.WATER){
				if (ch.buff(Burning.class) != null){
					ch.buff(Burning.class).act();
				}
				if (ch.buff(Ooze.class) != null){
					ch.buff(Ooze.class).act();
				}
			}

			if ( (map[ch.pos] == Terrain.GRASS || map[ch.pos] == Terrain.EMBERS)
					&& ch == Dungeon.hero && Dungeon.hero.hasTalent(Talent.REJUVENATING_STEPS)
					&& ch.buff(Talent.RejuvenatingStepsCooldown.class) == null){

				if (!Regeneration.regenOn()){
					set(ch.pos, Terrain.FURROWED_GRASS);
				} else if (ch.buff(Talent.RejuvenatingStepsFurrow.class) != null && ch.buff(Talent.RejuvenatingStepsFurrow.class).count() >= 200) {
					set(ch.pos, Terrain.FURROWED_GRASS);
				} else {
					set(ch.pos, Terrain.HIGH_GRASS);
					Buff.count(ch, Talent.RejuvenatingStepsFurrow.class, 3 - Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
				}
				GameScene.updateMap(ch.pos);
				Buff.affect(ch, Talent.RejuvenatingStepsCooldown.class, 15f - 5f*Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
			}
			
			if (pit[ch.pos]){
				if (ch == Dungeon.hero) {
					Chasm.heroFall(ch.pos);
				} else if (ch instanceof Mob) {
					Chasm.mobFall( (Mob)ch );
				}
				return;
			}
			
			//characters which are not the hero or a sheep 'soft' press cells
			pressCell( ch.pos, ch instanceof Hero || ch instanceof Sheep);
		} else {
			if (map[ch.pos] == Terrain.DOOR){
				Door.enter( ch.pos );
			}
		}

		if (ch.isAlive() && ch instanceof Piranha && !water[ch.pos]){
			((Piranha) ch).dieOnLand();
		}
	}
	
	//public method for forcing the hard press of a cell. e.g. when an item lands on it
	public void pressCell( int cell ){
		pressCell( cell, true );
	}
	
	//a 'soft' press ignores hidden traps
	//a 'hard' press triggers all things
	private void pressCell( int cell, boolean hard ) {

		Trap trap = null;
		
		switch (map[cell]) {
		
		case Terrain.SECRET_TRAP:
			if (hard) {
				trap = traps.get( cell );
				GLog.i(Messages.get(Level.class, "hidden_trap", trap.name()));
			}
			break;
			
		case Terrain.TRAP:
			trap = traps.get( cell );
			break;
			
		case Terrain.HIGH_GRASS:
		case Terrain.FURROWED_GRASS:
			HighGrass.trample( this, cell);
			break;

		case Terrain.OLD_HIGH_GRASS:
			OldHighGrass.trample(this, cell, Actor.findChar(cell));
			break;
			
		case Terrain.WELL:
			WellWater.affectCell( cell );
			break;

		case Terrain.DEW_BLESS:
			DewBlessRoom.trample(this, cell, Actor.findChar(cell));
			break;
			
		case Terrain.DOOR:
			Door.enter( cell );
			break;
		}

		TimekeepersHourglass.timeFreeze timeFreeze =
				Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);

		Swiftthistle.TimeBubble bubble =
				Dungeon.hero.buff(Swiftthistle.TimeBubble.class);

		if (trap != null) {
			if (bubble != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAP);
				discover(cell);
				bubble.setDelayedPress(cell);
				
			} else if (timeFreeze != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAP);
				discover(cell);
				timeFreeze.setDelayedPress(cell);
				
			} else {
				if (Dungeon.hero.pos == cell) {
					Dungeon.hero.interrupt();
				}
				trap.trigger();

			}
		}
		
		Plant plant = plants.get( cell );
		if (plant != null) {
			if (bubble != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				bubble.setDelayedPress(cell);

			} else if (timeFreeze != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				timeFreeze.setDelayedPress(cell);

			} else {
				plant.trigger();

			}
		}

		if (hard && Blob.volumeAt(cell, Web.class) > 0){
			blobs.get(Web.class).clear(cell);
		}
	}

	private static boolean[] heroMindFov;

	private static boolean[] modifiableBlocking;

	public void updateFieldOfView( Char c, boolean[] fieldOfView ) {

		int cx = c.pos % width();
		int cy = c.pos / width();
		
		boolean sighted = c.buff( Blindness.class ) == null && c.buff( Shadows.class ) == null
						&& c.buff(TentSleep.class) == null
						&& c.isAlive();
		if (sighted) {
			boolean[] blocking = null;

			if (modifiableBlocking == null || modifiableBlocking.length != Dungeon.level.losBlocking.length){
				modifiableBlocking = new boolean[Dungeon.level.losBlocking.length];
			}

			//grass is see-through by some specific entities, but not during the fungi quest
			if (!(Dungeon.level instanceof  MiningLevel) || Blacksmith.Quest.Type() != Blacksmith.Quest.FUNGI){
				if ((c instanceof Hero && ((Hero) c).subClass == HeroSubClass.WARDEN)
						|| c instanceof YogFist.SoiledFist || c instanceof GnollGeomancer) {
					if (blocking == null) {
						System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
						blocking = modifiableBlocking;
					}
					for (int i = 0; i < blocking.length; i++) {
						if (blocking[i] && (Dungeon.level.map[i] == Terrain.HIGH_GRASS || Dungeon.level.map[i] == Terrain.FURROWED_GRASS)) {
							blocking[i] = false;
						}
					}
				}
			}

			//allies and specific enemies can see through shrouding fog
			if ((c.alignment != Char.Alignment.ALLY && !(c instanceof GnollGeomancer))
					&& Dungeon.level.blobs.containsKey(SmokeScreen.class)
					&& Dungeon.level.blobs.get(SmokeScreen.class).volume > 0) {
				if (blocking == null) {
					System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
					blocking = modifiableBlocking;
				}
				Blob s = Dungeon.level.blobs.get(SmokeScreen.class);
				for (int i = 0; i < blocking.length; i++){
					if (!blocking[i] && s.cur[i] > 0){
						blocking[i] = true;
					}
				}
			}

			if (blocking == null){
				blocking = Dungeon.level.losBlocking;
			}

			float viewDist = c.viewDistance;
			if (c instanceof Hero){
				viewDist *= 1f + 0.25f*((Hero) c).pointsInTalent(Talent.FARSIGHT);
				viewDist *= EyeOfNewt.visionRangeMultiplier();
			}
			
			ShadowCaster.castShadow( cx, cy, width(), fieldOfView, blocking, Math.round(viewDist) );
		} else {
			BArray.setFalse(fieldOfView);
		}
		
		int sense = 1;
		//Currently only the hero can get mind vision
		if (c.isAlive() && c == Dungeon.hero) {
			for (Buff b : c.buffs( MindVision.class )) {
				sense = Math.max( ((MindVision)b).distance, sense );
			}
			if (c.buff(MagicalSight.class) != null){
				sense = Math.max( MagicalSight.DISTANCE, sense );
			}
		}
		
		//uses rounding
		if (!sighted || sense > 1) {
			
			int[][] rounding = ShadowCaster.rounding;
			
			int left, right;
			int pos;
			for (int y = Math.max(0, cy - sense); y <= Math.min(height()-1, cy + sense); y++) {
				if (rounding[sense][Math.abs(cy - y)] < Math.abs(cy - y)) {
					left = cx - rounding[sense][Math.abs(cy - y)];
				} else {
					left = sense;
					while (rounding[sense][left] < rounding[sense][Math.abs(cy - y)]){
						left--;
					}
					left = cx - left;
				}
				right = Math.min(width()-1, cx + cx - left);
				left = Math.max(0, left);
				pos = left + y * width();
				System.arraycopy(discoverable, pos, fieldOfView, pos, right - left + 1);
			}
		}

		if (c instanceof SpiritHawk.HawkAlly && Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE) >= 3){
			int range = 1+(Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE)-2);
			for (Mob mob : mobs()) {
				int p = mob.pos;
				if (!fieldOfView[p] && distance(c.pos, p) <= range) {
					for (int i : PathFinder.NEIGHBOURS9) {
						fieldOfView[mob.pos + i] = true;
					}
				}
			}
		}

		//Currently only the hero can get mind vision or awareness
		if (c.isAlive() && c == Dungeon.hero) {

			if (heroMindFov == null || heroMindFov.length != length()){
				heroMindFov = new boolean[length];
			} else {
				BArray.setFalse(heroMindFov);
			}

			Dungeon.hero.mindVisionEnemies.clear();

			int mindVisRange = 0;
			if (c.buff(MindVision.class) != null) {
				mindVisRange = Integer.MAX_VALUE;
			} else {
				if (((Hero) c).hasTalent(Talent.HEIGHTENED_SENSES)) {
					mindVisRange = 1 + ((Hero) c).pointsInTalent(Talent.HEIGHTENED_SENSES);
				}
				if (c.buff(DivineSense.DivineSenseTracker.class) != null) {
					if (((Hero) c).heroClass == HeroClass.CLERIC) {
						mindVisRange = 4 + 4 * ((Hero) c).pointsInTalent(Talent.DIVINE_SENSE);
					} else {
						mindVisRange = 1 + 2 * ((Hero) c).pointsInTalent(Talent.DIVINE_SENSE);
					}
				}
				mindVisRange = Math.max(mindVisRange, EyeOfNewt.mindVisionRange());
			}

			if (mindVisRange >= 1) {

				//power of many's life link spell allows allies to get divine sense
				Char ally = PowerOfMany.getPoweredAlly();
				if (ally != null && ally.buff(DivineSense.DivineSenseTracker.class) == null) {
					ally = null;
				}

				for (Mob mob : mobs()) {
					if ((mob instanceof Mimic && mob.alignment == Char.Alignment.NEUTRAL && ((Mimic) mob).stealthy())
						|| Char.hasProp(mob, Char.Property.OBJECT)){
						continue;
					}
					int p = mob.pos;
					if (!fieldOfView[p] && (distance(c.pos, p) <= mindVisRange || (ally != null && distance(ally.pos, p) <= mindVisRange))) {
						for (int i : PathFinder.NEIGHBOURS9) {
							heroMindFov[mob.pos + i] = true;
						}
					}
				}
			}
			
			if (c.buff( Awareness.class ) != null) {
				for (Heap heap : heaps.valueList()) {
					int p = heap.pos;
					for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p+i] = true;
				}
			}

			for (TalismanOfForesight.CharAwareness a : c.buffs(TalismanOfForesight.CharAwareness.class)){
				Char ch = (Char) Actor.findById(a.charID);
				if (ch == null || !ch.isAlive() || Char.hasProp(ch, Char.Property.OBJECT)) {
					continue;
				}
				int p = ch.pos;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p+i] = true;
			}

			for (TalismanOfForesight.HeapAwareness h : c.buffs(TalismanOfForesight.HeapAwareness.class)){
				if (Dungeon.depth != h.depth || Dungeon.branch != h.branch) continue;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[h.pos+i] = true;
			}

			for (Mob m : mobs()){
				if (m instanceof WandOfWarding.Ward
						|| m instanceof WandOfRegrowth.Lotus
						|| m instanceof SpiritHawk.HawkAlly
						|| m.buff(PowerOfMany.PowerBuff.class) != null){
					if (m.fieldOfView == null || m.fieldOfView.length != length()){
						m.fieldOfView = new boolean[length()];
						Dungeon.level.updateFieldOfView( m, m.fieldOfView );
					}
					BArray.or(heroMindFov, m.fieldOfView, heroMindFov);
				}
			}

			for (RevealedArea a : c.buffs(RevealedArea.class)){
				if (Dungeon.depth != a.depth || Dungeon.branch != a.branch) continue;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[a.pos+i] = true;
			}

			//set mind vision chars
			for (Mob mob : mobs()) {
				if (heroMindFov[mob.pos] && !fieldOfView[mob.pos]){
					Dungeon.hero.mindVisionEnemies.add(mob);
				}
			}

			BArray.or(heroMindFov, fieldOfView, fieldOfView);

		}

		if (c == Dungeon.hero) {
			for (Heap heap : heaps.valueList())
				if (!heap.seen && fieldOfView[heap.pos])
					heap.seen = true;
		}

	}

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
		
		switch (tile) {
			case Terrain.CHASM:
				return Messages.get(Level.class, "chasm_name");
			case Terrain.EMPTY:
			case Terrain.EMPTY_SP:
			case Terrain.EMPTY_DECO:
			case Terrain.CUSTOM_DECO_EMPTY:
			case Terrain.SECRET_TRAP:
				return Messages.get(Level.class, "floor_name");
			case Terrain.GRASS:
				return Messages.get(Level.class, "grass_name");
			case Terrain.WATER:
				return Messages.get(Level.class, "water_name");
			case Terrain.WALL:
			case Terrain.WALL_DECO:
			case Terrain.SECRET_DOOR:
				return Messages.get(Level.class, "wall_name");
			case Terrain.DOOR:
				return Messages.get(Level.class, "closed_door_name");
			case Terrain.OPEN_DOOR:
				return Messages.get(Level.class, "open_door_name");
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return Messages.get(Level.class, "entrace_name");
			case Terrain.EXIT:
				return Messages.get(Level.class, "exit_name");
			case Terrain.EMBERS:
				return Messages.get(Level.class, "embers_name");
			case Terrain.FURROWED_GRASS:
				return Messages.get(Level.class, "furrowed_grass_name");
			case Terrain.LOCKED_DOOR:
			case Terrain.HERO_LKD_DR:
				return Messages.get(Level.class, "locked_door_name");
			case Terrain.CRYSTAL_DOOR:
				return Messages.get(Level.class, "crystal_door_name");
			case Terrain.PEDESTAL:
				return Messages.get(Level.class, "pedestal_name");
			case Terrain.DEW_BLESS:
				return Messages.get(Level.class, "dew_bless_name");
			case Terrain.TENT:
				return Messages.get(Level.class, "tent_name");
			case Terrain.IRON_MAKER:
				return Messages.get(Level.class, "iron_maker_name");
			case Terrain.SIGN:
				return Messages.get(Level.class, "sign_name");
			case Terrain.WOOL_RUG:
				return Messages.get(Level.class, "wool_rug_name");
			case Terrain.FLEECING_TRAP:
				return Messages.get(Level.class, "fleecing_trap_name");
			case Terrain.CHANGE_SHEEP_TRAP:
				return Messages.get(Level.class, "change_sheep_trap_name");
			case Terrain.SOKOBAN_ITEM_REVEAL:
				return Messages.get(Level.class, "sokoban_item_reveal_name");
			case Terrain.SOKOBAN_PORT_SWITCH:
				return Messages.get(Level.class, "sokoban_port_switch_name");
			case Terrain.PORT_WELL:
				return Messages.get(Level.class, "port_well_name");
			case Terrain.BARRICADE:
				return Messages.get(Level.class, "barricade_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(Level.class, "high_grass_name");
			case Terrain.LOCKED_EXIT:
				return Messages.get(Level.class, "locked_exit_name");
			case Terrain.UNLOCKED_EXIT:
				return Messages.get(Level.class, "unlocked_exit_name");
			case Terrain.WELL:
				return Messages.get(Level.class, "well_name");
			case Terrain.EMPTY_WELL:
				return Messages.get(Level.class, "empty_well_name");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(Level.class, "statue_name");
			case Terrain.INACTIVE_TRAP:
				return Messages.get(Level.class, "inactive_trap_name");
			case Terrain.BOOKSHELF:
				return Messages.get(Level.class, "bookshelf_name");
			case Terrain.ALCHEMY:
				return Messages.get(Level.class, "alchemy_name");
			default:
				return Messages.get(Level.class, "default_name");
		}
	}
	
	public String tileDesc( int tile ) {
		
		switch (tile) {
			case Terrain.CHASM:
				return Messages.get(Level.class, "chasm_desc");
			case Terrain.WATER:
				return Messages.get(Level.class, "water_desc");
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return Messages.get(Level.class, "entrance_desc");
			case Terrain.EXIT:
			case Terrain.UNLOCKED_EXIT:
				return Messages.get(Level.class, "exit_desc");
			case Terrain.EMBERS:
				return Messages.get(Level.class, "embers_desc");
			case Terrain.HIGH_GRASS:
			case Terrain.FURROWED_GRASS:
				return Messages.get(Level.class, "high_grass_desc");
			case Terrain.LOCKED_DOOR:
			case Terrain.HERO_LKD_DR:
				return Messages.get(Level.class, "locked_door_desc");
			case Terrain.CRYSTAL_DOOR:
				return Messages.get(Level.class, "crystal_door_desc");
			case Terrain.LOCKED_EXIT:
				return Messages.get(Level.class, "locked_exit_desc");
			case Terrain.BARRICADE:
				return Messages.get(Level.class, "barricade_desc");
			case Terrain.DEW_BLESS:
				return Messages.get(Level.class, "dew_bless_desc");
			case Terrain.TENT:
				return Messages.get(Level.class, "tent_desc");
			case Terrain.IRON_MAKER:
				return Messages.get(Level.class, "iron_maker_desc");
			case Terrain.SIGN:
				return Messages.get(Level.class, "sign_desc");
			case Terrain.WOOL_RUG:
				return Messages.get(Level.class, "wool_rug_desc");
			case Terrain.FLEECING_TRAP:
				return Messages.get(Level.class, "fleecing_trap_desc");
			case Terrain.CHANGE_SHEEP_TRAP:
				return Messages.get(Level.class, "change_sheep_trap_desc");
			case Terrain.SOKOBAN_ITEM_REVEAL:
				return Messages.get(Level.class, "sokoban_item_reveal_desc");
			case Terrain.SOKOBAN_PORT_SWITCH:
				return Messages.get(Level.class, "sokoban_port_switch_desc");
			case Terrain.PORT_WELL:
				return Messages.get(Level.class, "port_well_desc");
			case Terrain.INACTIVE_TRAP:
				return Messages.get(Level.class, "inactive_trap_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(Level.class, "statue_desc");
			case Terrain.ALCHEMY:
				return Messages.get(Level.class, "alchemy_desc");
			case Terrain.EMPTY_WELL:
				return Messages.get(Level.class, "empty_well_desc");
			default:
				return "";
		}
	}
}
