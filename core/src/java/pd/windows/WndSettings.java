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

import pd.Assets;
import pd.Chrome;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.messages.Languages;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.services.updates.Updates;
import pd.sprites.CharSprite;
import pd.ui.CheckBox;
import pd.ui.GameLog;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.OptionSlider;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.SideQuickBar;
import pd.ui.Toolbar;
import pd.ui.Window;
import render.input.ControllerHandler;
import render.noosa.ColorBlock;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.PointerArea;
import render.noosa.audio.Sample;
import render.noosa.ui.Component;
import render.utils.math.Random;
import render.utils.platform.DeviceCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import pd.messages.InlineText;

public class WndSettings extends WndTabbed {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndSettings.class)
			.t("$displaytab.title", "显示设置")
			.t("$displaytab.fullscreen", "全屏模式")
			.t("$displaytab.hide_navigation", "隐藏导航栏")
			.t("$displaytab.hide_gesture", "隐藏手势栏")
			.t("$displaytab.okay", "开启")
			.t("$displaytab.cancel", "取消")
			.t("$displaytab.landscape", "强制横屏")
			.t("$displaytab.brightness", "亮度")
			.t("$displaytab.dark", "暗")
			.t("$displaytab.bright", "亮")
			.t("$displaytab.visual_grid", "网格可视度")
			.t("$displaytab.hero_path", "显示移动路径")
			.t("$displaytab.off", "关闭")
			.t("$displaytab.low", "低")
			.t("$displaytab.high", "最高")
			.t("$displaytab.camera_follow", "镜头追踪强度")
			.t("$displaytab.screenshake", "震屏")
			.t("$uitab.title", "界面设置")
			.t("$uitab.ui_mode", "界面模式")
			.t("$uitab.scale", "界面尺寸")
			.t("$uitab.mobile", "移动端")
			.t("$uitab.full", "全尺寸")
			.t("$uitab.toolbar_settings", "工具栏设置")
			.t("$uitab.mode", "工具栏模式：")
			.t("$uitab.split", "分散")
			.t("$uitab.group", "组合")
			.t("$uitab.center", "居中")
			.t("$uitab.flip_toolbar", "翻转工具栏")
			.t("$uitab.flip_indicators", "翻转指示器")
			.t("$uitab.qslot_bottom", "下快捷栏数量")
			.t("$uitab.qslot_left", "左快捷栏数量")
			.t("$uitab.qslot_right", "右快捷栏数量")
			.t("$uitab.quickslot_swapper", "切换快捷栏")
			.t("$uitab.swapper_desc", "当空间不足以显示所有6个快捷栏的时候，通过“切换快捷栏”按钮可以交替显示另外3个快捷栏。")
			.t("$uitab.system_font", "系统字体")
			.t("$uitab.off", "关闭")
			.t("$uitab.high", "最高")
			.t("$uitab.vibration", "振动")
			.t("$uitab.swap_aux_bar", "反转左侧按钮布局")      //SPSEXPD: 由「游戏辅助」页迁入
			.t("$uitab.swap_wait_search", "翻转等待与检视")    //SPSEXPD: 由「游戏辅助」页迁入
			.t("$inputtab.title", "输入设置")
			.t("$inputtab.key_bindings", "键鼠键位")
			.t("$inputtab.controller_bindings", "控制器键位")
			.t("$inputtab.controller_sensitivity", "控制器光标灵敏度")
			.t("$inputtab.movement_sensitivity", "控制器移动灵敏度")
			.t("$inputtab.off", "关闭")
			.t("$inputtab.high", "最高")
			.t("$auxtab.title", "游戏辅助")
			.t("$auxtab.hero_path", "显示移动路径")
			.t("$auxtab.search_pickup", "搜索捡拾物品")
			.t("$auxtab.quick_group", "快捷操作开关")   //SPSEXPD: 快捷操作分组的小标题
			.t("$auxtab.bag_group", "背包界面")   //SPSEXPD: 背包分组的小标题
			.t("$auxtab.quick_all", "启用快捷操作按钮")
			.t("$auxtab.quick_light", "照明")
			.t("$auxtab.quick_talent", "加点")
			.t("$auxtab.quick_eat", "进食")
			.t("$auxtab.bag_bottom_tabs", "底部背包标签栏")
			//SPSEXPD: 行末蓝色感叹号点开的「详细作用」（键名 = 上方各键 + _desc）
			.t("$auxtab.hero_path_desc", "在画面里画出你点击的移动路线，方便提前规划走位。")
			.t("$auxtab.search_pickup_desc", "使用「检索」时，会顺手拾取视野内可达的地面物品。")
			.t("$auxtab.quick_all_desc", "快捷操作按钮的总开关。关掉后按钮不再显示，但下面三项各自的设置会保留。")
			.t("$auxtab.quick_light_desc", "夜晚且身上带着光源时，显示一键照明的快捷按钮（优先用露珠瓶，其次火把）。")
			.t("$auxtab.quick_talent_desc", "有未分配的特质点时，显示一键打开加点界面的快捷按钮。")
			.t("$auxtab.quick_eat_desc", "饥饿时显示一键进食的快捷按钮，自动吃背包里的食物。")
			.t("$auxtab.bag_bottom_tabs_desc", "把包裹标签栏从背包窗口左右两侧移到底部，分页显示：每页 3 个包裹 + 翻页 + 主背包。切换包裹仍是一次点击，只有包裹多于 3 个时才需要先翻页；好处是标签不再占用窗口宽度，背包格子可以保持更大（高缩放时尤其明显）。")
			.t("$audiotab.title", "音频设置")
			.t("$audiotab.music_vol", "音乐音量")
			.t("$audiotab.music_mute", "关闭音乐")
			.t("$audiotab.sfx_vol", "音效音量")
			.t("$audiotab.sfx_mute", "关闭音效")
			.t("$audiotab.ignore_silent", "忽略静音模式")
			.t("$audiotab.music_bg", "后台播放音乐")
			.t("$langstab.title", "语言设置")
			.t("$langstab.completed", "这个语言已被完全翻译并审核完毕。")
			.t("$langstab.unreviewed", "_这个语言尚未被完全审核。_它也许还存在一些问题，不过所有的文本都已被翻译。")
			.t("$langstab.unfinished", "_这个语言尚未被完全翻译。_大量文本可能仍然为英语。")
			.t("$langstab.transifex", "所有翻译都由_Transifex_网站上的志愿者提供。")
			.t("$langstab.credits", "制作名单")
			.t("$langstab.reviewers", "审核员")
			.t("$langstab.translators", "翻译员");
	}




	private static final int WIDTH_P	    = 122;
	private static final int WIDTH_L	    = 223;

	private static final int SLIDER_HEIGHT	= 21;
	private static final int BTN_HEIGHT	    = 16;
	private static final float GAP          = 1;

	private DisplayTab  display;
	private UITab       ui;
	private InputTab    input;
	private AuxTab aux;   //SPSEXPD: 游戏辅助选项卡（替代原网络设置）
	private AudioTab    audio;
	private LangsTab    langs;

	public static int last_index = 0;

	public WndSettings() {
		super();

		float height;

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		//SPSEXPD: 游戏辅助选项卡（原「网络设置」已移除；用户要求放在第一个标签页）
		aux = new AuxTab();
		aux.setSize(width, AuxTab.paneHeight());   //SPSEXPD: 固定视口高度，否则窗口高度会算成 0
		height = aux.height();
		add( aux );

		add( new IconTab(new Image("sprites/items/specific/player.png")){
			@Override
			protected void select(boolean value) {
				super.select(value);
				aux.visible = aux.active = value;
				if (value) last_index = 0;
			}
		});

		display = new DisplayTab();
		display.setSize(width, 0);
		height = Math.max(height, display.height());   //SPSEXPD: 必须 max，否则会覆盖掉 aux 的高度导致窗口变矮、内容溢出
		add( display );

		add( new IconTab(Icons.get(Icons.DISPLAY)){
			@Override
			protected void select(boolean value) {
				super.select(value);
				display.visible = display.active = value;
				if (value) last_index = 1;
			}
		});

		ui = new UITab();
		ui.setSize(width, 0);
		height = Math.max(height, ui.height());
		add( ui );

		add( new IconTab(Icons.get(Icons.PREFS)){
			@Override
			protected void select(boolean value) {
				super.select(value);
				ui.visible = ui.active = value;
				if (value) last_index = 2;
			}
		});

		input = new InputTab();
		input.setSize(width, 0);
		height = Math.max(height, input.height());

		if (DeviceCompat.hasHardKeyboard() || ControllerHandler.isControllerConnected()) {
			add( input );
			Image icon;
			if (ControllerHandler.controllerActive || !DeviceCompat.hasHardKeyboard()){
				icon = Icons.get(Icons.CONTROLLER);
			} else {
				icon = Icons.get(Icons.KEYBOARD);
			}
			add(new IconTab(icon) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					input.visible = input.active = value;
					if (value) last_index = 3;
				}
			});
		}

		audio = new AudioTab();
		audio.setSize(width, 0);
		height = Math.max(height, audio.height());
		add( audio );

		add( new IconTab(Icons.get(Icons.AUDIO)){
			@Override
			protected void select(boolean value) {
				super.select(value);
				audio.visible = audio.active = value;
				if (value) last_index = 4;
			}
		});

		langs = new LangsTab();
		langs.setSize(width, 0);
		height = Math.max(height, langs.height());
		add( langs );


		IconTab langsTab = new IconTab(Icons.get(Icons.LANGS)){
			@Override
			protected void select(boolean value) {
				super.select(value);
				langs.visible = langs.active = value;
				if (value) last_index = 5;
			}

			@Override
			protected void createChildren() {
				super.createChildren();
				switch(Messages.lang().status()){
					case X_UNFINISH:
						icon.hardlight(1.5f, 0, 0);
						break;
					case __UNREVIEW:
						icon.hardlight(1.5f, 0.75f, 0f);
						break;
				}
			}

		};
		add( langsTab );

		resize(width, (int)Math.ceil(height));

		layoutTabs();

		if (tabs.size() == 5 && last_index >= 4){
			//input tab isn't visible
			select(last_index-1);
		} else {
			select(last_index);
		}

	}

	@Override
	public void hide() {
		super.hide();
		//resets generators because there's no need to retain chars for languages not selected
		ShatteredPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback() {
			@Override
			public void beforeCreate() {
				Game.platform.resetGenerators();
			}
			@Override
			public void afterCreate() {
				//do nothing
			}
		});
	}

	private static class DisplayTab extends Component {

		RenderedTextBlock title;
		ColorBlock sep1;
		CheckBox chkFullscreen;
		CheckBox chkLandscape;
		ColorBlock sep2;
		ColorBlock sep3;
		OptionSlider optBrightness;
		OptionSlider optVisGrid;
		OptionSlider optFollowIntensity;
		OptionSlider optScreenShake;

		@Override
		protected void createChildren() {
			title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			sep1 = new ColorBlock(1, 1, 0xFF000000);
			add(sep1);

			String fullscreenText = Messages.get(this, "fullscreen");
			if (DeviceCompat.isAndroid()){
				fullscreenText = Messages.get(this, "hide_navigation");
			} else if (DeviceCompat.isiOS()){
				fullscreenText = Messages.get(this, "hide_gesture");
			}
			chkFullscreen = new CheckBox( fullscreenText ) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.fullscreen(checked());
				}
			};
			if (Game.platform.supportsFullScreen()){
				chkFullscreen.checked(SPDSettings.fullscreen());
			} else {
				chkFullscreen.checked(true);
				chkFullscreen.enable(false);
			}
			add(chkFullscreen);

			if (DeviceCompat.isAndroid()) {
				chkLandscape = new CheckBox(Messages.get(this, "landscape")) {
					@Override
					protected void onClick() {
						super.onClick();
						SPDSettings.landscape(checked());
					}
				};
				chkLandscape.checked(SPDSettings.landscape());
				add(chkLandscape);
			}

			sep2 = new ColorBlock(1, 1, 0xFF000000);
			add(sep2);


			//SPSXPD: 「解锁炼金配方」已迁到「游戏辅助」页（用户要求集中放置）

			sep3 = new ColorBlock(1, 1, 0xFF000000);
			add(sep3);

			optBrightness = new OptionSlider(Messages.get(this, "brightness"),
					Messages.get(this, "dark"), Messages.get(this, "bright"), -1, 1) {
				@Override
				protected void onChange() {
					SPDSettings.brightness(getSelectedValue());
				}
			};
			optBrightness.setSelectedValue(SPDSettings.brightness());
			add(optBrightness);

			optVisGrid = new OptionSlider(Messages.get(this, "visual_grid"),
					Messages.get(this, "off"), Messages.get(this, "high"), -1, 2) {
				@Override
				protected void onChange() {
					SPDSettings.visualGrid(getSelectedValue());
				}
			};
			optVisGrid.setSelectedValue(SPDSettings.visualGrid());
			add(optVisGrid);

			optFollowIntensity = new OptionSlider(Messages.get(this, "camera_follow"),
					Messages.get(this, "low"), Messages.get(this, "high"), 1, 4) {
				@Override
				protected void onChange() {
					SPDSettings.cameraFollow(getSelectedValue());
				}
			};
			optFollowIntensity.setSelectedValue(SPDSettings.cameraFollow());
			add(optFollowIntensity);

			optScreenShake = new OptionSlider(Messages.get(this, "screenshake"),
					Messages.get(this, "off"), Messages.get(this, "high"), 0, 4) {
				@Override
				protected void onChange() {
					SPDSettings.screenShake(getSelectedValue());
				}
			};
			optScreenShake.setSelectedValue(SPDSettings.screenShake());
			add(optScreenShake);

		}

		@Override
		protected void layout() {

			float bottom = y;

			title.setPos((width - title.width())/2, bottom + GAP);
			sep1.size(width, 1);
			sep1.y = title.bottom() + 3*GAP;

			bottom = sep1.y + 1;

			chkFullscreen.setRect(0, bottom + GAP, width, BTN_HEIGHT);
			bottom = chkFullscreen.bottom();

			if (chkLandscape != null) {
				chkLandscape.setRect(0, bottom + GAP, width, BTN_HEIGHT);
				bottom = chkLandscape.bottom();
			}

			sep2.size(width, 1);
			sep2.y = bottom + GAP;
			bottom = sep2.y + 1;

			sep3.size(width, 1);
			sep3.y = bottom + GAP;
			bottom = sep3.y + 1;

			if (width > 200){
				optBrightness.setRect(0, bottom + GAP, width/2-GAP/2, SLIDER_HEIGHT);
				optVisGrid.setRect(optBrightness.right() + GAP, optBrightness.top(), width/2-GAP/2, SLIDER_HEIGHT);

				optFollowIntensity.setRect(0, optVisGrid.bottom() + GAP, width/2-GAP/2, SLIDER_HEIGHT);
				optScreenShake.setRect(optFollowIntensity.right() + GAP, optFollowIntensity.top(), width/2-GAP/2, SLIDER_HEIGHT);
			} else {
				optBrightness.setRect(0, bottom + GAP, width, SLIDER_HEIGHT);
				optVisGrid.setRect(0, optBrightness.bottom() + GAP, width, SLIDER_HEIGHT);

				optFollowIntensity.setRect(0, optVisGrid.bottom() + GAP, width, SLIDER_HEIGHT);
				optScreenShake.setRect(0, optFollowIntensity.bottom() + GAP, width, SLIDER_HEIGHT);
			}

			height = optScreenShake.bottom();
		}

	}

	private static class UITab extends Component {

		RenderedTextBlock title;

		ColorBlock sep1;
		OptionSlider optUIScale;
		RedButton btnToolbarSettings;
		CheckBox chkFlipTags;
		ColorBlock sep2;
		CheckBox chkFont;
		CheckBox chkVibrate;
		ColorBlock sep3;                    //SPSEXPD: 布局反转分组（自「游戏辅助」页迁入）
		CheckBox chkSwapAuxBar;

		@Override
		protected void createChildren() {
			title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			sep1 = new ColorBlock(1, 1, 0xFF000000);
			add(sep1);

			//SPS: 「界面模式」选项已取消（用户裁决 2026-09-28，UI 形式锁定移动端）

			if ((int)Math.ceil(2* Game.density) < PixelScene.maxDefaultZoom) {
				optUIScale = new OptionSlider(Messages.get(this, "scale"),
						(int)Math.ceil(2* Game.density)+ "X",
						PixelScene.maxDefaultZoom + "X",
						(int)Math.ceil(2* Game.density),
						PixelScene.maxDefaultZoom ) {
					@Override
					protected void onChange() {
						if (getSelectedValue() != SPDSettings.scale()) {
							SPDSettings.scale(getSelectedValue());
							ShatteredPixelDungeon.seamlessResetScene();
						}
					}
				};
				optUIScale.setSelectedValue(PixelScene.defaultZoom);
				add(optUIScale);
			}

			if (SPDSettings.interfaceSize() == 0) {
				btnToolbarSettings = new RedButton(Messages.get(this, "toolbar_settings"), 9){
					@Override
					protected void onClick() {
						ShatteredPixelDungeon.scene().addToFront(new Window(){

							RenderedTextBlock barDesc;
							RedButton btnSplit; RedButton btnGrouped; RedButton btnCentered;
							OptionSlider optQSlotBottom; OptionSlider optQSlotLeft; OptionSlider optQSlotRight;
							CheckBox chkFlipToolbar;
							CheckBox chkFlipTags;
							CheckBox chkSwapWaitSearch;   //SPSEXPD: 自「界面设置」迁入

							{
								barDesc = PixelScene.renderTextBlock(Messages.get(WndSettings.UITab.this, "mode"), 9);
								add(barDesc);

								btnSplit = new RedButton(Messages.get(WndSettings.UITab.this, "split")) {
									@Override
									protected void onClick() {
										textColor(TITLE_COLOR);
										btnGrouped.textColor(WHITE);
										btnCentered.textColor(WHITE);
										SPDSettings.toolbarMode(Toolbar.Mode.SPLIT.name());
										Toolbar.updateLayout();
									}
								};
								if (SPDSettings.toolbarMode().equals(Toolbar.Mode.SPLIT.name())) {
									btnSplit.textColor(TITLE_COLOR);
								}
								add(btnSplit);

								btnGrouped = new RedButton(Messages.get(WndSettings.UITab.this, "group")) {
									@Override
									protected void onClick() {
										btnSplit.textColor(WHITE);
										textColor(TITLE_COLOR);
										btnCentered.textColor(WHITE);
										SPDSettings.toolbarMode(Toolbar.Mode.GROUP.name());
										Toolbar.updateLayout();
									}
								};
								if (SPDSettings.toolbarMode().equals(Toolbar.Mode.GROUP.name())) {
									btnGrouped.textColor(TITLE_COLOR);
								}
								add(btnGrouped);

								btnCentered = new RedButton(Messages.get(WndSettings.UITab.this, "center")) {
									@Override
									protected void onClick() {
										btnSplit.textColor(WHITE);
										btnGrouped.textColor(WHITE);
										textColor(TITLE_COLOR);
										SPDSettings.toolbarMode(Toolbar.Mode.CENTER.name());
										Toolbar.updateLayout();
									}
								};
								if (SPDSettings.toolbarMode().equals(Toolbar.Mode.CENTER.name())) {
									btnCentered.textColor(TITLE_COLOR);
								}
								add(btnCentered);

								//SPS: 三区快捷栏数量（下 3-10、左 0-4、右 0-4，用户裁决 2026-09），改动即时重排
								optQSlotBottom = new OptionSlider(Messages.get(WndSettings.UITab.this, "qslot_bottom"), "3", "10", 3, 10) {
									@Override
									protected void onChange() {
										SPDSettings.quickslotsBottom(getSelectedValue());
										Toolbar.updateLayout();
										SideQuickBar.updateLayout();
									}
								};
								optQSlotBottom.setSelectedValue(SPDSettings.quickslotsBottom());
								add(optQSlotBottom);

								optQSlotLeft = new OptionSlider(Messages.get(WndSettings.UITab.this, "qslot_left"), "0", "4", 0, 4) {
									@Override
									protected void onChange() {
										SPDSettings.quickslotsLeft(getSelectedValue());
										Toolbar.updateLayout();
										SideQuickBar.updateLayout();
									}
								};
								optQSlotLeft.setSelectedValue(SPDSettings.quickslotsLeft());
								add(optQSlotLeft);

								optQSlotRight = new OptionSlider(Messages.get(WndSettings.UITab.this, "qslot_right"), "0", "4", 0, 4) {
									@Override
									protected void onChange() {
										SPDSettings.quickslotsRight(getSelectedValue());
										Toolbar.updateLayout();
										SideQuickBar.updateLayout();
									}
								};
								optQSlotRight.setSelectedValue(SPDSettings.quickslotsRight());
								add(optQSlotRight);

								chkFlipToolbar = new CheckBox(Messages.get(WndSettings.UITab.this, "flip_toolbar")) {
									@Override
									protected void onClick() {
										super.onClick();
										SPDSettings.flipToolbar(checked());
										Toolbar.updateLayout();
									}
								};
								chkFlipToolbar.checked(SPDSettings.flipToolbar());
								add(chkFlipToolbar);

								chkFlipTags = new CheckBox(Messages.get(WndSettings.UITab.this, "flip_indicators")){
									@Override
									protected void onClick() {
										super.onClick();
										SPDSettings.flipTags(checked());
										GameScene.layoutTags();
									}
								};
								chkFlipTags.checked(SPDSettings.flipTags());
								add(chkFlipTags);

								//SPSEXPD: 翻转等待与检视（自「界面设置」页迁入）
								chkSwapWaitSearch = new CheckBox(Messages.get(WndSettings.UITab.this, "swap_wait_search")) {
									@Override
									protected void onClick() {
										super.onClick();
										SPDSettings.swapWaitSearch(checked());
										//SPSEXPD: 立即重排工具栏，无需关闭窗口
										Toolbar.updateLayout();
									}
								};
								chkSwapWaitSearch.checked(SPDSettings.swapWaitSearch());
								add(chkSwapWaitSearch);

								//layout
								resize(WIDTH_P, 0);

								barDesc.setPos((width - barDesc.width()) / 2f, GAP);
								PixelScene.align(barDesc);

								int btnWidth = (int) (width - 2 * GAP) / 3;
								btnSplit.setRect(0, barDesc.bottom() + GAP, btnWidth, BTN_HEIGHT-2);
								btnGrouped.setRect(btnSplit.right() + GAP, btnSplit.top(), btnWidth, BTN_HEIGHT-2);
								btnCentered.setRect(btnGrouped.right() + GAP, btnSplit.top(), btnWidth, BTN_HEIGHT-2);

								optQSlotBottom.setRect(0, btnGrouped.bottom() + GAP, width, SLIDER_HEIGHT);
								optQSlotLeft.setRect(0, optQSlotBottom.bottom() + GAP, width/2-GAP/2, SLIDER_HEIGHT);
								optQSlotRight.setRect(optQSlotLeft.right() + GAP, optQSlotLeft.top(), width/2-GAP/2, SLIDER_HEIGHT);

								if (width > 200) {
									chkFlipToolbar.setRect(0, optQSlotRight.bottom() + GAP, width / 2 - 1, BTN_HEIGHT);
									chkFlipTags.setRect(chkFlipToolbar.right() + GAP, chkFlipToolbar.top(), width / 2 - 1, BTN_HEIGHT);
								} else {
									chkFlipToolbar.setRect(0, optQSlotRight.bottom() + GAP, width, BTN_HEIGHT);
									chkFlipTags.setRect(0, chkFlipToolbar.bottom() + GAP, width, BTN_HEIGHT);
								}

								//SPSEXPD: 文案较长，独占一行
								chkSwapWaitSearch.setRect(0, chkFlipTags.bottom() + GAP, width, BTN_HEIGHT);

								resize(WIDTH_P, (int)chkSwapWaitSearch.bottom());

							}
						});
					}
				};
				add(btnToolbarSettings);

			} else {

				chkFlipTags = new CheckBox(Messages.get(this, "flip_indicators")) {
					@Override
					protected void onClick() {
						super.onClick();
						SPDSettings.flipTags(checked());
						GameScene.layoutTags();
					}
				};
				chkFlipTags.checked(SPDSettings.flipTags());
				add(chkFlipTags);

			}

			sep2 = new ColorBlock(1, 1, 0xFF000000);
			add(sep2);

			chkFont = new CheckBox(Messages.get(this, "system_font")){
				@Override
				protected void onClick() {
					super.onClick();
					ShatteredPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback() {
						@Override
						public void beforeCreate() {
							SPDSettings.systemFont(checked());
						}

						@Override
						public void afterCreate() {
							//do nothing
						}
					});
				}
			};
			chkFont.checked(SPDSettings.systemFont());
			add(chkFont);

			chkVibrate = new CheckBox(Messages.get(this, "vibration")){
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.vibration(checked());
					if (checked()){
						Game.vibrate(250);
					}
				}
			};
			chkVibrate.enable(Game.platform.supportsVibration());
			if (chkVibrate.active) {
				chkVibrate.checked(SPDSettings.vibration());
			}
			add(chkVibrate);

			//SPSEXPD: 布局反转（自「游戏辅助」页迁入）——两个开关都即时重排
			sep3 = new ColorBlock(1, 1, 0xFF000000);
			add(sep3);

			chkSwapAuxBar = new CheckBox(Messages.get(this, "swap_aux_bar")) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.swapAuxBar(checked());
					//SPSEXPD: 立即重排快捷操作按钮与左侧快捷栏，无需关闭设置页或重开场景
					GameScene.refreshQuickActionLayout();
				}
			};
			chkSwapAuxBar.checked(SPDSettings.swapAuxBar());
			add(chkSwapAuxBar);
		}

		@Override
		protected void layout() {
			title.setPos((width - title.width())/2, y + GAP);
			sep1.size(width, 1);
			sep1.y = title.bottom() + 3*GAP;

			height = sep1.y + 1;

			//SPS: 界面模式滑条已移除（UI 形式锁定移动端），UI 缩放独占一行
			if (optUIScale != null) {
				optUIScale.setRect(0, height + GAP, width, SLIDER_HEIGHT);
				height = optUIScale.bottom();
			}

			if (btnToolbarSettings != null) {
				btnToolbarSettings.setRect(0, height + GAP, width, BTN_HEIGHT);
				height = btnToolbarSettings.bottom();
			} else {
				chkFlipTags.setRect(0, height + GAP, width, BTN_HEIGHT);
				height = chkFlipTags.bottom();
			}

			sep2.size(width, 1);
			sep2.y = height + GAP;

			if (width > 200) {
				chkFont.setRect(0, sep2.y + 1 + GAP, width/2-1, BTN_HEIGHT);
				chkVibrate.setRect(chkFont.right()+2, chkFont.top(), width/2-1, BTN_HEIGHT);
				height = chkVibrate.bottom();

			} else {
				chkFont.setRect(0, sep2.y + 1 + GAP, width, BTN_HEIGHT);
				chkVibrate.setRect(0, chkFont.bottom() + GAP, width, BTN_HEIGHT);
				height = chkVibrate.bottom();
			}

			//SPSEXPD: 布局反转分组（自「游戏辅助」页迁入）
			sep3.size(width, 1);
			sep3.y = height + GAP;
			height = sep3.y + 1;

			chkSwapAuxBar.setRect(0, height + GAP, width, BTN_HEIGHT);
			height = chkSwapAuxBar.bottom();
		}

	}

	private static class InputTab extends Component{

		RenderedTextBlock title;
		ColorBlock sep1;

		RedButton btnKeyBindings;
		RedButton btnControllerBindings;

		ColorBlock sep2;

		OptionSlider optControlSens;
		OptionSlider optHoldMoveSens;

		@Override
		protected void createChildren() {
			title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			sep1 = new ColorBlock(1, 1, 0xFF000000);
			add(sep1);

			if (DeviceCompat.hasHardKeyboard()){

				btnKeyBindings = new RedButton(Messages.get(this, "key_bindings")){
					@Override
					protected void onClick() {
						super.onClick();
						ShatteredPixelDungeon.scene().addToFront(new WndKeyBindings(false));
					}
				};

				add(btnKeyBindings);
			}

			if (ControllerHandler.isControllerConnected()){
				btnControllerBindings = new RedButton(Messages.get(this, "controller_bindings")){
					@Override
					protected void onClick() {
						super.onClick();
						ShatteredPixelDungeon.scene().addToFront(new WndKeyBindings(true));
					}
				};

				add(btnControllerBindings);
			}

			sep2 = new ColorBlock(1, 1, 0xFF000000);
			add(sep2);


			optControlSens = new OptionSlider(
					Messages.get(this, "controller_sensitivity"),
					"1",
					"10",
					1,
					10
			) {
				@Override
				protected void onChange() {
					SPDSettings.controllerPointerSensitivity(getSelectedValue());
				}
			};
			optControlSens.setSelectedValue(SPDSettings.controllerPointerSensitivity());
			add(optControlSens);

			optHoldMoveSens = new OptionSlider(
					Messages.get(this, "movement_sensitivity"),
					Messages.get(this, "off"),
					Messages.get(this, "high"),
					0,
					4
			) {
				@Override
				protected void onChange() {
					SPDSettings.movementHoldSensitivity(getSelectedValue());
				}
			};
			optHoldMoveSens.setSelectedValue(SPDSettings.movementHoldSensitivity());
			add(optHoldMoveSens);
		}

		@Override
		protected void layout() {
			title.setPos((width - title.width())/2, y + GAP);
			sep1.size(width, 1);
			sep1.y = title.bottom() + 3*GAP;

			height = sep1.y+1;

			if (width > 200 && btnKeyBindings != null && btnControllerBindings != null){
				btnKeyBindings.setRect(0, height + GAP, width/2-1, BTN_HEIGHT);
				btnControllerBindings.setRect(width/2+1, height + GAP, width/2-1, BTN_HEIGHT);
				height = btnControllerBindings.bottom();
			} else {
				if (btnKeyBindings != null) {
					btnKeyBindings.setRect(0, height + GAP, width, BTN_HEIGHT);
					height = btnKeyBindings.bottom();
				}

				if (btnControllerBindings != null) {
					btnControllerBindings.setRect(0, height + GAP, width, BTN_HEIGHT);
					height = btnControllerBindings.bottom();
				}
			}

			sep2.size(width, 1);
			sep2.y = height+ GAP;

			if (width > 200){
				optControlSens.setRect(0, sep2.y + 1 + GAP, width/2-1, SLIDER_HEIGHT);
				optHoldMoveSens.setRect(width/2 + 1, optControlSens.top(), width/2 -1, SLIDER_HEIGHT);
			} else {
				optControlSens.setRect(0, sep2.y + 1 + GAP, width, SLIDER_HEIGHT);
				optHoldMoveSens.setRect(0, optControlSens.bottom() + GAP, width, SLIDER_HEIGHT);
			}

			height = optHoldMoveSens.bottom();

		}
	}

	//SPSEXPD: 游戏辅助选项卡——移动路径点 / 搜索捡拾 / 快捷操作按钮开关 / 布局交换（替代原「网络设置」）
	//SPSEXPD: 游戏辅助选项卡——内容放进滚动容器，条目超出窗口高度时可以滚动
	private static class AuxTab extends Component {

		//SPSEXPD: 滚动容器的视口高度——内容超出这个高度时即可滚动。
		//不写死：横屏逻辑高仅约 160，减去 tab 栏 25 与 chrome 边距后放不下 160，会撑爆窗口
		static int paneHeight() {
			if (PixelScene.uiCamera == null) return 160;
			return (int) Math.max(60, Math.min(160, PixelScene.uiCamera.height - 42));   //42 ≈ tab 高 25 + chrome 边距/留白
		}

		private ScrollPane pane;
		private boolean laidOut = false;

		//SPSEXPD: 本页高度固定为视口高度（不再由内容撑开），否则窗口高度会算成 0 而完全不显示
		@Override
		public float height() {
			return paneHeight();
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			pane = new ScrollPane( new AuxContent() );
			add( pane );
			//SPSEXPD: 必须在 ScrollPane 构造完成之后再提升热区优先级（控制器在构造里注册，会插到最前）
			((AuxContent)pane.content()).givePointerPriority();
		}

		@Override
		protected void layout() {
			super.layout();
			//SPSEXPD: 构造期（还没 add 进窗口）父链上没有 camera，此时布局 ScrollPane 会 NPE，先跳过
			if (pane == null || camera() == null) return;

			//SPSEXPD: 父级宽度偶尔还没下发（挂载顺序问题），用窗口宽度常量兜底，
			//否则内容宽度为 0，文字根本画不出来（表现为“只有溢出、没有文本”）
			float w = width > 0 ? width : (PixelScene.landscape() ? WIDTH_L : WIDTH_P);

			AuxContent content = (AuxContent)pane.content();
			content.setSize( w, 0 );
			content.setSize( w, content.height() );

			pane.setRect( x, y, w, paneHeight() );
		}

		@Override
		public void update() {
			super.update();
			//SPSEXPD: 挂载到窗口后补一次布局；只补一次，否则每帧重排会把滚动位置一直清零
			if (!laidOut && pane != null && camera() != null) {
				layout();
				laidOut = true;
			}
		}
	}

	//SPSEXPD: 设置行末的说明按钮——用与「英雄信息」「挑战详情」等信息按钮相同的 Icons.INFO 图标，
	//点击弹出该开关的详细作用（WndTitledMessage）。
	//key 对应 windows.wndsettings$auxtab.<key>（标题）与 <key>_desc（详解）两个文案
	private static class AuxNoteButton extends IconButton {

		private final String key;

		AuxNoteButton(String key) {
			super( Icons.INFO.get() );
			this.key = key;
			width = AuxContent.NOTE_W;
			height = AuxContent.NOTE_W;
			//滚动区内必须 NEVER_BLOCK，否则 ScrollPane 的拖拽控制器会吞掉点击
			hotArea.blockLevel = PointerArea.NEVER_BLOCK;
		}

		@Override
		protected void onClick() {
			super.onClick();
			//SPSEXPD: 本按钮点击会替换当前窗口（GameScene.show），onPointerUp 送不到，
			//图标 brightness 会永久残留 —— 这里先手动还原；按压时的亮度反馈仍然保留
			if (icon() != null) icon().resetColor();
			GameScene.show(new WndTitledMessage(
					Icons.INFO.get(),
					Messages.get(AuxTab.class, key),
					Messages.get(AuxTab.class, key + "_desc") ));
		}

		//SPSEXPD: 不要悬浮提示——设置页在滚动区里，tooltip 容易闪；与旁边的复选框观感也一致
		@Override
		protected String hoverText() {
			return null;
		}
	}

	//SPSEXPD: 滚动容器内的复选框基类：热区必须 NEVER_BLOCK 且提到最前接收事件。
	//PointerEvent 信号是 stackMode（后注册者先收到、返回 true 即停止传播），
	//ScrollPane 的拖拽控制器后注册会吞掉点击，导致滚动正常但条目点不动（同 TalentButton/NEVER_BLOCK 的滚动区惯例）
	private static class AuxCheckBox extends CheckBox {
		AuxCheckBox(String label) {
			super(label);
			hotArea.blockLevel = PointerArea.NEVER_BLOCK;
		}
	}

	//SPSEXPD: 游戏辅助页的实际内容
	private static class AuxContent extends Component {

		RenderedTextBlock title;
		ColorBlock sep1;
		CheckBox chkHeroPath;
		CheckBox chkSearchPickUp;
		ColorBlock sep2;
		RenderedTextBlock quickGroup;   //SPSEXPD: 「快捷操作开关」小标题
		CheckBox chkQuickAll;
		CheckBox chkQuickLight;
		CheckBox chkQuickTalent;
		CheckBox chkQuickEat;
		//SPSEXPD: 背包相关开关——旧版布局（标签栏放底部，分页显示）
		CheckBox chkBagBottomTabs;

		//SPSEXPD: 背包分组的标题（小号字，风格同「快捷操作开关」）
		RenderedTextBlock bagGroup;
		ColorBlock sep3;

		//SPSEXPD: 每行行末「蓝色感叹号」说明按钮的边长
		static final int NOTE_W = 11;

		//SPSEXPD: 行集合——布局时逐行摆放（复选框 + 行末说明按钮），顺序＝添加顺序。
		//注意：必须在 createChildren() 里 new（见下方注释），不能用字段初始化器
		private ArrayList<Component> rowChecks;
		private ArrayList<AuxNoteButton> rowNotes;

		@Override
		protected void createChildren() {
			//SPSEXPD: 行集合必须在这里初始化——Component 的构造里就会调用 createChildren()，
			//那时子类的字段初始化器还没执行（用字段初始化器会 NPE）
			rowChecks = new ArrayList<>();
			rowNotes = new ArrayList<>();

			title = PixelScene.renderTextBlock(Messages.get(AuxTab.class, "title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			sep1 = new ColorBlock(1, 1, 0xFF000000);
			add(sep1);

			//移动路径点（原显示设置页迁入）
			chkHeroPath = new AuxCheckBox(Messages.get(AuxTab.class, "hero_path")) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.heroPath(checked());
					GameScene.clearHeroPath();
					GameScene.refreshHeroPath();
				}
			};
			chkHeroPath.checked(SPDSettings.heroPath());
			add(chkHeroPath);

			//搜索捡拾（原显示设置页迁入）
			chkSearchPickUp = new AuxCheckBox(Messages.get(AuxTab.class, "search_pickup")) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.searchPickUp(checked());
				}
			};
			chkSearchPickUp.checked(SPDSettings.searchPickUp());
			add(chkSearchPickUp);

			sep2 = new ColorBlock(1, 1, 0xFF000000);
			add(sep2);

			//SPSEXPD: 快捷操作分组的小标题（小号字，避免与页标题同尺寸）
			quickGroup = PixelScene.renderTextBlock(Messages.get(AuxTab.class, "quick_group"), 6);
			add(quickGroup);

			//快捷操作总开关（关闭后三个按钮都不显示，各自开关的状态保留）
			chkQuickAll = new AuxCheckBox(Messages.get(AuxTab.class, "quick_all")) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.quickAll(checked());
				}
			};
			chkQuickAll.checked(SPDSettings.quickAll());
			add(chkQuickAll);

			//快捷操作按钮：三种按钮各自开关，不需要的可单独关掉
			chkQuickLight = new AuxCheckBox(Messages.get(AuxTab.class, "quick_light")) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.quickLight(checked());
				}
			};
			chkQuickLight.checked(SPDSettings.quickLight());
			add(chkQuickLight);

			chkQuickTalent = new AuxCheckBox(Messages.get(AuxTab.class, "quick_talent")) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.quickTalent(checked());
				}
			};
			chkQuickTalent.checked(SPDSettings.quickTalent());
			add(chkQuickTalent);

			chkQuickEat = new AuxCheckBox(Messages.get(AuxTab.class, "quick_eat")) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.quickEat(checked());
				}
			};
			chkQuickEat.checked(SPDSettings.quickEat());
			add(chkQuickEat);

			//SPSEXPD: 背包分组——分隔线 + 小标题（风格与「快捷操作开关」一致）
			sep3 = new ColorBlock(1, 1, 0xFF000000);
			add(sep3);

			bagGroup = PixelScene.renderTextBlock(Messages.get(AuxTab.class, "bag_group"), 6);
			add(bagGroup);

			//SPSEXPD: 旧版背包界面——包裹标签栏从左右两侧移到底部（分页：3 包裹 + 翻页 + 主背包）
			chkBagBottomTabs = new AuxCheckBox(Messages.get(AuxTab.class, "bag_bottom_tabs")) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.bagBottomTabs(checked());
				}
			};
			chkBagBottomTabs.checked(SPDSettings.bagBottomTabs());
			add(chkBagBottomTabs);

			//SPSEXPD: 每一行行末都挂上蓝色感叹号说明按钮（顺序＝界面从上到下）
			addRowNote(chkHeroPath, "hero_path");
			addRowNote(chkSearchPickUp, "search_pickup");
			addRowNote(chkQuickAll, "quick_all");
			addRowNote(chkQuickLight, "quick_light");
			addRowNote(chkQuickTalent, "quick_talent");
			addRowNote(chkQuickEat, "quick_eat");
			addRowNote(chkBagBottomTabs, "bag_bottom_tabs");
		}

		//SPSEXPD: 给某一行挂上行末的说明按钮（复选框本身已在 createChildren 里 add 过）
		private void addRowNote(Component chk, String key) {
			AuxNoteButton note = new AuxNoteButton(key);
			add(note);
			rowChecks.add(chk);
			rowNotes.add(note);
		}

		//SPSEXPD: 提升本页复选框热区的事件优先级，必须在 ScrollPane 构造之后调用：
		//ScrollPane 的拖拽控制器在它自己的构造里注册，而 PointerEvent 信号是 stackMode（后注册者先收到），
		//不提升的话控制器会先吞掉按下/抬起事件，表现为能滚动但条目点不动
		void givePointerPriority() {
			for (Object o : members) {
				if (o instanceof AuxCheckBox) ((AuxCheckBox) o).givePointerPriority();
				else if (o instanceof AuxNoteButton) ((AuxNoteButton) o).givePointerPriority();
			}
		}

		@Override
		protected void layout() {
			float bottom = 0;
			title.setPos((width - title.width())/2, bottom + GAP);
			sep1.size(width, 1);
			sep1.y = title.bottom() + 3*GAP;
			bottom = sep1.y + 1;

			//SPSEXPD: 前 2 行——显示移动路径 / 搜索捡拾物品
			for (int i = 0; i < 2 && i < rowChecks.size(); i++) {
				bottom = layoutRow(i, width, bottom);
			}

			sep2.size(width, 1);
			sep2.y = bottom + GAP;
			bottom = sep2.y + 1;

			//SPSEXPD: 快捷操作分组的小标题
			quickGroup.setPos(0, bottom + GAP);
			bottom = quickGroup.bottom();

			//SPSEXPD: 快捷操作各行的索引是 2..5（总开关/照明/加点/进食），此处只摆这四行
			for (int i = 2; i < 6 && i < rowChecks.size(); i++) {
				bottom = layoutRow(i, width, bottom);
			}

			//SPSEXPD: 背包分组——分隔线 + 小标题，然后是「标签栏放底部」这一行
			sep3.size(width, 1);
			sep3.y = bottom + GAP;
			bottom = sep3.y + 1;

			bagGroup.setPos(0, bottom + GAP);
			bottom = bagGroup.bottom();

			for (int i = 6; i < rowChecks.size(); i++) {
				bottom = layoutRow(i, width, bottom);
			}

			height = bottom;
		}

		//SPSEXPD: 摆一行——复选框占左侧（行末留出说明按钮，两者之间空 2px 作隔断）
		private float layoutRow(int i, float width, float bottom) {
			Component chk = rowChecks.get(i);
			AuxNoteButton note = rowNotes.get(i);
			chk.setRect(0, bottom + GAP, width - NOTE_W - 2, BTN_HEIGHT);
			note.setRect(
					width - NOTE_W,
					bottom + GAP + (BTN_HEIGHT - NOTE_W) / 2f,
					NOTE_W, NOTE_W);
			return chk.bottom();
		}
	}
	private static class AudioTab extends Component {

		RenderedTextBlock title;
		ColorBlock sep1;
		OptionSlider optMusic;
		CheckBox chkMusicMute;
		ColorBlock sep2;
		OptionSlider optSFX;
		CheckBox chkMuteSFX;
		ColorBlock sep3;
		CheckBox chkIgnoreSilent;
		CheckBox chkMusicBG;

		@Override
		protected void createChildren() {
			title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			sep1 = new ColorBlock(1, 1, 0xFF000000);
			add(sep1);

			optMusic = new OptionSlider(Messages.get(this, "music_vol"), "0", "10", 0, 10) {
				@Override
				protected void onChange() {
					SPDSettings.musicVol(getSelectedValue());
				}
			};
			optMusic.setSelectedValue(SPDSettings.musicVol());
			add(optMusic);

			chkMusicMute = new CheckBox(Messages.get(this, "music_mute")){
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.music(!checked());
				}
			};
			chkMusicMute.checked(!SPDSettings.music());
			add(chkMusicMute);

			sep2 = new ColorBlock(1, 1, 0xFF000000);
			add(sep2);

			optSFX = new OptionSlider(Messages.get(this, "sfx_vol"), "0", "10", 0, 10) {
				@Override
				protected void onChange() {
					SPDSettings.SFXVol(getSelectedValue());
					if (Random.Int(100) == 0){
						Sample.INSTANCE.play(Assets.Sounds.MIMIC);
					} else {
						Sample.INSTANCE.play(Random.oneOf(Assets.Sounds.GOLD,
								Assets.Sounds.HIT,
								Assets.Sounds.ITEM,
								Assets.Sounds.SHATTER,
								Assets.Sounds.EVOKE,
								Assets.Sounds.SECRET));
					}
				}
			};
			optSFX.setSelectedValue(SPDSettings.SFXVol());
			add(optSFX);

			chkMuteSFX = new CheckBox( Messages.get(this, "sfx_mute") ) {
				@Override
				protected void onClick() {
					super.onClick();
					SPDSettings.soundFx(!checked());
					Sample.INSTANCE.play( Assets.Sounds.CLICK );
				}
			};
			chkMuteSFX.checked(!SPDSettings.soundFx());
			add( chkMuteSFX );

			if (DeviceCompat.isiOS()){

				sep3 = new ColorBlock(1, 1, 0xFF000000);
				add(sep3);

				chkIgnoreSilent = new CheckBox( Messages.get(this, "ignore_silent") ){
					@Override
					protected void onClick() {
						super.onClick();
						SPDSettings.ignoreSilentMode(checked());
					}
				};
				chkIgnoreSilent.checked(SPDSettings.ignoreSilentMode());
				add(chkIgnoreSilent);

			} else if (DeviceCompat.isDesktop()){

				sep3 = new ColorBlock(1, 1, 0xFF000000);
				add(sep3);

				chkMusicBG = new CheckBox( Messages.get(this, "music_bg") ){
					@Override
					protected void onClick() {
						super.onClick();
						SPDSettings.playMusicInBackground(checked());
					}
				};
				chkMusicBG.checked(SPDSettings.playMusicInBackground());
				add(chkMusicBG);
			}
		}

		@Override
		protected void layout() {
			title.setPos((width - title.width())/2, y + GAP);
			sep1.size(width, 1);
			sep1.y = title.bottom() + 3*GAP;

			if (width > 200) {
				optMusic.setRect(0, sep1.y + 1 + GAP, width/2-1, SLIDER_HEIGHT);
				chkMusicMute.setRect(0, optMusic.bottom() + GAP, width/2-1, BTN_HEIGHT);

				sep2.size(width, 1);
				sep2.y = sep1.y; //just have them overlap

				optSFX.setRect(optMusic.right()+2, sep2.y + 1 + GAP, width/2-1, SLIDER_HEIGHT);
				chkMuteSFX.setRect(chkMusicMute.right()+2, optSFX.bottom() + GAP, width/2-1, BTN_HEIGHT);

			} else {
				optMusic.setRect(0, sep1.y + 1 + GAP, width, SLIDER_HEIGHT);
				chkMusicMute.setRect(0, optMusic.bottom() + GAP, width, BTN_HEIGHT);

				sep2.size(width, 1);
				sep2.y = chkMusicMute.bottom() + GAP;

				optSFX.setRect(0, sep2.y + 1 + GAP, width, SLIDER_HEIGHT);
				chkMuteSFX.setRect(0, optSFX.bottom() + GAP, width, BTN_HEIGHT);
			}

			height = chkMuteSFX.bottom();

			if (chkIgnoreSilent != null){
				sep3.size(width, 1);
				sep3.y = chkMuteSFX.bottom() + GAP;

				chkIgnoreSilent.setRect(0, sep3.y + 1 + GAP, width, BTN_HEIGHT);
				height = chkIgnoreSilent.bottom();
			} else if (chkMusicBG != null){
				sep3.size(width, 1);
				sep3.y = chkMuteSFX.bottom() + GAP;

				chkMusicBG.setRect(0, sep3.y + 1 + GAP, width, BTN_HEIGHT);
				height = chkMusicBG.bottom();
			}
		}

	}

	private static class LangsTab extends Component{

		final static int COLS_P = 3;
		final static int COLS_L = 6;

		final static int BTN_HEIGHT = 11;

		RenderedTextBlock title;
		ColorBlock sep1;
		RenderedTextBlock txtLangInfo;
		ColorBlock sep2;
		RedButton[] lanBtns;
		ColorBlock sep3;
		RenderedTextBlock txtTranifex;
		RedButton btnCredits;

		@Override
		protected void createChildren() {
			title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			sep1 = new ColorBlock(1, 1, 0xFF000000);
			add(sep1);

			final ArrayList<Languages> langs = new ArrayList<>(Arrays.asList(Languages.values()));

			Languages nativeLang = Languages.matchLocale(Locale.getDefault());
			langs.remove(nativeLang);
			//move the native language to the top.
			langs.add(0, nativeLang);

			final Languages currLang = Messages.lang();

			txtLangInfo = PixelScene.renderTextBlock(6);
			String info = "_" + Messages.titleCase(currLang.nativeName()) + "_ - ";
			if (currLang == Languages.ENGLISH) info += "This is the source language, written by the developer.";
			else if (currLang.status() == Languages.Status.O_COMPLETE) info += Messages.get(this, "completed");
			else if (currLang.status() == Languages.Status.__UNREVIEW) info += Messages.get(this, "unreviewed");
			else if (currLang.status() == Languages.Status.X_UNFINISH) info += Messages.get(this, "unfinished");
			txtLangInfo.text(info);

			if (currLang.status() == Languages.Status.__UNREVIEW) txtLangInfo.setHightlighting(true, CharSprite.WARNING);
			else if (currLang.status() == Languages.Status.X_UNFINISH) txtLangInfo.setHightlighting(true, CharSprite.NEGATIVE);
			add(txtLangInfo);

			sep2 = new ColorBlock(1, 1, 0xFF000000);
			add(sep2);

			lanBtns = new RedButton[langs.size()];
			for (int i = 0; i < langs.size(); i++){
				final int langIndex = i;
				RedButton btn = new RedButton(Messages.titleCase(langs.get(i).nativeName()), 6){
					@Override
					protected void onClick() {
						super.onClick();
						Messages.setup(langs.get(langIndex));
						ShatteredPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback() {
							@Override
							public void beforeCreate() {
								SPDSettings.language(langs.get(langIndex));
								GameLog.wipe();
								Game.platform.resetGenerators();
							}
							@Override
							public void afterCreate() {
								//do nothing
							}
						});
					}
				};
				if (currLang == langs.get(i)){
					btn.textColor(TITLE_COLOR);
				} else {
					switch (langs.get(i).status()) {
						case X_UNFINISH:
							btn.textColor(0x888888);
							break;
						case __UNREVIEW:
							btn.textColor(0xBBBBBB);
							break;
					}
				}
				lanBtns[i] = btn;
				add(btn);
			}

			sep3 = new ColorBlock(1, 1, 0xFF000000);
			add(sep3);

			txtTranifex = PixelScene.renderTextBlock(5);
			txtTranifex.text(Messages.get(this, "transifex"));
			add(txtTranifex);

			if (currLang != Languages.ENGLISH) {
				String credText = Messages.titleCase(Messages.get(this, "credits"));
				btnCredits = new RedButton(credText, credText.length() > 9 ? 6 : 9) {
					@Override
					protected void onClick() {
						super.onClick();
						String[] reviewers = currLang.reviewers();
						String[] translators = currLang.translators();

						int totalCredits = 2*reviewers.length + translators.length;
						int totalTokens = 2*totalCredits; //for spaces

						//additional space for titles, and newline chars
						if (reviewers.length > 0) totalTokens+=6;
						totalTokens +=4;

						String[] entries = new String[totalTokens];
						int index = 0;
						if (reviewers.length > 0){
							entries[0] = "_";
							entries[1] = Messages.titleCase(Messages.get(LangsTab.this, "reviewers"));
							entries[2] = "_";
							entries[3] = "\n";
							index = 4;
							for (int i = 0; i < reviewers.length; i++){
								entries[index] = reviewers[i];
								if (i < reviewers.length-1) entries[index] += ", ";
								entries[index+1] = " ";
								index += 2;
							}
							entries[index] = "\n";
							entries[index+1] = "\n";
							index += 2;
						}

						entries[index] = "_";
						entries[index+1] = Messages.titleCase(Messages.get(LangsTab.this, "translators"));
						entries[index+2] = "_";
						entries[index+3] = "\n";
						index += 4;

						//reviewers are also shown as translators
						for (int i = 0; i < reviewers.length; i++){
							entries[index] = reviewers[i];
							if (i < reviewers.length-1 || translators.length > 0) entries[index] += ", ";
							entries[index+1] = " ";
							index += 2;
						}

						for (int i = 0; i < translators.length; i++){
							entries[index] = translators[i];
							if (i < translators.length-1) entries[index] += ", ";
							entries[index+1] = " ";
							index += 2;
						}

						Window credits = new Window(0, 0, Chrome.get(Chrome.Type.TOAST));

						int w = PixelScene.landscape() ? 120 : 80;
						if (totalCredits >= 25) w *= 1.5f;

						RenderedTextBlock title = PixelScene.renderTextBlock(9);
						title.text(Messages.titleCase(Messages.get(LangsTab.this, "credits")), w);
						title.hardlight(TITLE_COLOR);
						title.setPos((w - title.width()) / 2, 0);
						credits.add(title);

						RenderedTextBlock text = PixelScene.renderTextBlock(7);
						text.maxWidth(w);
						text.tokens(entries);

						text.setPos(0, title.bottom() + 4);
						credits.add(text);

						credits.resize(w, (int) text.bottom() + 2);
						ShatteredPixelDungeon.scene().addToFront(credits);
					}
				};
				add(btnCredits);
			}

		}

		@Override
		protected void layout() {
			title.setPos((width - title.width())/2, y + GAP);
			sep1.size(width, 1);
			sep1.y = title.bottom() + 3*GAP;

			txtLangInfo.setPos(0, sep1.y + 1 + GAP);
			txtLangInfo.maxWidth((int)width);

			y = txtLangInfo.bottom() + 2*GAP;
			int x = 0;

			sep2.size(width, 1);
			sep2.y = y;
			y += 2;

			int cols = PixelScene.landscape() ? COLS_L : COLS_P;
			int btnWidth = (int)Math.floor((width - (cols-1)) / cols);
			for (RedButton btn : lanBtns){
				btn.setRect(x, y, btnWidth, BTN_HEIGHT);
				btn.setPos(x, y);
				x += btnWidth+1;
				if (x + btnWidth > width){
					x = 0;
					y += BTN_HEIGHT+1;
				}
			}
			if (x > 0){
				y += BTN_HEIGHT+1;
			}

			sep3.size(width, 1);
			sep3.y = y;
			y += 2;

			if (btnCredits != null){
				btnCredits.setSize(btnCredits.reqWidth() + 2, 16);
				btnCredits.setPos(width - btnCredits.width(), y);

				txtTranifex.setPos(0, y);
				txtTranifex.maxWidth((int)btnCredits.left());

				height = Math.max(btnCredits.bottom(), txtTranifex.bottom());
			} else {
				txtTranifex.setPos(0, y);
				txtTranifex.maxWidth((int)width);

				height = txtTranifex.bottom();
			}

		}
	}
}
