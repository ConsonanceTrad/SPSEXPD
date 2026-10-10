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

package pd.windows;

import pd.atlas.IconEntry;

import pd.atlas.items.SpecificCurrencyDict;
import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.SPDAction;
import pd.SPDSettings;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.ChangeEquip;
import pd.items.Item;
import pd.items.equipment.bags.ArrowCollecter;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.bags.BambooBasket;
import pd.items.equipment.bags.HeartOfScarecrow;
import pd.items.equipment.bags.KeyRing;
import pd.items.equipment.bags.MagicalHolster;
import pd.items.equipment.bags.PotionBandolier;
import pd.items.equipment.bags.ScrollHolder;
import pd.items.equipment.bags.ShoppingCart;
import pd.items.equipment.bags.VelvetPouch;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.CurrencyIndicator;
import pd.ui.Button;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.InventorySlot;
import pd.ui.QuickSlotButton;
import pd.ui.RenderedTextBlock;
import pd.ui.RightClickMenu;
import pd.ui.Window;
import pd.utils.GLog;
import pd.windows.WndMessage;
import pd.windows.WndOptions;
import pd.windows.WndTextInput;
import render.gltextures.SmartTexture;
import render.gltextures.TextureCache;
import render.input.GameAction;
import render.input.KeyBindings;
import render.input.KeyEvent;
import render.input.PointerEvent;
import render.noosa.BitmapText;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.PointerArea;
import render.noosa.audio.Sample;
import render.noosa.ui.Component;
import render.utils.geom.PointF;
import render.utils.math.Random;
import render.utils.platform.DeviceCompat;

public class WndBag extends WndTabbed {
	
	//only one bag window can appear at a time
	public static Window INSTANCE;

	protected static final int COLS_P   = 5;
	protected static final int COLS_L   = 5;
	//SPSEXPD: 底部标签模式（旧版布局）——标签不占左右两侧，横向更宽裕而纵向要更矮，
	//所以背包区改为每行 7 格（主背包 35 格 = 5 行，正好压进 70% 高度上限）
	protected static final int COLS_BOTTOM = 7;
	
	protected static int SLOT_WIDTH_P   = 24;
	protected static int SLOT_WIDTH_L   = 24;

	protected static int SLOT_HEIGHT_P	= 24;
	protected static int SLOT_HEIGHT_L	= 24;

	protected static final int SLOT_MARGIN	= 1;

	//SPS: 正方形格子边长下限——再小则 16px 图标缩得过多、观感劣化；
	//格子缩到 16 以下时图标按需略缩（见 ItemSlot.layout）
	protected static final int MIN_SLOT	= 12;
	
	protected static final int TITLE_HEIGHT	= 14;

	//SPS: 窗口布局 = 装备区两排(10 格) + 背包 7 行(35 格)，主背包与包裹统一
	protected static final int EQUIP_ROWS	= 2;
	protected static final int BAG_ROWS		= 7;
	//SPSEXPD: 底部标签模式下背包区每行 7 格（COLS_BOTTOM），35 格即 5 行
	protected static final int BAG_ROWS_BOTTOM = 5;

	//SPS: 移动端安全边距（占屏幕比例）——「包裹窗口 + 左右标签带 + 外框」限制在此比例内，
	//避免窗口/选项卡被屏幕裁切（选项卡被裁则点不到，功能不可用）
	protected static final float MOBILE_SAFE	= 0.94f;

	//SPSEXPD: 背包窗口的尺寸上限（占屏幕比例）——「窗口 + 左右标签带 + 外框」不超过屏宽 80%，
	//「窗口 + 外框」不超过屏高上限。超出时按下方循环联动缩小格子直到进入上限（下限 MIN_SLOT）。
	//高度上限分两种布局：竖版（左右两侧标签）90%，旧版（底部标签）70%
	protected static final float MAX_WINDOW_WIDTH_RATIO		= 0.8f;
	protected static final float MAX_WINDOW_HEIGHT_RATIO_SIDE	= 0.9f;
	protected static final float MAX_WINDOW_HEIGHT_RATIO_BOTTOM	= 0.7f;

	//SPSEXPD: 窗口被上限缩小后，界面数字（格子上的数量/力量/等级、标题栏的金币/能量）的同步缩放
	private float textScale = 1f;

	//SPSEXPD: 旧版布局（标签栏放到底部）——每页 3 个包裹 + 翻页格 + 主背包格，底部只留一行标签高
	protected static final int BOTTOM_PER_PAGE = 3;
	//SPSEXPD: 底部标签栏固定 5 格（每格 1/5 宽）= 3 个包裹 + 翻页 + 主背包；
	//翻页与主背包恒定在最右两格，位置不随当前页包裹数量变化
	protected static final int BOTTOM_SLOTS = 5;
	protected static final int BOTTOM_TAB_H    = 22;
	private int bottomPage = 0;
	//SPSEXPD: 记住底部标签栏上次停在第几页——切包裹/重新打开时停在同一页，而不是弹回第一页
	private static int lastBottomPage = 0;
	private IconTab bottomPager;

	//SPS: 标签栏改到窗口左右两侧（用户裁决 2026-09-28）——标签竖置、底板旋转 90°、图案保持正向；
	//左侧 5 个、右侧其余，主背包固定右下角（参照归档 Godot 版 wnd_bag.gd 的侧栏布局）
	//SPS: 侧边标签尺寸由贴图 Assets.Interfaces.SIDE_TABS 决定（横向 2 帧）——贴图多大标签就多大，
	//构造时读取（initSideTabSize），避免硬编码尺寸与美术贴图不匹配。
	protected static int TAB_W		= 20;    //单帧宽（贴图宽 / 2）
	protected static int TAB_H		= 70;    //单帧高 = 上固定段 + 可裁中段 + 下固定段
	protected static final int PLATE_CAP	= 3;     //SPS: 底板上下固定段高度（与 side_tabs.png 规格绑定）
	protected static final int LEFT_TABS	= 5;
	
