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

import pd.Dungeon;
import pd.Statistics;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.items.TengusMask;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.HeroIcon;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;
import render.noosa.Game;
import render.utils.math.Random;
import pd.messages.InlineText;

public class WndChooseSubclass extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndChooseSubclass.class)
			.t("message", "面具激动地贴附到你的脸上。忽然，眼前的景象极速消逝，你什么都看不见了——不，是什么都看得见了，林林总总的新技巧和新能力灌入你的脑海，斑斓琳琅。您想要如何驾驭面具的魔力？")
			.t("cancel", "我将稍后决定")
			.t("are_you_sure", "您确定要选择这条专精道路吗？")
			.t("yes", "是的，我决定好了。")
			.t("no", "不了，我稍后决定。")
			.t("random_title", "随机专精")
			.t("random_sure", "你确定要选择随机一个专精吗？");
	}

	
	private static final int WIDTH		= 130;
	private static final float GAP		= 2;
	
	public WndChooseSubclass(final TengusMask tome, final Hero hero ) {
		
		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite( tome.image(), null ) );
		titlebar.label( tome.name() );
		titlebar.setRect( 0, 0, WIDTH-16, 0 );
		add( titlebar );

		IconButton random = new IconButton(Icons.SHUFFLE.get()){
			@Override
			protected void onClick() {
				super.onClick();
				GameScene.show(new WndOptions(Icons.SHUFFLE.get(),
						Messages.get(WndChooseSubclass.class, "random_title"),
						Messages.get(WndChooseSubclass.class, "random_sure"),
						Messages.get(WndChooseSubclass.class, "yes"),
						Messages.get(WndChooseSubclass.class, "no")){
					@Override
					protected void onSelect(int index) {
						super.onSelect(index);
						if (index == 0){
							WndChooseSubclass.this.hide();
							HeroSubClass cls = Random.oneOf(hero.heroClass.subClasses());
							tome.choose(cls);
							GameScene.show(new WndInfoSubclass(hero.heroClass, cls));
						}
					}
				});
			}

			@Override
			public void update() {
				if (Statistics.qualifiedForRandomVictoryBadge){
					icon.tint(1, 1, 1, (float)Math.abs(Math.cos(1.5f*Math.PI* Game.timeTotal)/2f));
				}
				super.update();
			}

			@Override
			protected String hoverText() {
				return Messages.get(WndChooseSubclass.class, "random_title");
			}
		};
		random.setRect(WIDTH-16, 0, 16, 16);
		add(random);

		RenderedTextBlock message = PixelScene.renderTextBlock( 6 );
		message.text( Messages.get(this, "message"), WIDTH );
		message.setPos( titlebar.left(), titlebar.bottom() + GAP );
		add( message );

		float pos = message.bottom() + 3*GAP;

		for (HeroSubClass subCls : hero.heroClass.subClasses()){
			RedButton btnCls = new RedButton( subCls.shortDesc(), 6 ) {
				@Override
				protected void onClick() {
					GameScene.show(new WndOptions(new HeroIcon(subCls),
							Messages.titleCase(subCls.title()),
							Messages.get(WndChooseSubclass.this, "are_you_sure"),
							Messages.get(WndChooseSubclass.this, "yes"),
							Messages.get(WndChooseSubclass.this, "no")){
						@Override
						protected void onSelect(int index) {
							hide();
							if (index == 0 && WndChooseSubclass.this.parent != null){
								WndChooseSubclass.this.hide();
								tome.choose( subCls );
								Statistics.qualifiedForRandomVictoryBadge = false;
							}
						}
					});
				}
			};
			btnCls.leftJustify = true;
			btnCls.multiline = true;
			btnCls.setSize(WIDTH-20, btnCls.reqHeight()+2);
			btnCls.setRect( 0, pos, WIDTH-20, btnCls.reqHeight()+2);
			add( btnCls );

			IconButton clsInfo = new IconButton(Icons.get(Icons.INFO)){
				@Override
				protected void onClick() {
					GameScene.show(new WndInfoSubclass(Dungeon.hero.heroClass, subCls));
				}
			};
			clsInfo.setRect(WIDTH-20, btnCls.top() + (btnCls.height()-20)/2, 20, 20);
			add(clsInfo);

			pos = btnCls.bottom() + GAP;
		}

		RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect( 0, pos, WIDTH, 18 );
		add( btnCancel );
		
		resize( WIDTH, (int)btnCancel.bottom() );
	}
}
