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

package pd.scenes;

import pd.Assets;
import pd.Badges;
import pd.Challenges;
import pd.Chrome;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.Rankings;
import pd.SPDAction;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.buffs.AscensionChallenge;
import pd.actors.buffs.ChampionEnemy;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.mobs.DemonSpawner;
import pd.actors.mobs.Ghoul;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Snake;
import pd.effects.BannerSprites;
import pd.effects.BlobEmitter;
import pd.effects.CheckedCell;
import pd.effects.EmoIcon;
import pd.effects.Flare;
import pd.effects.FloatingText;
import pd.effects.Ripple;
import pd.effects.SpellSprite;
import pd.effects.TargetedCell;
import pd.items.Ankh;
import pd.items.Heap;
import pd.items.Honeypot;
import pd.items.Item;
import pd.items.equipment.artifacts.DriedRose;
import pd.items.specific.journal.Guidebook;
import pd.items.consum.potions.Potion;
import pd.items.consum.scrolls.InventoryScroll;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.equipment.trinkets.DimensionalSundial;
import pd.items.equipment.trinkets.TrinketCatalyst;
import pd.items.equipment.weapon.melee.MeleeWeapon;
import pd.journal.Bestiary;
import pd.journal.Document;
import pd.journal.Journal;
import pd.journal.Notes;
import pd.levels.CellFlags;
import pd.levels.GroundItems;
import pd.levels.Level;
import pd.levels.RegularLevel;
import pd.levels.Terrain;
import pd.levels.rooms.Room;
import pd.levels.rooms.secret.SecretRoom;
import pd.levels.traps.Trap;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.sprites.CharSprite;
import pd.sprites.DiscardedItemSprite;
import pd.sprites.HeroSprite;
import pd.sprites.ItemSprite;
import pd.tiles.CustomTilemap;
import pd.tiles.DungeonTerrainTilemap;
import pd.tiles.DungeonTileSheet;
import pd.tiles.DungeonTilemap;
import pd.tiles.DungeonWallsTilemap;
import pd.tiles.FogOfWar;
import pd.tiles.GridTileMap;
import pd.tiles.RaisedTerrainTilemap;
import pd.tiles.SpsChasmEdgesTilemap;
import pd.effects.HeroPathCell;
import pd.mechanics.pathfind.HeroPath;
import pd.tiles.SpsWaterEdgesTilemap;
import pd.tiles.TerrainFeaturesTilemap;
import pd.tiles.WallBlockingTilemap;
import pd.tiles.WallOcclusionTilemap;
import pd.ui.ActionIndicator;
import pd.ui.AttackIndicator;
import pd.ui.Banner;
import pd.ui.BossHealthBar;
import pd.ui.CharHealthIndicator;
import pd.ui.GameLog;
import pd.ui.Icons;
import pd.ui.InventoryPane;
import pd.ui.LootIndicator;
import pd.ui.MenuPane;
import pd.ui.QuickActionBar;
import pd.ui.QuickSlotButton;
import pd.ui.ResumeIndicator;
import pd.ui.RightClickMenu;
import pd.ui.SideQuickBar;
import pd.ui.StatusPane;
import pd.ui.StyledButton;
import pd.ui.Tag;
import pd.ui.TargetHealthIndicator;
import pd.ui.Toast;
import pd.ui.Toolbar;
import pd.ui.Window;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndGame;
import pd.windows.WndHero;
import pd.windows.WndInfoCell;
import pd.windows.WndInfoItem;
import pd.windows.WndInfoMob;
import pd.windows.WndInfoPlant;
import pd.windows.WndInfoTrap;
import pd.windows.WndKeyBindings;
import pd.windows.WndMessage;
import pd.windows.WndOptions;
import pd.windows.WndResurrect;
import pd.windows.WndUpgrade;
import render.gltextures.TextureCache;
import render.glwrap.Blending;
import render.input.ControllerHandler;
import render.input.KeyBindings;
import render.input.PointerEvent;
import render.noosa.Camera;
import render.noosa.Game;
import render.noosa.Gizmo;
import render.noosa.Group;
import render.noosa.Image;
import render.noosa.NoosaScript;
import render.noosa.NoosaScriptNoLighting;
import render.noosa.PointerArea;
import render.noosa.SkinnedBlock;
import render.noosa.Visual;
import render.noosa.audio.Music;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import render.noosa.tweeners.Tweener;
import render.utils.data.Callback;
import render.utils.geom.Point;
import render.utils.geom.PointF;
import render.utils.geom.RectF;
import render.utils.math.GameMath;
import render.utils.math.Random;
import render.utils.platform.PlatformSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import pd.messages.InlineText;