	private ItemSelector selector;

	private int nCols;
	private int nRows;
	//SPSEXPD: 内容区格数（构造时按容量定；placeItems 与 placeItem 共用，供超容量守卫用）
	private int contentSlots;

	private int slotWidth;
	private int slotHeight;

	protected int count;
	protected int col;
	protected int row;
	
	private static Bag lastBag;

	public WndBag( Bag bag ) {
		this(bag, null);
	}

	public WndBag( Bag bag, ItemSelector selector ) {
		
		super();

		initSideTabSize();   //SPS: 侧边标签尺寸跟随贴图

		//SPS: 图层顺序方案——未选中的标签要"被窗框压住"，就必须位于 chrome 之下；
		//选中的标签要"在背包区之上"，则置于最顶层。Window 默认顺序是 blocker→shadow→chrome→内容，
		//Group 只能首/末插入，所以这里先摘下 blocker/shadow/chrome，等内容与标签都就位后再按序重插。
		remove( blocker );
		remove( shadow );
		remove( chrome );

		if( INSTANCE != null ){
			INSTANCE.hide();
		}
		INSTANCE = this;
		
		this.selector = selector;
		
		lastBag = bag;

		//SPSEXPD: 继承上次的翻页位置（页数变少时会在 layoutBottomTabs() 里取模兜底）
		bottomPage = lastBottomPage;

		slotWidth = PixelScene.landscape() ? SLOT_WIDTH_L : SLOT_WIDTH_P;
		slotHeight = PixelScene.landscape() ? SLOT_HEIGHT_L : SLOT_HEIGHT_P;

		//SPSEXPD: 底部标签模式改用每行 7 格（见 COLS_BOTTOM）；其余布局保持 5 列
		boolean bottomTabs = SPDSettings.bagBottomTabs();
		nCols = bottomTabs ? COLS_BOTTOM : (PixelScene.landscape() ? COLS_L : COLS_P);
		//SPSEXPD: 行数 = 装备区两排 + 背包行数（5 列时 7 行、7 列时 5 行，都是 35 格）。
		//SPSEXPD: 行数按「实际要摆的格数」算，并以「窗口能容纳的格数」封顶（两种布局都是 35 格）：
		//- 不因超容量物品多出一行（读档 force-add 会让主背包出现 36 件）
		//- 封顶用窗口格数而不是 bag.capacity()：窗口本来就只有 35 格，超出的物品由 placeItem 的守卫跳过（不显示但不丢失）
		//⚠️ 硬约束：包裹 capacity() + 1 必须是 nCols 的整数倍且不超过窗口容量，否则会多出一行空行。
		//所以各 Bag 子类的容量都取 34（34+1 = 35 = 5 列 x 7 行 = 7 列 x 5 行）
		int bagRows = bottomTabs ? BAG_ROWS_BOTTOM : BAG_ROWS;
		int contentCap = bagRows * nCols;
		int shown = 0;
		for (Item i : bag.items) if (!(i instanceof Bag)) shown++;
		if (bag != Dungeon.hero.belongings.backpack) shown++;   //包裹本体占一格
		int contentRows = (Math.min(shown, contentCap) + nCols - 1) / nCols;
		nRows = EQUIP_ROWS + Math.max(bagRows, contentRows);
		//SPSEXPD: 内容区格数在此定下来（装备区固定 EQUIP_ROWS 行；底部 7 列时第 2 行放不满 5 个、右侧留白）
		contentSlots = (nRows - EQUIP_ROWS) * nCols;

		//SPS: 标签移到窗框外侧，内容区回到满宽——包裹区外缘正好紧贴标签内缘（无缝隙）
		int contentWidth = slotWidth * nCols + SLOT_MARGIN * (nCols - 1);
		int windowWidth = contentWidth;
		int windowHeight = TITLE_HEIGHT + slotHeight * nRows + SLOT_MARGIN * (nRows - 1);

		//SPS: 格子恒为正方形——宽高联动收缩（同步 -1，初始均为 24，全程保持相等）。
		//此前横/竖屏循环与移动端安全区循环各自只缩一维，高缩放时格子被压成长方形。
		//约束统一为宽高两条同时满足：窗口 + 左右标签带 + 外框 ≤ 屏宽上限，
		//窗口 + 外框 ≤ 屏高上限（横屏沿用底部 20px 预留）；Android 再乘 MOBILE_SAFE。
		//只缩格子，绝不压缩标签带宽度——TAB_W 必须等于贴图帧宽，否则三段按 TAB_W
		//裁取会切掉图案右侧，在移动端高缩放下表现为选项卡渲染错位
		//SPSEXPD: 上限改为屏幕的 80% 宽 / 70% 高（含左右两列标签带与外框）；超出就缩格子
		//SPSEXPD: 上限——宽度 80%；高度按布局取（竖版两侧标签 90% / 旧版底部标签 70%）。
		//竖版时左右标签竖着占满窗口高度，所以它们也一并落在 90% 之内
		int limitW = (int)(PixelScene.uiCamera.width * MAX_WINDOW_WIDTH_RATIO);
		int limitH = (int)(PixelScene.uiCamera.height
				* (SPDSettings.bagBottomTabs() ? MAX_WINDOW_HEIGHT_RATIO_BOTTOM : MAX_WINDOW_HEIGHT_RATIO_SIDE));
		if (PixelScene.landscape()) {
			limitH -= 20;
		}
		if (DeviceCompat.isAndroid()) {
			//SPS: 移动端安全适配——把「窗口 + 左右标签带 + 外框」限制在屏幕 94% 以内，
			//避免包裹窗口/选项卡被裁切（选项卡被裁会点不到，功能不可用）
			limitW = (int)(limitW * MOBILE_SAFE);
			limitH = (int)(limitH * MOBILE_SAFE);
		}
		//SPSEXPD: 旧版布局下标签在底部，不再吃横向预算，因此同样屏幕下格子能保持更大
		int tabBudgetW = SPDSettings.bagBottomTabs() ? 0 : 2 * TAB_W;
		while (slotWidth > MIN_SLOT &&
				(windowWidth + tabBudgetW + chrome.marginHor() > limitW ||
				 windowHeight + chrome.marginVer() > limitH)) {
			slotWidth--;
			windowWidth -= nCols;
			slotHeight--;
			windowHeight -= nRows;
		}

		//SPSEXPD: 格子被缩小时，格子上的数字与标题栏的金币/能量也跟着等比缩小（格子没缩则保持 1）
		int baseSlot = PixelScene.landscape() ? SLOT_WIDTH_L : SLOT_WIDTH_P;
		//SPSEXPD: 缩放系数唯一真源——先吸附到像素网格（1/defaultZoom 的整数倍），
		//这样所有用它的地方（格子内数字、标题栏数字、金币/S金/能量图标及其布局乘法、标题字号）
		//拿到的是同一个值。若这里不吸附、而某处自行 PixelScene.align()，两者会不一致
		//（例如 0.6 被吸附成 0.667），图标就会比预留的位置更大/偏移。
		textScale = PixelScene.align( Math.min(1f, slotWidth / (float)baseSlot) );

		placeTitle( bag, windowWidth );
		
		placeItems( bag );

		resize( windowWidth, windowHeight );

		//SPSEXPD: 翻页格也用选项卡外观——走 IconTab 的原生底板（与底部包裹标签同一套），
		//图标与「兑换S金」按钮相同；它不参与标签选中，只负责翻页
		bottomPager = new IconTab( new ItemSprite( SpecificPlaceHolderDict.SPS_GOLD_TO_SCOIN, null ) ) {
			@Override
			protected void onClick() {
				Sample.INSTANCE.play( Assets.Sounds.CLICK, 0.7f, 0.7f, 1.2f );
				bottomPage++;
				layoutTabs();
			}
		};
		//SPSEXPD: Tab 的底板只在 select() 里创建，这里手动挂一次未选中态底板
		bottomPager.select( false );
		bottomPager.visible = false;
		//SPSEXPD: 必须按 Component 加入！IconTab 是 WndTabbed.Tab 的子类，直接 add(bottomPager)
		//会命中 WndTabbed.add(Tab) 并被收进 tabs 列表（而且排在主背包标签之前），
		//于是 tabs.get(0) 变成它、真正的主背包标签既不摆放也点不到
		Component pager = bottomPager;
		add( pager );

		int i = 1;
		for (Bag b : Dungeon.hero.belongings.getBags()) {
			if (b != null) {
				//SPSEXPD: 按布局选择实现——底部模式走 BottomBagTab（Tab 原生底板），
				//两侧走 BagTab（side_tabs 三段自绘）；两套渲染互不干涉
				Tab tab = SPDSettings.bagBottomTabs()
						? new BottomBagTab( b, i++ )
						: new BagTab( b, i++ );
				add( tab );
				tab.select( b == bag );
				if  (b == bag){
					selected = tab;
				}
			}
		}

		//SPS: 按"底→顶"重建图层：blocker → shadow → 未选中标签 → chrome → 内容 → 选中标签。
		//addToBack 每次插到最底，故标签需倒序插入以保持彼此相对次序。
		for (Tab t : tabs) {
			remove( t );
		}
		addToBack( chrome );
		for (int k = tabs.size() - 1; k >= 0; k--) {
			addToBack( tabs.get(k) );
		}
		addToBack( shadow );
		addToBack( blocker );
		if (selected != null) {
			bringToFront( selected );   //选中标签在背包区之上
		}

		layoutTabs();
	}

