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
import pd.items.Item;
import pd.ui.InventoryPane;
import pd.ui.ItemJournalButton;
import pd.ui.RedButton;
import pd.ui.Window;
import render.noosa.Game;

import java.util.ArrayList;

public class WndUseItem extends WndInfoItem {

	private static final float BUTTON_HEIGHT	= 16;
	
	private static final float GAP	= 2;

	public Window owner;
	public Item item;

	public WndUseItem( final Window owner, final Item item ) {
		
		super(item);

		this.owner = owner;
		this.item = item;

		float y = height;
		
		if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {
			y += GAP;
			ArrayList<RedButton> buttons = new ArrayList<>();
			//SPSXPD: 与 buttons 平行的消耗提示（露珠瓶用它显示露珠消耗）
			for (final String action : item.actions(Dungeon.hero)) {
				//SPSXPD: 需要另起一行的动作（如社会升降器的"上楼"与"快速抵达"）先把上一行布局掉
				if (item.actionBreakBefore(action) && !buttons.isEmpty()) {
					y = layoutButtons(buttons, width, y);
				}
				//SPSXPD: 该动作的消耗提示（露珠瓶用它显示露珠消耗）
				final String cost = item.actionCost(action, Dungeon.hero);
				final CostLabel costLabel = cost == null ? null : new CostLabel(cost, item.actionCostOk(action, Dungeon.hero));
				//SPSEXPD: 食用动作的耗时提示（小沙漏 + 小一号黄色数字，贴按钮右下角）
				final EatTimeLabel eatTimeLabel = eatTimeOf(item, action);

				RedButton btn = new RedButton(item.actionName(action, Dungeon.hero), 8) {
					@Override
					protected void onClick() {
						//SPSEXPD: 连续使用——物品类型在白名单内时，用后不关背包并重开操作框
						boolean keepOpen = pd.items.ContinuousUse.allows(item, action);
						hide();
						if (!keepOpen && owner != null && owner.parent != null) owner.hide();
						if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {
							item.execute(Dungeon.hero, action);
						}
						Item.updateQuickslot();
						if (keepOpen) {
							//SPSEXPD: 物品还在背包时重开操作框（保持背包打开）；用尽/死亡则退回普通关闭
							if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {
								Game.scene().addToFront(new WndUseItem(owner, item));
							} else if (owner != null && owner.parent != null) {
								owner.hide();
							}
						}
						if (action.equals(item.defaultAction()) && item.usesTargeting && owner == null) {
							InventoryPane.useTargeting();
						}
					}

					@Override
					protected void layout() {
						super.layout();
						//SPSXPD: 把消耗数字贴到按钮右上角（随按钮一起布局）
						if (costLabel != null) {
							costLabel.x = right() - costLabel.width() - 1;
							costLabel.y = top() + 1;
						}
						//SPSEXPD: 食用耗时贴到按钮右下角（右上角的消耗角标保持不动）
						if (eatTimeLabel != null) {
							eatTimeLabel.x = right() - eatTimeLabel.width() - 1;
							eatTimeLabel.y = bottom() - eatTimeLabel.height() - 1;
						}
					}
				};
				btn.setSize(btn.reqWidth(), BUTTON_HEIGHT);
				if (costLabel != null) {
					costLabel.visible = true;
					btn.add(costLabel);
				}
				if (eatTimeLabel != null) {
					eatTimeLabel.visible = true;
					btn.add(eatTimeLabel);
				}
				buttons.add(btn);
				add(btn);

				if (action.equals(item.defaultAction())) {
					btn.textColor(TITLE_COLOR);
				}

			}
			y = layoutButtons(buttons, width, y);

			ItemJournalButton btn = new ItemJournalButton(item, this);
			btn.setRect(width - 16, 0, 16, 16);
			add(btn);
		}

		resize( width, (int)(y) );
	}

	/** SPSXPD: 动作按钮右上角的消耗提示（绿色数字）。直接继承 BitmapText 以保证被绘制 */
	private static class CostLabel extends render.noosa.BitmapText {

		CostLabel(String cost, boolean affordable) {
			super(pd.scenes.PixelScene.pixelFont);
			text(cost);
			measure();
			hardlight(affordable ? 0x66FF66 : 0xFF5555);
		}

