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

import pd.Challenges;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.ui.CheckBox;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

import java.util.ArrayList;

public class WndChallenges extends Window {

	private static final int WIDTH		= 120;
	private static final int TTL_HEIGHT = 16;
	private static final int BTN_HEIGHT = 16;
	private static final int GAP        = 1;

	//SPS: 挑战已增至 18 项，一屏放不下——按页显示，每页 6 项
	private static final int PAGE_SIZE	= 6;

	private boolean editable;
	private ArrayList<CheckBox> boxes;

	//SPS: 翻页会重建窗口，勾选值必须跨页携带
	private int checkedValue;
	private int page;

	public WndChallenges( int checked, boolean editable ) {
		this( checked, editable, 0 );
	}

	private WndChallenges( int checked, boolean editable, int page ) {

		super();

		this.editable = editable;
		this.checkedValue = checked;
		this.page = page;

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 12 );
		title.hardlight( TITLE_COLOR );
		title.setPos(
				(WIDTH - title.width()) / 2,
				(TTL_HEIGHT - title.height()) / 2
		);
		PixelScene.align(title);
		add( title );

		boxes = new ArrayList<>();

		int pages = (Challenges.NAME_IDS.length + PAGE_SIZE - 1) / PAGE_SIZE;
		int from = page * PAGE_SIZE;
		int to = Math.min( from + PAGE_SIZE, Challenges.NAME_IDS.length );

		float pos = TTL_HEIGHT;
		for (int i=from; i < to; i++) {

			final String challenge = Challenges.NAME_IDS[i];

			CheckBox cb = new CheckBox( Messages.titleCase(Messages.get(Challenges.class, challenge)) );
			cb.checked( (checkedValue & Challenges.MASKS[i]) != 0 );
			cb.active = editable;

			if (i > from) {
				pos += GAP;
			}
			cb.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );

			add( cb );
			boxes.add( cb );

			IconButton info = new IconButton(Icons.get(Icons.INFO)){
				@Override
				protected void onClick() {
					super.onClick();
					ShatteredPixelDungeon.scene().add(
							new WndMessage(Messages.get(Challenges.class, challenge+"_desc"))
					);
				}
			};
			info.setRect(cb.right(), pos, 16, BTN_HEIGHT);
			add(info);

			pos = cb.bottom();
		}

		//SPS: 翻页栏——切页前先提交本页勾选，再把累积值传给新窗口
		if (pages > 1) {
			RedButton prev = new RedButton( "<" ) {
				@Override
				protected void onClick() {
					commitPage();
					hide();
					GameScene.show( new WndChallenges( checkedValue, editable, page - 1 ) );
				}
			};
			prev.enable( page > 0 );
			prev.setRect( 0, pos + GAP + 1, 20, 15 );
			add( prev );

			RedButton counter = new RedButton( (page + 1) + "/" + pages ) {
				@Override
				protected void onClick() {}
			};
			counter.textColor( TITLE_COLOR );
			counter.enable( false );
			counter.setRect( prev.right() + 2, pos + GAP + 1, WIDTH - 44, 15 );
			add( counter );

			RedButton next = new RedButton( ">" ) {
				@Override
				protected void onClick() {
					commitPage();
					hide();
					GameScene.show( new WndChallenges( checkedValue, editable, page + 1 ) );
				}
			};
			next.enable( page < pages - 1 );
			next.setRect( counter.right() + 2, pos + GAP + 1, 20, 15 );
			add( next );

			pos = next.bottom();
		}

		resize( WIDTH, (int)pos );
	}

	//SPS: 把当前页的勾选合并进 checkedValue（翻页与关闭时都要提交）
	private void commitPage() {
		int from = page * PAGE_SIZE;
		for (int i=0; i < boxes.size(); i++) {
			int mask = Challenges.MASKS[from + i];
			if (boxes.get( i ).checked()) {
				checkedValue |= mask;
			} else {
				checkedValue &= ~mask;
			}
		}
	}

	@Override
	public void onBackPressed() {

		if (editable) {
			commitPage();
			SPDSettings.challenges( checkedValue );
		}

		super.onBackPressed();
	}
}