	public ItemSelector getSelector() {
		return selector;
	}

	//SPS: 标签移到窗框外后，画布需左右各扩 TAB_W 才能容纳它们。
	//这里刻意不复用 WndTabbed.resize()——它会把所有标签 remove+add 拉回最上层，
	//破坏"未选中标签被窗框压住"的图层顺序，因此直接复刻 Window.resize 的必要逻辑。
	@Override
	public void resize( int w, int h ) {
		this.width = w;
		this.height = h;

		chrome.size( width + chrome.marginHor(), height + chrome.marginVer() );

		//SPSEXPD: 两侧标签要左右各留 TAB_W；旧版布局改为在底部留出一行标签
		if (SPDSettings.bagBottomTabs()) {
			camera.resize( (int)chrome.width, (int)chrome.height + BOTTOM_TAB_H + 2 );
		} else {
			camera.resize( (int)chrome.width + 2 * TAB_W, (int)chrome.height );
		}
		camera.x = (int)(Game.width - camera.screenWidth()) / 2;
		camera.y = (int)(Game.height - camera.screenHeight()) / 2;
		camera.y += yOffset * camera.zoom;
		camera.scroll.set(
				SPDSettings.bagBottomTabs() ? chrome.x : chrome.x - TAB_W,
				chrome.y );

		shadow.boxRect(
				camera.x / camera.zoom,
				camera.y / camera.zoom,
				chrome.width(), chrome.height );

		layoutTabs();
	}