		/** 摆到指定位置（访问自身继承的 protected x/y） */
		void placeAt(float px, float py) {
			x = px;
			y = py;
		}
	}

	/** SPSEXPD: 该动作若是「食用」则返回其耗时角标（沙漏 + 回合数），否则返回 null。 */
	private static EatTimeLabel eatTimeOf(Item item, String action) {
		if (item instanceof pd.items.consum.food.Food
				&& pd.items.consum.food.Food.AC_EAT.equals(action)) {
			return new EatTimeLabel(((pd.items.consum.food.Food) item).visibleEatingTime());
		}
		if (item instanceof pd.items.consum.brewed.Brewed
				&& pd.items.consum.brewed.Brewed.AC_EAT.equals(action)) {
			return new EatTimeLabel(((pd.items.consum.brewed.Brewed) item).visibleEatingTime());
		}
		return null;
	}

	/**
	 * SPSEXPD: 动作按钮右下角的「食用耗时」提示（小一号黄色数字）。
	 * 直接继承 BitmapText、与上面的 CostLabel 同款做法，不引入子组件：
	 * Component 的构造期就会调用 createChildren()，那时构造参数还没赋值，
	 * 在 createChildren() 里读它只会拿到默认值 0（之前「所有食物都显示 0」就是这个原因）。
	 */
	private static class EatTimeLabel extends render.noosa.BitmapText {

		private static final float TEXT_SCALE = 0.75f;

		EatTimeLabel(float time) {
			super(pd.scenes.PixelScene.pixelFont);
			text(String.valueOf((int) time));
			measure();
			scale.set(TEXT_SCALE, TEXT_SCALE);
			hardlight(0xFFFF66);
		}
	}

	private static float layoutButtons(ArrayList<RedButton> buttons, float width, float y){
		ArrayList<RedButton> curRow = new ArrayList<>();
		float widthLeftThisRow = width;
		
		while( !buttons.isEmpty() ){
			RedButton btn = buttons.get(0);
			
			widthLeftThisRow -= btn.width();
			if (curRow.isEmpty()) {
				curRow.add(btn);
				buttons.remove(btn);
			} else {
				widthLeftThisRow -= 1;
				if (widthLeftThisRow >= 0) {
					curRow.add(btn);
					buttons.remove(btn);
				}
			}
			
			//layout current row. Currently forces a max of 3 buttons but can work with more
			if (buttons.isEmpty() || widthLeftThisRow <= 0 || curRow.size() >= 3){
				
				//re-use this variable for laying out the buttons
				widthLeftThisRow = width - (curRow.size()-1);
				for (RedButton b : curRow){
					widthLeftThisRow -= b.width();
				}
				
				//while we still have space in this row, find the shortest button(s) and extend them
				while (widthLeftThisRow > 0){
					
					ArrayList<RedButton> shortest = new ArrayList<>();
					RedButton secondShortest = null;
					
					for (RedButton b : curRow) {
						if (shortest.isEmpty()) {
							shortest.add(b);
						} else {
							if (b.width() < shortest.get(0).width()) {
								secondShortest = shortest.get(0);
								shortest.clear();
								shortest.add(b);
							} else if (b.width() == shortest.get(0).width()) {
								shortest.add(b);
							} else if (secondShortest == null || secondShortest.width() > b.width()){
								secondShortest = b;
							}
						}
					}
					
					float widthToGrow;
					
					if (secondShortest == null){
						widthToGrow = widthLeftThisRow / shortest.size();
						widthLeftThisRow = 0;
					} else {
						widthToGrow = secondShortest.width() - shortest.get(0).width();
						if ((widthToGrow * shortest.size()) >= widthLeftThisRow){
							widthToGrow = widthLeftThisRow / shortest.size();
							widthLeftThisRow = 0;
						} else {
							widthLeftThisRow -= widthToGrow * shortest.size();
						}
					}
					
					for (RedButton toGrow : shortest){
						toGrow.setRect(0, 0, toGrow.width()+widthToGrow, toGrow.height());
					}
				}
				
				//finally set positions
				float x = 0;
				for (RedButton b : curRow){
					b.setRect(x, y, b.width(), b.height());
					x += b.width() + 1;
				}
				
				//move to next line and reset variables
				y += BUTTON_HEIGHT+1;
				widthLeftThisRow = width;
				curRow.clear();
				
			}
			
		}
		
		return y - 1;
	}

}
