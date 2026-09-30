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
 */

package pd.windows;

import pd.Challenges;
import pd.SPDSettings;
import pd.ShatteredPixelDungeon;
import pd.messages.Messages;
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

	//SPS: 全部项一次建成，翻页只切换可见性。不重建窗口——在暂停菜单里 hide() 掉
	//当前窗口再 show() 新建一个，会让场景的窗口/输入状态错乱（实测点分页就卡住）。
	private ArrayList<CheckBox> boxes;
	private ArrayList<IconButton> infos;

	private RedButton prev;
	private RedButton next;
	private RedButton counter;

	private int page;
	private int pages;

	public WndChallenges( int checked, boolean editable ) {

		super();

		this.editable = editable;

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 12 );
		title.hardlight( TITLE_COLOR );
		title.setPos(
				(WIDTH - title.width()) / 2,
				(TTL_HEIGHT - title.height()) / 2
		);
		PixelScene.align(title);
		add( title );

		boxes = new ArrayList<>();
		infos = new ArrayList<>();

		pages = (Challenges.NAME_IDS.length + PAGE_SIZE - 1) / PAGE_SIZE;

		float pos = TTL_HEIGHT;
		for (int i=0; i < Challenges.NAME_IDS.length; i++) {

			final String challenge = Challenges.NAME_IDS[i];

			CheckBox cb = new CheckBox( Messages.titleCase(Messages.get(Challenges.class, challenge)) );
			cb.checked( (checked & Challenges.MASKS[i]) != 0 );
			cb.active = editable;
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
			infos.add( info );

			pos = cb.bottom() + GAP;
		}

		//SPS: 翻页栏——只切页、重排，不重建窗口
		if (pages > 1) {
			prev = new RedButton( "<" ) {
				@Override
				protected void onClick() {
					if (page > 0) {
						page--;
						applyPage();
					}
				}
			};
			prev.setRect( 0, pos + 1, 20, 15 );
			add( prev );

			counter = new RedButton( "" ) {
				@Override
				protected void onClick() {}
			};
			counter.textColor( TITLE_COLOR );
			counter.enable( false );
			counter.setRect( prev.right() + 2, pos + 1, WIDTH - 44, 15 );
			add( counter );

			next = new RedButton( ">" ) {
				@Override
				protected void onClick() {
					if (page < pages - 1) {
						page++;
						applyPage();
					}
				}
			};
			next.setRect( counter.right() + 2, pos + 1, 20, 15 );
			add( next );

			pos = next.bottom();
		}

		resize( WIDTH, (int)pos );

		applyPage();
	}

	//SPS: 按当前页重排——不可见的项只置 visible=false（不再接收点击），
	//可见项从标题下方紧凑排列，窗口高度随之为「一页 + 翻页栏」
	private void applyPage() {

		int from = page * PAGE_SIZE;
		int to = Math.min( from + PAGE_SIZE, boxes.size() );

		float pos = TTL_HEIGHT;
		for (int i=0; i < boxes.size(); i++) {

			boolean show = i >= from && i < to;

			CheckBox cb = boxes.get( i );
			cb.visible = show;
			cb.active = show && editable;

			IconButton info = infos.get( i );
			info.visible = show;

			if (show) {
				cb.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );
				info.setRect( cb.right(), pos, 16, BTN_HEIGHT );
				pos = cb.bottom() + GAP;
			}
		}

		if (pages > 1) {
			counter.text( (page + 1) + "/" + pages );
			prev.enable( page > 0 );
			next.enable( page < pages - 1 );

			prev.setRect( 0, pos + 1, 20, 15 );
			counter.setRect( prev.right() + 2, pos + 1, WIDTH - 44, 15 );
			next.setRect( counter.right() + 2, pos + 1, 20, 15 );

			pos = next.bottom();
		}

		resize( WIDTH, (int)pos );
	}

	@Override
	public void onBackPressed() {

		if (editable) {
			int value = 0;
			for (int i=0; i < boxes.size(); i++) {
				if (boxes.get( i ).checked()) {
					value |= Challenges.MASKS[i];
				}
			}
			SPDSettings.challenges( value );
		}

		super.onBackPressed();
	}
}