	//SPS: 延续破碎"分配空间"的排布——侧栏每项恒定占 1/5（不拉伸）：
	//左右两栏都按 1/5 网格从顶部逐格摆放，主背包恒定占右栏底部 1/5。
	//（用户裁决 2026-09：左侧也占恒定 1/5，不再按数量均分整侧）
	@Override
	public void layoutTabs(){
		int n = tabs.size();
		if (n == 0) return;

		//SPSEXPD: 旧版布局——标签栏放到底部一行，分页显示
		if (SPDSettings.bagBottomTabs()) {
			layoutBottomTabs();
			return;
		}

		//SPS: 标签带贴近窗口外缘、四周内缩 1px——露出 1px 窗口边框作过渡
		//（微调定稿：0px 盖压略大、2px 内缩略小，1px 正好）
		float top = -chrome.marginTop() + 1;
		//SPS: 底部多留 1px 内边距（上 1 + 下 2），让左栏第 5 个标签与背包下边缘像素衔接
		float usableH = height + chrome.marginVer() - 3;

		int bagCount = n - 1;                                  //除主背包
		int leftCount = Math.min( bagCount, LEFT_TABS );

		//SPS: 左右两栏每项都恒定占 1/5（不拉伸）。
		//整数网格（用户裁决 2026-09-30）：标签页间恒留 1px 间隙避免像素融合——
		//高度 = g-1、步进 = g（浮点步进取整会在 0~1px 间抖动导致相邻标签粘连/融合）
		int g = Math.max( 2, (int)( (usableH + 1) / 5 ) );

		//SPS: 标签在窗框外侧——未选中时压在窗框下、选中时探入框带。
		//探入深度 = 窗框带 6px - 2px = 4px（再外移 2px 让选中态探入浅一点）
		float leftX = -TAB_W - 2, rightX = width + 2;

		int sideIdx = 0;
		for (int i = 1; i < n; i++) {
			Tab tab = tabs.get(i);
			boolean left = sideIdx < leftCount;
			int slotIdx = left ? sideIdx : sideIdx - leftCount;   //本栏内第几格（0 起）
			tab.setSize( TAB_W, g - 1 );
			tab.setPos(
					left ? leftX : rightX,
					top + slotIdx * g );
			if (tab instanceof BagTab) ((BagTab)tab).setLeftSide( left );
			PixelScene.align( tab );
			sideIdx++;
		}

		//主背包（getBags() 首位）固定右栏最下一格（1/5）——与左栏格线严格对齐
		Tab main = tabs.get(0);
		main.setSize( TAB_W, g - 1 );
		main.setPos( rightX, top + (LEFT_TABS - 1) * g );
		if (main instanceof BagTab) ((BagTab)main).setLeftSide( false );
		PixelScene.align( main );
	}

	//SPSEXPD: 旧版背包界面（用户方案）——标签栏固定在窗口底部一行，分页显示：
	//每页 = 3 个包裹 + 翻页格 + 主背包格（主背包恒在最后一格）。包裹不超过一页时隐藏翻页格；
	//切包仍是「一次点击」，只有包裹多于 3 个时才需要先翻页。横向预算里不再扣 2*TAB_W，
	//所以同样屏幕下背包格子能保持更大（高缩放时尤其明显）。
	private void layoutBottomTabs() {
		int n = tabs.size();
		if (n == 0) return;

		int bagCount = n - 1;                                         //除主背包
		int pages = Math.max( 1, (bagCount + BOTTOM_PER_PAGE - 1) / BOTTOM_PER_PAGE );
		bottomPage = ((bottomPage % pages) + pages) % pages;          //翻到底回到第一页
		lastBottomPage = bottomPage;                                  //SPSEXPD: 记住页数，供下次打开/切包裹时沿用

		int firstBag = 1 + bottomPage * BOTTOM_PER_PAGE;
		int shownBags = Math.max( 0, Math.min( BOTTOM_PER_PAGE, n - firstBag ) );
		boolean pager = pages > 1;

		//先全部隐藏，再只显示当前页的格子（主背包恒显示）
		for (int i = 0; i < n; i++) {
			tabs.get(i).visible = false;
		}

		//SPSEXPD: 每个选项卡固定占 1/5 宽，且「换页」「主背包」恒定在最右两格——
		//这样翻页、回主背包的位置永远不变；包裹不足一格时左侧留白，而不是被挤到右边
		float step = (float)width / BOTTOM_SLOTS;
		float cellW = step;                                            //满 1/5 宽（不再留 1px 间隙）
		float ty = height;                                         //SPSEXPD: 正好贴窗口下沿（原为 +1，用户要求整体上移 1px 使接缝对接）

		//包裹从第一格起（左对齐）
		for (int i = 0; i < shownBags; i++) {
			placeBottomTab( tabs.get( firstBag + i ), i, cellW, step, ty );
		}
		//翻页格恒定在倒数第二格、主背包恒定在最后一格
		if (bottomPager != null) {
			if (pager) {
				placeBottomTab( bottomPager, BOTTOM_SLOTS - 2, cellW, step, ty );
			} else {
				bottomPager.visible = false;
			}
		}
		placeBottomTab( tabs.get(0), BOTTOM_SLOTS - 1, cellW, step, ty );   //主背包固定最右格
	}

	private void placeBottomTab( Button c, int slot, float cellW, float step, float ty ) {
		c.setRect( slot * step, ty, cellW, BOTTOM_TAB_H );
		c.visible = true;
		PixelScene.align( c );
	}

	//SPS: 从贴图读取侧边标签的单帧尺寸（横向 2 帧：左=选中、右=未选）。
	//贴图规格：单帧 = 顶部 PLATE_CAP px 边框段 + 中间可裁段（纵向可任意裁取）+ 底部 PLATE_CAP px 边框段。
	//底板不再做九宫格拉伸，而是按标签高度从中间段 1:1 裁取（避免移动端非整数缩放的拉伸条纹）。
	private static void initSideTabSize() {
		SmartTexture tex = TextureCache.get( Assets.Interfaces.SIDE_TABS );
		if (tex != null && tex.width > 0 && tex.height > 0) {
			TAB_W = Math.max( 1, tex.width / 2 );
			TAB_H = Math.max( 2 * PLATE_CAP + 1, tex.height );
		}
	}

	public static WndBag lastBag(ItemSelector selector ) {
		
		if (lastBag != null && Dungeon.hero.belongings.backpack.contains( lastBag )) {
			
			return new WndBag( lastBag, selector );
			
		} else {
			
			return new WndBag( Dungeon.hero.belongings.backpack, selector );
			
		}
	}

