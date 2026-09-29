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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

//SPS: 两侧快捷栏（参照 SPS-PD 0.9.8 两侧快捷栏机制，用户裁决 2026-09）：
//左栏槽 10-13、右栏槽 14-17，贴屏幕左右边缘、自上而下、距顶避让状态栏；
//数量由设置控制（左 0-4、右 0-4），隐藏槽位的物品绑定保留（只是不显示）。
//
//绘制定案（用户裁决 2026-09）：专用正方形格子背景（不再旋转下栏纹理）——
//每格 22x22 整体正方形，四边 1px 描黑描边，与整栏外边框/格间均以 1px 描黑视觉隔断；
//格间描边重叠共享（步进 21）。整栏共性外边框 = 上下帽 1px 亮框线 + 左右竖线段。
//纹理 side_toolbar.png 由 tools/make-side-toolbar.ps1 程序化生成。
public class SideQuickBar extends Component {

	private static SideQuickBar instance;

	//SPS: 布局参数——贴边、自上而下、距顶起始（避让顶部状态栏，参照 SPS 的 48~50px）
	private static final float TOP_OFFSET = 48;

	//SPS: 纹理帧规格（side_toolbar.png）
	private static final int BAR_W		= 24;	//栏宽 = 外框 1 + 格 22 + 外框 1
	private static final int CELL_SIZE	= 22;	//格子整体（含 1px 描黑描边），正方形
	private static final int CELL_STEP	= 21;	//格竖向步进（22-1，描边重叠共享 = 格间 1px）
	private static final int CAP_H		= 1;	//上下帽（亮框线行）
	private static final int SIDE_BLOCK	= 20;	//竖线段单块高（按需裁高拼接）

	private Bar leftBar;
	private Bar rightBar;

	private boolean lastEnabled = true;

	public SideQuickBar() {
		super();
		instance = this;
	}

	@Override
	public synchronized void destroy() {
		super.destroy();
		if (instance == this) instance = null;
	}

	public static void updateLayout() {
		if (instance != null) instance.layout();
	}

	@Override
	protected void createChildren() {
		leftBar = new Bar( QuickSlot.LEFT_START );
		rightBar = new Bar( QuickSlot.RIGHT_START );
	}

	@Override
	protected void layout() {
		leftBar.layout( x, y + TOP_OFFSET, SPDSettings.quickslotsLeft() );
		rightBar.layout( x + width - BAR_W, y + TOP_OFFSET, SPDSettings.quickslotsRight() );
	}

	@Override
	public void update() {
		super.update();

		boolean enabled = Dungeon.hero != null && Dungeon.hero.ready && Dungeon.hero.isAlive();
		if (lastEnabled != enabled) {
			lastEnabled = enabled;
			leftBar.enable( lastEnabled );
			rightBar.enable( lastEnabled );
		}
	}

	//SPS: 一栏 = 整体外框（上帽 + 竖线段×N + 下帽）+ 正方形格子背景 + 物品槽
	private class Bar {

		private Image top;          //上帽（1px 亮框线）
		private Image bot;          //下帽（1px 亮框线）
		private Image[] sides;      //竖线段（左右 1px 亮框线，裁高拼接覆盖格区）
		private Image[] cells;      //格子背景（CELL 帧重复贴，步进 21 描边重叠）
		private QuickSlotButton[] btns;

		Bar( int startSlot ) {
			int maxSlots = Math.max( QuickSlot.LEFT_SIZE, QuickSlot.RIGHT_SIZE );

			top = new Image( Assets.Interfaces.SIDE_TOOLBAR );
			top.frame( 22, 0, 24, CAP_H );
			add( top );

			sides = new Image[(CELL_SIZE + (maxSlots - 1) * CELL_STEP + SIDE_BLOCK - 1) / SIDE_BLOCK];
			for (int i = 0; i < sides.length; i++) {
				sides[i] = new Image( Assets.Interfaces.SIDE_TOOLBAR );
				add( sides[i] );
			}

			cells = new Image[maxSlots];
			for (int i = 0; i < cells.length; i++) {
				cells[i] = new Image( Assets.Interfaces.SIDE_TOOLBAR );
				cells[i].frame( 0, 0, CELL_SIZE, CELL_SIZE );
				add( cells[i] );
			}

			bot = new Image( Assets.Interfaces.SIDE_TOOLBAR );
			bot.frame( 70, 0, 24, CAP_H );
			add( bot );

			btns = new QuickSlotButton[maxSlots];
			for (int i = 0; i < btns.length; i++) {
				add( btns[i] = new QuickSlotButton( startSlot + i ) );
				btns[i].slotMargins( 1, 1, 1, 1 );
			}
		}

		//SPS: n=0 整栏隐藏；否则按格数拼接：
		//帽(1px) + 格区[CELL 步进 21，22+(n-1)*21 高] + 帽(1px)，竖线段裁高覆盖格区
		void layout( float bx, float by, int n ) {
			if (n <= 0) {
				top.visible = bot.visible = false;
				for (Image s : sides) s.visible = false;
				for (Image c : cells) c.visible = false;
				for (QuickSlotButton b : btns) {
					b.visible = false;
					b.enable( false );
					b.setPos( bx, by );
				}
				return;
			}

			float cellH = CELL_SIZE + (n - 1) * CELL_STEP;

			top.visible = true;
			top.x = bx;
			top.y = by;

			//竖线段覆盖格区（by+1 起，按 SIDE_BLOCK 裁高拼接）
			float remain = cellH;
			float placed = 0;
			for (int i = 0; i < sides.length; i++) {
				if (remain <= 0) {
					sides[i].visible = false;
					continue;
				}
				int h = (int)Math.min( SIDE_BLOCK, remain );
				sides[i].visible = true;
				sides[i].frame( 46, 0, 24, h );
				sides[i].x = bx;
				sides[i].y = by + CAP_H + placed;
				placed += h;
				remain -= h;
			}

			bot.visible = true;
			bot.x = bx;
			bot.y = by + CAP_H + cellH;

			for (int i = 0; i < btns.length; i++) {
				if (i >= n) {
					cells[i].visible = false;
					btns[i].visible = false;
					btns[i].enable( false );
					btns[i].setPos( bx, by + CAP_H + cellH + CAP_H );   //隐藏槽移出可视区
					continue;
				}
				float cellY = by + CAP_H + i * CELL_STEP;
				cells[i].visible = true;
				cells[i].x = bx + 1;
				cells[i].y = cellY;

				btns[i].visible = true;
				//物品槽 = 格内区 20x20（距描黑描边 1px），图标 16x16 居中
				btns[i].setRect( bx + 2, cellY + 1, CELL_SIZE - 2, CELL_SIZE - 2 );
				btns[i].enable( lastEnabled );
			}
		}

		void enable( boolean value ) {
			for (int i = 0; i < btns.length; i++) {
				if (btns[i].visible) btns[i].enable( value );
			}
		}
	}
}
