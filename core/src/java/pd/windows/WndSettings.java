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
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.services.updates.Updates;
import pd.sprites.CharSprite;
import pd.ui.CheckBox;
import pd.ui.GameLog;
import pd.ui.Icons;
import pd.ui.OptionSlider;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.SideQuickBar;
import pd.ui.Toolbar;
import pd.ui.Window;
import render.input.ControllerHandler;
import render.noosa.ColorBlock;
import render.noosa.Game;
import render.noosa.Image;
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
			.t("displaytab.title", "显示设置")
			.t("displaytab.fullscreen", "全屏模式")
			.t("displaytab.hide_navigation", "隐藏导航栏")
			.t("displaytab.hide_gesture", "隐藏手势栏")
			.t("displaytab.okay", "开启")
			.t("displaytab.cancel", "取消")
			.t("displaytab.landscape", "强制横屏")
			.t("displaytab.brightness", "亮度")
			.t("displaytab.dark", "暗")
			.t("displaytab.bright", "亮")
			.t("displaytab.visual_grid", "网格可视度")
			.t("displaytab.off", "关闭")
			.t("displaytab.low", "低")
			.t("displaytab.high", "最高")
			.t("displaytab.camera_follow", "镜头追踪强度")
			.t("displaytab.screenshake", "震屏")
			.t("uitab.title", "界面设置")
			.t("uitab.ui_mode", "界面模式")
			.t("uitab.scale", "界面尺寸")
			.t("uitab.mobile", "移动端")
			.t("uitab.full", "全尺寸")
			.t("uitab.toolbar_settings", "工具栏设置")
			.t("uitab.mode", "工具栏模式：")
			.t("uitab.split", "分散")
			.t("uitab.group", "组合")
			.t("uitab.center", "居中")
			.t("uitab.flip_toolbar", "翻转工具栏")
			.t("uitab.flip_indicators", "翻转指示器")
			.t("uitab.qslot_bottom", "下快捷栏数量")
			.t("uitab.qslot_left", "左快捷栏数量")
			.t("uitab.qslot_right", "右快捷栏数量")
			.t("uitab.quickslot_swapper", "切换快捷栏")
			.t("uitab.swapper_desc", "当空间不足以显示所有6个快捷栏的时候，通过“切换快捷栏”按钮可以交替显示另外3个快捷栏。")
			.t("uitab.system_font", "系统字体")
			.t("uitab.off", "关闭")
			.t("uitab.high", "最高")
			.t("uitab.vibration", "振动")
			.t("inputtab.title", "输入设置")
			.t("inputtab.key_bindings", "键鼠键位")
			.t("inputtab.controller_bindings", "控制器键位")
			.t("inputtab.controller_sensitivity", "控制器光标灵敏度")
			.t("inputtab.movement_sensitivity", "控制器移动灵敏度")
			.t("inputtab.off", "关闭")
			.t("inputtab.high", "最高")
			.t("datatab.title", "网络设置")
			.t("datatab.news", "自动检查新闻")
			.t("datatab.updates", "自动检查更新")
			.t("datatab.betas", "包含Beta测试的内容")
			.t("datatab.wifi", "仅在WIFI环境下检查")
			.t("audiotab.title", "音频设置")
			.t("audiotab.music_vol", "音乐音量")
			.t("audiotab.music_mute", "关闭音乐")
			.t("audiotab.sfx_vol", "音效音量")
			.t("audiotab.sfx_mute", "关闭音效")
			.t("audiotab.ignore_silent", "忽略静音模式")
			.t("audiotab.music_bg", "后台播放音乐")
			.t("langstab.title", "语言设置")
			.t("langstab.completed", "这个语言已被完全翻译并审核完毕。")
			.t("langstab.unreviewed", "_这个语言尚未被完全审核。_它也许还存在一些问题，不过所有的文本都已被翻译。")
			.t("langstab.unfinished", "_这个语言尚未被完全翻译。_大量文本可能仍然为英语。")
			.t("langstab.transifex", "所有翻译都由_Transifex_网站上的志愿者提供。")
			.t("langstab.credits", "制作名单")
			.t("langstab.reviewers", "审核员")
			.t("langstab.translators", "翻译员");
	}


	private static final int WIDTH_P	    = 122;
	private static final int WIDTH_L	    = 223;

	private static final int SLIDER_HEIGHT	= 21;
	private static final int BTN_HEIGHT	    = 16;
	private static final float GAP          = 1;

	private DisplayTab  display;
	private UITab       ui;
	private InputTab    input;
	private DataTab     data;
	private AudioTab    audio;
	private LangsTab    langs;

	public static int last_index = 0;

	public WndSettings() {
		super();

		float height;

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		display = new DisplayTab();
		display.setSize(width, 0);
		height = display.height();
		add( display );

		add( new IconTab(Icons.get(Icons.DISPLAY)){
			@Override
			protected void select(boolean value) {
				super.select(value);
				display.visible = display.active = value;
				if (value) last_index = 0;
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
				if (value) last_index = 1;
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
					if (value) last_index = 2;
				}
			});
		}

		data = new DataTab();
		data.setSize(width, 0);
		height = Math.max(height, data.height());
		add( data );

		add( new IconTab(Icons.get(Icons.DATA)){
			@Override
			protected void select(boolean value) {
				super.select(value);
				data.visible = data.active = value;
				if (value) last_index = 3;
			}
		});

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

		if (tabs.size() == 5 && last_index >= 3){
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

								resize(WIDTH_P, (int)chkFlipTags.bottom());

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

	private static class DataTab extends Component{

		RenderedTextBlock title;
		ColorBlock sep1;
		CheckBox chkUpdates;
		CheckBox chkBetas;
		CheckBox chkWifi;

		@Override
		protected void createChildren() {
			title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			sep1 = new ColorBlock(1, 1, 0xFF000000);
			add(sep1);

			//SPSEXPD: 新闻入口已移除（上游新闻服务不再接入），数据页不再有「新闻」开关
			if (Updates.supportsUpdates() && Updates.supportsUpdatePrompts()) {
				chkUpdates = new CheckBox(Messages.get(this, "updates")) {
					@Override
					protected void onClick() {
						super.onClick();
						SPDSettings.updates(checked());
						Updates.clearUpdate();
					}
				};
				chkUpdates.checked(SPDSettings.updates());
				add(chkUpdates);

				if (Updates.supportsBetaChannel()){
					chkBetas = new CheckBox(Messages.get(this, "betas")) {
						@Override
						protected void onClick() {
							super.onClick();
							SPDSettings.betas(checked());
							Updates.clearUpdate();
						}
					};
					chkBetas.checked(SPDSettings.betas());
					add(chkBetas);
				}
			}

			if (!DeviceCompat.isDesktop()){
				chkWifi = new CheckBox(Messages.get(this, "wifi")){
					@Override
					protected void onClick() {
						super.onClick();
						SPDSettings.WiFi(checked());
					}
				};
				chkWifi.checked(SPDSettings.WiFi());
				add(chkWifi);
			}
		}

		@Override
		protected void layout() {
			title.setPos((width - title.width())/2, y + GAP);
			sep1.size(width, 1);
			sep1.y = title.bottom() + 3*GAP;

			float pos = sep1.y + 1 + GAP;

			if (chkUpdates != null) {
				chkUpdates.setRect(0, pos, width, BTN_HEIGHT);
				pos = chkUpdates.bottom();
			}

			if (chkBetas != null){
				chkBetas.setRect(0, pos + GAP, width, BTN_HEIGHT);
				pos = chkBetas.bottom();
			}

			if (chkWifi != null){
				chkWifi.setRect(0, pos + GAP, width, BTN_HEIGHT);
				pos = chkWifi.bottom();
			}

			height = pos;

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