	public static WndBag getBag( ItemSelector selector ) {
		if (selector.preferredBag() == Belongings.Backpack.class){
			return new WndBag( Dungeon.hero.belongings.backpack, selector );

		} else if (selector.preferredBag() != null){
			Bag bag = Dungeon.hero.belongings.getItem( selector.preferredBag() );
			if (bag != null)    return new WndBag( bag, selector );
			//if a specific preferred bag isn't present, then the relevant items will be in backpack
			else                return new WndBag( Dungeon.hero.belongings.backpack, selector );
		}

		return lastBag( selector );
	}
	
	protected void placeTitle( Bag bag, int width ){

		//SPS: 标签已移到窗框外，标题行右对齐基准即内容区右缘
		float titleWidth;
		if (Dungeon.energy == 0) {
			ItemSprite gold = new ItemSprite(SpecificCurrencyDict.GOLD_0, null);
			//SPSEXPD: 金币图标跟着窗口缩放。注意 Visual.width()/height() 已经含 scale，
			//所以布局直接用它、绝不能再乘一次 textScale（否则成了 s*s，右下会溢出背包框）
			gold.scale.set(PixelScene.align(textScale));
			gold.x = width - gold.width();
			gold.y = (TITLE_HEIGHT - gold.height()) / 2f;
			PixelScene.align(gold);
			add(gold);

			BitmapText amt = new BitmapText(Integer.toString(Dungeon.gold), PixelScene.pixelFont);
			amt.hardlight(TITLE_COLOR);
			amt.scale.set(PixelScene.align(textScale));
			amt.measure();
			amt.x = width - gold.width() - amt.width() - 1;
			amt.y = (TITLE_HEIGHT - amt.baseLine()) / 2f - 1;
			PixelScene.align(amt);
			add(amt);

			//SPS: 金币数量左侧的 S金兑换按钮
			titleWidth = placeSGoldExchangeButton( amt.x );
		} else {

			Image gold = Icons.get(Icons.COIN_SML);
			gold.scale.set(PixelScene.align(textScale));
			gold.x = width - gold.width() - 0.5f;
			gold.y = 0;
			PixelScene.align(gold);
			add(gold);

			BitmapText amt = new BitmapText(Integer.toString(Dungeon.gold), PixelScene.pixelFont);
			amt.hardlight(TITLE_COLOR);
			amt.scale.set(PixelScene.align(textScale));
			amt.measure();
			amt.x = width - gold.width() - amt.width() - 2f;
			amt.y = 0;
			PixelScene.align(amt);
			add(amt);

			//SPS: 金币数量左侧的 S金兑换按钮
			titleWidth = placeSGoldExchangeButton( amt.x );

			Image energy = Icons.get(Icons.ENERGY_SML);
			energy.scale.set(PixelScene.align(textScale));
			energy.x = width - energy.width();
			energy.y = gold.height();
			PixelScene.align(energy);
			add(energy);

			amt = new BitmapText(Integer.toString(Dungeon.energy), PixelScene.pixelFont);
			amt.hardlight(0x44CCFF);
			amt.scale.set(PixelScene.align(textScale));
			amt.measure();
			amt.x = width - energy.width() - amt.width() - 1;
			amt.y = energy.y;
			PixelScene.align(amt);
			add(amt);

			titleWidth = Math.min(titleWidth, amt.x);
		}

		String title = selector != null ? selector.textPrompt() : null;
		//SPSEXPD: 标题字号也跟随窗口缩放（RenderedTextBlock 没有 scale，按比例降字号，最小 6）
		int titleSize = Math.max( 6, Math.round( 8 * textScale ) );
		RenderedTextBlock txtTitle = PixelScene.renderTextBlock(
				title != null ? Messages.titleCase(title) : Messages.titleCase( bag.name() ), titleSize );
		txtTitle.hardlight( TITLE_COLOR );
		txtTitle.maxWidth( (int)titleWidth - 2 );
		txtTitle.setPos(
				1,
				(TITLE_HEIGHT - txtTitle.height()) / 2f - 1
		);
		PixelScene.align(txtTitle);
		add( txtTitle );
	}
	
	//SPS: 金币数量左侧的 S金兑换按钮。图标取自主副手转换道具（SPS_EQUIP_CHANGE），
	//返回按钮左缘供标题避让
	private float placeSGoldExchangeButton( float right ) {
		IconButton btn = new IconButton( new ItemSprite( SpecificPlaceHolderDict.SPS_GOLD_TO_SCOIN, null ) ) {
			@Override
			protected void onClick() {
				askSGoldExchange();
			}
		};
		//SPSEXPD: S金兑换图标也随窗口缩放（0.75 是它的基准缩放）
		btn.icon().scale.set( 0.75f * textScale );
		btn.icon().originToCenter();
		final float left = right - 12 - 3;
		btn.setSize( 12, TITLE_HEIGHT );
		btn.setPos( left, -3 );   //SPS: 图标视觉上偏低，整体上移 3px
		add( btn );
		return left;
	}

