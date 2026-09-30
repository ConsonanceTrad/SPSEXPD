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
import pd.Chrome;
import pd.ShatteredPixelDungeon;
import pd.messages.Languages;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.ui.ExitButton;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.StyledButton;
import pd.ui.TitleBackground;
import pd.ui.changelist.ChangeInfo;
import pd.ui.changelist.Pixel_Dungeon_Changes;
import pd.ui.changelist.WndChanges;
import pd.ui.changelist.WndChangesTabbed;
import pd.ui.changelist.v0_1_X_Changes;
import pd.ui.changelist.v0_2_X_Changes;
import pd.ui.changelist.v0_3_X_Changes;
import pd.ui.changelist.v0_4_X_Changes;
import pd.ui.changelist.v0_5_X_Changes;
import pd.ui.changelist.v0_6_X_Changes;
import pd.ui.changelist.v0_7_X_Changes;
import pd.ui.changelist.v0_8_X_Changes;
import pd.ui.changelist.v0_9_X_Changes;
import pd.ui.changelist.v1_X_Changes;
import pd.ui.changelist.v2_X_Changes;
import pd.ui.changelist.v3_X_Changes;
import pd.ui.changelist.v4_X_Changes;
import pd.windows.IconTitle;
import render.noosa.Camera;
import render.noosa.Image;
import render.noosa.NinePatch;
import render.noosa.Scene;
import render.noosa.audio.Music;
import render.noosa.ui.Component;
import render.utils.geom.RectF;

import java.util.ArrayList;

public class ChangesScene extends PixelScene {
	
	public static int changesSelected = 0;
	//SPS: 三来源标签页（0=PD 破碎 1=SPS 特别惊喜 2=SPSEX 移植版）；默认展示 SPSEX
	public static int sourceSelected = 2;

	//SPS: 侧栏来源标签的图标尺寸与竖向步进
	private static final int TAB_ICON = 16;
	private static final int TAB_STEP = 18;
	
	private NinePatch rightPanel;
	private ScrollPane rightScroll;
	private IconTitle changeTitle;
	private RenderedTextBlock changeBody;
	
