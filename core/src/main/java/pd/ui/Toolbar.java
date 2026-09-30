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

package pd.ui;

import pd.Assets;
import pd.Dungeon;
import pd.QuickSlot;
import pd.SPDAction;
import pd.SPDSettings;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HoldFast;
import pd.actors.hero.Belongings;
import pd.actors.hero.Talent;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;
import pd.tiles.DungeonTerrainTilemap;
import pd.windows.WndBag;
import pd.windows.WndKeyBindings;
import pd.windows.WndMessage;
import pd.windows.WndQuickBag;
import pd.windows.WndUseItem;
import render.input.ControllerHandler;
import render.input.GameAction;
import render.input.KeyBindings;
import render.noosa.Camera;
import render.noosa.Game;
import render.noosa.Gizmo;
import render.noosa.Image;
import render.noosa.PointerArea;
import render.noosa.ui.Component;
import render.utils.Point;
import render.utils.PointF;

import java.util.ArrayList;

public class Toolbar extends Component {

	private Tool btnWait;
	private Tool btnSearch;
	private Tool btnInventory;
	private QuickslotTool[] btnQuick;
	
	private PickedUpItem pickedUp;
	
	private boolean lastEnabled = true;
	public boolean examining = false;

	private static Toolbar instance;

	public enum Mode {
		SPLIT,
		GROUP,
		CENTER
	}
	
	public Toolbar() {
		super();

		instance = this;

		height = btnInventory.height();
	}

	@Override
	public synchronized void destroy() {
		super.destroy();
		if (instance == this) instance = null;
	}