	//SPS: 点按钮 → 输入要兑换的 S金数量 → 二次确认后按 2333:1 扣金币入账
	private void askSGoldExchange() {
		if (Dungeon.hero == null || !Dungeon.hero.isAlive()) return;

		final int maxCoin = CurrencyIndicator.sCoinForGold( Dungeon.gold );
		if (maxCoin <= 0) {
			GameScene.show( new WndMessage( Messages.get( CurrencyIndicator.class, "not_enough", CurrencyIndicator.SC_EXCHANGE_RATE ) ) );
			return;
		}

		GameScene.show( new WndTextInput(
				Messages.get( CurrencyIndicator.class, "exchange_title" ),
				Messages.get( CurrencyIndicator.class, "exchange_body", maxCoin * CurrencyIndicator.SC_EXCHANGE_RATE, maxCoin ),
				Integer.toString( maxCoin ),
				10, false,
				Messages.get( CurrencyIndicator.class, "exchange_confirm" ),
				Messages.get( CurrencyIndicator.class, "cancel" ) ) {
			@Override
			public void onSelect( boolean positive, String text ) {
				if (!positive) return;

				int sCoin;
				try {
					sCoin = Integer.parseInt( text.trim() );
				} catch (NumberFormatException e) {
					GLog.w( Messages.get( CurrencyIndicator.class, "exchange_invalid" ) );
					return;
				}
				if (sCoin <= 0) {
					GLog.w( Messages.get( CurrencyIndicator.class, "exchange_invalid" ) );
					return;
				}
				//超出可兑上限时按上限处理（余数金币保留）
				final int gain = Math.min( sCoin, maxCoin );
				final int spend = gain * CurrencyIndicator.SC_EXCHANGE_RATE;

				GameScene.show( new WndOptions(
						Messages.get( CurrencyIndicator.class, "exchange_title" ),
						Messages.get( CurrencyIndicator.class, "exchange_body", spend, gain ),
						Messages.get( CurrencyIndicator.class, "exchange_confirm" ),
						Messages.get( CurrencyIndicator.class, "cancel" ) ) {
					@Override
					protected void onSelect( int index ) {
						if (index != 0) return;
						if (Dungeon.gold < spend) return;
						Dungeon.gold -= spend;
						SPDSettings.sCoinAdd( gain );
						GLog.p( Messages.get( CurrencyIndicator.class, "exchange_ok", spend, gain ) );
						Sample.INSTANCE.play( Assets.Sounds.GOLD, 1, 1, Random.Float( 0.9f, 1.1f ) );
					}
				} );
			}
		} );
	}

	protected void placeItems( Bag container ) {

		// SPS: 装备区固定两排 10 格（饰品槽 1-3 只收非戒指，戒指槽 4-5 只收戒指）
		// 第一行：主武器 / 主护甲 / 饰品1 / 饰品2 / 饰品3
		Belongings stuff = Dungeon.hero.belongings;
		//SPSEXPD: 底部标签模式每行 7 格——把两个戒指挪到第一行（武器/护甲/神器/杂物/戒指/戒指1/戒指2
		//正好 7 个），第二行剩副武器/副护甲/徽章；两侧模式（5 列）保持原来的两行顺序
		boolean wideEquip = SPDSettings.bagBottomTabs();
		placeItem( stuff.weapon != null ? stuff.weapon : new Placeholder( SpecificPlaceHolderDict.SPS_PH_WEAPON ) );
		placeItem( stuff.armor != null ? stuff.armor : new Placeholder( SpecificPlaceHolderDict.SPS_PH_ARMOR ) );
		placeItem( stuff.artifact != null ? stuff.artifact : new Placeholder( SpecificPlaceHolderDict.ARTIFACT_HOLDER_0 ) );
		placeItem( stuff.misc != null ? stuff.misc : new Placeholder( SpecificPlaceHolderDict.ARTIFACT_HOLDER_0 ) );
		placeItem( stuff.ring != null ? stuff.ring : new Placeholder( SpecificPlaceHolderDict.ARTIFACT_HOLDER_0 ) );
		if (wideEquip) {
			placeItem( stuff.accessory4 != null ? stuff.accessory4 : new Placeholder( SpecificPlaceHolderDict.RING_HOLDER_0 ) );
			placeItem( stuff.accessory5 != null ? stuff.accessory5 : new Placeholder( SpecificPlaceHolderDict.RING_HOLDER_0 ) );
		}
		// 第二行：副武器 / 副护甲 /（两侧模式）戒指1 / 戒指2 / 徽章
		placeItem( stuff.secondWep != null ? stuff.secondWep : new Placeholder( SpecificPlaceHolderDict.SPS_PH_WEAPON_SPARE ) );
		placeItem( stuff.secondArmor != null ? stuff.secondArmor : new Placeholder( SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE ) );
		if (!wideEquip) {
			placeItem( stuff.accessory4 != null ? stuff.accessory4 : new Placeholder( SpecificPlaceHolderDict.RING_HOLDER_0 ) );
			placeItem( stuff.accessory5 != null ? stuff.accessory5 : new Placeholder( SpecificPlaceHolderDict.RING_HOLDER_0 ) );
		}
		placeItem( stuff.badge != null ? stuff.badge : new Placeholder( SpecificPlaceHolderDict.SPS_PH_TRINKET ) );

		//SPSEXPD: 装备槽固定 10 个（两排 × 5 列），与 nCols 无关——底部模式 7 列时不能算成 14
		int equipped = EQUIP_ROWS * COLS_P;

		//SPSEXPD: 主背包之外的窗口也要显示包裹本体自己（占其中一格）
		boolean showsSelf = container != Dungeon.hero.belongings.backpack;
		if (showsSelf) {
			placeItem( container );
		}

		// Items in the bag, except other containers (they have tags at the bottom)
		//SPSEXPD: 包裹袋不占格子，也不计入格子数——否则主背包会凭空多出空行
		//SPSEXPD: 露珠瓶恒定占用并显示在主背包右下角最后一格，不参与顺序摆放
		boolean mainBackpack = container == Dungeon.hero.belongings.backpack;
		Item waterskin = null;
		for (Item item : container.items.toArray(new Item[0])) {
			if (item instanceof Bag) continue;
			if (mainBackpack && item instanceof pd.items.Waterskin) {
				waterskin = item;
				continue;
			}
			placeItem( item );
		}

		// Free Space
		//SPSEXPD: 空格填到「内容区格数」为止（上限含本体占格），与窗口行数一致，不会溢到窗口外
		//SPSEXPD: 装备区独占 EQUIP_ROWS 行（7 列时第 2 行放不满 5 个、右侧留白），
		//所以内容格数按「行数 − 装备行数」算；旧式 nRows*nCols - equipped 会多出 4 格并越过窗口底边
		//（contentSlots 已在构造里按容量算好，这里直接取用）
		int wanted = Math.min(container.capacity() + (showsSelf ? 1 : 0), contentSlots);
		while ((count - equipped) < wanted - (waterskin != null ? 1 : 0)) {
			placeItem( null );
		}
		//SPSEXPD: 露珠瓶最后落位，恒为右下角最后一格
		if (waterskin != null) placeItem( waterskin );
	}
	
