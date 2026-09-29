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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Callback;
import com.watabou.utils.RectF;

public class SupporterScene extends PixelScene {

	private static final int BTN_HEIGHT = 22;
	private static final int GAP = 2;

	//SPS: 三来源标签页（0=PD 破碎 1=SPS 特别惊喜 2=SPSEX 移植版）
	public static int sourceSelected = 0;

	@Override
	public void create() {
		super.create();

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;
		RectF insets = getCommonInsets();

		int elementWidth = PixelScene.landscape() ? 202 : 120;

		TitleBackground BG = new TitleBackground(w, h);
		add(BG);

		w -= insets.right + insets.left;
		h -= insets.top + insets.bottom;

		ExitButton btnExit = new ExitButton();
		btnExit.setPos(insets.left + w - btnExit.width(), insets.top);
		add(btnExit);

		IconTitle title = new IconTitle(Icons.GOLD.get(), Messages.get(this, "title"));
		title.setSize(200, 0);
		title.setPos(
				insets.left + (w - title.reqWidth()) / 2f,
				insets.top + (20 - title.height()) / 2f
		);
		align(title);
		add(title);

		//SPS: 三来源标签页按钮
		float tabW = (elementWidth - 4) / 3f;
		float tabLeft = insets.left + (w - elementWidth) / 2f;
		float tabTop = insets.top + 21;

		StyledButton tabPD = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "tab_pd")){
			@Override
			protected void onClick() { sourceSelected = 0; ShatteredPixelDungeon.switchNoFade(SupporterScene.class); }
		};
		tabPD.setRect(tabLeft, tabTop, tabW, 14);
		align(tabPD);
		add(tabPD);

		StyledButton tabSPS = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "tab_sps")){
			@Override
			protected void onClick() { sourceSelected = 1; ShatteredPixelDungeon.switchNoFade(SupporterScene.class); }
		};
		tabSPS.setRect(tabPD.right() + 2, tabTop, tabW, 14);
		align(tabSPS);
		add(tabSPS);

		StyledButton tabSPSEX = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "tab_spsex")){
			@Override
			protected void onClick() { sourceSelected = 2; ShatteredPixelDungeon.switchNoFade(SupporterScene.class); }
		};
		tabSPSEX.setRect(tabSPS.right() + 2, tabTop, tabW, 14);
		align(tabSPSEX);
		add(tabSPSEX);

		//SPS: 内容按标签页切换
		String message;
		String linkLabel = null;
		String linkUrl = null;
		switch (sourceSelected) {
			case 1:
				message = Messages.get(this, "sps_msg");
				break;
			case 2:
				message = Messages.get(this, "spsex_msg");
				linkLabel = Messages.get(this, "spsex_link");
				linkUrl = Messages.get(this, "spsex_url");
				break;
			default:
				message = Messages.get(this, "intro");
				message += "\n\n" + Messages.get(this, "patreon_msg");
				if (Messages.lang() != Languages.ENGLISH) {
					message += "\n" + Messages.get(this, "patreon_english");
				}
				message += "\n\n- Evan";
				linkLabel = Messages.get(this, "supporter_link");
				linkUrl = "https://www.patreon.com/ShatteredPixel?utm_source=shatteredpd&utm_medium=supporter_page&utm_campaign=ingame_link";
		}

		SupporterMessage msg = new SupporterMessage(message);
		msg.setSize(elementWidth, 0);
		add(msg);

		StyledButton link = null;
		if (linkLabel != null) {
			final String url = linkUrl;
			link = new StyledButton(Chrome.Type.GREY_BUTTON_TR, linkLabel){
				@Override
				protected void onClick() {
					super.onClick();
					ShatteredPixelDungeon.platform.openURI(url);
				}
			};
			link.icon(Icons.get(Icons.GOLD));
			link.textColor(Window.TITLE_COLOR);
			link.setSize(elementWidth, BTN_HEIGHT);
			add(link);
		}

		float elementHeight = msg.height() + (link != null ? BTN_HEIGHT + GAP : 0);

		float top = insets.top + 40 + (h - 40 - elementHeight)/2f;
		float left = insets.left + (w-elementWidth)/2f;

		msg.setPos(left, top);
		align(msg);

		if (link != null) {
			link.setPos(left, msg.bottom()+GAP);
			align(link);
		}

	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade( TitleScene.class );
	}

	private static class SupporterMessage extends Component {

		NinePatch bg;
		RenderedTextBlock text;
		Image icon;
		String message;

		public SupporterMessage( String message ){
			this.message = message;
		}

		@Override
		protected void createChildren() {
			bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
			add(bg);

			text = PixelScene.renderTextBlock(message, 6);
			add(text);

			icon = Icons.get(Icons.SHPX);
			add(icon);

		}

		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			text.maxWidth((int)width - bg.marginHor());
			text.setPos(x + bg.marginLeft(), y + bg.marginTop() + 1);

			icon.y = text.bottom() - icon.height() + 4;
			icon.x = x + 25;

			height = (text.bottom() + 3) - y;

			height += bg.marginBottom();

			bg.size(width, height);

		}

	}

}