	@Override
	protected void createChildren() {

		//SPS: 下栏只建下段槽位（0-9），左右栏槽位由 SideQuickBar 创建；
		//显示数量由设置控制（3-10，用户裁决 2026-09），翻页机制随 quickSwapper 一并移除
		btnQuick = new QuickslotTool[QuickSlot.BOTTOM_SIZE];
		for (int i = 0; i < btnQuick.length; i++){
			add( btnQuick[i] = new QuickslotTool(64, 0, 22, 24, i) );
		}

		//hidden button for quickslot selector keybind
		add(new Button(){
			@Override
			protected void onClick() {
				if (QuickSlotButton.targetingSlot != -1){
					int cell = QuickSlotButton.autoAim(QuickSlotButton.lastTarget, Dungeon.quickslot.getItem(QuickSlotButton.targetingSlot));

					if (cell != -1){
						GameScene.handleCell(cell);
					} else {
						//couldn't auto-aim, just target the position and hope for the best.
						GameScene.handleCell( QuickSlotButton.lastTarget.pos );
					}
					return;
				}

				if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {

					//SPS: 只列出当前设置下可见的槽位（下/左/右三区，隐藏槽不列出）
					final int[] slotNums = new int[QuickSlot.SIZE];
					int shown = 0;
					for (int i = 0; i < QuickSlot.SIZE; i++){
						if (QuickSlotButton.slotVisible(i)) slotNums[shown++] = i;
					}
					int shownCount = shown;

					String[] slotNames = new String[shownCount];
					Image[] slotIcons = new Image[shownCount];
					for (int s = 0; s < shownCount; s++){
						int i = slotNums[s];
						Item item = Dungeon.quickslot.getItem(i);

						if (item != null && !Dungeon.quickslot.isPlaceholder(i) &&
								(!Dungeon.hero.belongings.lostInventory() || item.keptThroughLostInventory())){
							slotNames[s] = Messages.titleCase(item.name());
							slotIcons[s] = new ItemSprite(item);
						} else {
							slotNames[s] = Messages.get(Toolbar.class, "quickslot_assign");
							slotIcons[s] = new ItemSprite(ItemSpriteSheet.SOMETHING);
						}
					}

					String info = "";
					if (ControllerHandler.controllerActive){
						info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_select") + "\n";
						info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.RIGHT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_assign") + "\n";
						info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_cancel");
					} else {
						info += Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "quickslot_select") + "\n";
						info += Messages.get(WndKeyBindings.class, SPDAction.RIGHT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "quickslot_assign") + "\n";
						info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "quickslot_cancel");
					}

					Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "quickslot_prompt"), info, slotNames, slotIcons) {
						@Override
						public void onSelect(int idx, boolean alt) {
							final int slotIdx = slotNums[idx];
							Item item = Dungeon.quickslot.getItem(slotIdx);

							if (item == null || Dungeon.quickslot.isPlaceholder(slotIdx)
									|| (Dungeon.hero.belongings.lostInventory() && !item.keptThroughLostInventory())
									|| alt){
								//TODO would be nice to use a radial menu for this too
								// Also a bunch of code could be moved out of here into subclasses of RadialMenu
								GameScene.selectItem(new WndBag.ItemSelector() {
									@Override
									public String textPrompt() {
										return Messages.get(QuickSlotButton.class, "select_item");
									}

									@Override
									public boolean itemSelectable(Item item) {
										return item.defaultAction() != null;
									}

									@Override
									public void onSelect(Item item) {
										if (item != null) {
											QuickSlotButton.set(slotIdx, item);
										}
									}
								});
							} else {

								item.execute(Dungeon.hero);
								if (item.usesTargeting) {
									QuickSlotButton.useTargeting(slotIdx);
								}
							}
							super.onSelect(idx, alt);
						}
					});
				}
			}

			@Override
			public GameAction keyAction() {
				if (btnWait.active) return SPDAction.QUICKSLOT_SELECTOR;
				else				return null;
			}
		});
		
		add(btnWait = new Tool(24, 0, 20, 26) {
			@Override
			protected void onClick() {
				if (Dungeon.hero != null &&  Dungeon.hero.ready && !GameScene.cancel()) {
					examining = false;
					Dungeon.hero.rest(false);
				}
			}
			
			@Override
			public GameAction keyAction() {
				return SPDAction.WAIT;
			}

			@Override
			public GameAction secondaryTooltipAction() {
				return SPDAction.WAIT_OR_PICKUP;
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Messages.get(WndKeyBindings.class, "wait"));
			}

			protected boolean onLongClick() {
				if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
					examining = false;
					Dungeon.hero.rest(true);
				}
				return true;
			}
		});
		btnWait.icon( 176, 0, 16, 16 );

		//hidden button for rest keybind
		add(new Button(){
			@Override
			protected void onClick() {
				if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
					examining = false;
					Dungeon.hero.rest(true);
				}
			}

			@Override
			public GameAction keyAction() {
				if (btnWait.active) return SPDAction.REST;
				else				return null;
			}
		});

		//hidden button for wait / pickup keybind
		add(new Button(){
			@Override
			protected void onClick() {
				if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
					Dungeon.hero.waitOrPickup = true;
					if ((Dungeon.level.heaps.get(Dungeon.hero.pos) != null || Dungeon.hero.canSelfTrample())
						&& Dungeon.hero.handle(Dungeon.hero.pos)){
						//trigger hold fast and patient strike here, even if the hero didn't specifically wait
						if (Dungeon.hero.hasTalent(Talent.HOLD_FAST)){
							Buff.affect(Dungeon.hero, HoldFast.class).pos = Dungeon.hero.pos;
						}
						if (Dungeon.hero.hasTalent(Talent.PATIENT_STRIKE)){
							Buff.affect(Dungeon.hero, Talent.PatientStrikeTracker.class).pos = Dungeon.hero.pos;
						}
						Dungeon.hero.next();
					} else {
						examining = false;
						Dungeon.hero.rest(false);
					}
				}
			}

			protected boolean onLongClick() {
				if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
					examining = false;
					Dungeon.hero.rest(true);
				}
				return true;
			}

			@Override
			public GameAction keyAction() {
				if (btnWait.active) return SPDAction.WAIT_OR_PICKUP;
				else				return null;
			}
		});
		
		add(btnSearch = new Tool(44, 0, 20, 26) {
			@Override
			protected void onClick() {
				if (Dungeon.hero != null && Dungeon.hero.ready) {
					if (!examining && !GameScene.cancel()) {
						GameScene.selectCell(informer);
						examining = true;
					} else if (examining) {
						informer.onSelect(null);
						Dungeon.hero.search(true);
					}
				}
			}
			
			@Override
			public GameAction keyAction() {
				return SPDAction.EXAMINE;
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Messages.get(WndKeyBindings.class, "examine"));
			}
			
			@Override
			protected boolean onLongClick() {
				Dungeon.hero.search(true);
				return true;
			}
		});
		btnSearch.icon( 192, 0, 16, 16 );
		
		add(btnInventory = new Tool(0, 0, 24, 26) {
			private CurrencyIndicator ind;

			private Image arrow;

			@Override
			protected void onClick() {
				if (Dungeon.hero != null && (Dungeon.hero.ready || !Dungeon.hero.isAlive())) {
					if (SPDSettings.interfaceSize() == 2) {
						GameScene.toggleInvPane();
					} else {
						if (!GameScene.cancel()) {
							GameScene.show(new WndBag(Dungeon.hero.belongings.backpack));
						}
					}
				}
			}
			
			@Override
			public GameAction keyAction() {
				return SPDAction.INVENTORY;
			}

			@Override
			public GameAction secondaryTooltipAction() {
				return SPDAction.INVENTORY_SELECTOR;
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Messages.get(WndKeyBindings.class, "inventory"));
			}
			
			@Override
			protected boolean onLongClick() {
				GameScene.show(new WndQuickBag(null));
				return true;
			}

			@Override
			protected void createChildren() {
				super.createChildren();
				arrow = Icons.get(Icons.COMPASS);
				arrow.originToCenter();
				arrow.visible = SPDSettings.interfaceSize() == 2;
				arrow.tint(0x3D2E18, 1f);
				add(arrow);

				ind = new CurrencyIndicator();
				add(ind);
			}

			@Override
			protected void layout() {
				super.layout();
				ind.fill(this);
				bringToFront(ind);

				arrow.x = left() + (width - arrow.width())/2;
				arrow.y = bottom()-arrow.height-1;
				arrow.angle = bottom() == camera().height ? 0 : 180;
				PixelScene.align(arrow);
			}

			@Override
			public void enable(boolean value) {
				if (value != active){
					arrow.alpha( value ? 1f : 0.4f );
				}
				super.enable(value);
			}
		});
		btnInventory.icon( 160, 0, 16, 16 );

		//hidden button for inventory selector keybind
		add(new Button(){
			@Override
			protected void onClick() {
				if (Dungeon.hero != null && Dungeon.hero.ready && !GameScene.cancel()) {
					ArrayList<Bag> bags = Dungeon.hero.belongings.getBags();
					String[] names = new String[bags.size()];
					Image[] images = new Image[bags.size()];
					for (int i = 0; i < bags.size(); i++){
						names[i] = Messages.titleCase(bags.get(i).name());
						images[i] = new ItemSprite(bags.get(i));
					}
					String info = "";
					if (ControllerHandler.controllerActive){
						info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "container_select") + "\n";
						info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "container_cancel");
					} else {
						info += Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "container_select") + "\n";
						info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "container_cancel");
					}

					Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "container_prompt"), info, names, images){
						@Override
						public void onSelect(int idx, boolean alt) {
							super.onSelect(idx, alt);
							Bag bag = bags.get(idx);
							ArrayList<Item> items = (ArrayList<Item>) bag.items.clone();

							for(Item i : bag.items){
								if (i instanceof Bag) items.remove(i);
								if (Dungeon.hero.belongings.lostInventory() && !i.keptThroughLostInventory()) items.remove(i);
							}

							if (idx == 0){
								Belongings b = Dungeon.hero.belongings;
								if (b.ring() != null) items.add(0, b.ring());
								if (b.misc() != null) items.add(0, b.misc());
								if (b.artifact() != null) items.add(0, b.artifact());
								if (b.accessory5 != null) items.add(0, b.accessory5);
								if (b.accessory4 != null) items.add(0, b.accessory4);
								if (b.badge != null) items.add(0, b.badge);
								if (b.armor() != null) items.add(0, b.armor());
								if (b.secondArmor() != null) items.add(0, b.secondArmor());
								if (b.secondWep() != null) items.add(0, b.secondWep());
								if (b.weapon() != null) items.add(0, b.weapon());
							}

							if (items.size() == 0){
								GameScene.show(new WndMessage(Messages.get(Toolbar.class, "container_empty")));
								return;
							}

							String[] itemNames = new String[items.size()];
							Image[] itemIcons = new Image[items.size()];
							for (int i = 0; i < items.size(); i++){
								itemNames[i] = Messages.titleCase(items.get(i).name());
								itemIcons[i] = new ItemSprite(items.get(i));
							}

							String info = "";
							if (ControllerHandler.controllerActive){
								info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "item_select") + "\n";
								info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.RIGHT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "item_use") + "\n";
								info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "item_cancel");
							} else {
								info += Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "item_select") + "\n";
								info += Messages.get(WndKeyBindings.class, SPDAction.RIGHT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "item_use") + "\n";
								info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "item_cancel");
							}

							Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "item_prompt"), info, itemNames, itemIcons){
								@Override
								public void onSelect(int idx, boolean alt) {
									super.onSelect(idx, alt);
									Item item = items.get(idx);
									if (alt && item.defaultAction() != null) {
										item.execute(Dungeon.hero);
									} else {
										InventoryPane.clearTargetingSlot();
										Game.scene().addToFront(new WndUseItem(null, item));
									}
								}
							});
						}
					});
				}
			}

			@Override
			public GameAction keyAction() {
				if (btnWait.active) return SPDAction.INVENTORY_SELECTOR;
				else				return null;
			}
		});

		add(pickedUp = new PickedUpItem());
	}
	
	@Override
	protected void layout() {

		float right = width;

		//SPS: 下栏数量由设置控制（3-10，用户裁决 2026-09），按设置数量并排显示，
		//不设翻页/缩放兜底——窄屏放不下时由玩家自行调小数量
		int startingSlot = 0;
		int endingSlot = SPDSettings.quickslotsBottom() - 1;

		for (int i = 0; i < btnQuick.length; i++){
			btnQuick[i].visible = i >= startingSlot && i <= endingSlot;
			btnQuick[i].enable(btnQuick[i].visible && lastEnabled);
			if (i < startingSlot || i > endingSlot){
				btnQuick[i].setPos(btnQuick[i].left(), PixelScene.uiCamera.height);
			}
		}

		if (SPDSettings.interfaceSize() > 0){
			btnInventory.setPos(right - btnInventory.width(), y);
			btnWait.setPos(btnInventory.left() - btnWait.width(), y);
			btnSearch.setPos(btnWait.left() - btnSearch.width(), y);

			right = btnSearch.left();
			for(int i = endingSlot; i >= startingSlot; i--) {
				if (i == endingSlot){
					btnQuick[i].border(0, 2);
					btnQuick[i].frame(106, 0, 19, 24);
				} else if (i == 0){
					btnQuick[i].border(2, 1);
					btnQuick[i].frame(86, 0, 20, 24);
				} else {
					btnQuick[i].border(0, 1);
					btnQuick[i].frame(88, 0, 18, 24);
				}
				btnQuick[i].setPos(right-btnQuick[i].width(), y+2);
				right = btnQuick[i].left();
			}

			return;
		}

		for(int i = startingSlot; i <= endingSlot; i++) {
			if (i == startingSlot && !SPDSettings.flipToolbar() ||
				i == endingSlot && SPDSettings.flipToolbar()){
				btnQuick[i].border(0, 2);
				btnQuick[i].frame(106, 0, 19, 24);
			} else if (i == startingSlot && SPDSettings.flipToolbar() ||
					i == endingSlot && !SPDSettings.flipToolbar()){
				btnQuick[i].border(2, 1);
				btnQuick[i].frame(86, 0, 20, 24);
			} else {
				btnQuick[i].border(0, 1);
				btnQuick[i].frame(88, 0, 18, 24);
			}
		}

		Toolbar.Mode mode;
		try {
			mode = Mode.valueOf(SPDSettings.toolbarMode());
		} catch (Exception e){
			Game.reportException(e);
			mode = PixelScene.landscape() ? Mode.GROUP : Mode.SPLIT;
		}
		switch(mode){
			case SPLIT:
				btnWait.setPos(x, y);
				btnSearch.setPos(btnWait.right(), y);

				btnInventory.setPos(right - btnInventory.width(), y);

				float left = 0;

				btnQuick[startingSlot].setPos(btnInventory.left() - btnQuick[startingSlot].width(), y + 2);
				for (int i = startingSlot+1; i <= endingSlot; i++) {
					btnQuick[i].setPos(btnQuick[i-1].left() - btnQuick[i].width(), y + 2);
				}

				break;

			//center = group but.. well.. centered, so all we need to do is pre-emptively set the right side further in.
			case CENTER:
				float toolbarWidth = btnWait.width() + btnSearch.width() + btnInventory.width();
				for(Button slot : btnQuick){
					if (slot.visible) toolbarWidth += slot.width();
				}
				//SPS: 超宽时钳制为右对齐（与 GROUP 一致），不再向两侧溢出
				right = Math.min( (width + toolbarWidth)/2, width );

			case GROUP:
				btnWait.setPos(right - btnWait.width(), y);
				btnSearch.setPos(btnWait.left() - btnSearch.width(), y);
				btnInventory.setPos(btnSearch.left() - btnInventory.width(), y);

				btnQuick[startingSlot].setPos(btnInventory.left() - btnQuick[startingSlot].width(), y + 2);
				for (int i = startingSlot+1; i <= endingSlot; i++) {
					btnQuick[i].setPos(btnQuick[i-1].left() - btnQuick[i].width(), y + 2);
				}
				
				break;
		}

		//SPS: 超宽时不居中分摊——右端（背包按钮侧）绝对固定，格子向左延伸，
		//溢出只吞尾部（出左屏/被等待搜索按钮压住），保证槽 0 起的前几格位置恒定可用
		right = width;

		if (SPDSettings.flipToolbar()) {

			btnWait.setPos( (right - btnWait.right()), y);
			btnSearch.setPos( (right - btnSearch.right()), y);
			btnInventory.setPos( (right - btnInventory.right()), y);

			for(int i = startingSlot; i <= endingSlot; i++) {
				btnQuick[i].setPos( right - btnQuick[i].right(), y+2);
			}

		}

	}

	public static void updateLayout(){
		if (instance != null) instance.layout();
	}
	
	@Override
	public void update() {
		super.update();
		
		if (lastEnabled != (Dungeon.hero.ready && Dungeon.hero.isAlive())) {
			lastEnabled = (Dungeon.hero.ready && Dungeon.hero.isAlive());
			
			for (Gizmo tool : members.toArray(new Gizmo[0])) {
				if (tool instanceof Tool) {
					((Tool)tool).enable( lastEnabled );
				}
			}
		}
		
		if (!Dungeon.hero.isAlive()) {
			btnInventory.enable(true);
		}
	}

	public void alpha( float value ){
		btnWait.alpha( value );
		btnSearch.alpha( value );
		btnInventory.alpha( value );
		for (QuickslotTool tool : btnQuick){
			tool.alpha(value);
		}
	}

	public void pickup( Item item, int cell ) {
		pickedUp.reset( item,
			cell,
			btnInventory.centerX(),
			btnInventory.centerY());
	}
	
	private static CellSelector.Listener informer = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer cell ) {
			if (instance != null) {
				instance.examining = false;
				GameScene.examineCell(cell);
			}
		}
		@Override
		public String prompt() {
			return Messages.get(Toolbar.class, "examine_prompt");
		}
	};
	
	static class Tool extends Button {
		
		private static final int BGCOLOR = 0x7B8073;
		
		private Image base;
		private Image icon;
		
		public Tool( int x, int y, int width, int height ) {
			super();

			hotArea.blockLevel = PointerArea.ALWAYS_BLOCK;
			frame(x, y, width, height);
		}

		public void frame( int x, int y, int width, int height) {
			base.frame( x, y, width, height );

			this.width = width;
			this.height = height;
		}

		public void icon( int x, int y, int width, int height){
			if (icon == null) icon = new Image( Assets.Interfaces.TOOLBAR );
			add(icon);

			icon.frame( x, y, width, height);
		}
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			base = new Image( Assets.Interfaces.TOOLBAR );
			add( base );
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			base.x = x;
			base.y = y;

			if (icon != null){
				icon.x = x + (width()- icon.width())/2f;
				icon.y = y + (height()- icon.height())/2f;
			}
		}

		public void alpha( float value ){
			base.alpha(value);
			if (icon != null) icon.alpha(value);
		}

		@Override
		protected void onPointerDown() {
			base.brightness( 1.4f );
		}
		
		@Override
		protected void onPointerUp() {
			if (active) {
				base.resetColor();
			} else {
				base.tint( BGCOLOR, 0.7f );
			}
		}
		
		public void enable( boolean value ) {
			if (value != active) {
				if (icon != null) icon.alpha( value ? 1f : 0.4f);
				active = value;
			}
		}
	}
	
	static class QuickslotTool extends Tool {
		
		private QuickSlotButton slot;
		private int borderLeft = 2;
		private int borderRight = 2;
		
		public QuickslotTool( int x, int y, int width, int height, int slotNum ) {
			super( x, y, width, height );

			slot = new QuickSlotButton( slotNum );
			add( slot );
		}

		public void border( int left, int right ){
			borderLeft = left;
			borderRight = right;
			layout();
		}
		
		@Override
		protected void layout() {
			super.layout();
			slot.setRect( x, y, width, height );
			slot.slotMargins(borderLeft, 2, borderRight, 2);
		}

		@Override
		public void alpha(float value) {
			super.alpha(value);
			slot.alpha(value);
		}

		@Override
		public void enable( boolean value ) {
			super.enable( value && visible );
			slot.enable( value && visible );
		}
	}

	
	public static class PickedUpItem extends ItemSprite {
		
		private static final float DURATION = 0.5f;
		
		private float startScale;
		private float startX, startY;
		private float endX, endY;
		private float left;
		
		public PickedUpItem() {
			super();
			
			originToCenter();
			
			active =
			visible =
				false;
		}
		
		public void reset( Item item, int cell, float endX, float endY ) {
			view( item );
			
			active =
			visible =
				true;
			
			PointF tile = DungeonTerrainTilemap.raisedTileCenterToWorld(cell);
			Point screen = Camera.main.cameraToScreen(tile.x, tile.y);
			PointF start = camera().screenToCamera(screen.x, screen.y);
			
			x = this.startX = start.x - width() / 2;
			y = this.startY = start.y - width() / 2;
			
			this.endX = endX - width() / 2;
			this.endY = endY - width() / 2;
			left = DURATION;
			
			scale.set( startScale = Camera.main.zoom / camera().zoom );
			
		}
		
		@Override
		public void update() {
			super.update();
			
			if ((left -= Game.elapsed) <= 0) {
				
				visible =
				active =
					false;
				if (emitter != null) emitter.on = false;
				
			} else {
				float p = left / DURATION;
				scale.set( startScale * (float)Math.sqrt( p ) );
				
				x = startX*p + endX*(1-p);
				y = startY*p + endY*(1-p);
			}
		}
	}
}