	protected void placeItem( final Item item ) {

		//SPSEXPD: 内容区已满就不再摆放——读档 force-add 可能让物品数超过容量
		//（主背包 35 格含露珠瓶）。物品仍留在 bag.items 里，腾出空间后重开背包就会显示，不会丢失
		if (count - EQUIP_ROWS * COLS_P >= contentSlots) return;

		count++;
		
		//SPS: 内容区满宽（标签已在窗框外）
		int x = col * (slotWidth + SLOT_MARGIN);
		int y = TITLE_HEIGHT + row * (slotHeight + SLOT_MARGIN);

		InventorySlot slot = new InventorySlot( item ){
			@Override
			protected void onClick() {
				if (!(item instanceof ChangeEquip) && lastBag != item && !lastBag.contains(item) && !item.isEquipped(Dungeon.hero)){

					hide();

				} else if (selector != null) {

					if (selector.hideAfterSelecting()){
						hide();
					}
					selector.onSelect( item );

				} else {

					Game.scene().addToFront(new WndUseItem( WndBag.this, item ) );

				}
			}

			@Override
			protected void onRightClick() {
				if (!(item instanceof ChangeEquip) && lastBag != item && !lastBag.contains(item) && !item.isEquipped(Dungeon.hero)){

					hide();

				} else if (selector != null) {

					if (selector.hideAfterSelecting()){
						hide();
					}
					selector.onSelect( item );

				} else {

					RightClickMenu r = new RightClickMenu(item){
						@Override
						public void onSelect(int index) {
							WndBag.this.hide();
						}
					};
					parent.addToFront(r);
					r.camera = camera();
					PointF mousePos = PointerEvent.currentHoverPos();
					mousePos = camera.screenToCamera((int)mousePos.x, (int)mousePos.y);
					r.setPos(mousePos.x-3, mousePos.y-3);

				}
			}

			@Override
			protected boolean onLongClick() {
				if (selector == null && item.canQuickSlot()) {
					hide();
					QuickSlotButton.set( item );
					return true;
				} else if (selector != null) {
					Game.scene().addToFront(new WndInfoItem(item));
					return true;
				} else {
					return false;
				}
			}
		};
		//SPSEXPD: 让格子里的数字跟随窗口缩放（必须在 setRect 前设置，setRect 会触发 layout）
		slot.textScale = textScale;
		slot.setRect( x, y, slotWidth, slotHeight );
		add(slot);

		if (item == null || (selector != null && !selector.itemSelectable(item))){
			slot.enable(false);
		}
		
		//SPSEXPD: 装备区与背包区统一按 nCols 换行——底部模式 7 列时装备 10 格正好排成 7+3 两行
		//（两个戒指已挪到第一行），两侧模式 5 列时仍是整齐的两行 × 5
		if (++col >= nCols) {
			col = 0;
			row++;
		}

	}

