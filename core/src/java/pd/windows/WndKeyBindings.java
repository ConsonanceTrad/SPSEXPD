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

import pd.SPDAction;
import pd.ShatteredPixelDungeon;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.Window;
import render.input.ControllerHandler;
import render.input.GameAction;
import render.input.KeyBindings;
import render.input.KeyEvent;
import render.input.PointerEvent;
import render.noosa.ColorBlock;
import render.noosa.ui.Component;
import render.utils.geom.PointF;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

public class WndKeyBindings extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndKeyBindings.class)
			.t("controller_info", "使用左摇杆移动你的角色\n使用右摇杆移动光标")
			.t("ttl_action", "行动")
			.t("ttl_key1", "按键1")
			.t("ttl_key2", "按键2")
			.t("ttl_key3", "按键3")
			.t("default", "恢复默认键位")
			.t("confirm", "确定")
			.t("cancel", "取消")
			.t("none", "无按键")
			.t("back", "返回")
			.t("left_click", "左键单击")
			.t("right_click", "右键单击")
			.t("middle_click", "中键单击")
			.t("menu", "菜单")
			.t("hero_info", "英雄信息")
			.t("journal", "事件记录")
			.t("wait", "等待")
			.t("examine", "检视")
			.t("rest", "休息")
			.t("inventory", "打开背包")
			.t("inventory_selector", "选择容器")
			.t("quickslot_selector", "选择快捷栏")
			.t("quickslot_1", "快捷栏1")
			.t("quickslot_2", "快捷栏2")
			.t("quickslot_3", "快捷栏3")
			.t("quickslot_4", "快捷栏4")
			.t("quickslot_5", "快捷栏5")
			.t("quickslot_6", "快捷栏6")
			.t("quickslot_7", "快捷栏7")
			.t("quickslot_8", "快捷栏8")
			.t("quickslot_9", "快捷栏9")
			.t("bag_1", "容器1")
			.t("bag_2", "容器2")
			.t("bag_3", "容器3")
			.t("bag_4", "容器4")
			.t("bag_5", "容器5")
			.t("tag_attack", "攻击")
			.t("tag_danger", "切换攻击目标")
			.t("tag_action", "特殊行动")
			.t("tag_loot", "捡拾物品")
			.t("tag_resume", "恢复行进")
			.t("cycle", "下一个目标/下一个表标签")
			.t("zoom_in", "画面放大/向下滚动")
			.t("zoom_out", "画面缩小/向上滚动")
			.t("n", "向上")
			.t("e", "向右")
			.t("s", "向下")
			.t("w", "向左")
			.t("ne", "右上")
			.t("se", "右下")
			.t("sw", "左下")
			.t("nw", "左上")
			.t("wait_or_pickup", "等待/捡起物品")
			.t("wndchangebinding.desc_first", "按下一个按键以替代_%s_的第一键位。")
			.t("wndchangebinding.desc_second", "按下一个按键以替代_%s_的第二键位。")
			.t("wndchangebinding.desc_third", "按下一个按键以替代_%s_的第三键位。")
			.t("wndchangebinding.desc_current", "当前键位：_%s_")
			.t("wndchangebinding.changed_bind", "新的键位：_%s_")
			.t("wndchangebinding.warning", "这个按键将不再应用于_%s_。")
			.t("wndchangebinding.error", "这个按键已经应用于该行动。")
			.t("wndchangebinding.unbind", "无按键")
			.t("wndchangebinding.cant_unbind", "至少要有一个键位对应此行动。")
			.t("wndchangebinding.confirm", "确定")
			.t("wndchangebinding.cancel", "取消");
	}


	private static final int WIDTH = 135;

	private static final int BTN_HEIGHT = 16;

	private static final int COL1_CENTER = WIDTH/5;
	private static final int COL2_CENTER = 5*WIDTH/10;
	private static final int COL3_CENTER = 7*WIDTH/10;
	private static final int COL4_CENTER = 9*WIDTH/10;

	private Component bindingsList;
	private ArrayList<BindingItem> listItems = new ArrayList<>();

	private LinkedHashMap<Integer, GameAction> changedBindings;

	private static boolean controller = false;

	public WndKeyBindings(Boolean controller) {

		this.controller = controller;

		changedBindings = controller ? KeyBindings.getAllControllerBindings() : KeyBindings.getAllBindings();

		RenderedTextBlock ttlAction = PixelScene.renderTextBlock(Messages.get(this, "ttl_action"), 9);
		ttlAction.setPos( COL1_CENTER - ttlAction.width()/2, (BTN_HEIGHT - ttlAction.height())/2);
		add(ttlAction);

		ColorBlock ttlSep1 = new ColorBlock(1, BTN_HEIGHT, 0xFF222222);
		ttlSep1.x = 0.4f*WIDTH - 1;
		add(ttlSep1);

		RenderedTextBlock ttlKey1 = PixelScene.renderTextBlock(Messages.get(this, "ttl_key1"), 6);
		ttlKey1.maxWidth(WIDTH/5);
		ttlKey1.align(RenderedTextBlock.CENTER_ALIGN);
		ttlKey1.setPos(COL2_CENTER - ttlKey1.width()/2, (BTN_HEIGHT - ttlKey1.height())/2);
		add(ttlKey1);

		ColorBlock ttlSep2 = new ColorBlock(1, BTN_HEIGHT, 0xFF222222);
		ttlSep2.x = 0.6f*WIDTH - 1;
		add(ttlSep2);

		RenderedTextBlock ttlKey2 = PixelScene.renderTextBlock(Messages.get(this, "ttl_key2"), 6);
		ttlKey2.maxWidth(WIDTH/5);
		ttlKey2.align(RenderedTextBlock.CENTER_ALIGN);
		ttlKey2.setPos(COL3_CENTER - ttlKey2.width()/2, (BTN_HEIGHT - ttlKey2.height())/2);
		add(ttlKey2);

		ColorBlock ttlSep3 = new ColorBlock(1, BTN_HEIGHT, 0xFF222222);
		ttlSep3.x = 0.8f*WIDTH - 1;
		add(ttlSep3);

		RenderedTextBlock ttlKey3 = PixelScene.renderTextBlock(Messages.get(this, "ttl_key3"), 6);
		ttlKey3.maxWidth(WIDTH/5);
		ttlKey3.align(RenderedTextBlock.CENTER_ALIGN);
		ttlKey3.setPos(COL4_CENTER - ttlKey2.width()/2, (BTN_HEIGHT - ttlKey2.height())/2);
		add(ttlKey3);

		ColorBlock ttlSep4 = new ColorBlock(WIDTH, 1, 0xFF222222);
		ttlSep4.y = BTN_HEIGHT;
		add(ttlSep4);

		bindingsList = new Component();

		ScrollPane scrollingList = new ScrollPane(bindingsList){
			@Override
			public void onClick(float x, float y) {
				for (BindingItem i : listItems) {
					if (i.onClick( x, y )) {
						break;
					}
				}
			}
		};

		add(scrollingList);

		int y = 0;

		if (controller){
			RenderedTextBlock controllerInfo = PixelScene.renderTextBlock(Messages.get(this, "controller_info"), 6);
			controllerInfo.maxWidth(WIDTH);
			controllerInfo.setPos(0, 2);
			controllerInfo.hardlight(TITLE_COLOR);
			bindingsList.add(controllerInfo);
			y = (int)controllerInfo.bottom()+3;

			ColorBlock sep = new ColorBlock(WIDTH, 1, 0xFF222222);
			sep.y = y;
			bindingsList.add(sep);
		}

		LinkedHashMap<Integer, GameAction> defaults = controller ? SPDAction.getControllerDefaults() : SPDAction.getDefaults();

		ArrayList<GameAction> actionList = GameAction.allActions();
		for (GameAction action : actionList.toArray(new GameAction[0])) {
			//start at 1. No bindings for NONE
			if (action.code() < 1) {
				actionList.remove(action);

			//mouse bindings are only available to controllers
			} else if ((action == GameAction.LEFT_CLICK
					|| action == GameAction.RIGHT_CLICK
					|| action == GameAction.MIDDLE_CLICK) && !controller) {
				actionList.remove(action);

			//actions with no default binding are moved to the end of the list
			} else if (!defaults.containsValue(action)){
						actionList.remove(action);
						actionList.add(action);
			}
		}

		for (GameAction action : actionList){
			BindingItem item = new BindingItem(action);
			item.setRect(0, y, WIDTH, BindingItem.HEIGHT);
			bindingsList.addToBack(item);
			listItems.add(item);
			y += item.height();
		}
		bindingsList.setSize(WIDTH, y+1);

		resize(WIDTH, Math.min(BTN_HEIGHT *3 + 3 + BindingItem.HEIGHT*listItems.size(), PixelScene.uiCamera.height-20));

		RedButton btnDefaults = new RedButton(Messages.get(this, "default"), 9){
			@Override
			protected void onClick() {
				changedBindings = controller ? SPDAction.getControllerDefaults() : SPDAction.getDefaults();
				for (BindingItem i : listItems){
					int key1 = 0;
					int key2 = 0;
					int key3 = 0;
					for( int k : changedBindings.keySet()){
						if (changedBindings.get(k) == i.gameAction){
							if (key1 == 0)          key1 = k;
							else if (key2 == 0)     key2 = k;
							else                    key3 = k;
						}
					}
					i.updateBindings(key1, key2, key3);
				}
			}
		};
		btnDefaults.setRect(0, height - BTN_HEIGHT *2 -1, WIDTH, BTN_HEIGHT);
		add(btnDefaults);

		RedButton btnConfirm = new RedButton(Messages.get(this, "confirm"), 9){
			@Override
			protected void onClick() {
				if (controller) KeyBindings.setAllControllerBindings(changedBindings);
				else            KeyBindings.setAllBindings(changedBindings);
				SPDAction.saveBindings();
				hide();
			}
		};
		btnConfirm.setRect(0, height - BTN_HEIGHT, WIDTH/2, BTN_HEIGHT);
		add(btnConfirm);

		RedButton btnCancel = new RedButton(Messages.get(this, "cancel"), 9){
			@Override
			protected void onClick() {
				hide(); //close and don't save
			}
		};
		btnCancel.setRect(WIDTH/2 + 1, height - BTN_HEIGHT, WIDTH/2 - 1, BTN_HEIGHT);
		add(btnCancel);

		scrollingList.setRect(0, BTN_HEIGHT +1, WIDTH, btnDefaults.top()- BTN_HEIGHT - 1);

	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		bindingsList.setPos(bindingsList.left(), bindingsList.top()); //calls layout
	}

	@Override
	public void onBackPressed() {
		//do nothing, avoids accidental back presses which would lose progress.
	}

	private class BindingItem extends Component {

		private static final int HEIGHT = 13;

		private static final int CHANGED = TITLE_COLOR;
		private static final int DEFAULT = 0xFFFFFF;
		private static final int UNBOUND = 0x888888;
		private static final int UNBOUND_CHANGED = 0x888822;

		private GameAction gameAction;
		private int key1;
		private int key2;
		private int key3;

		private int origKey1;
		private int origKey2;
		private int origKey3;

		private RenderedTextBlock actionName;
		private RenderedTextBlock key1Name;
		private RenderedTextBlock key2Name;
		private RenderedTextBlock key3Name;

		private ColorBlock sep1;
		private ColorBlock sep2;
		private ColorBlock sep3;
		private ColorBlock sep4;

		public BindingItem( GameAction action ){
			gameAction = action;

			actionName = PixelScene.renderTextBlock(Messages.get(WndKeyBindings.class, action.name()), 6 );
			actionName.setHightlighting(false);
			add(actionName);

			ArrayList<Integer> keys;
			if (controller){
				keys = KeyBindings.getControllerKeysForAction(action);
			} else {
				keys = KeyBindings.getKeyboardKeysForAction(action);
			}
			origKey1 = key1 = keys.isEmpty() ? 0 : keys.remove(0);
			origKey2 = key2 = keys.isEmpty() ? 0 : keys.remove(0);
			origKey3 = key3 = keys.isEmpty() ? 0 : keys.remove(0);

			key1Name = PixelScene.renderTextBlock( KeyBindings.getKeyName(key1), 6 );
			if (key1 == 0) key1Name.hardlight(UNBOUND);
			add(key1Name);

			key2Name = PixelScene.renderTextBlock( KeyBindings.getKeyName(key2), 6 );
			if (key2 == 0) key2Name.hardlight(UNBOUND);
			add(key2Name);

			key3Name = PixelScene.renderTextBlock( KeyBindings.getKeyName(key3), 6 );
			if (key3 == 0) key3Name.hardlight(UNBOUND);
			add(key3Name);

			sep1 = new ColorBlock(1, 1, 0xFF222222);
			add(sep1);

			sep2 = new ColorBlock(1, 1, 0xFF222222);
			add(sep2);

			sep3 = new ColorBlock(1, 1, 0xFF222222);
			add(sep3);

			sep4 = new ColorBlock(1, 1, 0xFF222222);
			add(sep4);
		}

		public void updateBindings(int first, int second, int third){
			if (second == 0 && third != 0){
				second = third;
				third = 0;
			}
			if (first == 0 && second != 0){
				first = second;
				second = 0;
			}

			key1 = first;
			key2 = second;
			key3 = third;

			key1Name.text(KeyBindings.getKeyName(key1));
			if (key1 != origKey1) key1Name.hardlight( key1 == 0 ? UNBOUND_CHANGED : CHANGED);
			else                  key1Name.hardlight( key1 == 0 ? UNBOUND : DEFAULT);

			key2Name.text(KeyBindings.getKeyName(key2));
			if (key2 != origKey2) key2Name.hardlight( key2 == 0 ? UNBOUND_CHANGED : CHANGED);
			else                  key2Name.hardlight( key2 == 0 ? UNBOUND : DEFAULT);

			key3Name.text(KeyBindings.getKeyName(key3));
			if (key3 != origKey3) key3Name.hardlight( key3 == 0 ? UNBOUND_CHANGED : CHANGED);
			else                  key3Name.hardlight( key3 == 0 ? UNBOUND : DEFAULT);

			layout();
		}

		@Override
		protected void layout() {
			super.layout();

			actionName.maxWidth((int) (2*width/5));
			key1Name.maxWidth((int) (width/5) - 2);
			key2Name.maxWidth((int) (width/5) - 2);
			key3Name.maxWidth((int) (width/5) - 2);

			actionName.setPos( x, y + (height() - actionName.height())/2);
			key1Name.setPos(x + 2*width()/5 + 1, y + 0.5f + (height() - key1Name.height())/2f);
			key2Name.setPos(x + 3*width()/5 + 1, y + 0.5f + (height() - key2Name.height())/2f);
			key3Name.setPos(x + 4*width()/5 + 1, y + 0.5f + (height() - key3Name.height())/2f);

			sep1.size(width, 1);
			sep1.x = x;
			sep1.y = bottom();

			sep2.size(1, height);
			sep2.x = key1Name.left()-2;
			sep2.y = y;

			sep3.size(1, height);
			sep3.x = key2Name.left()-2;
			sep3.y = y;

			sep4.size(1, height);
			sep4.x = key3Name.left()-2;
			sep4.y = y;
		}

		private boolean onClick( float x, float y ){
			if (inside(x, y)){
				//assigning third key
				if (x >= this.x + 4*width()/5 - 1 && key2 != 0) {
					ShatteredPixelDungeon.scene().addToFront( new WndChangeBinding(gameAction, this, 3, key3, key1, key2));

				//assigning second key
				} else if (x >= this.x + 3*width()/5 - 1 && key1 != 0) {
					ShatteredPixelDungeon.scene().addToFront( new WndChangeBinding(gameAction, this, 2, key2, key1, key3));

				//assigning first key
				} else if (x >= this.x + 2*width()/5){
					ShatteredPixelDungeon.scene().addToFront( new WndChangeBinding(gameAction, this, 1, key1, key2, key3));

				}
				return true;

			} else {
				return false;
			}
		}

	}

	private class WndChangeBinding extends Window {

		private int curKeyCode;
		private int otherBoundKey1;
		private int otherBoundKey2;
		private int changedKeyCode = -1;

		private BindingItem changedAction;
		private RenderedTextBlock changedKey;
		private RenderedTextBlock warnErr;

		private RedButton btnUnbind;
		private RedButton btnConfirm;
		private RedButton btnCancel;

		public WndChangeBinding(GameAction action, BindingItem listItem, int keyAssigning, int curKeyCode, int otherBoundKey1, int otherBoundKey2 ){

			this.curKeyCode = curKeyCode;
			this.otherBoundKey1 = otherBoundKey1;
			this.otherBoundKey2 = otherBoundKey2;

			String descKey = "";
			if (keyAssigning == 1) descKey = "desc_first";
			else if (keyAssigning == 2) descKey = "desc_second";
			else if (keyAssigning == 3) descKey = "desc_third";
			RenderedTextBlock desc = PixelScene.renderTextBlock( Messages.get(this, descKey,
						Messages.get(WndKeyBindings.class, action.name()),
						KeyBindings.getKeyName(curKeyCode)), 6 );
			desc.maxWidth(WIDTH);
			desc.setRect(0, 0, WIDTH, desc.height());
			add(desc);

			RenderedTextBlock curBind;
			curBind = PixelScene.renderTextBlock(Messages.get(this, "desc_current", KeyBindings.getKeyName(curKeyCode)), 6);
			curBind.maxWidth(WIDTH);
			curBind.setRect((WIDTH - curBind.width())/2, desc.bottom()+6, WIDTH, curBind.height());
			add(curBind);

			changedKey = PixelScene.renderTextBlock(6);
			changedKey.maxWidth(WIDTH);
			changedKey.setRect(0, curBind.bottom()+2, WIDTH, changedKey.height());
			add(changedKey);

			warnErr = PixelScene.renderTextBlock(6);
			warnErr.maxWidth(WIDTH);
			warnErr.setRect(0, changedKey.bottom() + 10, WIDTH, warnErr.height());
			add(warnErr);

			btnUnbind = new RedButton(Messages.get(this, "unbind"), 9){
				@Override
				protected void onClick() {
					if (action == GameAction.LEFT_CLICK && listItem.key2 == 0 && listItem.key3 == 0){
						ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(WndChangeBinding.class, "cant_unbind")));
					} else {
						onSignal(new KeyEvent(0, true));
					}
				}
			};
			btnUnbind.setRect(0, warnErr.bottom() + 6, WIDTH, BTN_HEIGHT);
			add(btnUnbind);

			btnConfirm = new RedButton(Messages.get(this, "confirm"), 9){
				@Override
				protected void onClick() {
					if (changedKeyCode != -1){

						changedBindings.remove(changedKeyCode);
						changedBindings.remove(listItem.key1);
						changedBindings.remove(listItem.key2);
						changedBindings.remove(listItem.key3);

						if (keyAssigning == 1){
							if (changedKeyCode != 0) changedBindings.put(changedKeyCode, action);
							if (listItem.key2 != 0) changedBindings.put(listItem.key2, action);
							if (listItem.key3 != 0) changedBindings.put(listItem.key3, action);
							listItem.updateBindings(changedKeyCode, listItem.key2, listItem.key3);
						} else if (keyAssigning == 2) {
							if (listItem.key1 != 0) changedBindings.put(listItem.key1, action);
							if (changedKeyCode != 0) changedBindings.put(changedKeyCode, action);
							if (listItem.key3 != 0) changedBindings.put(listItem.key3, action);
							listItem.updateBindings(listItem.key1, changedKeyCode, listItem.key3);
						} else {
							if (listItem.key1 != 0) changedBindings.put(listItem.key1, action);
							if (listItem.key2 != 0) changedBindings.put(listItem.key2, action);
							if (changedKeyCode != 0) changedBindings.put(changedKeyCode, action);
							listItem.updateBindings(listItem.key1, listItem.key2, changedKeyCode);
						}

						if (changedAction != null){
							if (changedAction.key1 == changedKeyCode){
								changedAction.updateBindings(0, changedAction.key2, changedAction.key3);
							} else if (changedAction.key2 == changedKeyCode){
								changedAction.updateBindings(changedAction.key1, 0, changedAction.key3);
							} else if (changedAction.key3 == changedKeyCode){
								changedAction.updateBindings(changedAction.key1, changedAction.key2, 0);
							}
						}
					}

					hide();
				}
			};
			btnConfirm.setRect(0, btnUnbind.bottom()+1, WIDTH/2, BTN_HEIGHT);
			btnConfirm.enable(false);
			add(btnConfirm);

			btnCancel = new RedButton(Messages.get(this, "cancel"), 9){
				@Override
				protected void onClick() {
					hide();
				}
			};
			btnCancel.setRect(btnConfirm.right()+1, btnUnbind.bottom()+1, WIDTH/2-1, BTN_HEIGHT);
			add(btnCancel);

			resize(WIDTH, (int)btnCancel.bottom());
			KeyBindings.bindingKey = true;

		}

		@Override
		public boolean onSignal(KeyEvent event) {
			//ignore left clicks if we are pressing a button
			if (KeyBindings.getActionForKey(event) == GameAction.LEFT_CLICK){
				PointF hoverPos = camera().screenToCamera((int)PointerEvent.currentHoverPos().x, (int)PointerEvent.currentHoverPos().y);
				if (btnUnbind.inside(hoverPos.x, hoverPos.y)) return true;
				if (btnConfirm.inside(hoverPos.x, hoverPos.y)) return true;
				if (btnCancel.inside(hoverPos.x, hoverPos.y)) return true;
			}

			//ignore controller buttons on key bindings, and vice-versa
			if (controller && !ControllerHandler.icControllerKey(event.code)){
				return true;
			} else if (!controller && !KeyEvent.isKeyboardKey(event.code)){
				return true;
			}

			if (event.pressed){
				changedKey.text(Messages.get(this, "changed_bind", KeyBindings.getKeyName(event.code)));
				changedKey.setPos((WIDTH - changedKey.width())/2, changedKey.top());

				changedKeyCode = event.code;
				changedAction = null;

				if (event.code != 0 && (event.code == curKeyCode || event.code == otherBoundKey1 || event.code == otherBoundKey2)){
					warnErr.text(Messages.get(this, "error"));
					warnErr.hardlight(CharSprite.NEGATIVE);
					btnConfirm.enable(false);

				} else if (event.code != 0 && changedBindings.get(changedKeyCode) != null){
					for (BindingItem i : listItems) {
						if (i.gameAction == changedBindings.get(changedKeyCode)) {
							changedAction = i;
							break;
						}
					}
					warnErr.text(Messages.get(this, "warning", Messages.get(WndKeyBindings.class, changedBindings.get(changedKeyCode).name() )));
					warnErr.hardlight(CharSprite.WARNING);
					btnConfirm.enable(true);

				} else {
					warnErr.text(" ");
					btnConfirm.enable(true);
				}
			}

			return true;
		}

		@Override
		public void destroy() {
			super.destroy();
			KeyBindings.bindingKey = false;
		}

	}
}
