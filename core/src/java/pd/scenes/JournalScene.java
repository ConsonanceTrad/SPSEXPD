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

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.Assets;
import pd.Badges;
import pd.Chrome;
import pd.Dungeon;
import pd.ShatteredPixelDungeon;
import pd.items.consum.potions.Potion;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.Scroll;
import pd.journal.Journal;
import pd.messages.Messages;
import pd.sprites.ItemSprite;
import pd.tiles.TerrainFeaturesTilemap;
import pd.ui.ExitButton;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.StyledButton;
import pd.ui.TitleBackground;
import pd.windows.IconTitle;
import pd.windows.WndJournal;
import render.noosa.Camera;
import render.noosa.NinePatch;
import render.noosa.audio.Music;
import render.utils.data.SparseArray;
import render.utils.geom.RectF;
import pd.messages.InlineText;

public class JournalScene extends PixelScene {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(JournalScene.class)
			.t("title", "日志");
	}




	public static final int WIDTH_P     = 126;
	public static final int WIDTH_L     = 216;

	private static int lastIDX = 0;

	@Override
	public void create() {

		super.create();

		Dungeon.hero = null;
		Badges.loadGlobal();
		Journal.loadGlobal();

		Potion.clearColors();
		Scroll.clearLabels();
		Ring.clearGems();

		//need to re-initialize the texture here, as it may be invalid
		new TerrainFeaturesTilemap(new SparseArray<>(), new SparseArray<>());

		Music.INSTANCE.playTracks(
				new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
				new float[]{1, 1},
				false);

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		RectF insets = getCommonInsets();

		TitleBackground BG = new TitleBackground(w, h);
		//BG added later

		w -= insets.left + insets.right;
		h -= insets.top + insets.bottom;

		float top = 20;

		IconTitle title = new IconTitle( Icons.JOURNAL.get(), Messages.get(this, "title") );
		title.setSize(200, 0);
		title.setPos(
				insets.left + (w - title.reqWidth()) / 2f,
				insets.top + (top - title.height()) / 2f
		);
		align(title);
		add(title);

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		int pw = (landscape() ? WIDTH_L : WIDTH_P) + panel.marginHor();
		int ph = h - 50 + panel.marginVer();

		panel.size(pw, ph);
		panel.x = insets.left + (w - pw) / 2f;
		panel.y = insets.top + top;
		add(panel);

		switch (lastIDX){
			case 0: default:
				WndJournal.BadgesTab badges = new WndJournal.BadgesTab();
				add(badges);
				badges.setRect(panel.x + panel.marginLeft(),
						panel.y + panel.marginTop(),
						panel.width() - panel.marginHor(),
						panel.height() - panel.marginVer());
				break;
			case 1:
				WndJournal.CatalogTab catalog = new WndJournal.CatalogTab();
				add(catalog);
				catalog.setRect(panel.x + panel.marginLeft(),
						panel.y + panel.marginTop(),
						panel.width() - panel.marginHor(),
						panel.height() - panel.marginVer());
				catalog.updateList();
				break;
			case 2:
				WndJournal.GuideTab guidebook = new WndJournal.GuideTab();
				add(guidebook);
				guidebook.setRect(panel.x + panel.marginLeft(),
						panel.y + panel.marginTop(),
						panel.width() - panel.marginHor(),
						panel.height() - panel.marginVer());
				guidebook.updateList();
				break;
			case 3:
				WndJournal.AlchemyTab alchemy = new WndJournal.AlchemyTab();
				add(alchemy);
				alchemy.setRect(panel.x + panel.marginLeft(),
						panel.y + panel.marginTop(),
						panel.width() - panel.marginHor(),
						panel.height() - panel.marginVer());
				break;
		}

		StyledButton btnBadges =  new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){
			@Override
			protected void onClick() {
				if (lastIDX != 0) {
					lastIDX = 0;
				}
				ShatteredPixelDungeon.seamlessResetScene();
				super.onClick();
			}

			@Override
			protected String hoverText() {
				return Messages.get(WndJournal.BadgesTab.class, "title");
			}
		};
		btnBadges.icon(Icons.BADGES.get());
		btnBadges.setRect(panel.x, panel.y + ph - 3, pw/4f + 1.5f, lastIDX == 0 ? 25 : 20);
		align(btnBadges);
		if (lastIDX != 0) btnBadges.icon().brightness(0.6f);
		addToBack(btnBadges);

		StyledButton btnCatalog =  new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){
			@Override
			protected void onClick() {
				if (lastIDX != 1) {
					lastIDX = 1;
				}
				ShatteredPixelDungeon.seamlessResetScene();
				super.onClick();
			}
			@Override
			protected String hoverText() {
				return Messages.get(WndJournal.CatalogTab.class, "title");
			}
		};
		btnCatalog.icon(Icons.CATALOG.get());
		btnCatalog.setRect(btnBadges.right()-2, btnBadges.top(), pw/4f + 1.5f, lastIDX == 1 ? 25 : 20);
		align(btnCatalog);
		if (lastIDX != 1) btnCatalog.icon().brightness(0.6f);
		addToBack(btnCatalog);

		StyledButton btnGuide =  new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){
			@Override
			protected void onClick() {
				if (lastIDX != 2) {
					lastIDX = 2;
				}
				ShatteredPixelDungeon.seamlessResetScene();
				super.onClick();
			}
			@Override
			protected String hoverText() {
				return Messages.get(WndJournal.GuideTab.class, "title");
			}
		};
		btnGuide.icon(new ItemSprite(ConsumUsefulProcessEnhanceDict.MASTERY_0));
		btnGuide.setRect(btnCatalog.right()-2, btnBadges.top(), pw/4f + 1.5f, lastIDX == 2 ? 25 : 20);
		align(btnGuide);
		if (lastIDX != 2) btnGuide.icon().brightness(0.6f);
		addToBack(btnGuide);

		StyledButton btnAlchemy =  new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){
			@Override
			protected void onClick() {
				if (lastIDX != 3) {
					lastIDX = 3;
				}
				ShatteredPixelDungeon.seamlessResetScene();
				super.onClick();
			}
			@Override
			protected String hoverText() {
				return Messages.get(WndJournal.AlchemyTab.class, "title");
			}
		};
		btnAlchemy.icon(Icons.ALCHEMY.get());
		btnAlchemy.setRect(btnGuide.right()-2, btnBadges.top(), pw/4f + 1.5f, lastIDX == 3 ? 25 : 20);
		align(btnAlchemy);
		if (lastIDX != 3) btnAlchemy.icon().brightness(0.6f);
		addToBack(btnAlchemy);

		addToBack(BG);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( insets.left + w - btnExit.width(), insets.top );
		add( btnExit );

		fadeIn();
	}

	@Override
	public void destroy() {

		Badges.saveGlobal();

		super.destroy();
	}

	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade( TitleScene.class );
	}

}