	@Override
	public boolean onSignal(KeyEvent event) {
		if (event.pressed && KeyBindings.getActionForKey( event ) == SPDAction.INVENTORY) {
			onBackPressed();
			return true;
		} else {
			return super.onSignal(event);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (selector != null) {
			selector.onSelect( null );
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onClick( Tab tab ) {
		hide();
		Window w = new WndBag(((BagHolder) tab).bag(), selector);
		if (Game.scene() instanceof GameScene){
			GameScene.show(w);
		} else {
			Game.scene().addToFront(w);
		}
	}
	
	@Override
	public void hide() {
		super.hide();
		if (INSTANCE == this){
			INSTANCE = null;
		}
	}
	
	@Override
	protected int tabHeight() {
		//SPS: 标签栏在左右两侧时底部不留高度；SPSEXPD: 旧版布局时底部留出一行标签
		return SPDSettings.bagBottomTabs() ? BOTTOM_TAB_H + 2 : 0;
	}
	
	//SPS: 选项卡图标按 SPS 0.9.8 原版映射（不再复用别的袋子图标）；SPS 独有的
	//SHOP_CART / KEYRING / HOS / ARROW_C 四个图标已从 SPS 图集原像素补入本基底图标集
	//SPSEXPD: 改为 static，供其它面板复用同一套图标映射
	static Image icon( Bag bag ) {
		if (bag instanceof BambooBasket) {
			return Icons.get( Icons.BAMBOO_BASKET );
		} else if (bag instanceof VelvetPouch) {
			return Icons.get( Icons.SEED_POUCH );
		} else if (bag instanceof ScrollHolder) {
			return Icons.get( Icons.SCROLL_HOLDER );
		} else if (bag instanceof MagicalHolster) {
			return Icons.get( Icons.WAND_HOLSTER );
		} else if (bag instanceof PotionBandolier) {
			return Icons.get( Icons.POTION_BANDOLIER );
		} else if (bag instanceof ShoppingCart) {
			return Icons.get( Icons.SHOP_CART );
		} else if (bag instanceof KeyRing) {
			return Icons.get( Icons.KEYRING );
		} else if (bag instanceof HeartOfScarecrow) {
			return Icons.get( Icons.HOS );
		} else if (bag instanceof ArrowCollecter) {
			return Icons.get( Icons.ARROW_C );
		} else {
			return Icons.get( Icons.BACKPACK );
		}
	}
	
	//SPSEXPD: 包裹标签的共同契约——两侧与底部各有一套渲染实现，互不干扰
	private interface BagHolder {
		Bag bag();
	}

	//SPSEXPD: 包裹标签的公共部分：携带的是哪个包裹、快捷键与悬浮名。
	//渲染完全交给子类——BagTab（两侧，side_tabs 三段自绘）与 BottomBagTab（底部，Tab/IconTab 原生底板），
	//两套路径彼此独立，不会交叉影响
	private abstract class BagTabBase extends IconTab implements BagHolder {

		protected final Bag bag;
		private final int index;

		BagTabBase( Bag bag, int index ) {
			super( icon(bag) );

			this.bag = bag;
			this.index = index;
		}

		@Override
		public Bag bag() {
			return bag;
		}

		@Override
		public GameAction keyAction() {
			switch (index){
				case 1: default:
					return SPDAction.BAG_1;
				case 2:
					return SPDAction.BAG_2;
				case 3:
					return SPDAction.BAG_3;
				case 4:
					return SPDAction.BAG_4;
				case 5:
					return SPDAction.BAG_5;
			}
		}

		@Override
		protected String hoverText() {
			return Messages.titleCase(bag.name());
		}
	}

	//SPSEXPD: 底部（旧版）标签栏专用——直接用 Tab/IconTab 的原生渲染
	//（Chrome.Type.TAB_SELECTED / TAB_UNSELECTED 九宫格底板 + 图标居中、未选中时上移与裁切），
	//完全不碰 side_tabs 三段自绘，因此与两侧标签不可能互相影响
	private class BottomBagTab extends BagTabBase {

		BottomBagTab( Bag bag, int index ) {
			super( bag, index );
		}
	}

	private class BagTab extends BagTabBase {

		//SPS: 底板用「三段 1:1 裁取」而非九宫格拉伸——移动端非整数缩放下拉伸会拉出条纹/发虚，
		//改为直接从 side_tabs.png 按需裁取：上固定段 + 中段（按标签高度裁取）+ 下固定段。
		//贴图规格（每帧）：顶部 PLATE_CAP px 边框段 + 中间可裁段 + 底部 PLATE_CAP px 边框段。
		//横向不做任何缩放（帧宽 == TAB_W，1:1 显示）。
		private boolean leftSide = true;   //SPS: 位于左侧还是右侧栏（layoutTabs 指定）
		private Image plateTop;            //上固定段
		private Image[] plateMids;         //中段（多块循环拼接，贴图不够长时按需追加）
		private Image plateBot;            //下固定段

		BagTab( Bag bag, int index ) {
			super( bag, index );
		}

		@Override
		protected void createChildren() {
			super.createChildren();

			plateTop = new Image( Assets.Interfaces.SIDE_TABS );
			plateBot = new Image( Assets.Interfaces.SIDE_TABS );
			//SPS: 中段 4 块循环拼接（每块最多裁剪贴图中段长度），保证任意标签高度都不漏空
			plateMids = new Image[4];
			for (int i = 0; i < plateMids.length; i++) {
				plateMids[i] = new Image( Assets.Interfaces.SIDE_TABS );
			}

			addToBack( plateBot );
			for (int i = plateMids.length - 1; i >= 0; i--) {
				addToBack( plateMids[i] );
			}
			addToBack( plateTop );
		}

		//SPS: 覆写 select 以跳过原版九宫格底板（Tab.select 会 addToBack(bg)），只用贴图底板
		@Override
		protected void select( boolean value ) {
			selected = value;
			if (icon != null) icon.am = value ? 1.0f : 0.6f;   //沿用 IconTab 的未选变暗
			layout();
		}

		//SPS: 侧栏位置由 layoutTabs() 指定
		public void setLeftSide( boolean left ) {
			this.leftSide = left;
			layout();
		}

		@Override
		protected void layout() {
			super.layout();

			if (plateTop == null) return;

			//SPS: 帧 0 = 选中、帧 1 = 未选；中段按标签高度裁取，绝不缩放。
			//贴图不够长时用多块中段循环拼接（原先只支持 1 块续块，标签变高后会漏空）
			int fx = selected ? 0 : TAB_W;
			int midMax = Math.max( 1, TAB_H - 2 * PLATE_CAP );          //贴图可提供的中段长度
			int remaining = Math.max( 0, Math.round( height ) - 2 * PLATE_CAP );

			plateTop.frame( fx, 0, TAB_W, PLATE_CAP );
			plateBot.frame( fx, TAB_H - PLATE_CAP, TAB_W, PLATE_CAP );

			int placed = 0;
			for (int i = 0; i < plateMids.length; i++) {
				Image seg = plateMids[i];
				int h = Math.min( remaining, midMax );
				if (h > 0) {
					seg.frame( fx, PLATE_CAP, TAB_W, h );
					seg.x = x;
					seg.y = y + PLATE_CAP + placed;
					seg.visible = true;
					placed += h;
					remaining -= h;
				} else {
					seg.visible = false;
				}
				seg.flipHorizontal = !leftSide;
			}

			plateTop.flipHorizontal = !leftSide;
			plateBot.flipHorizontal = !leftSide;

			plateTop.x = x;  plateTop.y = y;
			plateBot.x = x;  plateBot.y = y + PLATE_CAP + placed;

			//SPS: 图标居中（去掉 IconTab 面向底部标签的上移/裁切）
			icon.x = x + (width - icon.width) / 2;
			icon.y = y + (height - icon.height) / 2 - 1;
			PixelScene.align( icon );
		}

	}

	public static class Placeholder extends Item {

		public Placeholder(IconEntry image ) {
			this.image = image;
		}

		@Override
		public String name() {
			return null;
		}

		@Override
		public boolean isIdentified() {
			return true;
		}
		
		@Override
		public boolean isEquipped( Hero hero ) {
			return true;
		}
	}

	public abstract static class ItemSelector {
		public abstract String textPrompt();
		public Class<?extends Bag> preferredBag(){
			return null; //defaults to last bag opened
		}
		public boolean hideAfterSelecting(){
			return true; //defaults to hiding the window when an item is picked
		}
		public abstract boolean itemSelectable( Item item );
		public abstract void onSelect( Item item );
	}
}