	@Override
	public void create() {
		super.create();

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		int w = Camera.main.width;
		int h = Camera.main.height;

		RectF insets = getCommonInsets();

		TitleBackground BG = new TitleBackground(w, h);
		//background added later

		w -= insets.left + insets.right;
		h -= insets.top + insets.bottom;

		//SPS: 内容区让出左侧来源图标栏的宽度（标题与内容面板同以此居中）
		float contentLeft = insets.left + TAB_STEP;
		float contentW = w - TAB_STEP;

		IconTitle title = new IconTitle(Icons.CHANGES.get(), Messages.get(this, "title"));
		title.setSize(200, 0);
		title.setPos(
				contentLeft + (contentW - title.reqWidth()) / 2f,
				insets.top + (20 - title.height()) / 2f
		);
		align(title);
		add(title);

		//SPS: 三来源标签页按钮（侧栏竖排图标，避免被内容面板覆盖）
		IconButton tabPD = new IconButton(Icons.get(Icons.SHPX)){
			@Override
			protected void onClick() { sourceSelected = 0; ShatteredPixelDungeon.switchNoFade(ChangesScene.class); }
			@Override
			protected String hoverText() { return Messages.get(ChangesScene.class, "tab_pd"); }
		};
		IconButton tabSPS = new IconButton(Icons.get(Icons.LUCKY_BADGE)){
			@Override
			protected void onClick() { sourceSelected = 1; ShatteredPixelDungeon.switchNoFade(ChangesScene.class); }
			@Override
			protected String hoverText() { return Messages.get(ChangesScene.class, "tab_sps"); }
		};
		IconButton tabSPSEX = new IconButton(Icons.get(Icons.DEMON_BLADE)){
			@Override
			protected void onClick() { sourceSelected = 2; ShatteredPixelDungeon.switchNoFade(ChangesScene.class); }
			@Override
			protected String hoverText() { return Messages.get(ChangesScene.class, "tab_spsex"); }
		};

		IconButton[] sourceTabs = { tabPD, tabSPS, tabSPSEX };
		for (int i = 0; i < sourceTabs.length; i++) {
			sourceTabs[i].setRect(insets.left + 1, insets.top + 22 + i * TAB_STEP, TAB_ICON, TAB_ICON);
			//SPS: 选中项不透明、其余半透明表示未选中（仍可点击）
			sourceTabs[i].icon().alpha(i == sourceSelected ? 1f : 0.4f);
			align(sourceTabs[i]);
			add(sourceTabs[i]);
		}

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( insets.left + w - btnExit.width(), insets.top );
		add( btnExit );

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		int pw = 135 + panel.marginLeft() + panel.marginRight() - 2;
		int ph = h - 36;

		if (h >= PixelScene.MIN_HEIGHT_FULL && w >= 300) {
			panel.size( pw, ph );
			panel.x = contentLeft + (contentW - pw) / 2f - pw/2 - 1;
			panel.y = insets.top + 20;

			rightPanel = Chrome.get(Chrome.Type.TOAST);
			rightPanel.size( pw, ph );
			rightPanel.x = contentLeft + (contentW - pw) / 2f + pw/2 + 1;
			rightPanel.y = insets.top + 20;
			add(rightPanel);

			rightScroll = new ScrollPane(new Component());
			add(rightScroll);
			rightScroll.setRect(
					rightPanel.x + rightPanel.marginLeft(),
					rightPanel.y + rightPanel.marginTop()-1,
					rightPanel.innerWidth() + 2,
					rightPanel.innerHeight() + 2);
			rightScroll.scrollTo(0, 0);

			changeTitle = new IconTitle(Icons.get(Icons.CHANGES), Messages.get(this, "right_title"));
			changeTitle.setPos(0, 1);
			changeTitle.setSize(pw, 20);
			rightScroll.content().add(changeTitle);

			String body = Messages.get(this, "right_body");

			changeBody = PixelScene.renderTextBlock(body, 6);
			changeBody.maxWidth(pw - panel.marginHor());
			changeBody.setPos(0, changeTitle.bottom()+2);
			rightScroll.content().add(changeBody);

		} else {
			panel.size( pw, ph );
			panel.x = contentLeft + (contentW - pw) / 2f;
			panel.y = insets.top + 20;
		}
		align( panel );
		add( panel );
		
		final ArrayList<ChangeInfo> changeInfos = new ArrayList<>();

		if (sourceSelected == 0 && Messages.lang() != Languages.ENGLISH){
			ChangeInfo langWarn = new ChangeInfo("", true, Messages.get(this, "lang_warn"));
			langWarn.hardlight(CharSprite.WARNING);
			changeInfos.add(langWarn);
		}

		//SPS: 三来源标签页内容（PD=破碎官方 changelist；SPS/SPSEX 为移植项目自述进度）
		if (sourceSelected == 1) {
			changeInfos.add(new ChangeInfo(Messages.get(this, "sps_title"), true, Messages.get(this, "sps_body")));
		} else if (sourceSelected == 2) {
			changeInfos.add(new ChangeInfo(Messages.get(this, "spsex_title"), true, Messages.get(this, "spsex_body")));
			changeInfos.add(new ChangeInfo(Messages.get(this, "spsex_title2"), true, Messages.get(this, "spsex_body2")));
		} else
		switch (changesSelected){
			case 0: default:
				v4_X_Changes.addAllChanges(changeInfos);
				break;
			case 1:
				v3_X_Changes.addAllChanges(changeInfos);
				break;
			case 2:
				v2_X_Changes.addAllChanges(changeInfos);
				break;
			case 3:
				v1_X_Changes.addAllChanges(changeInfos);
				break;
			case 4:
				v0_9_X_Changes.addAllChanges(changeInfos);
				break;
			case 5:
				v0_8_X_Changes.addAllChanges(changeInfos);
				break;
			case 6:
				v0_7_X_Changes.addAllChanges(changeInfos);
				break;
			case 7:
				v0_6_X_Changes.addAllChanges(changeInfos);
				break;
			case 8:
				v0_5_X_Changes.addAllChanges(changeInfos);
				v0_4_X_Changes.addAllChanges(changeInfos);
				v0_3_X_Changes.addAllChanges(changeInfos);
				v0_2_X_Changes.addAllChanges(changeInfos);
				v0_1_X_Changes.addAllChanges(changeInfos);
				Pixel_Dungeon_Changes.addAllChanges(changeInfos);
				break;
		}

		ScrollPane list = new ScrollPane( new Component() ){

			@Override
			public void onClick(float x, float y) {
				for (ChangeInfo info : changeInfos){
					if (info.onClick( x, y )){
						return;
					}
				}
			}

		};
		add( list );

		Component content = list.content();
		content.clear();

		float posY = 0;
		float nextPosY = 0;
		boolean second = false;
		for (ChangeInfo info : changeInfos){
			if (info.major) {
				posY = nextPosY;
				second = false;
				info.setRect(0, posY, panel.innerWidth(), 0);
				content.add(info);
				posY = nextPosY = info.bottom();
			} else {
				if (!second){
					second = true;
					info.setRect(0, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = info.bottom();
				} else {
					second = false;
					info.setRect(panel.innerWidth()/2f, posY, panel.innerWidth()/2f, 0);
					content.add(info);
					nextPosY = Math.max(info.bottom(), nextPosY);
					posY = nextPosY;
				}
			}
		}

		content.setSize( panel.innerWidth(), (int)Math.ceil(posY) );

		list.setRect(
				panel.x + panel.marginLeft(),
				panel.y + panel.marginTop() - 1,
				panel.innerWidth() + 2,
				panel.innerHeight() + 2);
		list.scrollTo(0, 0);

		float left = list.left()-4f;

		if (sourceSelected == 0) {
		if (changesSelected <= 3){

			left = setupChangesSelectionButton(0, "v4.X", left, list.bottom(), 24);
			left = setupChangesSelectionButton(1, "v3.X", left, list.bottom(), 24);
			left = setupChangesSelectionButton(2, "v2.X", left, list.bottom(), 24);
			left = setupChangesSelectionButton(3, "v1.X", left, list.bottom(), 24);
			left = setupChangesSelectionButton(4, "PreRelease->", left, list.bottom(), 53);

		} else {

			left = setupChangesSelectionButton(3, "<-Release", left, list.bottom(), 40);
			left = setupChangesSelectionButton(4, "v0.9", left, list.bottom(), 22);
			left = setupChangesSelectionButton(5, "v0.8", left, list.bottom(), 22);
			left = setupChangesSelectionButton(6, "v0.7", left, list.bottom(), 22);
			left = setupChangesSelectionButton(7, "v0.6", left, list.bottom(), 22);
			left = setupChangesSelectionButton(8, "v0.5-", left, list.bottom(), 23);

		}
		}   //SPS: 版本切换按钮仅 PD 破碎标签页显示

		addToBack( BG );

		fadeIn();
	}

	private float setupChangesSelectionButton(int idx, String text, float left, float top, float width){
		StyledButton button = new StyledButton(Chrome.Type.GREY_BUTTON_TR, text, 8){
			@Override
			protected void onClick() {
				super.onClick();
				if (changesSelected != idx) {
					changesSelected = idx;
					ShatteredPixelDungeon.seamlessResetScene();
				}
			}
		};
		if (changesSelected != idx) button.textColor( 0xBBBBBB );
		button.setRect(left, top, width, changesSelected == idx ? 19 : 15);
		addToBack(button);
		return button.right()-2;
	}

	private void updateChangesText(Image icon, String title, String... messages){
		if (changeTitle != null){
			changeTitle.icon(icon);
			changeTitle.label(title);
			changeTitle.setPos(changeTitle.left(), changeTitle.top());

			String message = "";
			for (int i = 0; i < messages.length; i++){
				message += messages[i];
				if (i != messages.length-1){
					message += "\n\n";
				}
			}
			changeBody.text(message);
			rightScroll.content().setSize(rightScroll.width(), changeBody.bottom()+2);
			rightScroll.setSize(rightScroll.width(), rightScroll.height());
			rightScroll.scrollTo(0, 0);

		} else {
			if (messages.length == 1) {
				addToFront(new WndChanges(icon, title, messages[0]));
			} else {
				addToFront(new WndChangesTabbed(icon, title, messages));
			}
		}
	}

	public static void showChangeInfo(Image icon, String title, String... messages){
		Scene s = ShatteredPixelDungeon.scene();
		if (s instanceof ChangesScene){
			((ChangesScene) s).updateChangesText(icon, title, messages);
			return;
		}
		if (messages.length == 1) {
			s.addToFront(new WndChanges(icon, title, messages[0]));
		} else {
			s.addToFront(new WndChangesTabbed(icon, title, messages));
		}
	}
	
	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade(TitleScene.class);
	}

}
