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
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.CombatStyle;
import pd.actors.hero.HeroClass;
import pd.journal.Journal;
import pd.messages.Messages;
import pd.sprites.HeroSprite;
import pd.ui.ActionIndicator;
import pd.ui.CheckBox;
import pd.ui.ExitButton;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.OptionSlider;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.StyledButton;
import pd.ui.Window;
import pd.utils.DungeonSeed;
import pd.windows.WndChallenges;
import pd.windows.WndHeroInfo;
import pd.windows.WndKeyBindings;
import pd.windows.WndMessage;
import pd.windows.WndOptions;
import pd.windows.WndTextInput;
import pd.windows.WndTitledMessage;
import pd.windows.WndVictoryCongrats;
import render.gltextures.TextureCache;
import render.noosa.Camera;
import render.noosa.ColorBlock;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.PointerArea;
import render.noosa.SkinnedBlock;
import render.noosa.ui.Component;
import render.utils.geom.RectF;
import render.utils.math.Random;
import render.utils.platform.DeviceCompat;
import render.utils.platform.PlatformSupport;

import java.util.ArrayList;
import pd.messages.InlineText;

public class HeroSelectScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(HeroSelectScene.class)
			.t("start", "开始")
			.t("skin_title", "选择外观")
			.t("skin_desc", "外观仅改变角色图像，不会影响战斗数值；所选外观会随本局存档保存。")
			.t("skin_0", "经典")
			.t("skin_1", "赤焰")
			.t("skin_2", "苍穹")
			.t("skin_3", "荒野")
			.t("skin_4", "星辉")
			.t("skin_5", "暗影")
			.t("skin_6", "霜雪")
			.t("skin_7", "异界")
			.t("skin_back", "返回职业选择")
			.t("style_title", "选择战斗风格")
			.t("style_desc", "战斗风格与外观相互独立。除平衡外，每种风格都有数值接近的优势与代价，并会随本局存档保存。")
			.t("style_back", "返回外观选择")
			.t("options", "游戏选项")
			.t("custom_seed", "自定义种子")
			.t("daily", "每日挑战")
			.t("daily_desc", "每天都会有一局对所有玩家都一样的新游戏可供游玩！“每日挑战”会为所有玩家生成相同的地牢(前提是他们所玩的游戏版本也相同)。\n\n你可以不受时限地完成每日挑战，但同时只能进行一局每日挑战。_每日挑战无法获得徽章，而享有独立的排行榜页面。_\n\n你想以当前选择的英雄与挑战开启今天的每日挑战吗？")
			.t("daily_yes", "出发！")
			.t("daily_no", "算了")
			.t("daily_repeat", "你已经游玩过了今天的每日挑战。你当然可以反复游玩它，但仅有练习的意义，只有第一次游玩的数据能参与排位。\n\n当你再次完成今天的每日挑战之后，你依旧可以在排位界面查看其数据，只是该数据会很快被删除。\n\n你想要使用当前选中的英雄与挑战重玩今天的每日挑战吗？")
			.t("daily_unavailable_long", "你似乎在未来开始了一场每日挑战！这种情况通常在你改变了时区的情况下发生，亦有可能是因为你篡改了系统时间。_你的下一场每日挑战将在%d天后可用。_")
			.t("daily_existing", "你已有一场每日挑战在进行中。在开启下一场每日挑战之前，你必须先完成它。")
			.t("daily_nowin", "每天都有一场新游戏，对每个人都是一样的！“每日挑战”会为每个玩家生成相同的地牢(前提是他们在游玩相同的游戏版本)。\n\n_你必须先正常通关至少一次，才能游玩每日挑战。_")
			.t("custom_seed_title", "输入自定义种子")
			.t("custom_seed_desc", "游戏通过使用种子来生成地牢，在版本不变的情况下，使用同一个种子将总是生成相同的地牢！_在应用自定义种子的游戏当中既无法获得徽章，也不计入已进行的游戏，仅会显示在排行榜界面的底部。_")
			.t("custom_seed_duplicate", "你已有一场使用该种子的游戏在进行中。在使用该种子开始新游戏之前，你必须先结束先前的游戏。")
			.t("custom_seed_nowin", "游戏通过使用种子来生成地牢，在版本不变的情况下，使用同一个种子将总是生成相同的地牢！\n\n_你必须先正常通关至少一次，才能使用自定义种子进行游玩。_")
			.t("custom_seed_set", "设置")
			.t("custom_seed_clear", "清除")
			.t("challenges_nowin", "挑战是一类为游戏增添难度的设置选项，可自行选择开启与否。其中的一些会使地牢变得更加危险，另一些则会削弱角色本身或其他物品的力量。\n\n_你必须先正常通关至少一次，才能开启挑战进行游玩。_")
			.t("randomize", "随机")
			.t("randomize_hero", "随机英雄")
			.t("randomize_chals", "随机挑战")
			.t("randomize_chals_title", "挑战个数")
			.t("randomize_confirm", "确定")
			.t("randomize_cancel", "取消");
	}




	//SPSEXPD: 选角素材规格（splashes/avatars.png 每格 28x36；splashes/closeup/*.png 为 800x140 横幅）
	private static final int AVATAR_W = 28;
	private static final int AVATAR_H = 36;
	private static final int CLOSEUP_H = 140;
	private static final int BG_COLOR = 0xFF2d2f31;

	//SPSEXPD: 特写图底部过渡阴影的分段数（逐段 ColorBlock 近似竖直渐变，避免绕 origin 旋转的定位坑）
	private static final int SHADE_STEPS = 24;

	private Image background;
	private Image closeup;         //职业特写横幅
	private ColorBlock[] closeupShade; //特写图底部向背景色的过渡阴影

	//fading UI elements
	private ArrayList<HeroBtn> heroBtns = new ArrayList<>();
	private RenderedTextBlock heroName;
	private RenderedTextBlock heroDesc;
	private StyledButton startBtn;
	private IconButton infoButton;
	private GameOptions optionsPane;
	private IconButton btnExit;

	//SPSEXPD: 上一次布局用的屏幕尺寸（窗口缩放/旋转时重排）
	private float layoutW = -1, layoutH = -1;

	//SPSEXPD: 「名字 + 描述」区的预留高度（= 所有可玩职业里最长的描述高度，-1 表示未测量）
	private float descReserveH = -1;

	private RectF insets;

	private static boolean heroWasRandomized = true;
	private static boolean chalWasRandomized = false;

	@Override
	public void create() {
		super.create();

		Dungeon.hero = null;

		Badges.loadGlobal();
		Journal.loadGlobal();

		insets = Game.platform.getSafeInsets(PlatformSupport.INSET_BLK).scale(1f/defaultZoom);

		float w = (Camera.main.width - insets.left - insets.right);
		float h = (Camera.main.height - insets.top - insets.bottom);

		//SPSEXPD: 背景退化为纯色底（职业立绘改为顶部特写横幅，见 closeup）
		background = new Image(TextureCache.createSolid(BG_COLOR), 0, 0, 1, 1);
		add(background);

		closeup = new Image();
		closeup.visible = false;
		add(closeup);

		closeupShade = new ColorBlock[SHADE_STEPS];
		for (int i = 0; i < closeupShade.length; i++){
			closeupShade[i] = new ColorBlock(1, 1, BG_COLOR);
			closeupShade[i].visible = false;
			add(closeupShade[i]);
		}

		startBtn = new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){
			@Override
			protected void onClick() {
				super.onClick();

				if (GamesInProgress.selectedClass == null) return;
				chooseSkinAndStart();
			}
		};
		startBtn.icon(Icons.get(Icons.ENTER));
		startBtn.setSize(80, 21);
		startBtn.textColor(Window.TITLE_COLOR);
		add(startBtn);
		startBtn.visible = startBtn.active = false;

		infoButton = new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				super.onClick();
				HeroClass cls = GamesInProgress.selectedClass;
				if (cls != null) {
					Window info = new WndHeroInfo(GamesInProgress.selectedClass);
					if (landscape()) {
						info.offset((int)(w / 6), 0);
					}
					ShatteredPixelDungeon.scene().addToFront(info);
				}
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info"));
			}
		};
		infoButton.visible = infoButton.active = false;
		infoButton.setSize(20, 21);
		add(infoButton);

		for (HeroClass cl : HeroClass.playableClasses()){
			HeroBtn button = new HeroBtn(cl);
			add(button);
			heroBtns.add(button);
		}

		//SPSEXPD: 选项面板常显在底部（不再用齿轮图标切换显示）
		optionsPane = new GameOptions();
		optionsPane.layout();
		add(optionsPane);

		heroName = renderTextBlock(9);
		add(heroName);

		heroDesc = renderTextBlock(6);
		heroDesc.align(RenderedTextBlock.CENTER_ALIGN);
		add(heroDesc);

		//add a darkening bar along bottom
		if (insets.bottom > 0){
			SkinnedBlock bar = new SkinnedBlock(Camera.main.width, insets.bottom, TextureCache.createSolid(0xAA000000));
			bar.y = h + insets.top;
			add(bar);

			PointerArea blocker = new PointerArea(0, Camera.main.width - insets.bottom, Camera.main.width, insets.bottom);
			add(blocker);
		}

		layoutScene();

		btnExit = new ExitButton();
		int ofs = PixelScene.landscape() ? 0 : 4;
		btnExit.setPos( Camera.main.width - btnExit.width() - ofs, ofs );
		add( btnExit );
		btnExit.visible = btnExit.active = !SPDSettings.intro();

		if (GamesInProgress.selectedClass != null){
			setSelectedHero(GamesInProgress.selectedClass);
		}

		if (Badges.isUnlocked(Badges.Badge.VICTORY) && !SPDSettings.victoryNagged()) {
			SPDSettings.victoryNagged(true);
			add(new WndVictoryCongrats());
		}

		fadeIn();

	}

	private void setSelectedHero(HeroClass cl){
		if (GamesInProgress.selectedClass != cl) {
			GamesInProgress.selectedSkin = 0;
			GamesInProgress.selectedStyle = CombatStyle.BALANCED;
		}
		GamesInProgress.selectedClass = cl;
		GamesInProgress.randomizedClass = false;

		//SPSEXPD: 顶部特写横幅（没有特写素材的隐藏职业回退到原职业立绘）
		String art = cl.closeupArt();
		if (art == null) art = cl.splashArt();
		try {
			//loading these big jpgs fails sometimes, so we have a catch for it
			closeup.texture(art);
		} catch (Exception e){
			Game.reportException(e);
			closeup.texture(TextureCache.createSolid(BG_COLOR));
			closeup.frame(0, 0, 1, 1);
		}
		closeup.visible = true;

		heroName.text(Messages.titleCase(cl.title()));
		heroName.hardlight(Window.TITLE_COLOR);

		heroDesc.text(cl.shortDesc());

		startBtn.visible = startBtn.active = true;
		infoButton.visible = infoButton.active = true;

		layoutScene();
	}

	//SPSEXPD: 选角界面自上而下的布局：特写横幅 / 过渡阴影 / 选角头像 / 英雄名与描述 / 底部图标选项行与开始
	private void layoutScene(){

		float w = Math.max(1, Camera.main.width - insets.left - insets.right);
		float h = Math.max(1, Camera.main.height - insets.top - insets.bottom);
		float left = insets.left;

		//纯色底铺满整屏（含安全区）；向外多铺 2 逻辑像素，
		//避免相机视口按物理像素取整时在屏幕边缘留下 1px 未绘制的空白条
		background.x = -2;
		background.y = -2;
		background.scale.set(Camera.main.width + 4, Camera.main.height + 4);

		//SPSEXPD: 顶部不再显示标题，特写横幅贴安全区顶部；底部图标选项行居中，「开始」在其下方单独一行居中（贴底）
		optionsPane.layout();

		startBtn.text(Messages.titleCase(Messages.get(this, "start")));
		startBtn.setSize(startBtn.reqWidth() + 8, 21);
		startBtn.setPos(left + (w - startBtn.width())/2f, Camera.main.height - insets.bottom - 4 - startBtn.height());
		align(startBtn);

		optionsPane.setPos(left + (w - optionsPane.width())/2f, startBtn.top() - 4 - optionsPane.height());
		align(optionsPane);

		float bottomTop = optionsPane.top() - 4;

		//SPSEXPD: 描述高度按"所有可玩职业里最长的描述"预留，整块内容（横幅/头像/名字/描述）在可用空间里居中，
		//这样切换职业时描述行数变化不会顶动上方的头像与横幅
		heroDesc.maxWidth(Math.max(40, (int)(w - 8)));
		if (descReserveH < 0){
			//用临时文本块测量（不污染正在显示的 heroDesc）
			RenderedTextBlock probe = renderTextBlock(6);
			probe.maxWidth(Math.max(40, (int)(w - 8)));
			descReserveH = 0;
			for (HeroClass cl : HeroClass.playableClasses()){
				probe.text(cl.shortDesc());
				descReserveH = Math.max(descReserveH, probe.height());
			}
		}

		float infoH = heroName.height() + 3 + descReserveH;
		float space = Math.max(40, bottomTop - 4 - insets.top);

		//特写横幅：横屏按宽度完整展示（contain），竖屏放大到屏高 1/3 并裁掉两侧（cover）
		float tipY = insets.top;
		float texW = Math.max(1, closeup.width);
		float texH = Math.max(1, closeup.height);

		float areaH = landscape()
				? texH * (w / texW)
				: h / 3f;
		areaH = Math.max(12, Math.min(areaH, space - infoH - 24));

		float closeScale = landscape()
				? Math.min(w / texW, areaH / texH)
				: Math.max(w / texW, areaH / texH);

		//选角头像：横屏一行、竖屏两行，尺寸按剩余空间自适应
		int rows = landscape() ? 1 : 2;
		int cols = (int)Math.ceil(heroBtns.size() / (float)rows);
		float avatarArea = Math.max(16, space - areaH - infoH - 8);
		float rowMaxH = (avatarArea - (rows - 1)) / rows;
		float rowMaxW = (w - 8 - (cols - 1) * 2) / cols - 4;
		float scale = Math.min(1f, Math.min(rowMaxH / AVATAR_H, rowMaxW / AVATAR_W));
		scale = Math.max(0.45f, scale);

		float btnW = AVATAR_W * scale + 4;
		float btnH = AVATAR_H * scale + 4;
		float avatarsH = rows * btnH + (rows - 1);

		//SPSEXPD: 特写横幅贴顶；「头像 + 名字 + 描述」整块在横幅下方到图标行之间的空间里垂直居中
		float contentH = avatarsH + 4 + infoH;
		float offsetY = Math.max(0, (space - areaH - contentH)/2f);

		closeup.scale.set(closeScale, closeScale);
		closeup.x = left + (w - texW * closeScale)/2f;
		closeup.y = tipY + (areaH - texH * closeScale)/2f;
		align(closeup);

		//特写图底部向背景色过渡的阴影
		float shadeH = Math.min(areaH * 0.45f, 26);
		float stepH = shadeH / closeupShade.length;
		for (int i = 0; i < closeupShade.length; i++){
			ColorBlock blk = closeupShade[i];
			blk.x = 0;
			blk.y = tipY + areaH - shadeH + stepH * i;
			blk.size(Camera.main.width, stepH + 0.5f);
			blk.alpha((i + 1f) / closeupShade.length);
			blk.visible = closeup.visible;
		}

		float rowsY = tipY + areaH + offsetY;

		for (int r = 0; r < rows; r++){
			int count = Math.min(cols, heroBtns.size() - r * cols);
			if (count <= 0) break;
			float rowW = count * btnW + (count - 1) * 2;
			float rowX = left + (w - rowW)/2f;
			for (int c = 0; c < count; c++){
				HeroBtn btn = heroBtns.get(r * cols + c);
				btn.setAvatarScale(scale);
				btn.setRect(rowX + c * (btnW + 2), rowsY + r * (btnH + 1), btnW, btnH);
				align(btn);
			}
		}

		//英雄名与描述紧跟头像行下方（信息按钮贴在名字右侧、整体居中）
		float nameTop = rowsY + avatarsH + 4;
		float nameRowW = heroName.width() + 2 + infoButton.width();
		heroName.setPos(left + (w - nameRowW)/2f, nameTop);
		align(heroName);

		heroDesc.setPos(left + Math.max(0, (w - heroDesc.width())/2f), heroName.bottom() + 3);
		align(heroDesc);

		infoButton.setPos(heroName.right() + 2, heroName.top() + (heroName.height() - infoButton.height())/2f);
		align(infoButton);

		layoutW = Camera.main.width;
		layoutH = Camera.main.height;
	}

	private void chooseSkinAndStart() {
		final HeroClass cl = GamesInProgress.selectedClass;
		if (!cl.supportsSkins()) {
			GamesInProgress.selectedSkin = 0;
			GamesInProgress.selectedStyle = CombatStyle.BALANCED;
			startSelectedGame();
			return;
		}

		String[] options = new String[9];
		for (int i = 0; i < 8; i++) {
			options[i] = Messages.get(HeroSelectScene.class, "skin_" + i);
		}
		options[8] = Messages.get(HeroSelectScene.class, "skin_back");

		ShatteredPixelDungeon.scene().addToFront(new WndOptions(
				HeroSprite.skinPreview(cl, GamesInProgress.selectedSkin),
				Messages.get(HeroSelectScene.class, "skin_title"),
				Messages.get(HeroSelectScene.class, "skin_desc"), options) {
			@Override
			protected void onSelect(int index) {
				if (index == 8) return;
				GamesInProgress.selectedSkin = index;
				chooseStyleAndStart();
			}

			@Override
			protected boolean hasIcon(int index) {
				return index < 8;
			}

			@Override
			protected Image getIcon(int index) {
				return HeroSprite.skinPreview(cl, index);
			}
		});
	}

	private void chooseStyleAndStart() {
		final CombatStyle[] styles = CombatStyle.values();
		String[] options = new String[styles.length + 1];
		for (int i = 0; i < styles.length; i++) {
			options[i] = styles[i].title() + " - " + styles[i].summary();
		}
		options[styles.length] = Messages.get(HeroSelectScene.class, "style_back");
		ShatteredPixelDungeon.scene().addToFront(new WndOptions(
				Messages.get(HeroSelectScene.class, "style_title"),
				Messages.get(HeroSelectScene.class, "style_desc"), options) {
			@Override
			protected void onSelect(int index) {
				if (index == styles.length) {
					chooseSkinAndStart();
					return;
				}
				GamesInProgress.selectedStyle = styles[index];
				startSelectedGame();
			}

			@Override
			public void onBackPressed() {
				hide();
				chooseSkinAndStart();
			}
		});
	}

	private void startSelectedGame() {
		Dungeon.hero = null;
		Dungeon.daily = Dungeon.dailyReplay = false;
		Dungeon.initSeed();
		ActionIndicator.clearAction();
		InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
		if (SPDSettings.intro()) {
			SPDSettings.intro(false);
			Game.switchScene(IntroScene.class);
		} else {
			Game.switchScene(InterlevelScene.class);
		}
	}

	@Override
	public void update() {
		super.update();
		if (SPDSettings.intro() && Rankings.INSTANCE.totalNumber > 0){
			SPDSettings.intro(false);
		}
		btnExit.visible = btnExit.active = !SPDSettings.intro();
		//SPSEXPD: 屏幕尺寸变化（窗口缩放/旋转）时重排（描述预留高度按新宽度重测）
		if (Camera.main.width != layoutW || Camera.main.height != layoutH){
			descReserveH = -1;
			layoutScene();
		}
	}

	@Override
	protected void onBackPressed() {
		if (btnExit.active){
			ShatteredPixelDungeon.switchScene(TitleScene.class);
		} else {
			super.onBackPressed();
		}
	}

	//SPSEXPD: 选角头像按钮——不画按钮边框（splashes/avatars.png 每格自带边框）
	private class HeroBtn extends IconButton {

		private HeroClass cl;

		HeroBtn ( HeroClass cl ){
			super();

			this.cl = cl;

			//SPSEXPD: 头像取自 splashes/avatars.png（格序同可玩职业列表）；无头像的职业回退到原站立帧
			Image avatar = new Image(Assets.Splashes.AVATARS);
			int idx = cl.avatarIndex();
			if (idx >= 0){
				avatar.frame(idx * AVATAR_W, 0, AVATAR_W, AVATAR_H);
			} else {
				avatar.texture(cl.spritesheet());
				avatar.frame(0, 90, 12, 15);
			}
			icon(avatar);
		}

		void setAvatarScale(float scale){
			icon.scale.set(scale);
			layout();
		}

		@Override
		public void update() {
			super.update();
			if (cl != GamesInProgress.selectedClass){
				if (!cl.isUnlocked()){
					icon.brightness(0.1f);
				} else {
					icon.brightness(0.6f);
				}
			} else {
				icon.brightness(1f);
			}
		}

		@Override
		protected void onClick() {
			super.onClick();

			if( !cl.isUnlocked() ){
				ShatteredPixelDungeon.scene().addToFront( new WndMessage(cl.unlockMsg()));
			} else if (GamesInProgress.selectedClass == cl) {
				Window w = new WndHeroInfo(cl);
				if (landscape()){
					w.offset(Camera.main.width/6, 0);
				}
				ShatteredPixelDungeon.scene().addToFront(w);
			} else {
				setSelectedHero(cl);
			}
		}
	}

	//SPSEXPD: 底部选项栏——无边框图标按钮横排（自定义种子 / 每日挑战 / 挑战 / 随机）
	private class GameOptions extends Component {

		private static final int ICON_SIZE = 20;
		private static final int ICON_GAP = 2;

		private ArrayList<IconButton> buttons;
		protected IconButton challengeButton;

		@Override
		protected void createChildren() {

			buttons = new ArrayList<>();
			IconButton seedButton = new IconButton(Icons.get(Icons.SEED)){
				@Override
				protected String hoverText() {
					return Messages.get(HeroSelectScene.class, "custom_seed");
				}

				@Override
				protected void onClick() {
					if (!Badges.isUnlocked(Badges.Badge.VICTORY) && !DeviceCompat.isDebug()){
						ShatteredPixelDungeon.scene().addToFront( new WndTitledMessage(
								Icons.get(Icons.SEED),
								Messages.get(HeroSelectScene.class, "custom_seed"),
								Messages.get(HeroSelectScene.class, "custom_seed_nowin"))
						);
						return;
					}

					String existingSeedtext = SPDSettings.customSeed();
					ShatteredPixelDungeon.scene().addToFront( new WndTextInput(Messages.get(HeroSelectScene.class, "custom_seed_title"),
							Messages.get(HeroSelectScene.class, "custom_seed_desc"),
							existingSeedtext,
							20,
							false,
							Messages.get(HeroSelectScene.class, "custom_seed_set"),
							Messages.get(HeroSelectScene.class, "custom_seed_clear")){
						@Override
						public void onSelect(boolean positive, String text) {
							text = DungeonSeed.formatText(text);
							long seed = DungeonSeed.convertFromText(text);

							if (positive && seed != -1){

								for (GamesInProgress.Info info : GamesInProgress.checkAll()){
									if (info.customSeed.isEmpty() && info.seed == seed){
										SPDSettings.customSeed("");
										icon.resetColor();
										ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(HeroSelectScene.class, "custom_seed_duplicate")));
										return;
									}
								}

								SPDSettings.customSeed(text);
								icon.hardlight(1f, 1.5f, 0.67f);
							} else {
								SPDSettings.customSeed("");
								icon.resetColor();
							}
						}
					});
				}
			};
			seedButton.setSize(ICON_SIZE, ICON_SIZE + 1);
			if (!SPDSettings.customSeed().isEmpty()) seedButton.icon().hardlight(1f, 1.5f, 0.67f);
			buttons.add(seedButton);
			add(seedButton);

			IconButton dailyButton = new IconButton(Icons.get(Icons.CALENDAR)){

				private static final long HOUR = 60 * 60 * 1000;
				private static final long DAY = 24 * HOUR;

				@Override
				protected String hoverText() {
					return Messages.get(HeroSelectScene.class, "daily");
				}

				@Override
				protected void onClick() {
					super.onClick();

					if (!Badges.isUnlocked(Badges.Badge.VICTORY) && !DeviceCompat.isDebug()){
						ShatteredPixelDungeon.scene().addToFront( new WndTitledMessage(
								Icons.get(Icons.CALENDAR),
								Messages.get(HeroSelectScene.class, "daily"),
								Messages.get(HeroSelectScene.class, "daily_nowin"))
						);
						return;
					}

					long diff = (SPDSettings.lastDaily() + DAY) - Game.realTime;
					if (diff > 24*HOUR){
						ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(HeroSelectScene.class, "daily_unavailable_long", (diff / DAY)+1)));
						return;
					}

					for (GamesInProgress.Info game : GamesInProgress.checkAll()){
						if (game.daily){
							ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(HeroSelectScene.class, "daily_existing")));
							return;
						}
					}

					Image icon = Icons.get(Icons.CALENDAR);
					if (diff <= 0)  icon.hardlight(0.5f, 1f, 2f);
					else            icon.hardlight(1f, 0.5f, 2f);
					ShatteredPixelDungeon.scene().addToFront(new WndOptions(
							icon,
							Messages.get(HeroSelectScene.class, "daily"),
							diff > 0 ?
								Messages.get(HeroSelectScene.class, "daily_repeat") :
								Messages.get(HeroSelectScene.class, "daily_desc"),
							Messages.get(HeroSelectScene.class, "daily_yes"),
							Messages.get(HeroSelectScene.class, "daily_no")){
						@Override
						protected void onSelect(int index) {
							if (index == 0){
								if (diff <= 0) {
									long time = Game.realTime - (Game.realTime % DAY);

									//earliest possible daily for v4.0 is Apr 01 2026
									//which is 20,544 days after Jan 1 1970
									time = Math.max(time, 20_544 * DAY);

									SPDSettings.lastDaily(time);
									Dungeon.dailyReplay = false;
								} else {
									Dungeon.dailyReplay = true;
								}

								Dungeon.hero = null;
								Dungeon.daily = true;
								Dungeon.initSeed();
								ActionIndicator.clearAction();
								InterlevelScene.mode = InterlevelScene.Mode.DESCEND;

								Game.switchScene( InterlevelScene.class );
							}
						}
					});
				}

			};
			dailyButton.setSize(ICON_SIZE, ICON_SIZE + 1);
			add(dailyButton);
			buttons.add(dailyButton);

			challengeButton = new IconButton(Icons.get(SPDSettings.challenges() > 0 ? Icons.CHALLENGE_COLOR : Icons.CHALLENGE_GREY)){
				@Override
				protected String hoverText() {
					return Messages.get(WndChallenges.class, "title");
				}

				@Override
				protected void onClick() {
					ShatteredPixelDungeon.scene().addToFront(new WndChallenges(SPDSettings.challenges(), true) {
						public void onBackPressed() {
							super.onBackPressed();
							icon(Icons.get(SPDSettings.challenges() > 0 ? Icons.CHALLENGE_COLOR : Icons.CHALLENGE_GREY));
						}
					} );
				}
			};
			challengeButton.setSize(ICON_SIZE, ICON_SIZE + 1);
			add(challengeButton);
			buttons.add(challengeButton);

			int unlockedCount = 0;
			for (HeroClass cls : HeroClass.playableClasses()){
				if (cls.isUnlocked()) unlockedCount++;
			}

			if (unlockedCount >= 2) {
				IconButton randomButton = new IconButton(Icons.SHUFFLE.get()) {
					@Override
					protected String hoverText() {
						return Messages.get(HeroSelectScene.class, "randomize");
					}

					@Override
					protected void onClick() {
						ShatteredPixelDungeon.scene().addToFront(new WndRandomize());
					}
				};
				randomButton.setSize(ICON_SIZE, ICON_SIZE + 1);
				buttons.add(randomButton);
				add(randomButton);
			}
		}

		private class WndRandomize extends Window {

			CheckBox chkHero;
			CheckBox chkChals;
			OptionSlider optChals;

			public WndRandomize(){
				super();

				chkHero = new CheckBox(Messages.get(HeroSelectScene.class, "randomize_hero")){
					@Override
					public void checked(boolean value) {
						super.checked(value);
						heroWasRandomized = value;
					}
				};
				chkHero.setRect(0, 0, 120, 16);
				chkHero.checked(heroWasRandomized);
				add(chkHero);

				chkChals = new CheckBox(Messages.get(HeroSelectScene.class, "randomize_chals")){
					@Override
					public void checked(boolean value) {
						super.checked(value);
						optChals.enable(value);
						chalWasRandomized = value;
					}
				};
				chkChals.setRect(0, 20, 120, 16);
				add(chkChals);

				int max = Challenges.MAX_CHALS;
				optChals = new OptionSlider(Messages.get(HeroSelectScene.class, "randomize_chals_title"), "0", Integer.toString(max), 0, max) {
					@Override
					protected void onChange() {
						//do nothing immediately
					}
				};
				optChals.enable(false);
				optChals.setSelectedValue(Challenges.activeChallenges(SPDSettings.challenges()));
				optChals.setRect(0, 38, 120, 22);
				add(optChals);

				chkChals.checked(chalWasRandomized);

				RedButton btnCancel = new RedButton(Messages.get(HeroSelectScene.class, "randomize_cancel")){
					@Override
					protected void onClick() {
						super.onClick();
						hide();
					}
				};
				btnCancel.setRect(61, 64, 60, 16);
				add(btnCancel);

				RedButton btnConfirm = new RedButton(Messages.get(HeroSelectScene.class, "randomize_confirm")){
					@Override
					protected void onClick() {
						super.onClick();
						hide();

						if (chkChals.checked()){
							int chals = optChals.getSelectedValue();
							ArrayList<Integer> chalMasks = new ArrayList<>();
							for (int i = 0; i < Challenges.MAX_CHALS; i++){
								chalMasks.add((int)Math.pow(2, i));
							}
							Random.shuffle(chalMasks);
							int mask = 0;
							for (int i = 0; i < chals; i++){
								mask += chalMasks.remove(0);
							}
							SPDSettings.challenges(mask);
							challengeButton.icon(Icons.get(SPDSettings.challenges() > 0 ? Icons.CHALLENGE_COLOR : Icons.CHALLENGE_GREY));
							ShatteredPixelDungeon.scene().addToFront(new WndChallenges(mask, false));
						}

						if (chkHero.checked()){
							HeroClass randomCls;
							do {
								randomCls = Random.oneOf(HeroClass.playableClasses());
							} while (!randomCls.isUnlocked());
							setSelectedHero(randomCls);
							GamesInProgress.randomizedClass = true;
						} else {
							setSelectedHero(GamesInProgress.selectedClass);
						}
					}
				};
				btnConfirm.setRect(0, 64, 60, 16);
				add(btnConfirm);

				resize(120, (int)btnConfirm.bottom());

			}

		}

		@Override
		protected void layout() {
			super.layout();

			float left = x;
			for (IconButton btn : buttons){
				btn.setRect(left, y, ICON_SIZE, ICON_SIZE + 1);
				PixelScene.align(btn);
				left += ICON_SIZE + ICON_GAP;
			}

			this.width = Math.max(0, buttons.size() * (ICON_SIZE + ICON_GAP) - ICON_GAP);
			this.height = ICON_SIZE + 1;
		}
	}

}