public class GameScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(GameScene.class)
			.t("descend", "你降入了地牢的第%d层。")
			.t("spawner_warn", "你感觉自己上方有一个恶魔能量的供给源...")
			.t("spawner_warn_final", "你能感觉到恶魔能量从楼上辐射到了这里！")
			.t("warp", "你四周的墙壁开始扭曲变换！")
			.t("return", "你回到了地牢的第%d层。")
			.t("resurrect", "你出现在了第%d层的某处。")
			.t("secret_hint", "直觉告诉你这层的某处一定存在着一个隐藏的房间...")
			.t("choose_examine", "选择检查目标")
			.t("multiple_examine", "这里引人注意的东西不止一件，你想检查哪个？")
			.t("dont_know", "你不清楚那里有什么。")
			.t("multiple", "多重目标")
			.t("go_here", "去那里")
			.t("cancel", "取消")
			.t("interact", "互动")
			.t("attack", "攻击")
			.t("pick_up", "拾取")
			.t("purchase", "购买")
			.t("trample", "践踏")
			.t("examine", "检视")
			.t("path_unknown", "你暂时不知道怎么到达那里。")
			.t("tutorial_move_mobile", "点击一个位置以进行移动或互动。")
			.t("tutorial_move_desktop", "使用鼠标或方向键进行移动与互动。")
			.t("tutorial_move_controller", "选择一个位置或使用左摇杆进行移动与互动。")
			.t("tutorial_guidebook_mobile", "点击屏幕右上角闪光的日志按钮以阅读你刚刚拾起的书页。")
			.t("tutorial_guidebook_desktop", "选取屏幕右上角闪光的日志按钮(%s)以阅读你刚刚拾起的书页。")
			.t("tutorial_ui_mobile", "英雄的信息显示于界面的左上角。而物品栏、快捷栏以及一些行动按钮则位于界面下方。开始你的探索吧！")
			.t("tutorial_ui_desktop", "英雄信息(%1$s)显示于界面的下方。而物品栏(%2$s)、快捷栏以及一些交互按钮则位于界面右侧。开始你的探索吧！");
	}




	static GameScene scene;

	private SkinnedBlock water;
	private DungeonTerrainTilemap tiles;
	//SPS: 水缝合边独立层（水脱离地形图集的 48-63 段语义）
	private SpsWaterEdgesTilemap waterEdges;
	private SpsChasmEdgesTilemap chasmEdges;
	//SPS: 移动路径提示层
	private Group heroPathCells;
	//SPS: 谨慎移动当前锁定的目标格（-1 = 未锁定）
	private static int pathTarget = -1;
	//SPS: 是否已进入「首次点击仅预览」的锁定状态
	private static boolean lockedTarget = false;
	//SPS: 谨慎移动开关（长按检视按钮切换）
	public static boolean cautiousMove = false;
	//SPS: 目标格上站着生物时记下来，路径预览每帧跟随它的新位置
	private static Mob pathTrackedMob = null;
	private GridTileMap visualGrid;
	private WallOcclusionTilemap occlusion;
	private TerrainFeaturesTilemap terrainFeatures;
	private RaisedTerrainTilemap raisedTerrain;
	private DungeonWallsTilemap walls;
	private WallBlockingTilemap wallBlocking;
	private FogOfWar fog;
	private HeroSprite hero;

	private MenuPane menu;
	private StatusPane status;

	private BossHealthBar boss;

	private GameLog log;

	private static CellSelector cellSelector;
	
	private Group terrain;
	private Group customTiles;
	private Group levelVisuals;
	private Group customTerrain;
	private Group levelWallVisuals;
	private Group customWalls;
	private Group ripples;
	private Group plants;
	private Group traps;
	private Group heaps;
	private Group mobs;
	private Group floorEmitters;
	private Group emitters;
	private Group effects;
	private Group gases;
	private Group spells;
	private Group statuses;
	private Group emoicons;
	private Group overFogEffects;
	private Group checkedCells;
	private Group targetedCells;
	private Group healthIndicators;

	private InventoryPane inventory;
	private static boolean invVisible = true;

	private Toolbar toolbar;
	//SPS: 两侧快捷栏（左 0-4、右 0-4，数量可调，用户裁决 2026-09）
	private SideQuickBar sideQuickBar;
	private QuickActionBar quickActionBar;   //SPSEXPD: 左下角快捷操作按钮
	private Toast prompt;

	private AttackIndicator attack;
	private LootIndicator loot;
	private ActionIndicator action;
	private ResumeIndicator resume;

	{
		inGameScene = true;
	}

	@Override
	public void create() {
		
		if (Dungeon.hero == null || Dungeon.level == null){
			ShatteredPixelDungeon.switchNoFade(TitleScene.class);
			return;
		}

		Music.INSTANCE.play(Assets.Music.SPS_GAME, true);
		Music.INSTANCE.volume(1f);

		SPDSettings.lastClass(Dungeon.hero.heroClass.ordinal());
		
		super.create();
		Camera.main.zoom( GameMath.gate(minZoom, defaultZoom + SPDSettings.zoom(), maxZoom));
		Camera.main.edgeScroll.set(1);

		switch (SPDSettings.cameraFollow()) {
			case 4: default:    Camera.main.setFollowDeadzone(0);      break;
			case 3:             Camera.main.setFollowDeadzone(0.2f);   break;
			case 2:             Camera.main.setFollowDeadzone(0.5f);   break;
			case 1:             Camera.main.setFollowDeadzone(0.9f);   break;
		}

		RectF insets = getCommonInsets();
		//we want to check if large is the same as blocking here
		float largeInsetTop = Game.platform.getSafeInsets(PlatformSupport.INSET_LRG).scale(1f/defaultZoom).top;

		scene = this;

		terrain = new Group();
		add( terrain );

		water = new SkinnedBlock(
			Dungeon.level.width() * DungeonTilemap.SIZE,
			Dungeon.level.height() * DungeonTilemap.SIZE,
			Dungeon.level.waterTex() ){

			@Override
			protected NoosaScript script() {
				return NoosaScriptNoLighting.get();
			}

			@Override
			public void draw() {
				//water has no alpha component, this improves performance
				Blending.disable();
				super.draw();
				Blending.enable();
			}
		};
		water.autoAdjust = true;
		terrain.add( water );

		ripples = new Group();
		terrain.add( ripples );

		DungeonTileSheet.setupVariance(Dungeon.level.map.length, Dungeon.seedCurDepth());
		
		tiles = new DungeonTerrainTilemap();
		terrain.add( tiles );

		//SPS: 水缝合边独立层（紧贴地形层之上；水不再取地形图集的 48-63 帧段，
		//避免破碎系地形图集关卡把水渲染成物件图标）
		waterEdges = new SpsWaterEdgesTilemap();
		terrain.add( waterEdges );

		//SPSXPD: 区域装饰独立层（紧贴水边层之上、customTiles 之前，避免被后续层盖住）
		terrain.add( new pd.tiles.SpsRegionDecoTilemap() );

		//SPS: 移动路径提示改挂独立 Group（见 targetedCells），不再进 terrain 层
		customTiles = new Group();
		terrain.add(customTiles);

		//SPS: 回 2D 渲染 —— 停用 2.5D 网格线/墙影层（visualGrid、occlusion）
		//visualGrid = new GridTileMap();
		//terrain.add( visualGrid );
		//occlusion = new WallOcclusionTilemap();
		//terrain.add( occlusion );

		terrainFeatures = new TerrainFeaturesTilemap(Dungeon.level.plants, Dungeon.level.traps);
		terrain.add(terrainFeatures);

		customTerrain = new Group();
		terrain.add(customTerrain);

		//SPS: 深渊缝合边排在 customTiles/customTerrain 之后。过渡层（如 0 层 BetweenLevel）
		//整张地图是靠 customTiles 里的 SpsLegacyLevelVisual 画的，若排在其前面会被整层盖住，
		//表现就是"深渊没有邻接效果"（只在这些层出现，普通层没有 customTiles 所以正常）。
		chasmEdges = new SpsChasmEdgesTilemap();
		terrain.add( chasmEdges );

		levelVisuals = Dungeon.level.addVisuals();
		add(levelVisuals);

		floorEmitters = new Group();
		add(floorEmitters);

		heaps = new Group();
		add( heaps );
		
		for ( Heap heap : Dungeon.level.heaps.valueList() ) {
			addHeapSprite( heap );
		}

		emitters = new Group();
		effects = new Group();
		healthIndicators = new Group();
		emoicons = new Group();
		overFogEffects = new Group();
		
		mobs = new Group();
		add( mobs );

		hero = new HeroSprite();
		hero.place( Dungeon.hero.pos );
		hero.updateArmor();
		mobs.add( hero );
		
		for (Mob mob : Dungeon.level.mobs()) {
			addMobSprite( mob );
		}
		
		//SPS: 回 2D 渲染 —— 停用 2.5D raised/walls/wallBlocking 三层
		//（锅顶悬挑、墙内面、墙遮挡均并入单层 2D 帧）
		//raisedTerrain = new RaisedTerrainTilemap();
		//add( raisedTerrain );
		//walls = new DungeonWallsTilemap();
		//add(walls);

		customWalls = new Group();
		add(customWalls);

		levelWallVisuals = Dungeon.level.addWallVisuals();
		add( levelWallVisuals );

		//wallBlocking = new WallBlockingTilemap();
		//add (wallBlocking);

		add( emitters );
		add( effects );

		gases = new Group();
		add( gases );

		for (Blob blob : Dungeon.level.blobs.values()) {
			blob.emitter = null;
			addBlobSprite( blob );
		}


		fog = new FogOfWar( Dungeon.level.width(), Dungeon.level.height() );
		add( fog );

		spells = new Group();
		add( spells );

		add(overFogEffects);

		checkedCells = new Group();
		add(checkedCells);

		targetedCells = new Group();
		add(targetedCells);
		for (TargetedCell cell : TargetedCell.cells.valueList()){
			cell.reset(cell.pos, cell.time);
			targetedCells.add(cell);
		}

		//SPS: 移动路径提示也挂独立 Group，避免被后加入 terrain 的自定义层遮住
		heroPathCells = new Group();
		add( heroPathCells );

		//set these up later so that they can influence previous tilemaps if needed
		for( CustomTilemap visual : Dungeon.level.customTiles){
			addCustomTile(visual);
		}

		for( CustomTilemap visual : Dungeon.level.customTerrain){
			addCustomTerrain(visual);
		}

		for( CustomTilemap visual : Dungeon.level.customWalls){
			addCustomWall(visual);
		}
		
		statuses = new Group();
		add( statuses );
		
		add( healthIndicators );
		//always appears ontop of other health indicators
		add( new TargetHealthIndicator() );
		
		add( emoicons );
		
		add( cellSelector = new CellSelector( tiles ) );

		int uiSize = SPDSettings.interfaceSize();

		//display cutouts can obstruct various UI elements, so we need to adjust for that sometimes
		float heroPaneExtraWidth = insets.left;
		float menuBarMaxLeft = uiCamera.width-insets.right-MenuPane.WIDTH;
		int hpBarMaxWidth = 50; //default max width
		float[] buffBarRowLimits = new float[9];
		float[] buffBarRowAdjusts = new float[9];

		if (largeInsetTop == 0 && insets.top > 0){
				//smaller non-notch cutouts are of varying size and may obstruct various UI elements
				// some are small hole punches, some are huge dynamic islands
				RectF cutout = Game.platform.getDisplayCutout().scale(1f / defaultZoom);
				//if the cutout is positioned to obstruct the hero portrait in the status pane
				if (cutout.top < 30
						&& cutout.left < 20
						&& cutout.right > 12) {
					heroPaneExtraWidth = Math.max(heroPaneExtraWidth, cutout.right-12);
					//make sure we have space to actually move it though
					heroPaneExtraWidth = Math.min(heroPaneExtraWidth, uiCamera.width - PixelScene.MIN_WIDTH_P);
				}
				//if the cutout is positioned to obstruct the menu bar
				else if (cutout.top < 20
						&& cutout.left < menuBarMaxLeft + MenuPane.WIDTH
						&& cutout.right > menuBarMaxLeft) {
					menuBarMaxLeft = Math.min(menuBarMaxLeft, cutout.left - MenuPane.WIDTH);
					//make sure we have space to actually move it though
					menuBarMaxLeft = Math.max(menuBarMaxLeft, PixelScene.MIN_WIDTH_P-MenuPane.WIDTH);
				}
				//if the cutout is positioned to obstruct the HP bar
				else if (cutout.left < 78
						&& cutout.top < 4
						&& cutout.right > 32) {
					//subtract starting position, but add a bit back due to end of bar
					hpBarMaxWidth = Math.round(cutout.left - 32 + 4);
					hpBarMaxWidth = Math.max(hpBarMaxWidth, 21); //cannot go below 21 (30 effective)
				}
				//if the cutout is positioned to obstruct the buff bar
				if (cutout.left < 84
						&& cutout.top < 10
						&& cutout.right > 32
						&& cutout.bottom > 11) {
					int i = 1;
					int rowTop = 11;
					//in most cases this just obstructs one row, but dynamic island can block more =S
					while (cutout.bottom > rowTop){
						if (i == 1 || cutout.bottom > rowTop+2 ) { //always shorten first row
							//subtract starting position, add a bit back to allow slight overlap
							buffBarRowLimits[i] = cutout.left - 32 + 3;
						} else {
							//if row is only slightly cut off, lower it instead of limiting width
							buffBarRowAdjusts[i] = cutout.bottom - rowTop + 1;
							rowTop += buffBarRowAdjusts[i];
						}
						i++;
						rowTop += 8;
					}
				}
		}

		float screentop = largeInsetTop;
		if (screentop == 0 && uiSize == 0){
			screentop--; //on mobile UI, if we render in fullscreen, clip the top 1px;
		}

		menu = new MenuPane();
		menu.camera = uiCamera;
		menu.setPos( menuBarMaxLeft, screentop);
		add(menu);

		float extraRight = uiCamera.width - (menuBarMaxLeft + MenuPane.WIDTH);
		if (extraRight > 0){
			SkinnedBlock bar = new SkinnedBlock(extraRight, 20, TextureCache.createSolid(0x88000000));
			bar.x = uiCamera.width - extraRight;
			bar.camera = uiCamera;
			add(bar);

			PointerArea blocker = new PointerArea(uiCamera.width - extraRight, 0, extraRight, 20);
			blocker.camera = uiCamera;
			add(blocker);
		}

		status = new StatusPane( SPDSettings.interfaceSize() > 0 );
		status.camera = uiCamera;
		StatusPane.heroPaneExtraWidth = heroPaneExtraWidth;
		StatusPane.hpBarMaxWidth = hpBarMaxWidth;
		StatusPane.buffBarRowMaxWidths = buffBarRowLimits;
		StatusPane.buffBarRowAdjusts = buffBarRowAdjusts;
		status.setRect(insets.left, uiSize > 0 ? uiCamera.height-39-insets.bottom : screentop, uiCamera.width - insets.left - insets.right, 0 );
		add(status);

		if (uiSize < 2 && largeInsetTop != 0) {
			SkinnedBlock bar = new SkinnedBlock(uiCamera.width, largeInsetTop, TextureCache.createSolid(0x88000000));
			bar.camera = uiCamera;
			add(bar);

			PointerArea blocker = new PointerArea(0, 0, uiCamera.width, largeInsetTop);
			blocker.camera = uiCamera;
			add(blocker);
		}

		boss = new BossHealthBar();
		boss.camera = uiCamera;
		boss.setPos( (uiCamera.width - boss.width())/2, screentop + (landscape() ? 7 : 26));
		if (buffBarRowLimits[2] != 0){
			//if we potentially have a 3rd buff bar row, lower by 7px
			boss.setPos(boss.left(), boss.top() + 7);
		} else if (buffBarRowAdjusts[2] != 0){
			//
			boss.setPos(boss.left(), boss.top() + buffBarRowAdjusts[2]);
		}
		add(boss);

		resume = new ResumeIndicator();
		resume.camera = uiCamera;
		add( resume );

		action = new ActionIndicator();
		action.camera = uiCamera;
		add( action );

		loot = new LootIndicator();
		loot.camera = uiCamera;
		add( loot );

		attack = new AttackIndicator();
		attack.camera = uiCamera;
		add( attack );

		log = new GameLog();
		log.camera = uiCamera;
		log.newLine();
		add( log );

		if (uiSize > 0){
			bringToFront(status);
		}

		toolbar = new Toolbar();
		toolbar.camera = uiCamera;
		add( toolbar );

		//SPS: 两侧快捷栏（左 0-4、右 0-4，数量可调，用户裁决 2026-09）
		sideQuickBar = new SideQuickBar();
		sideQuickBar.camera = uiCamera;
		add( sideQuickBar );

		//SPSEXPD: 左下角快捷操作按钮（按需出现，各自可在「游戏辅助」里开关）
		quickActionBar = new QuickActionBar();
		quickActionBar.camera = uiCamera;
		add( quickActionBar );

		if (uiSize == 2) {
			inventory = new InventoryPane();
			inventory.camera = uiCamera;
			inventory.setPos(uiCamera.width - inventory.width() - insets.right, uiCamera.height - inventory.height() - insets.bottom);
			add(inventory);

			toolbar.setRect( insets.left, uiCamera.height - toolbar.height() - inventory.height() - insets.bottom, uiCamera.width - insets.right, toolbar.height() );
		} else {
			toolbar.setRect( insets.left, uiCamera.height - toolbar.height() - insets.bottom, uiCamera.width - insets.right, toolbar.height() );
		}

		//SPS: 两侧快捷栏占满内容高、贴左右边缘（随 insets 避让安全区）
		sideQuickBar.setRect( insets.left, 0, uiCamera.width - insets.left - insets.right, uiCamera.height - insets.bottom );

		//SPSEXPD: 快捷操作按钮与左侧快捷栏的定位（设置里可互换位置，切换后立即生效）
		refreshQuickActionLayout();

		if (insets.bottom > 0){
			SkinnedBlock bar = new SkinnedBlock(uiCamera.width, insets.bottom, TextureCache.createSolid(0x88000000));
			bar.camera = uiCamera;
			bar.y = uiCamera.height - insets.bottom;
			add(bar);

			PointerArea blocker = new PointerArea(0, uiCamera.height - insets.bottom, uiCamera.width, insets.bottom);
			blocker.camera = uiCamera;
			add(blocker);
		}

		layoutTags();

		switch (InterlevelScene.mode) {
			case RESURRECT:
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				ScrollOfTeleportation.appearVFX( Dungeon.hero );
				SpellSprite.show(Dungeon.hero, SpellSprite.ANKH);
				new Flare( 5, 16 ).color( 0xFFFF00, true ).show( hero, 4f ) ;
				break;
			case RETURN:
				if (Dungeon.level.pit[Dungeon.hero.pos] && !Dungeon.hero.flying){
					//delay this so falling into the chasm processes properly
					ShatteredPixelDungeon.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							ScrollOfTeleportation.appearVFX(Dungeon.hero);
						}
					});
				} else {
					ScrollOfTeleportation.appearVFX(Dungeon.hero);
				}
				break;
			case DESCEND:
			case FALL:
				if (Dungeon.hero.isAlive()) {
					Badges.validateNoKilling();
				}
				break;
		}

		ArrayList<Item> dropped = Dungeon.droppedItems.get( Dungeon.depth );
		if (dropped != null) {
			for (Item item : dropped) {
				int pos = Dungeon.level.randomRespawnCell( null );
				if (pos == -1) pos = Dungeon.level.entrance();
				if (item instanceof Potion) {
					((Potion) item).shatter(pos);
				} else if (item instanceof Plant.Seed && !Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
					GroundItems.plant( Dungeon.level, (Plant.Seed) item, pos);
				} else if (item instanceof Honeypot) {
					Dungeon.level.drop(((Honeypot) item).shatter(null, pos), pos);
				} else {
					Dungeon.level.drop(item, pos);
				}
			}
			Dungeon.droppedItems.remove( Dungeon.depth );
		}

		Dungeon.hero.next();

		switch (InterlevelScene.mode){
			case FALL: case DESCEND: case CONTINUE: case LEARN:
				Camera.main.snapTo(hero.center().x, hero.center().y - DungeonTilemap.SIZE * (defaultZoom/Camera.main.zoom));
				break;
			case ASCEND:
				Camera.main.snapTo(hero.center().x, hero.center().y + DungeonTilemap.SIZE * (defaultZoom/Camera.main.zoom));
				break;
			default:
				Camera.main.snapTo(hero.center().x, hero.center().y);
		}
		Camera.main.panTo(hero.center(), 2.5f);

		if (InterlevelScene.mode != InterlevelScene.Mode.NONE) {
			int displayDepth = Dungeon.displayDepth();
			if (InterlevelScene.mode == InterlevelScene.Mode.LEARN) {
				// The legacy tutorial starts without a normal floor-transition message.
			} else if (displayDepth >= Statistics.deepestFloor
					&& (InterlevelScene.mode == InterlevelScene.Mode.DESCEND || InterlevelScene.mode == InterlevelScene.Mode.FALL)) {
				GLog.h(Messages.get(this, "descend"), displayDepth);
				Sample.INSTANCE.play(Assets.Sounds.DESCEND);
				
				for (Char ch : Actor.chars()){
					if (ch instanceof DriedRose.GhostHero){
						((DriedRose.GhostHero) ch).sayAppeared();
					}
				}

				int spawnersAbove = Statistics.spawnersAlive;
				if (spawnersAbove > 0 && Dungeon.depth <= 39) {
					for (Mob m : Dungeon.level.mobs()) {
						if (m instanceof DemonSpawner && ((DemonSpawner) m).spawnRecorded) {
							spawnersAbove--;
						}
					}

					if (spawnersAbove > 0) {
						GLog.newLine();
						if (Dungeon.bossLevel()) {
							GLog.n(Messages.get(this, "spawner_warn_final"));
						} else {
							GLog.n(Messages.get(this, "spawner_warn"));
						}
					}
				}
				
			} else if (InterlevelScene.mode == InterlevelScene.Mode.RESET) {
				GLog.h(Messages.get(this, "warp"));
			} else if (InterlevelScene.mode == InterlevelScene.Mode.RESURRECT) {
				GLog.h(Messages.get(this, "resurrect"), displayDepth);
			} else {
				GLog.h(Messages.get(this, "return"), displayDepth);
			}

			if (Dungeon.hero.hasTalent(Talent.ROGUES_FORESIGHT)
					&& Dungeon.level instanceof RegularLevel && Dungeon.branch == 0){
				int reqSecrets = Dungeon.level.feeling == Level.Feeling.SECRETS ? 2 : 1;
				for (Room r : ((RegularLevel) Dungeon.level).rooms()){
					if (r instanceof SecretRoom) reqSecrets--;
				}

				//75%/100% chance, use level's seed so that we get the same result for the same level
				//offset seed slightly to avoid output patterns
				Random.pushGenerator(Dungeon.seedCurDepth()+1);
					if (reqSecrets <= 0 && Random.Int(4) < 2+Dungeon.hero.pointsInTalent(Talent.ROGUES_FORESIGHT)){
						GLog.p(Messages.get(this, "secret_hint"));
					}
				Random.popGenerator();
			}

			boolean unspentTalents = false;
			for (int i = 1; i <= Dungeon.hero.talents.size(); i++){
				if (Dungeon.hero.talentPointsAvailable(i) > 0){
					unspentTalents = true;
					break;
				}
			}
			if (unspentTalents){
				GLog.newLine();
				GLog.w( Messages.get(Dungeon.hero, "unspent") );
				StatusPane.talentBlink = 10f;
				WndHero.lastIdx = 1;
			}

			switch (Dungeon.level.feeling) {
				case CHASM:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.CHASM_FLOOR);
					break;
				case WATER:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.WATER_FLOOR);
					break;
				case GRASS:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.GRASS_FLOOR);
					break;
				case DARK:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.DARK_FLOOR);
					break;
				case LARGE:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.LARGE_FLOOR);
					break;
				case TRAPS:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.TRAPS_FLOOR);
					break;
				case SECRETS:
					GLog.w(Dungeon.level.feeling.desc());
					Notes.add(Notes.Landmark.SECRETS_FLOOR);
					break;
			}

			for (Mob mob : Dungeon.level.mobs()) {
				if (!mob.buffs(ChampionEnemy.class).isEmpty()) {
					GLog.w(Messages.get(ChampionEnemy.class, "warn"));
				}
			}

			if (Dungeon.hero.buff(AscensionChallenge.class) != null){
				Dungeon.hero.buff(AscensionChallenge.class).saySwitch();
			}

			DimensionalSundial.sundialWarned = true;
			if (DimensionalSundial.spawnMultiplierAtCurrentTime() > 1){
				GLog.w(Messages.get(DimensionalSundial.class, "warning"));
			} else {
				DimensionalSundial.sundialWarned = false;
			}

			InterlevelScene.mode = InterlevelScene.Mode.NONE;

			
		}

		//Tutorial
		if (SPDSettings.intro()){

			if (Document.ADVENTURERS_GUIDE.isPageFound(Document.GUIDE_INTRO)){
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_INTRO);
			} else if (ControllerHandler.isControllerConnected()) {
				GameLog.wipe();
				GLog.p(Messages.get(GameScene.class, "tutorial_move_controller"));
			} else if (SPDSettings.interfaceSize() == 0) {
				GameLog.wipe();
				GLog.p(Messages.get(GameScene.class, "tutorial_move_mobile"));
			} else {
				GameLog.wipe();
				GLog.p(Messages.get(GameScene.class, "tutorial_move_desktop"));
			}
			toolbar.visible = toolbar.active = false;
			status.visible = status.active = false;
			if (inventory != null) inventory.visible = inventory.active = false;
		}

		if (!SPDSettings.intro() &&
				Rankings.INSTANCE.totalNumber > 0 &&
				!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_DIEING)){
			GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_DIEING);
		}

		TrinketCatalyst cata = Dungeon.hero.belongings.getItem(TrinketCatalyst.class);
		if (cata != null && cata.hasRolledTrinkets()){
			addToFront(new TrinketCatalyst.WndTrinket(cata));
		}

		if (!invVisible) toggleInvPane();
		fadeIn();

		//re-show WndResurrect if needed
		if (!Dungeon.hero.isAlive()){
			//check if hero has an unblessed ankh
			Ankh ankh = null;
			for (Ankh i : Dungeon.hero.belongings.getAllItems(Ankh.class)){
				if (!i.isBlessed()){
					ankh = i;
				}
			}
			if (ankh != null && GamesInProgress.gameExists(GamesInProgress.curSlot)) {
				add(new WndResurrect(ankh));
			} else {
				gameOver();
			}
		}

	}
	
	public void destroy() {
		
		//tell the actor thread to finish, then wait for it to complete any actions it may be doing.
		if (!waitForActorThread( 4500, true )){
			Throwable t = new Throwable();
			t.setStackTrace(actorThread.getStackTrace());
			throw new RuntimeException("timeout waiting for actor thread! ", t);
		}

		Emitter.freezeEmitters = false;
		
		scene = null;
		Badges.saveGlobal();
		Journal.saveGlobal();
		
		super.destroy();
	}
	
	public static void endActorThread(){
		if (actorThread != null && actorThread.isAlive()){
			Actor.keepActorThreadAlive = false;
			actorThread.interrupt();
		}
	}

	public boolean waitForActorThread(int msToWait, boolean interrupt){
		if (actorThread == null || !actorThread.isAlive()) {
			return true;
		}
		synchronized (actorThread) {
			if (interrupt) actorThread.interrupt();
			try {
				actorThread.wait(msToWait);
			} catch (InterruptedException e) {
				ShatteredPixelDungeon.reportException(e);
			}
			return !Actor.processing();
		}
	}
	
	@Override
	public synchronized void onPause() {
		try {
			if (!Dungeon.hero.ready) waitForActorThread(500, false);
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}
	}

	private static Thread actorThread;
	
	//sometimes UI changes can be prompted by the actor thread.
	// We queue any removed element destruction, rather than destroying them in the actor thread.
	private ArrayList<Gizmo> toDestroy = new ArrayList<>();

	//the actor thread processes at a maximum of 60 times a second
	//this caps the speed of resting for higher refresh rate displays
	private float notifyDelay = 1/60f;

	public static boolean updateItemDisplays = false;

	public static boolean tagDisappeared = false;
	public static boolean updateTags = false;

	private static float waterOfs = 0;

	@Override
	public synchronized void update() {
		lastOffset = null;

		if (updateItemDisplays){
			updateItemDisplays = false;
			QuickSlotButton.refresh();
			InventoryPane.refresh();
			if (ActionIndicator.action instanceof MeleeWeapon.Charger) {
				//Champion weapon swap uses items, needs refreshing whenever item displays are updated
				ActionIndicator.refresh();
			}
		}

		if (Dungeon.hero == null || scene == null) {
			return;
		}

		//SPS: 路径目标若是生物，每帧跟随它的新位置（生物死了就收起预览）
		if (pathTrackedMob != null && pathTarget >= 0) {
			if (!pathTrackedMob.isAlive()) {
				clearHeroPath();
			} else if (pathTrackedMob.pos != pathTarget) {
				pathTarget = pathTrackedMob.pos;
				refreshHeroPath();
			}
		}

		super.update();

		if (notifyDelay > 0) notifyDelay -= Game.elapsed;

		if (!Emitter.freezeEmitters) {
			waterOfs -= 5 * Game.elapsed;
			water.offsetTo( 0, waterOfs );
			waterOfs = water.offsetY(); //re-assign to account for auto adjust
		}

		if (!Actor.processing() && Dungeon.hero.isAlive()) {
			if (actorThread == null || !actorThread.isAlive()) {
				
				actorThread = new Thread() {
					@Override
					public void run() {
						Actor.process();
					}
				};

				//if cpu cores are limited, game should prefer drawing the current frame
				if (Runtime.getRuntime().availableProcessors() == 1) {
					actorThread.setPriority(Thread.NORM_PRIORITY - 1);
				}
				actorThread.setName("SHPD Actor Thread");
				Thread.currentThread().setName("SHPD Render Thread");
				Actor.keepActorThreadAlive = true;
				actorThread.start();
			} else if (notifyDelay <= 0f) {
				notifyDelay += 1/60f;
				synchronized (actorThread) {
					actorThread.notify();
				}
			}
		}

		if (Dungeon.hero.ready && Dungeon.hero.paralysed == 0) {
			log.newLine();
		}

		if (updateTags){
			tagAttack = attack.active;
			tagLoot = loot.visible;
			tagAction = action.visible;
			tagResume = resume.visible;

			layoutTags();

		} else if (tagAttack != attack.active ||
				tagLoot != loot.visible ||
				tagAction != action.visible ||
				tagResume != resume.visible) {

			boolean tagAppearing = (attack.active && !tagAttack) ||
									(loot.visible && !tagLoot) ||
									(action.visible && !tagAction) ||
									(resume.visible && !tagResume);

			tagAttack = attack.active;
			tagLoot = loot.visible;
			tagAction = action.visible;
			tagResume = resume.visible;

			//if a new tag appears, re-layout tags immediately
			//otherwise, wait until the hero acts, so as to not suddenly change their position
			if (tagAppearing)   layoutTags();
			else                tagDisappeared = true;

		}

		cellSelector.enable(Dungeon.hero.ready);

		if (!toDestroy.isEmpty()) {
			for (Gizmo g : toDestroy) {
				g.destroy();
			}
			toDestroy.clear();
		}
	}

	private static Point lastOffset = null;

	@Override
	public synchronized Gizmo erase (Gizmo g) {
		Gizmo result = super.erase(g);
		if (result instanceof Window){
			lastOffset = ((Window) result).getOffset();
		}
		return result;
	}

	private boolean tagAttack    = false;
	private boolean tagLoot      = false;
	private boolean tagAction    = false;
	private boolean tagResume    = false;

	//SPSEXPD: 重排左下/左上快捷操作按钮与左侧快捷栏——场景创建时调用，
	//设置页切换「交换左侧快捷栏与快捷操作按钮的位置」后也会立即调用（静态，便于窗口层触发）
	public static void refreshQuickActionLayout() {

		if (scene == null || scene.quickActionBar == null) return;

		RectF insets = scene.getCommonInsets();
		int uiSize = SPDSettings.interfaceSize();
		boolean swapped = SPDSettings.swapAuxBar();

		//SPSEXPD: 日志基线（取法与 layoutTags() 一致），再整体让出日志文本区高度 + 4px 间距。
		//文本区高度按运行时动态计算（行数上限随界面模式、单行高度随设备字号），避免不同设备下遮挡日志上半部分
		float logBottom = (uiSize == 0 ? scene.toolbar.top() : scene.status.top()) - 2;
		float bottomAnchor = logBottom - GameLog.maxTextHeight() - 4;

		//交换后按钮到左上：必须避开顶部状态栏（全尺寸模式下状态栏与头像在顶部）
		float topAnchor = (uiSize == 0) ? scene.status.bottom() + 4 : SideQuickBar.TOP_OFFSET;
		float ax = insets.left + 1;
		//默认：按钮在下、左栏在左上；交换后：按钮到左上，左栏改成底部对齐
		float ay = swapped ? Math.max( SideQuickBar.TOP_OFFSET, topAnchor ) : bottomAnchor - QuickActionBar.BTN_H;

		SideQuickBar.topReserve = 0;
		SideQuickBar.leftBottomAnchor = swapped ? bottomAnchor : 0;
		SideQuickBar.updateLayout();

		//SPSEXPD: 按钮位于上方时改为向下生长（否则会向上越过预留区）
		scene.quickActionBar.setGrowDown( swapped );
		scene.quickActionBar.setPos( ax, ay );
		scene.quickActionBar.refresh();   //SPSEXPD: 定位后立即排好按钮
	}

	public static void layoutTags() {

		updateTags = false;

		if (scene == null) return;

		//move the camera center up a bit if we're on full UI and it is taking up lots of space
		if (scene.inventory != null && scene.inventory.visible
				&& (uiCamera.width < 460 && uiCamera.height < 300)){
			Camera.main.setCenterOffset(0, Math.min(300-uiCamera.height, 460-uiCamera.width) / Camera.main.zoom);
		} else {
			Camera.main.setCenterOffset(0, 0);
		}
		//Camera.main.panTo(Dungeon.hero.sprite.center(), 5f);

		//adjust spacing for elements based on display cutouts
		// We use ALL here as some elements can be a fair but up the side of the screen
		RectF insets = Game.platform.getSafeInsets( PlatformSupport.INSET_ALL );
		insets = insets.scale(1f / uiCamera.zoom);

		boolean tagsOnLeft = SPDSettings.flipTags();
		float tagWidth = Tag.SIZE + (tagsOnLeft ? insets.left : insets.right);
		float tagLeft = tagsOnLeft ? 0 : uiCamera.width - tagWidth;

		float y = SPDSettings.interfaceSize() == 0 ? scene.toolbar.top()-2 : scene.status.top()-2;
		if (SPDSettings.interfaceSize() == 0){
			if (tagsOnLeft) {
				scene.log.setRect(tagWidth, y, uiCamera.width - tagWidth - insets.right, 0);
			} else {
				scene.log.setRect(insets.left, y, uiCamera.width - tagWidth - insets.left, 0);
			}
		} else {
			if (tagsOnLeft) {
				scene.log.setRect(tagWidth, y, 160 - tagWidth, 0);
			} else {
				scene.log.setRect(insets.left, y, 160 - insets.left, 0);
			}
		}

		float pos = scene.toolbar.top();
		if (tagsOnLeft && SPDSettings.interfaceSize() > 0){
			pos = scene.status.top();
		}

		if (scene.tagAttack){
			scene.attack.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
			scene.attack.flip(tagsOnLeft);
			pos = scene.attack.top();
		}

		if (scene.tagLoot) {
			scene.loot.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
			scene.loot.flip(tagsOnLeft);
			pos = scene.loot.top();
		}

		if (scene.tagAction) {
			scene.action.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
			scene.action.flip(tagsOnLeft);
			pos = scene.action.top();
		}

		if (scene.tagResume) {
			scene.resume.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
			scene.resume.flip(tagsOnLeft);
		}
	}
	
	@Override
	protected void onBackPressed() {
		if (!cancel()) {
			add( new WndGame() );
		}
	}

	public void addCustomTile( CustomTilemap visual){
		customTiles.add( visual.create() );
	}

	public void addCustomTerrain(CustomTilemap visual){
		customTerrain.add( visual.create() );
	}

	public void addCustomWall( CustomTilemap visual){
		customWalls.add( visual.create() );
	}

	private void addHeapSprite( Heap heap ) {
		ItemSprite sprite = heap.sprite = (ItemSprite)heaps.recycle( ItemSprite.class );
		sprite.revive();
		sprite.link( heap );
		heaps.add( sprite );
	}
	
	private void addDiscardedSprite( Heap heap ) {
		heap.sprite = (DiscardedItemSprite)heaps.recycle( DiscardedItemSprite.class );
		heap.sprite.revive();
		heap.sprite.link( heap );
		heaps.add( heap.sprite );
	}
	
	private void addBlobSprite( final Blob gas ) {
		if (gas.emitter == null) {
			gases.add( new BlobEmitter( gas ) );
		}
	}
	
	private synchronized void addMobSprite( Mob mob ) {
		CharSprite sprite = mob.sprite();
		sprite.visible = sprite.visibleOutOfFFOV || Dungeon.level.heroFOV[mob.pos];
		mobs.add( sprite );
		sprite.link( mob );
		sortMobSprites();
	}

	//ensures that mob sprites are drawn from top to bottom, in case of overlap
	public static void sortMobSprites(){
		if (scene != null){
			synchronized (scene) {
				scene.mobs.sort(new Comparator() {
					@Override
					public int compare(Object a, Object b) {
						//elements that aren't visual go to the end of the list
						if (a instanceof Visual && b instanceof Visual) {
							return (int) Math.signum((((Visual) a).y + ((Visual) a).height())
									- (((Visual) b).y + ((Visual) b).height()));
						} else if (a instanceof Visual){
							return -1;
						} else if (b instanceof Visual){
							return 1;
						} else {
							return 0;
						}
					}
				});
			}
		}
	}
	
	private synchronized void prompt( String text ) {
		
		if (prompt != null) {
			prompt.killAndErase();
			toDestroy.add(prompt);
			prompt = null;
		}
		
		if (text != null) {
			prompt = new Toast( text ) {
				@Override
				protected void onClose() {
					cancel();
				}
			};
			prompt.camera = uiCamera;
			prompt.setPos( (uiCamera.width - prompt.width()) / 2, uiCamera.height - 60 );

			if (inventory != null && inventory.visible && prompt.right() > inventory.left() - 10){
				prompt.setPos(inventory.left() - prompt.width() - 10, prompt.top());
			}

			add( prompt );
		}
	}
	
	private void showBanner( Banner banner ) {
		banner.camera = uiCamera;

		float offset = Camera.main.centerOffset.y;
		banner.x = align( uiCamera, (uiCamera.width - banner.width) / 2 );
		banner.y = align( uiCamera, (uiCamera.height - banner.height) / 2 - 32 - offset );

		addToFront( banner );
	}
	
	// -------------------------------------------------------
	
	public static void add( Blob gas ) {
		Actor.add( gas );
		if (scene != null) {
			scene.addBlobSprite( gas );
		}
	}
	
	public static void add( Heap heap ) {
		if (scene != null) {
			//heaps that aren't added as part of levelgen don't count for exploration bonus
			heap.autoExplored = true;
			scene.addHeapSprite( heap );
		}
	}
	
	public static void discard( Heap heap ) {
		if (scene != null) {
			scene.addDiscardedSprite( heap );
		}
	}
	
	public static void add( Mob mob ) {
		add( mob, 0);
	}

	public static void addSprite( Mob mob ) {
		scene.addMobSprite( mob );
	}
	
	public static void add( Mob mob, float delay ) {
		Dungeon.level.mobs().add( mob );
		//mobs added on partial turns wait until next full turn to act
		delay = (float)Math.ceil(Actor.now() + delay) - Actor.now();
		if (scene != null) {
			scene.addMobSprite(mob);
			Actor.addDelayed(mob, delay);
			mob.spendToWhole();
		}
	}
	
	public static void add( EmoIcon icon ) {
		scene.emoicons.add( icon );
	}
	
	public static void add( CharHealthIndicator indicator ){
		if (scene != null) scene.healthIndicators.add(indicator);
	}
	
	public static void add( CustomTilemap t, boolean wall ){
		if (scene == null) return;
		if (wall){
			scene.addCustomWall(t);
		} else {
			scene.addCustomTile(t);
		}
	}
	
	public static void effect( Visual effect ) {
		if (scene != null) scene.effects.add( effect );
	}

	public static void effectOverFog( Visual effect ) {
		if (scene != null) scene.overFogEffects.add( effect );
	}
	
	public static CheckedCell checkedCell( int pos, int source ){
		if (scene != null) {
			CheckedCell check = (CheckedCell) scene.checkedCells.recycle(CheckedCell.class);
			check.reset(pos, source);
			return check;
		} else {
			return null;
		}
	}

	public static TargetedCell targetedCell(int pos, int color, float delay){
		return targetedCell(pos, delay);
	}

	public static TargetedCell targetedCell(int pos, float delay){
		if (scene != null) {
			TargetedCell cell;
			synchronized (TargetedCell.cells) {
				if (TargetedCell.cells.containsKey(pos)) {
					cell = TargetedCell.cells.get(pos);
					cell.reset(pos, Actor.now()+delay);
					return cell;
				}
			}
			cell = (TargetedCell) scene.targetedCells.recycle(TargetedCell.class);
			cell.reset(pos, Actor.now()+delay);
			return cell;
		} else {
			return null;
		}
	}

	public static Ripple ripple( int pos ) {
		if (scene != null) {
			Ripple ripple = (Ripple) scene.ripples.recycle(Ripple.class);
			ripple.reset(pos);
			return ripple;
		} else {
			return null;
		}
	}
	
	public static synchronized SpellSprite spellSprite() {
		return (SpellSprite)scene.spells.recycle( SpellSprite.class );
	}
	
	public static synchronized Emitter emitter() {
		if (scene != null) {
			Emitter emitter = (Emitter)scene.emitters.recycle( Emitter.class );
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}

	public static boolean hasActiveScene() {
		return scene != null;
	}

	public static synchronized Emitter floorEmitter() {
		if (scene != null) {
			Emitter emitter = (Emitter)scene.floorEmitters.recycle( Emitter.class );
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}
	
	public static FloatingText status() {
		return scene != null ? (FloatingText)scene.statuses.recycle( FloatingText.class ) : null;
	}
	
	public static void pickUp( Item item, int pos ) {
		if (scene != null) scene.toolbar.pickup( item, pos );
	}

	public static void pickUpJournal( Item item, int pos ) {
		if (scene != null) scene.menu.pickup( item, pos );
	}

	public static void flashForDocument( Document doc, String page ){
		if (scene != null) {
			if (doc == Document.ADVENTURERS_GUIDE){
				if (!page.equals(Document.GUIDE_INTRO)) {
					if (SPDSettings.interfaceSize() == 0) {
						GLog.p(Messages.get(Guidebook.class, "hint_mobile"));
					} else {
						GLog.p(Messages.get(Guidebook.class, "hint_desktop", KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.JOURNAL, ControllerHandler.isControllerConnected()))));
					}
				}
				Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(Guidebook.class, "hint_status"));
			}
			scene.menu.flashForPage( doc, page );
		}
	}

	public static void endIntro(){
		if (scene != null){
			SPDSettings.intro(false);
			scene.add(new Tweener(scene, 2f){
				@Override
				protected void updateValues(float progress) {
					if (progress <= 0.5f) {
						scene.status.alpha(2*progress);
						scene.status.visible = scene.status.active = true;
						scene.toolbar.visible = scene.toolbar.active = false;
						if (scene.inventory != null) scene.inventory.visible = scene.inventory.active = false;
					} else {
						scene.status.alpha(1f);
						scene.status.visible = scene.status.active = true;
						scene.toolbar.alpha((progress - 0.5f)*2);
						scene.toolbar.visible = scene.toolbar.active = true;
						if (scene.inventory != null){
							scene.inventory.visible = scene.inventory.active = true;
							scene.inventory.alpha((progress - 0.5f)*2);
						}
					}
				}
			});
			GameLog.wipe();
			if (SPDSettings.interfaceSize() == 0){
				GLog.p(Messages.get(GameScene.class, "tutorial_ui_mobile"));
			} else {
				GLog.p(Messages.get(GameScene.class, "tutorial_ui_desktop",
						KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.HERO_INFO, ControllerHandler.isControllerConnected())),
						KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.INVENTORY, ControllerHandler.isControllerConnected()))));
			}

			//clear hidden doors, it's floor 1 so there are only the entrance ones
			for (int i = 0; i < Dungeon.level.length(); i++){
				if (Dungeon.level.map[i] == Terrain.SECRET_DOOR){
					CellFlags.discover( Dungeon.level, i);
					discoverTile(i, Terrain.SECRET_DOOR);
				}
			}
		}
	}
	
	public static void updateKeyDisplay(){
		if (scene != null && scene.menu != null) scene.menu.updateKeys();
	}

	public static void showlevelUpStars(){
		if (scene != null && scene.status != null) scene.status.showStarParticles();
	}

	public static void updateAvatar(){
		if (scene != null && scene.status != null) scene.status.updateAvatar();
	}

	public static void resetMap() {
		if (scene != null) {
			scene.tiles.map(Dungeon.level.map, Dungeon.level.width() );
			//SPS: 回 2D —— 已停用层置空，刷新前守卫
			if (scene.occlusion != null) scene.occlusion.map(Dungeon.level.map, Dungeon.level.width() );
			if (scene.visualGrid != null) scene.visualGrid.map(Dungeon.level.map, Dungeon.level.width() );
			scene.terrainFeatures.map(Dungeon.level.map, Dungeon.level.width() );
			if (scene.waterEdges != null) scene.waterEdges.map(Dungeon.level.map, Dungeon.level.width() );
			if (scene.chasmEdges != null) scene.chasmEdges.map(Dungeon.level.map, Dungeon.level.width() );
			if (scene.raisedTerrain != null) scene.raisedTerrain.map(Dungeon.level.map, Dungeon.level.width() );
			if (scene.walls != null) scene.walls.map(Dungeon.level.map, Dungeon.level.width() );
		}
		updateFog();
	}

	//updates the whole map
	public static void updateMap() {
		if (scene != null) {
			scene.tiles.updateMap();
			if (scene.occlusion != null) scene.occlusion.updateMap();
			if (scene.visualGrid != null) scene.visualGrid.updateMap();
			scene.terrainFeatures.updateMap();
			if (scene.waterEdges != null) scene.waterEdges.updateMap();
			if (scene.chasmEdges != null) scene.chasmEdges.updateMap();
			if (scene.raisedTerrain != null) scene.raisedTerrain.updateMap();
			if (scene.walls != null) scene.walls.updateMap();
			updateFog();
		}
	}
	
	public static void updateMap( int cell ) {
		if (scene != null) {
			scene.tiles.updateMapCell( cell );
			if (scene.occlusion != null) scene.occlusion.updateMapCell( cell );
			if (scene.visualGrid != null) scene.visualGrid.updateMapCell( cell );
			scene.terrainFeatures.updateMapCell( cell );
			//水缝合边跨格（邻格变化影响本格帧），连同邻格一起刷新
			if (scene.waterEdges != null) {
				scene.waterEdges.updateMapCell( cell );
				for (int i = 0; i < PathFinder.CIRCLE4.length; i++) {
					int n = cell + PathFinder.CIRCLE4[i];
					if (n >= 0 && n < Dungeon.level.map.length) {
						scene.waterEdges.updateMapCell( n );
					}
				}
			}
			//深渊缝合边只看上方格：本格或上下邻格变化都要刷新
			if (scene.chasmEdges != null) {
				scene.chasmEdges.updateMapCell( cell );
				scene.chasmEdges.updateMapCell( cell - Dungeon.level.width() );
				scene.chasmEdges.updateMapCell( cell + Dungeon.level.width() );
			}
			if (scene.raisedTerrain != null) scene.raisedTerrain.updateMapCell( cell );
			if (scene.walls != null) scene.walls.updateMapCell( cell );
			//update adjacent cells too
			updateFog( cell, 1 );
		}
	}

	public static void plantSeed( int cell ) {
		if (scene != null) {
			scene.terrainFeatures.growPlant( cell );
		}
	}

	public static void discoverTile( int pos, int oldValue ) {
		if (scene != null) {
			scene.tiles.discover( pos, oldValue );
			if (scene.waterEdges != null) scene.waterEdges.discover( pos, oldValue );
			if (scene.chasmEdges != null) scene.chasmEdges.discover( pos, oldValue );
		}
	}
	
	public static void show( Window wnd ) {
		if (scene != null) {
			cancel();

			//If a window is already present (or was just present)
			// then inherit the offset it had
			if (scene.inventory != null && scene.inventory.visible){
				Point offsetToInherit = null;
				for (Gizmo g : scene.members){
					if (g instanceof Window) offsetToInherit = ((Window) g).getOffset();
				}
				if (lastOffset != null) {
					offsetToInherit = lastOffset;
				}
				if (offsetToInherit != null && !offsetToInherit.isZero()) {
					wnd.offset(offsetToInherit);
					wnd.boundOffsetWithMargin(3);
				}
			}

			scene.addToFront(wnd);
		}
	}

	public static boolean showingWindow(){
		if (scene == null) return false;

		for (Gizmo g : scene.members){
			if (g instanceof Window) return true;
		}

		return false;
	}

	public static boolean interfaceBlockingHero(){
		if (scene == null) return false;

		if (showingWindow()) return true;

		if (scene.inventory != null && scene.inventory.isSelecting()){
			return true;
		}

		return false;
	}

	public static void toggleInvPane(){
		if (scene != null && scene.inventory != null){
			if (scene.inventory.visible){
				scene.inventory.visible = scene.inventory.active = invVisible = false;
				scene.toolbar.setPos(scene.toolbar.left(), uiCamera.height-scene.toolbar.height());
			} else {
				scene.inventory.visible = scene.inventory.active = invVisible = true;
				scene.toolbar.setPos(scene.toolbar.left(), scene.inventory.top()-scene.toolbar.height());
			}
			layoutTags();
		}
	}

	public static void centerNextWndOnInvPane(){
		if (scene != null && scene.inventory != null && scene.inventory.visible){
			lastOffset = new Point((int)scene.inventory.centerX() - uiCamera.width/2,
					(int)scene.inventory.centerY() - uiCamera.height/2);
		}
	}

	public static void nextWndOffset(Point ofs){
		if (scene != null){
			lastOffset = ofs;
		}
	}

	public static void updateFog(){
		if (scene != null) {
			scene.fog.updateFog();
			if (scene.wallBlocking != null) scene.wallBlocking.updateMap();
		}
	}

	public static void updateFog(int x, int y, int w, int h){
		if (scene != null) {
			scene.fog.updateFogArea(x, y, w, h);
			if (scene.wallBlocking != null) scene.wallBlocking.updateArea(x, y, w, h);
		}
	}
	
	public static void updateFog( int cell, int radius ){
		if (scene != null) {
			scene.fog.updateFog( cell, radius );
			if (scene.wallBlocking != null) scene.wallBlocking.updateArea( cell, radius );
		}
	}
	
	public static void afterObserve() {
		if (scene != null) {
			for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
				if (mob.sprite != null) {
					if (mob instanceof Mimic && mob.state == mob.PASSIVE && ((Mimic) mob).stealthy() && Dungeon.level.visited[mob.pos]){
						//mimics stay visible in fog of war after being first seen
						//TODO can probably migrate this to Charsprite.visibleOutOfFFOV
						mob.sprite.visible = true;
					} else {
						mob.sprite.visible = mob.sprite.visibleOutOfFFOV || Dungeon.level.heroFOV[mob.pos];
					}
				}
				if (mob instanceof Ghoul){
					for (Ghoul.GhoulLifeLink link : mob.buffs(Ghoul.GhoulLifeLink.class)){
						link.updateVisibility();
					}
				}
			}
		}
	}

	public static void flash( int color ) {
		flash( color, true);
	}

	public static void flash( int color, boolean lightmode ) {
		if (scene != null) {
			//don't want to do this on the actor thread
			ShatteredPixelDungeon.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					//greater than 0 to account for negative values (which have the first bit set to 1)
					if (scene != null) {
						if (color > 0 && color < 0x01000000) {
							scene.fadeIn(0xFF000000 | color, lightmode);
						} else {
							scene.fadeIn(color, lightmode);
						}
					}
				}
			});
		}
	}

	public static void gameOver() {
		if (scene == null) return;

		Banner gameOver = new Banner( BannerSprites.get( BannerSprites.Type.GAME_OVER ) );
		gameOver.show( 0x000000, 2f );
		scene.showBanner( gameOver );

		StyledButton restart = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(StartScene.class, "new"), 9){
			@Override
			protected void onClick() {
				GamesInProgress.selectedClass = Dungeon.hero.heroClass;
				GamesInProgress.selectedSkin = Dungeon.hero.skin;
				GamesInProgress.selectedStyle = Dungeon.hero.combatStyle;
				GamesInProgress.curSlot = GamesInProgress.firstEmpty();
				ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
			}

			@Override
			public void update() {
				alpha((float)Math.pow(gameOver.am, 2));
				super.update();
			}
		};
		restart.icon(Icons.get(Icons.ENTER));
		restart.alpha(0);
		restart.camera = uiCamera;
		float offset = Camera.main.centerOffset.y;
		restart.setSize(Math.max(80, restart.reqWidth()), 20);
		restart.setPos(
				align(uiCamera, (restart.camera.width - restart.width()) / 2),
				align(uiCamera, (restart.camera.height - restart.height()) / 2 + 8 - offset)
		);
		scene.add(restart);

		StyledButton menu = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(WndKeyBindings.class, "menu"), 9){
			@Override
			protected void onClick() {
				GameScene.show(new WndGame());
			}

			@Override
			public void update() {
				alpha((float)Math.pow(gameOver.am, 2));
				super.update();
			}
		};
		menu.icon(Icons.get(Icons.PREFS));
		menu.alpha(0);
		menu.camera = uiCamera;
		menu.setSize(Math.max(80, menu.reqWidth()), 20);
		menu.setPos(
				align(uiCamera, (menu.camera.width - menu.width()) / 2),
				restart.bottom() + 2
		);
		scene.add(menu);
	}
	
	public static void bossSlain() {
		if (scene == null || Dungeon.hero == null) return;
		if (Dungeon.hero.isAlive()) {
			Banner bossSlain = new Banner( BannerSprites.get( BannerSprites.Type.BOSS_SLAIN ) );
			bossSlain.show( 0xFFFFFF, 0.3f, 5f );
			scene.showBanner( bossSlain );
			
			Sample.INSTANCE.play( Assets.Sounds.BOSS );
		}
	}
	
	public static void handleCell( int cell ) {
		cellSelector.select( cell, PointerEvent.LEFT );
	}
	
	public static void selectCell( CellSelector.Listener listener ) {
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener){
			cellSelector.listener.onSelect(null);
		}
		cellSelector.listener = listener;
		cellSelector.enabled = Dungeon.hero.ready;
		if (scene != null) {
			scene.prompt(listener.prompt());
		}
	}
	
	public static boolean cancelCellSelector() {
		if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
			cellSelector.resetKeyHold();
			cellSelector.cancel();
			return true;
		} else {
			return false;
		}
	}
	
	public static WndBag selectItem( WndBag.ItemSelector listener ) {
		cancel();

		if (scene != null) {
			//TODO can the inventory pane work in these cases? bad to fallback to mobile window
			if (scene.inventory != null && scene.inventory.visible && !showingWindow()){
				scene.inventory.setSelector(listener);
				return null;
			} else {
				WndBag wnd = WndBag.getBag( listener );
				show(wnd);
				return wnd;
			}
		}

		return null;
	}

	//logic for preserving inventory selection windows on scene reset (e.g. via auto-rotate)
	private static WndBag.ItemSelector savedSelector;

	@Override
	public synchronized void saveWindows() {
		if (members == null) return;

		super.saveWindows();
		if (scene != null && scene.inventory != null && scene.inventory.getSelector() != null){
			savedSelector = scene.inventory.getSelector();
		} else {
			for (Gizmo g : members.toArray(new Gizmo[0])){
				if (g instanceof WndBag){
					savedSelector = ((WndBag) g).getSelector();
				//also keeps selector active over inventory scroll cancel and upgrade window
				} else if (g instanceof InventoryScroll.WndConfirmCancel){
					savedSelector = ((InventoryScroll.WndConfirmCancel) g).getItemSelector();
				} else if (g instanceof WndUpgrade){
					savedSelector = ((WndUpgrade) g).getItemSelector();
				}
			}
		}
	}

	@Override
	public synchronized void restoreWindows() {
		super.restoreWindows();
		if (savedSelector != null){
			if (scene != null && scene.inventory != null){
				scene.inventory.setSelector(savedSelector);
			} else {
				addToFront(new WndBag(Dungeon.hero.belongings.backpack, savedSelector));
			}
			savedSelector = null;
		}
	}

	public static boolean cancel() {
		cellSelector.resetKeyHold();
		if (Dungeon.hero != null && (Dungeon.hero.curAction != null || Dungeon.hero.resting)) {
			
			Dungeon.hero.curAction = null;
			Dungeon.hero.resting = false;
			return true;
			
		} else {
			
			return cancelCellSelector();
			
		}
	}
	
	public static void ready() {
		selectCell( defaultCellListener );
		QuickSlotButton.cancel();
		InventoryPane.cancelTargeting();
		if (scene != null && scene.toolbar != null) scene.toolbar.examining = false;
		if (tagDisappeared) {
			tagDisappeared = false;
			updateTags = true;
		}
	}
	
	public static void checkKeyHold(){
		cellSelector.processKeyHold();
	}
	
	public static void resetKeyHold(){
		if (cellSelector != null) cellSelector.resetKeyHold();
	}

	public static void examineCell( Integer cell ) {
		if (cell == null
				|| cell < 0
				|| cell > Dungeon.level.length()
				|| (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell])) {
			return;
		}

		ArrayList<Object> objects = getObjectsAtCell(cell);

		if (objects.isEmpty()) {
			GameScene.show(new WndInfoCell(cell));
		} else if (objects.size() == 1){
			examineObject(objects.get(0));
		} else {
			String[] names = getObjectNames(objects).toArray(new String[0]);

			GameScene.show(new WndOptions(Icons.get(Icons.INFO),
					Messages.get(GameScene.class, "choose_examine"),
					Messages.get(GameScene.class, "multiple_examine"),
					names){
				@Override
				protected void onSelect(int index) {
					examineObject(objects.get(index));
				}
			});

		}
	}

	private static ArrayList<Object> getObjectsAtCell( int cell ){
		ArrayList<Object> objects = new ArrayList<>();

		Char ch = Actor.findChar(cell);
		if (ch != null && ch != Dungeon.hero){
			if (Dungeon.level.heroFOV[cell] || Char.hasProp(ch, Char.Property.OBJECT)){
				objects.add(ch);
			}
		}

		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap != null && heap.seen) objects.add(heap);

		Plant plant = Dungeon.level.plants.get( cell );
		if (plant != null) objects.add(plant);

		if (cell == Dungeon.hero.pos) {
			objects.add(Dungeon.hero);
		}

		Trap trap = Dungeon.level.traps.get( cell );
		if (trap != null && trap.visible) objects.add(trap);

		return objects;
	}

	private static ArrayList<String> getObjectNames( ArrayList<Object> objects ){
		ArrayList<String> names = new ArrayList<>();
		for (Object obj : objects){
			if (obj instanceof Hero)        names.add(((Hero) obj).className().toUpperCase(Locale.ENGLISH));
			else if (obj instanceof Mob)    names.add(Messages.titleCase( ((Mob)obj).name() ));
			else if (obj instanceof Heap)   names.add(Messages.titleCase( ((Heap)obj).title() ));
			else if (obj instanceof Plant)  names.add(Messages.titleCase( ((Plant) obj).name() ));
			else if (obj instanceof Trap)   names.add(Messages.titleCase( ((Trap) obj).name() ));
		}
		return names;
	}

	public static void examineObject(Object o){
		if (o == Dungeon.hero){
			GameScene.show( new WndHero() );
		} else if (o instanceof pd.actors.mobs.pets.LegacyPet){
			//SPSXPD: 伙伴投影用宠物信息窗（普通点击已改为移动 / 交换位置）
			GameScene.show(new pd.windows.WndPetInfo((pd.actors.mobs.pets.LegacyPet) o));
		} else if ( o instanceof Mob && ((Mob) o).isActive() ){
			GameScene.show(new WndInfoMob((Mob) o));
			if (o instanceof Snake && !Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_SURPRISE_ATKS)){
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_SURPRISE_ATKS);
			}
		} else if ( o instanceof Heap && !((Heap) o).isEmpty() ){
			GameScene.show(new WndInfoItem((Heap)o));
		} else if ( o instanceof Plant ){
			GameScene.show( new WndInfoPlant((Plant) o) );
			//plants can be harmful to trample, so let the player ID just by examine
			Bestiary.setSeen(o.getClass());
		} else if ( o instanceof Trap ){
			GameScene.show( new WndInfoTrap((Trap) o));
			//traps are often harmful to trigger, so let the player ID just by examine
			Bestiary.setSeen(o.getClass());
		} else {
			GameScene.show( new WndMessage( Messages.get(GameScene.class, "dont_know") ) ) ;
		}
	}

	
	/**
	 * SPS: 刷新移动路径提示。
	 * 起点固定为英雄当前格，因此走过的路径点会自然消失；到达目标后自动清除。
	 * 谨慎移动锁定了目标时始终显示（不看设置），常态下由 SPDSettings.heroPath() 控制。
	 */
	public static void refreshHeroPath(){
		if (scene == null || scene.heroPathCells == null || Dungeon.hero == null) return;

		if (pathTarget < 0){
			drawHeroPath( null );
			return;
		}

		//到达目标 -> 收起
		if (Dungeon.hero.pos == pathTarget){
			pathTarget = -1;
			drawHeroPath( null );
			return;
		}

		//谨慎移动锁定目标时始终显示；否则看设置开关
		boolean forced = cautiousMove && lockedTarget;
		if (!forced && !SPDSettings.heroPath()){
			drawHeroPath( null );
			return;
		}

		//谨慎移动要求沿途都是走过的路；判定"知不知道怎么过去"
		boolean cautious = cautiousMove && lockedTarget;
		ArrayList<Integer> path = cautious
				? HeroPath.computeExplored( Dungeon.hero.pos, pathTarget )
				: HeroPath.compute( Dungeon.hero.pos, pathTarget );

		if (path == null){
			drawHeroPath( null );
			if (cautious){
				//需要在未探索区域里穿行 -> 不显示路径，只给提示
				GLog.w( Messages.get(GameScene.class, "path_unknown") );
			}
			pathTarget = -1;
			lockedTarget = false;
			pathTrackedMob = null;
			return;
		}

		//SPS: 目标格上站着生物就记下来，之后每帧跟随它的新位置（见 update）
		if (pathTrackedMob == null) {
			Char atTarget = Actor.findChar( pathTarget );
			pathTrackedMob = (atTarget instanceof Mob) ? (Mob) atTarget : null;
		}

		drawHeroPath( path );
	}

	//SPS: 路径提示的实际绘制。标记挂独立 Group（与 targetedCells 同级），
	//不进 terrain 层 —— 否则会被后加入的自定义层遮住而看不见。
	private static void drawHeroPath( ArrayList<Integer> path ){
		if (scene == null || scene.heroPathCells == null) return;

		scene.heroPathCells.clear();
		if (path == null || path.isEmpty()) return;

		//SPS: 跳过第 0 个点（英雄当前格）——它下一步就离开了，
		//提前一格消失，避免标记跟不上英雄的脚步。
		for (int i = 1; i < path.size(); i++){
			HeroPathCell mark = (HeroPathCell) scene.heroPathCells.recycle( HeroPathCell.class );
			mark.reset( path.get(i), i == path.size() - 1 );
		}
	}

	/** SPS: 收起路径提示并解除锁定。 */
	public static void clearHeroPath(){
		pathTarget = -1;
		lockedTarget = false;
		pathTrackedMob = null;
		drawHeroPath( null );
	}

	/** SPS: 当前是否已用谨慎移动锁定了目标（再次点击同一格才真正移动）。 */
	public static boolean isCautiousLocked(){
		return cautiousMove && lockedTarget && pathTarget >= 0;
	}

	private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {

		@Override
		public void onSelect( Integer cell ) {
			//SPS: 取消选择（Esc/右键取消）时收起路径提示
			if (cell == null){
				clearHeroPath();
				return;
			}

			//SPS: 谨慎移动——首次点击只预览路径（相机不返回），再点同一格才真正移动
			if (cautiousMove && !(lockedTarget && pathTarget == cell)){
				pathTarget = cell;
				lockedTarget = true;
				//SPS: 换目标时解除对原生物的跟踪，否则 update() 会把 pathTarget 拽回生物位置，
				//导致锁定生物后无法脱离它去预设行动目的地
				pathTrackedMob = null;
				refreshHeroPath();
				return;
			}

			//正常移动：记录目标，边走边显示路径（是否显示由设置决定）
			lockedTarget = false;
			pathTarget = SPDSettings.heroPath() ? cell : -1;
			//SPS: 同上，防止旧跟踪生物劫持新目标
			pathTrackedMob = null;
			refreshHeroPath();

			if (Dungeon.hero.handle( cell )) {
				Dungeon.hero.next();
			}
		}

		@Override
		public void onRightClick(Integer cell) {
			if (cell == null
					|| cell < 0
					|| cell > Dungeon.level.length()
					|| (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell])) {
				return;
			}

			ArrayList<Object> objects = getObjectsAtCell(cell);
			ArrayList<String> textLines = getObjectNames(objects);

			//determine title and image
			String title = null;
			Image image = null;
			if (objects.isEmpty()) {
				title = WndInfoCell.cellName(cell);
				image = WndInfoCell.cellImage(cell);
			} else if (objects.size() > 1){
				title = Messages.get(GameScene.class, "multiple");
				image = Icons.get(Icons.INFO);
			} else if (objects.get(0) instanceof Hero) {
				title = textLines.remove(0);
				image = HeroSprite.avatar((Hero) objects.get(0));
			} else if (objects.get(0) instanceof Mob) {
				title = textLines.remove(0);
				image = ((Mob) objects.get(0)).sprite();
			} else if (objects.get(0) instanceof Heap) {
				title = textLines.remove(0);
				image = new ItemSprite((Heap) objects.get(0));
			} else if (objects.get(0) instanceof Plant) {
				title = textLines.remove(0);
				image = TerrainFeaturesTilemap.tile(cell, Dungeon.level.map[cell]);
			} else if (objects.get(0) instanceof Trap) {
				title = textLines.remove(0);
				image = TerrainFeaturesTilemap.tile(cell, Dungeon.level.map[cell]);
			}

			//determine first text line
			if (objects.isEmpty()) {
				textLines.add(0, Messages.get(GameScene.class, "go_here"));
			} else if (objects.get(0) instanceof Hero) {
				textLines.add(0, Messages.get(GameScene.class, "cancel"));
			} else if (objects.get(0) instanceof Mob) {
				if (((Mob) objects.get(0)).alignment != Char.Alignment.ENEMY) {
					textLines.add(0, Messages.get(GameScene.class, "interact"));
				} else {
					textLines.add(0, Messages.get(GameScene.class, "attack"));
				}
			} else if (objects.get(0) instanceof Heap) {
				switch (((Heap) objects.get(0)).type) {
					case HEAP:
						textLines.add(0, Messages.get(GameScene.class, "pick_up"));
						break;
					case FOR_SALE:
						textLines.add(0, Messages.get(GameScene.class, "purchase"));
						break;
					case FOR_LIFE:
						textLines.add(0, Messages.get(GameScene.class, "purchase"));
						break;
					default:
						textLines.add(0, Messages.get(GameScene.class, "interact"));
						break;
				}
			} else if (objects.get(0) instanceof Plant) {
				textLines.add(0, Messages.get(GameScene.class, "trample"));
			} else if (objects.get(0) instanceof Trap) {
				textLines.add(0, Messages.get(GameScene.class, "interact"));
			}

			//final text formatting
			if (objects.size() > 1){
				textLines.add(0, "_" + textLines.remove(0) + ":_ " + textLines.get(0));
				for (int i = 1; i < textLines.size(); i++){
					textLines.add(i, "_" + Messages.get(GameScene.class, "examine") + ":_ " + textLines.remove(i));
				}
			} else {
				textLines.add(0, "_" + textLines.remove(0) + "_");
				textLines.add(1, "_" + Messages.get(GameScene.class, "examine") + "_");
			}

			RightClickMenu menu = new RightClickMenu(image,
					title,
					textLines.toArray(new String[0])){
				@Override
				public void onSelect(int index) {
					if (index == 0){
						handleCell(cell);
					} else {
						if (objects.size() == 0){
							GameScene.show(new WndInfoCell(cell));
						} else {
							examineObject(objects.get(index-1));
						}
					}
				}
			};
			scene.addToFront(menu);
			menu.camera = PixelScene.uiCamera;
			PointF mousePos = PointerEvent.currentHoverPos();
			mousePos = menu.camera.screenToCamera((int)mousePos.x, (int)mousePos.y);
			menu.setPos(mousePos.x-3, mousePos.y-3);

		}

		@Override
		public String prompt() {
			return null;
		}
	};
}
