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

import pd.atlas.items.SpecificPlaceHolderDict;

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
import pd.items.equipment.bags.Bag;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;
import pd.tiles.DungeonTerrainTilemap;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndBagPicker;
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
import render.noosa.Gizmo;
import render.noosa.Image;
import render.noosa.PointerArea;
import render.noosa.ui.Component;
import render.utils.geom.Point;
import render.utils.geom.PointF;

import java.util.ArrayList;
import pd.messages.InlineText;

public class Toolbar extends Component {
	//SPSEXPD: inline Chinese text (generated from messages/ui/zh)
	static {
		InlineText.of(Toolbar.class)
			.t("quickslot_prompt", "选择一个快捷栏")
			.t("quickslot_select", "选择快捷栏")
			.t("quickslot_assign", "分配快捷栏")
			.t("quickslot_cancel", "取消")
			.t("container_prompt", "选择一个容器")
			.t("container_select", "选择容器")
			.t("container_cancel", "取消")
			.t("container_empty", "这个容器是空的！")
			.t("item_prompt", "选择一件物品")
			.t("cautious_on", "谨慎移动：开")
			.t("cautious_off", "谨慎移动：关")
			.t("item_select", "选择物品")
			.t("item_use", "快速使用物品")
			.t("item_cancel", "取消")
			.t("examine_prompt", "再按一遍此按钮以搜索周边；\n点选一个地格以获得其信息。");
	}




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
							slotIcons[s] = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0);
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
										return item.canQuickSlot();
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
			//SPS: 用于区分单击（检视）与双击（搜寻）
			private long lastClickAt = 0L;
			private static final long DOUBLE_CLICK_MS = 350L;

			//SPS: 谨慎移动开启时，给放大镜图标叠一层金色。
			//用同一帧的图标（而非方块），这样只覆盖放大镜的不透明像素，透明区域不受影响。
			private Image cautiousOverlay;

			@Override
			protected void createChildren() {
				super.createChildren();
				cautiousOverlay = new Image( Assets.Interfaces.TOOLBAR );
				cautiousOverlay.frame( 192, 0, 16, 16 );
				cautiousOverlay.hardlight( CharSprite.GOLD );
				cautiousOverlay.alpha( 0.55f );
				cautiousOverlay.visible = false;
				add( cautiousOverlay );
			}

			@Override
			protected void layout() {
				super.layout();
				cautiousOverlay.x = x + (width() - cautiousOverlay.width()) / 2f;
				cautiousOverlay.y = y + (height() - cautiousOverlay.height()) / 2f;
			}

			@Override
			public void update() {
				super.update();
				boolean on = GameScene.cautiousMove;
				if (cautiousOverlay.visible != on) {
					cautiousOverlay.visible = on;
					//图标是构造之后才 icon() 加进来的，层级在后；显示时把它提到最前
					if (on) bringToFront( cautiousOverlay );
				}
			}

			@Override
			protected void onClick() {
				if (Dungeon.hero == null || !Dungeon.hero.ready) return;

				//SPS: 双击 = 搜寻（原长按功能）
				long now = System.currentTimeMillis();
				if (now - lastClickAt <= DOUBLE_CLICK_MS){
					lastClickAt = 0L;
					if (examining){
						informer.onSelect(null);
						examining = false;
					}
					GameScene.clearHeroPath();
					Dungeon.hero.search(true);
					return;
				}
				lastClickAt = now;

				if (!examining && !GameScene.cancel()) {
					GameScene.selectCell(informer);
					examining = true;
				} else if (examining) {
					informer.onSelect(null);
					examining = false;
				}
			}
			
			@Override
			public GameAction keyAction() {
				return SPDAction.EXAMINE;
			}

			@Override
			protected String hoverText() {
				String base = Messages.titleCase(Messages.get(WndKeyBindings.class, "examine"));
				if (GameScene.cautiousMove){
					return base + " / " + Messages.get(Toolbar.class, "cautious_on");
				}
				return base;
			}
			
			//SPS: 长按 = 开关谨慎移动（原搜寻移到双击）
			@Override
			protected boolean onLongClick() {
				GameScene.cautiousMove = !GameScene.cautiousMove;
				if (!GameScene.cautiousMove){
					GameScene.clearHeroPath();
				}
				GLog.g(Messages.get(Toolbar.class,
						GameScene.cautiousMove ? "cautious_on" : "cautious_off"));
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
					} else if (SPDSettings.quickBag()) {
						//SPSEXPD: 快捷背包——单击弹出浮动的包裹选择面板（每行 4 个）；
						//面板已经开着时再次点击背包按钮（即双击）＝直接打开主背包
						boolean pickerOpen = WndBagPicker.INSTANCE != null;
						//SPSEXPD: 注意 GameScene.cancel() 在“确实关掉了窗口”时返回 true，
						//所以不能把两个分支都塞进 if(!cancel()) —— 否则面板开着时这一击只会关掉面板。
						if (pickerOpen) {
							//SPSEXPD: 选框不是 Window，没有 hide()；先摘掉它再开主背包
							WndBagPicker.INSTANCE.close();
							GameScene.show(new WndBag(Dungeon.hero.belongings.backpack));
						} else if (!GameScene.cancel()) {
							//SPSEXPD: 取背包按钮【右上角】的绝对坐标——面板以它为基准向左上渲染
							//（沿 parent 链逐级累加 Component 的偏移；Group 自身没有坐标，只有 Component 有）
							float cx = 0f, top = 0f;
							for (Gizmo g = btnInventory; g != null; g = g.parent) {
								if (g instanceof Component) {
									cx += ((Component) g).left();
									top += ((Component) g).top();
								}
							}
							cx += btnInventory.width();
							//SPSEXPD: 兜底——万一父链坐标没参与进来（结果落在屏幕左/上半），
							//按已知 HUD 布局回退：背包按钮在下快捷栏右端、其上方就是面板该在的位置
							if (cx < PixelScene.uiCamera.width / 2f) {
								cx = PixelScene.uiCamera.width - 12f;
							}
							if (top < PixelScene.uiCamera.height / 2f) {
								top = PixelScene.uiCamera.height - 40f;
							}
							//它不是 Window，构造时自己挂到场景顶层，所以不用 GameScene.show()。
							new WndBagPicker( cx, top );
						}
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
				//SPSEXPD: 可交换等待与检索按钮的位置
				if (SPDSettings.swapWaitSearch()) {
					btnSearch.setPos(x, y);
					btnWait.setPos(btnSearch.right(), y);
				} else {
					btnWait.setPos(x, y);
					btnSearch.setPos(btnWait.right(), y);
				}

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
				//SPSEXPD: 可交换等待与检索按钮的位置
				Button firstBtn = SPDSettings.swapWaitSearch() ? btnSearch : btnWait;
				Button secondBtn = SPDSettings.swapWaitSearch() ? btnWait : btnSearch;
				firstBtn.setPos(right - firstBtn.width(), y);
				secondBtn.setPos(firstBtn.left() - secondBtn.width(), y);
				btnInventory.setPos(secondBtn.left() - btnInventory.width(), y);

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
