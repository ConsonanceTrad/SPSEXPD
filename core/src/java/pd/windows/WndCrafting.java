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

import pd.atlas.items.SpecificCurrencyDict;
import pd.atlas.items.SpecificPagesDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.EquipmentNonEquipDict;
import pd.atlas.items.ConsumGoodsMaterialsMaterialsExtraDict;
import pd.atlas.items.EquipmentBagsDict;
import pd.atlas.IconEntry;

import pd.Assets;
import pd.Badges;
import pd.Chrome;
import pd.Dungeon;
import pd.SPDAction;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.hero.Belongings;
import pd.effects.Speck;
import pd.effects.particles.SparkParticle;
import pd.actors.hero.Hero;
import pd.items.EnergyCrystal;
import pd.items.BrewRecipe;
import pd.items.Generator;
import pd.items.Heap;
import pd.items.Item;
import pd.items.LiquidMetal;
import pd.items.Recipe;
import pd.items.SpsBrewRecipes;
import pd.items.SpsFoodPrepRecipes;
import pd.items.StoneOre;
import pd.utils.GLog;
import pd.items.equipment.artifacts.AlchemistsToolkit;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.bags.PotionBandolier;
import pd.items.equipment.bags.ShoppingCart;
import pd.items.equipment.bags.VelvetPouch;
import pd.items.consum.materials.Tinder;
import pd.items.consum.materials.WillOWisp;
import pd.items.consum.food.WaterItem;
import pd.items.equipment.trinkets.TrinketCatalyst;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.journal.Catalog;
import pd.journal.Document;
import pd.journal.Journal;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.Button;
import pd.ui.ExitButton;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.ItemSlot;
import pd.ui.RadialMenu;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.StatusPane;
import pd.ui.StyledButton;
import pd.ui.Toolbar;
import pd.ui.Window;
import pd.windows.IconTitle;
import pd.windows.WndBag;
import pd.windows.WndEnergizeItem;
import pd.windows.WndInfoItem;
import pd.windows.WndJournal;
import pd.windows.WndKeyBindings;
import pd.windows.WndMessage;
import render.gltextures.TextureCache;
import render.glwrap.Blending;
import render.input.ControllerHandler;
import render.input.GameAction;
import render.input.KeyBindings;
import render.noosa.Camera;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.NinePatch;
import render.noosa.NoosaScript;
import render.noosa.NoosaScriptNoLighting;
import render.noosa.SkinnedBlock;
import render.noosa.audio.Sample;
import render.noosa.particles.Emitter;
import render.noosa.ui.Component;
import render.utils.geom.RectF;

import java.io.IOException;
import java.util.ArrayList;
import pd.messages.InlineText;

public class WndCrafting extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/scenes/zh)
	static {
		InlineText.of(WndCrafting.class)
			.t("title", "炼金")
			.t("text", "")
			.t("select", "选择一件物品")
			.t("energy", "能量：")
			.t("add", "添加物品")
			.t("no_items", "这个包里没有可炼金的物品。")
			.t("craft", "合成")
			.t("guide", "指南")
			.t("energize", "提炼物品")
			.t("cancel", "取消加料")
			.t("repeat", "重复加料")
			.t("chance", "成功率")
			.t("quick_water", "加水（放进空辅材格）")
			.t("quick_fire", "加火种（放进空辅材格）")
			.t("quick_bandolier", "打开药水箱")
			.t("quick_pouch", "打开种子袋")
			.t("quick_cart", "打开购物车")
			.t("no_material", "背包里没有这种材料。")
			.t("no_space", "没有空闲的辅材格了。")
			.t("no_bag", "你还没有这个包裹。")
			.t("title_distill", "蒸馏压片机")
			.t("title_fill", "分装灌液机")
			.t("title_cook", "平底煎锅")
			.t("title_board", "菜板")
			.t("label_main", "主材料")
			.t("label_adjuvant", "副材料");
	}

	/** SPSEXPD: 打开指定模式的加工窗口（炼金釜 / 蒸馏 / 分装 / 平底锅 / 菜板）。 */
	public static void open(int mode) {
		if (Dungeon.hero == null) return;
		spsMode = mode;
		Game.scene().addToFront(new WndCrafting());
	}

	//SPSEXPD: 当前打开的加工窗口（替代原先「scene() instanceof AlchemyScene」的判定）
	private static WndCrafting instance = null;

	/** 当前打开的加工窗口，没有则为 null。 */
	public static WndCrafting active() {
		return instance;
	}




	//SPSEXPD: 炼金釜固定 5 格——1 主材 / 4 辅材（2×2 两行）；
	//水与火种不再有专用格，作为辅材之一放进辅材格（docs/alchemy-rework.md §1.2）
	private static final int SLOT_MAIN = 0;
	private static final int SLOT_ADJUVANT = 1;
	private static final InputButton[] inputs = new InputButton[5];
	private static final CombineButton[] combines = new CombineButton[3];
	private static final OutputSlot[] outputs = new OutputSlot[3];
	private int activeInputCount = 5;

	//SPSEXPD: 左侧快速添加按钮列（加水 / 加火种 / 药水箱 / 种子袋 / 购物车）
	private static final int QUICK_BTN = 20;
	private static final int QUICK_COL_W = QUICK_BTN + 8;

	//SPSEXPD: 加工模式——炼金釜 / 蒸馏压片机 / 分装灌液机 / 平底锅 / 菜板
	public static final int MODE_ALCHEMY = 0;
	public static final int MODE_DISTILL = 1;
	public static final int MODE_FILL = 2;
	public static final int MODE_COOK = 3;
	public static final int MODE_BOARD = 4;
	/** 当前模式；每次打开窗口由入口决定，关闭时必须调用 {@link #resetMode()} 复位。 */
	public static int spsMode = MODE_ALCHEMY;

	/** SPSEXPD: 关闭加工界面时复位为炼金釜模式，避免下次开釜仍处于其它模式。 */
	public static void resetMode() {
		spsMode = MODE_ALCHEMY;
	}

	//SPSEXPD: 5 格区域的上下边界，供合成按钮/输出槽纵向居中对齐
	private float slotsTop;
	private float slotsBottom;
	private float slotsMiddleY;

	private IconButton cancel;
	private IconButton repeat;
	private static ArrayList<Item> lastIngredients = new ArrayList<>();
	private static Recipe lastRecipe = null;

	private Emitter smokeEmitter;

	private Image energyIcon;
	private RenderedTextBlock energyLeft;
	private IconButton energyAdd;
	private boolean energyAddBlinking = false;

	private static int centerW;
	//SPSEXPD: 窗口模式下「合成按钮 + 输出槽」那一行的纵向位置（材料区下方）
	private float craftRowY;
	//SPSEXPD: 快速加料按钮列的横向起点（与材料块一起居中）
	private float quickColumnX = 4;

	private static final int BTN_SIZE	= 28;

	//SPSEXPD: 窗口化后的尺寸——横版（左边材料格含快速加料列，右边合成按钮 + 输出槽，底部能量条）
	private static final int WIN_W = 216;
	private static final int WIN_H = 170;

	//*** 子类/模式可定制的差异点（窗口标题、图标、格数、是否带快速加料列与指南）

	/** 标题文案键（按模式）。 */
	protected String titleKey() {
		switch (spsMode) {
			case MODE_DISTILL: return "title_distill";
			case MODE_FILL:    return "title_fill";
			case MODE_COOK:    return "title_cook";
			case MODE_BOARD:   return "title_board";
			default:           return "title";
		}
	}

	/** 标题图标（按模式，视觉上与炼金釜区分开）。 */
	protected Image titleIcon() {
		switch (spsMode) {
			case MODE_DISTILL:
				return new ItemSprite(new pd.items.equipment.artifacts.ProcessTool.Distiller());
			case MODE_FILL:
				return new ItemSprite(new pd.items.equipment.artifacts.ProcessTool.AmpouleFiller());
			case MODE_COOK:
				return new ItemSprite(new pd.items.equipment.weapon.melee.special.MeleePan());
			case MODE_BOARD:
				return new ItemSprite(new pd.items.equipment.tools.CuttingBoard());
			default:
				return Icons.ALCHEMY.get();
		}
	}

	/** 每个模式的材料格数量（炼金 5 / 蒸馏 2 / 分装 2 / 平底锅 3 / 菜板 3）。 */
	protected int modeSlotCount(int mode) {
		switch (mode) {
			case MODE_DISTILL:
			case MODE_FILL:
				return 2;
			case MODE_COOK:
			case MODE_BOARD:
				return 3;
			default:
				return 5;
		}
	}

	/** 左侧快速加料按钮列只有炼金釜需要（水/火种/包裹）。 */
	protected boolean hasQuickColumn() {
		return spsMode == MODE_ALCHEMY;
	}

	/** SPSEXPD: 材料格是否改用「单列从上到下」布局——炼金釜以外的工具都用单列。 */
	protected boolean singleColumnLayout() {
		return spsMode != MODE_ALCHEMY;
	}

	/** 是否需要「指南」按钮（只有炼金有炼金指南）。 */
	protected boolean hasGuideButton() {
		return spsMode == MODE_ALCHEMY;
	}

	/** SPSEXPD: 窗口内容在构造期一次性搭好（Window 没有 createChildren 钩子）。 */
	public WndCrafting() {
		//SPSEXPD: 横版窗口——左边材料格（含快速加料列），右边合成按钮 + 输出槽，底部能量条
		super(WIN_W, WIN_H);
		buildUi();
	}

	private void buildUi() {
		instance = this;

		int winW = (int)width;
		int winH = (int)height;

		activeInputCount = Math.min(spsInputCapacity(activeToolkit()), modeSlotCount(spsMode));

		int w = winW;
		int h = winH;

		IconTitle title = new IconTitle(titleIcon(), Messages.get(this, titleKey()));
		title.setSize(w, 0);
		title.setPos(0, 0);
		PixelScene.align(title);
		add(title);

		int pw = w;
		int left = 0;

		centerW = left + pw/2;

		int pos = (int)title.bottom() + 2;

		//SPSEXPD: 材料格布局——炼金釜是「主材 + 辅材 2×2」，其余工具（蒸馏/分装/平底锅/菜板）改成单列三格
		boolean singleColumn = singleColumnLayout();
		//SPSEXPD: 辅材四格之间用小间距；主材与辅材之间留大间距，并在两区上方各加一行文字标记
		float gapX = 6;
		float inputW = singleColumn ? BTN_SIZE : 2*BTN_SIZE + gapX;
		float slotsX0;
		float slotsX1;
		float slotsMainX;
		if (singleColumn) {
			slotsX0 = Math.round((w - BTN_SIZE) / 2f);
			slotsX1 = slotsX0;
			slotsMainX = slotsX0;
		} else {
			//SPSEXPD: 快速加料列与材料块作为一个整体水平居中，避免左侧留大片空白
			float slotsBlockW = 2*BTN_SIZE + gapX;
			float quickW = hasQuickColumn() ? QUICK_COL_W : 0;
			float blockLeft = Math.round((w - (quickW + slotsBlockW)) / 2f);
			slotsX0 = blockLeft + quickW;
			slotsX1 = slotsX0 + BTN_SIZE + gapX;
			slotsMainX = Math.round((w - BTN_SIZE) / 2f);
			quickColumnX = blockLeft;
		}

		pos += 4;

		float labelH = singleColumn ? 0 : 10;
		float slotsY0 = pos + labelH;
		//主材到辅材之间额外留出间距（含辅材标签高度）
		float slotsY1 = slotsY0 + BTN_SIZE + (singleColumn ? 6 : 20);
		float slotsY2 = slotsY1 + BTN_SIZE + 6;
		//单列模式下槽区的实际底边按格数算
		float slotsLastY = singleColumn
				? slotsY0 + (Math.max(1, activeInputCount) - 1) * (BTN_SIZE + 6)
				: slotsY2;

		if (!singleColumn) {
			//SPSEXPD: 主材料 / 副材料 文字标记
			RenderedTextBlock mainLabel = PixelScene.renderTextBlock(Messages.get(this, "label_main"), 6);
			mainLabel.setPos(slotsMainX + (BTN_SIZE - mainLabel.width())/2f, slotsY0 - labelH);
			mainLabel.hardlight(0xCCCCCC);
			add(mainLabel);

			RenderedTextBlock adjuvantLabel = PixelScene.renderTextBlock(Messages.get(this, "label_adjuvant"), 6);
			adjuvantLabel.setPos(slotsX0 + (inputW - adjuvantLabel.width())/2f, slotsY1 - labelH);
			adjuvantLabel.hardlight(0xCCCCCC);
			add(adjuvantLabel);
		}

		NinePatch inputBG = Chrome.get(Chrome.Type.TOAST_TR);
		if (singleColumn) {
			//单列布局：整列一个外框
			inputBG.x = slotsX0 - 4;
			inputBG.y = slotsY0 - 4;
			inputBG.size(inputW + 8, (slotsLastY + BTN_SIZE) - slotsY0 + 8);
			add(inputBG);
		} else {
			//SPSEXPD: 炼金釜——主材单独一个外框，辅材单独一个外框（不再框在一起）
			inputBG.x = slotsMainX - 4;
			inputBG.y = slotsY0 - 4;
			inputBG.size(BTN_SIZE + 8, BTN_SIZE + 8);
			add(inputBG);

			NinePatch adjuvantBG = Chrome.get(Chrome.Type.TOAST_TR);
			adjuvantBG.x = slotsX0 - 4;
			adjuvantBG.y = slotsY1 - 4;
			adjuvantBG.size(inputW + 8, (slotsY2 + BTN_SIZE) - slotsY1 + 8);
			add(adjuvantBG);
		}

		slotsTop = slotsY0 - 4;
		slotsBottom = slotsLastY + BTN_SIZE + 4;
		slotsMiddleY = slotsTop + (slotsBottom - slotsTop - BTN_SIZE)/2f;
		//SPSEXPD: 合成按钮 + 输出槽在材料区右侧，纵向居中
		craftRowY = slotsMiddleY;

		//左侧快速添加按钮列：加水 / 加火种 / 药水箱 / 种子袋 / 购物车（紧贴材料块左侧）
		if (hasQuickColumn()) {
			float quickX = quickColumnX;
			float quickY = slotsTop;
			addQuickButton(quickX, quickY + 0*(QUICK_BTN + 4),
					EquipmentNonEquipDict.PURE_DEWDROP_WATER, this::quickAddWater);
			addQuickButton(quickX, quickY + 1*(QUICK_BTN + 4),
					ConsumGoodsMaterialsMaterialsExtraDict.TINDER, this::quickAddFire);
			addQuickButton(quickX, quickY + 2*(QUICK_BTN + 4),
					EquipmentBagsDict.BANDOLIER, () -> openBag(PotionBandolier.class));
			addQuickButton(quickX, quickY + 3*(QUICK_BTN + 4),
					EquipmentBagsDict.POUCH, () -> openBag(VelvetPouch.class));
			addQuickButton(quickX, quickY + 4*(QUICK_BTN + 4),
					EquipmentBagsDict.SHOPPING_CART_0, () -> openBag(ShoppingCart.class));
		}

		synchronized (inputs) {
			for (int i = 0; i < activeInputCount; i++) {
				InputButton button = new InputButton();

				//窗口重开时保留已放的物品
				if (inputs[i] != null) {
					Item carried = inputs[i].item();
					if (carried != null) {
						button.item(carried);
					}
				}
				inputs[i] = button;
				button.item(button.item());

				float x;
				float y;
				if (singleColumn) {
					//单列布局：从上到下依次排，不分主材/辅材的位置语义
					x = slotsMainX;
					y = slotsY0 + i * (BTN_SIZE + 6);
				} else if (i == SLOT_MAIN) {
					x = slotsMainX;
					y = slotsY0;
				} else {
					int adj = i - SLOT_ADJUVANT;
					x = (adj % 2 == 0) ? slotsX0 : slotsX1;
					y = (adj / 2 == 0) ? slotsY1 : slotsY2;
				}

				inputs[i].setRect(x, y, BTN_SIZE, BTN_SIZE);
				add(inputs[i]);
			}
		}
		//窗口化后格数少于 5 时，清掉多余格的残留物品
		for (int i = activeInputCount; i < inputs.length; i++) {
			if (inputs[i] != null) {
				inputs[i] = null;
			}
		}

		pos = (int)(slotsLastY + BTN_SIZE + 8);

		Button invSelector = new Button(){
			@Override
			protected void onClick() {
						if (Dungeon.hero != null) {
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
										if (Dungeon.hero.belongings.lostInventory() && !i.keptThroughLostInventory()) items.remove(i);
										if (!Recipe.usableInRecipe(i)) items.remove(i);
									}

									if (items.size() == 0){
										ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(WndCrafting.class, "no_items")));
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
										info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "item_cancel");
									} else {
										info += Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "item_select") + "\n";
										info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "item_cancel");
									}

									Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "item_prompt"), info, itemNames, itemIcons){
										@Override
										public void onSelect(int idx, boolean alt) {
											super.onSelect(idx, alt);
											Item item = items.get(idx);
											synchronized (inputs) {
												if (item != null && inputs[0] != null) {
											for (int i = 0; i < activeInputCount; i++) {
														if (inputs[i].item() == null) {
															if (item instanceof LiquidMetal || item instanceof MissileWeapon){
																inputs[i].item(item.detachAll(Dungeon.hero.belongings.backpack));
															} else {
																inputs[i].item(item.detach(Dungeon.hero.belongings.backpack));
															}
															break;
														}
													}
													updateState();
												}
											}

										}
									});
								}
							});
						}
			}

			@Override
			public GameAction keyAction() {
				return SPDAction.INVENTORY_SELECTOR;
			}
		};
		add(invSelector);

		cancel = new IconButton(Icons.CLOSE.get()){
			@Override
			protected void onClick() {
				super.onClick();
				clearSlots();
				updateState();
			}

			@Override
			public GameAction keyAction() {
				return SPDAction.BACK;
			}

			@Override
			protected String hoverText() {
				return Messages.get(WndCrafting.class, "cancel");
			}
		};
		//SPSEXPD: 取消/重复按钮挪到窗口左下角（窗口制下底部要给能量条与指南留位）
		cancel.setRect(6, h - 22, 16, 16);
		cancel.enable(false);
		add(cancel);

		repeat = new IconButton(Icons.REPEAT.get()){
			@Override
			protected void onClick() {
				super.onClick();
				if (lastRecipe != null){
					populate(lastIngredients, Dungeon.hero.belongings);
				}
			}

			@Override
			public GameAction keyAction() {
				return SPDAction.TAG_RESUME;
			}

			@Override
			protected String hoverText() {
				return Messages.get(WndCrafting.class, "repeat");
			}
		};
		repeat.setRect(26, h - 22, 16, 16);
		repeat.enable(false);
		add(repeat);

		//SPSEXPD: 原来的「废料重铸」按钮已随垃圾物品一并移除

		lastIngredients.clear();
		lastRecipe = null;

		for (int i = 0; i < combines.length; i++){
			combines[i] = new CombineButton(i);
			combines[i].enable(false);

			outputs[i] = new OutputSlot();
			outputs[i].item(null);

			if (i == 0){
				//SPSEXPD: 合成按钮 + 输出槽在材料区右侧，纵向居中
				combines[i].setRect(slotsX1 + BTN_SIZE + 8, slotsMiddleY + 5, 30, BTN_SIZE - 10);
				outputs[i].setRect(combines[i].left() + 34, slotsMiddleY, BTN_SIZE, BTN_SIZE);
			} else {
				combines[i].visible = false;
				outputs[i].visible = false;
			}

			add(combines[i]);
			add(outputs[i]);
		}

		smokeEmitter = new Emitter();
		smokeEmitter.pos(outputs[0].left() + (BTN_SIZE-16)/2f, outputs[0].top() + (BTN_SIZE-16)/2f, 16, 16);
		smokeEmitter.autoKill = false;
		add(smokeEmitter);
		
		pos += 10;

		String energyText = Messages.get(WndCrafting.class, "energy") + " " + Dungeon.energy;
		if (toolkit != null){
			energyText += "+" + toolkit.availableEnergy();
		}

		energyLeft = PixelScene.renderTextBlock(energyText, 9);
		energyLeft.setPos(
				centerW - energyLeft.width()/2,
				h - 18
		);
		energyLeft.hardlight(0x44CCFF);
		add(energyLeft);

		energyIcon = new ItemSprite( toolkit != null ? SpecificPlaceHolderDict.SOMETHING_0 : SpecificCurrencyDict.ENERGY_0);
		energyIcon.x = energyLeft.left() - energyIcon.width();
		energyIcon.y = energyLeft.top() - (energyIcon.height() - energyLeft.height())/2;
		PixelScene.align(energyIcon);
		add(energyIcon);

		energyAdd = new IconButton(Icons.get(Icons.PLUS)){

			private float time = 0;

			@Override
			public void update() {
				super.update();
				if (energyAddBlinking){
					icon.brightness( 0.5f + (float)Math.abs(Math.cos( StatusPane.FLASH_RATE * (time += Game.elapsed) )));
				} else {
					if (time > 0){
						icon.resetColor();
					}
					time = 0;
				}
			}

			@Override
			protected void onClick() {
				WndEnergizeItem.openItemSelector();
			}

			@Override
			public GameAction keyAction() {
				return SPDAction.TAG_ACTION;
			}

			@Override
			protected String hoverText() {
				return Messages.get(WndCrafting.class, "energize");
			}
		};
		energyAdd.setRect(energyLeft.right(), energyLeft.top() - (16 - energyLeft.height())/2, 16, 16);
		PixelScene.align(energyAdd);
		add(energyAdd);

		StyledButton btnGuide = new StyledButton( Chrome.Type.TOAST_TR, Messages.get(WndCrafting.class, "guide")){
			@Override
			protected void onClick() {
				super.onClick();
				clearSlots();
				updateState();
				Game.scene().addToFront(new Window() {

					{
						WndJournal.AlchemyTab t = new WndJournal.AlchemyTab();
						int w, h;
						if (PixelScene.landscape()) {
							w = WndJournal.WIDTH_L;
							h = WndJournal.HEIGHT_L+8;
						} else {
							w = WndJournal.WIDTH_P;
							h = WndJournal.HEIGHT_P+10;
						}
						resize(w, h);
						add(t);
						t.setRect(0, 0, w, h);
					}

				});
			}

			@Override
			public GameAction keyAction() {
				return SPDAction.JOURNAL;
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Document.ALCHEMY_GUIDE.title());
			}
		};
		if (hasGuideButton()) {
			btnGuide.icon(new ItemSprite(SpecificPagesDict.ALCH_PAGE_0));
			btnGuide.setSize(btnGuide.reqWidth()+4, 18);
			btnGuide.setPos(w - btnGuide.width() - 6, h - 24);
			PixelScene.align(btnGuide);
			add(btnGuide);
		}

		TrinketCatalyst cata = Dungeon.hero.belongings.getItem(TrinketCatalyst.class);
		if (cata != null && cata.hasRolledTrinkets()){
			Game.scene().addToFront(new TrinketCatalyst.WndTrinket(cata));
		}

		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}
	}
	
	@Override
	public void onBackPressed() {
		resetMode();
		instance = null;
		hide();
	}
	
	protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(WndCrafting.class, "select");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return Recipe.usableInRecipe(item);
		}

		@Override
		public void onSelect( Item item ) {
			synchronized (inputs) {
				if (item != null && inputs[0] != null) {
					for (int i = 0; i < activeInputCount; i++) {
						//SPSEXPD: 格子可能已被窗口销毁置空，跳过
						if (inputs[i] == null) continue;
						if (inputs[i].item() == null) {
							inputs[i].item(takeFromCarried(item));
							break;
						}
					}
					updateState();
				}
			}
		}
	};

	/**
	 * SPSEXPD: 把物品从它实际所在的容器（主背包或任意包裹）里取出。
	 * 原来只按主背包 detach，从包裹里点物品会取不出来。
	 */
	private static Item takeFromCarried(Item item) {
		if (item == null || Dungeon.hero == null || Dungeon.hero.belongings == null) return null;
		boolean all = item instanceof LiquidMetal || item instanceof MissileWeapon;
		Item taken = all
				? item.detachAll(Dungeon.hero.belongings.backpack)
				: item.detach(Dungeon.hero.belongings.backpack);
		if (taken != null) return taken;
		for (Bag bag : Dungeon.hero.belongings.getBags()) {
			taken = all ? item.detachAll(bag) : item.detach(bag);
			if (taken != null) return taken;
		}
		return null;
	}
	
	private<T extends Item> ArrayList<T> filterInput(Class<? extends T> itemClass){
		ArrayList<T> filtered = new ArrayList<>();
		for (int i = 0; i < activeInputCount; i++){
			//SPSEXPD: 空格子（含窗口销毁后置空的）直接跳过
			if (inputs[i] == null) continue;
			Item item = inputs[i].item();
			if (item != null && itemClass.isInstance(item)){
				filtered.add((T)item);
			}
		}
		return filtered;
	}

	/** SPSEXPD: 把 5 个格子的真实槽位交给配方引擎（主材 + 辅材×4）。
	 * 槽位语义固定：主材格里是什么就是主材，辅材格不会自动顶替主材；
	 * 水与火种没有专用格，由 BrewRecipe 从辅材里识别（hasWater/hasFire）。 */
	private BrewRecipe.Slots currentSlots(){
		BrewRecipe.Slots slots = new BrewRecipe.Slots();
		slots.main = inputs[SLOT_MAIN] == null ? null : inputs[SLOT_MAIN].item();
		for (int i = 0; i < BrewRecipe.ADJUVANT_SLOTS; i++){
			slots.adjuvant[i] = inputs[SLOT_ADJUVANT + i] == null ? null : inputs[SLOT_ADJUVANT + i].item();
		}
		return slots;
	}
	
	/** SPSEXPD: 按当前加工模式过滤可用公式——炼金釜 / 蒸馏压片机 / 分装灌液机 / 平底锅 / 菜板 各用各的公式集。 */
	private static ArrayList<Recipe> filterByMode(ArrayList<Recipe> recipes, ArrayList<Item> ingredients) {
		ArrayList<Recipe> out = new ArrayList<>();
		//SPSEXPD: 食物配方按工具分流（含水的归分装灌液机、需加热的归平底锅、无水冷食归菜板）
		int foodKind = SpsFoodPrepRecipes.kindForMode(spsMode);
		ArrayList<Recipe> foodRecipes = foodKind >= 0
				? SpsFoodPrepRecipes.forKind(foodKind)
				: new ArrayList<>();

		for (Recipe recipe : recipes) {
			boolean food = SpsFoodPrepRecipes.isFoodPrep(recipe);
			switch (spsMode) {
				case MODE_DISTILL:
					if (recipe instanceof SpsBrewRecipes.DistillBrew) out.add(recipe);
					break;
				case MODE_FILL:
					if (recipe instanceof SpsBrewRecipes.FillBrew) out.add(recipe);
					break;
				default:
					//炼金釜只保留药剂工艺与加工产物，食物一律交给新工具
					if (!(recipe instanceof SpsBrewRecipes.DistillBrew)
							&& !(recipe instanceof SpsBrewRecipes.FillBrew)
							&& !food) {
						out.add(recipe);
					}
					break;
			}
		}

		//食物配方已从炼金釜表移出，这里按当前材料单独匹配
		for (Recipe recipe : foodRecipes) {
			if (recipe.testIngredients(ingredients) && !out.contains(recipe)) {
				out.add(recipe);
			}
		}
		return out;
	}

	//SPSEXPD: 原来的「废料重铸」（wasteCount/reforgeWaste）已随垃圾物品一并移除

	private void updateState(){

		repeat.enable(false);

		ArrayList<Item> ingredients = filterInput(Item.class);
		ArrayList<Recipe> recipes = filterByMode(Recipe.findRecipes(ingredients, currentSlots()), ingredients);

		//disables / hides unneeded buttons
		for (int i = recipes.size(); i < combines.length; i++){
			combines[i].enable(false);
			outputs[i].item(null);

			if (i != 0){
				combines[i].visible = false;
				outputs[i].visible = false;
			}
		}

		cancel.enable(!ingredients.isEmpty());

		if (recipes.isEmpty()){
			combines[0].setPos(combines[0].left(), slotsMiddleY + 5);
			outputs[0].setPos(outputs[0].left(), slotsMiddleY);
			energyAddBlinking = false;
			return;
		}

		//SPSEXPD: 右侧纵向堆叠——多个候选配方时在材料区高度内居中排开
		float gap = recipes.size() == 2 ? 6 : 2;

		float height = slotsBottom - slotsTop;
		height -= recipes.size()*BTN_SIZE + (recipes.size()-1)*gap;
		float top = slotsTop + height/2;

		//positions and enables active buttons
		boolean promptToAddEnergy = false;
		for (int i = 0; i < recipes.size(); i++){

			Recipe recipe = recipes.get(i);

			int cost = recipe.cost(ingredients);

			outputs[i].visible = true;
			outputs[i].setRect(outputs[0].left(), top, BTN_SIZE, BTN_SIZE);
			outputs[i].item(recipe.sampleOutput(ingredients));
			top += BTN_SIZE+gap;

			int availableEnergy = Dungeon.energy;
			if (toolkit != null){
				availableEnergy += toolkit.availableEnergy();
			}

			combines[i].visible = true;
			combines[i].setRect(combines[0].left(), outputs[i].top()+5, 30, BTN_SIZE - 10);
			//SPSEXPD: 全部公式 100% 成功，合成按钮只需判断能量（平底锅另需 1 个火种）是否够
			boolean fuelOk = spsMode != MODE_COOK
					|| Dungeon.hero.belongings.getItem(pd.items.consum.materials.Tinder.class) != null;
			combines[i].enable(cost <= availableEnergy && fuelOk, cost);

			if (cost > availableEnergy && recipe instanceof TrinketCatalyst.Recipe){
				promptToAddEnergy = true;
			}

		}

		energyAddBlinking = promptToAddEnergy;

	}
	
	private void combine( int slot ){
		
		ArrayList<Item> ingredients = filterInput(Item.class);
		if (ingredients.isEmpty()) return;

		lastIngredients.clear();
		for (Item i : ingredients){
			lastIngredients.add(i.duplicate());
		}

		ArrayList<Recipe> recipes = filterByMode(Recipe.findRecipes(ingredients, currentSlots()), ingredients);
		if (recipes.size() <= slot) return;

		Recipe recipe = recipes.get(slot);
		
		Item result = null;
		ArrayList<Item> extras = new ArrayList<>();
		
		if (recipe != null){
			//SPSEXPD: 平底锅做菜要生火——每做一道菜消耗 1 个火种
			if (spsMode == MODE_COOK && !pd.items.equipment.weapon.melee.special.MeleePan.consumeTinder(Dungeon.hero)) {
				return;
			}
			int cost = recipe.cost(ingredients);
			if (toolkit != null){
				cost = toolkit.consumeEnergy(cost);
			}
			Catalog.countUses(EnergyCrystal.class, cost);
			Dungeon.energy -= cost;

			String energyText = Messages.get(WndCrafting.class, "energy") + " " + Dungeon.energy;
			if (toolkit != null){
				energyText += "+" + toolkit.availableEnergy();
			}
			energyLeft.text(energyText);
			energyLeft.setPos(
					centerW - energyLeft.width()/2,
					energyLeft.top()
			);

			energyIcon.x = energyLeft.left() - energyIcon.width();
			PixelScene.align(energyIcon);

			energyAdd.setPos(energyLeft.right(), energyAdd.top());
			PixelScene.align(energyAdd);
			
			result = recipe.brew(ingredients);
			//SPSEXPD: 一次合成的额外产物（例如提炼的良性 + 恶性各一瓶）
			if (recipe instanceof BrewRecipe){
				extras = ((BrewRecipe) recipe).takeExtraOutputs();
			}
		}
		
		if (result != null){

			for (Item extra : extras){
				craftItem(ingredients, extra);
			}
			craftItem(ingredients, result);

		}

		boolean foundItems = true;
		for (Item i : lastIngredients){
			Item found = Dungeon.hero.belongings.getSimilar(i);
			if (found == null){ //atm no quantity check as items are always loaded individually
				//currently found can be true if we need, say, 3x of an item but only have 2x of it
				foundItems = false;
			}
		}

		lastRecipe = recipe;
		repeat.enable(foundItems);

		cancel.enable(false);
		synchronized (inputs) {
			for (int i = 0; i < activeInputCount; i++) {
				if (inputs[i] != null && inputs[i].item() != null) {
					cancel.enable(true);
					break;
				}
			}
		}
	}

	public void craftItem( ArrayList<Item> ingredients, Item result ){
		smokeEmitter.burst(Speck.factory( Speck.WOOL ), 10 );
		Sample.INSTANCE.play( Assets.Sounds.PUFF );

		int resultQuantity = result.quantity();
		if (!result.collect()){
			Dungeon.level.drop(result, Dungeon.hero.pos);
		}

		Statistics.itemsCrafted++;
		Badges.validateItemsCrafted();

		saveNeeded = false;
		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}

		synchronized (inputs) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] != null && inputs[i].item() != null) {
					Item item = inputs[i].item();
					if (item.quantity() <= 0) {
						inputs[i].item(null);
					} else {
						inputs[i].slot.updateText();
					}
				}
			}
		}

		updateState();
		//we reset the quantity in case the result was merged into another stack in the backpack
		result.quantity(resultQuantity);
		outputs[0].item(result);
	}
	
	public void populate(ArrayList<Item> toFind, Belongings inventory){
		clearSlots();
		
		int curslot = 0;
		for (Item finding : toFind){
			int needed = finding.quantity();
			ArrayList<Item> found = inventory.getAllSimilar(finding);
			while (!found.isEmpty() && needed > 0 && curslot < activeInputCount){
				Item detached;
				if (finding instanceof LiquidMetal || finding instanceof MissileWeapon) {
					detached = found.get(0).detachAll(inventory.backpack);
				} else {
					detached = found.get(0).detach(inventory.backpack);
				}
				inputs[curslot].item(detached);
				curslot++;
				needed -= detached.quantity();
				if (detached == found.get(0)) {
					found.remove(0);
				}
			}
		}
		updateState();
	}

	private boolean saveNeeded = false;

	/** SPSEXPD: 窗口没有 onPause 钩子，改为在 Game 暂停时由 saveNeeded 标记兜底。 */
	public void onGamePause() {
		if (saveNeeded) {
			saveNeeded = false;
			clearSlots();
			updateState();
			try {
				Dungeon.saveAll();
				Badges.saveGlobal();
				Journal.saveGlobal();
			} catch (IOException e) {
				ShatteredPixelDungeon.reportException(e);
			}
		}
	}

	@Override
	public void destroy() {
		if (instance == this) instance = null;
		synchronized ( inputs ) {
			clearSlots();
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = null;
			}
		}

		saveNeeded = false;
		try {
			Dungeon.saveAll();
			Badges.saveGlobal();
			Journal.saveGlobal();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}
		super.destroy();
	}
	
	public void clearSlots(){
		synchronized ( inputs ) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] != null && inputs[i].item() != null) {
					Item item = inputs[i].item();
					if (!item.collect()) {
						Dungeon.level.drop(item, Dungeon.hero.pos);
					}
					inputs[i].item(null);
				}
			}
		}
		cancel.enable(false);
		repeat.enable(lastRecipe != null);
	}

	public void createEnergy(){
		String energyText = Messages.get(WndCrafting.class, "energy") + " " + Dungeon.energy;
		if (toolkit != null){
			energyText += "+" + toolkit.availableEnergy();
		}
		energyLeft.text(energyText);
		energyLeft.setPos(
				centerW - energyLeft.width()/2,
				energyLeft.top()
		);

		energyIcon.x = energyLeft.left() - energyIcon.width();
		PixelScene.align(energyIcon);

		energyAdd.setPos(energyLeft.right(), energyAdd.top());
		PixelScene.align(energyAdd);

		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );

		//queue a save here, as items may be in the input windows and we don't want to clear them
		// but if the game becomes paused we do this to prevent exploits
		saveNeeded = true;
		updateState();
	}

	public void showIdentify(Item item){
		if (item.isIdentified()) return;

		NinePatch BG = Chrome.get(Chrome.Type.TOAST);

		IconTitle oldName = new IconTitle(item){
			@Override
			public synchronized void update() {
				super.update();
				alpha(this.alpha()-Game.elapsed);
				if (this.alpha() <= 0){
					killAndErase();
				}
			}
		};
		item.identify();
		IconTitle newName = new IconTitle(item){

			boolean fading;

			@Override
			public synchronized void update() {
				super.update();
				if (!fading) {
					alpha(this.alpha() + Game.elapsed);
					if (this.alpha() >= 1) {
						fading = true;
					}
				} else {
					alpha(this.alpha() - Game.elapsed);
					BG.alpha(this.alpha());
					if (this.alpha() <= 0){
						killAndErase();
						BG.killAndErase();
					}
				}
			}
		};
		newName.alpha(-0.5f);

		oldName.setSize(200, oldName.height());
		newName.setSize(200, newName.height());

		int w = (int)Math.ceil(Math.max(oldName.reqWidth(), newName.reqWidth())+5);

		oldName.setSize(w, oldName.height());
		oldName.setPos(
				centerW - oldName.width()/2,
				energyAdd.top()
		);
		PixelScene.align(oldName);

		newName.setSize(w, oldName.height());
		newName.setPos(
				centerW - newName.width()/2,
				energyAdd.top()
		);
		PixelScene.align(newName);

		BG.x = oldName.left()-2;
		BG.y = oldName.top()-2;
		BG.size(oldName.width()+4, oldName.height()+4);

		add(BG);
		add(oldName);
		add(newName);

	}
	
	private class InputButton extends Component {
		
		protected NinePatch bg;
		protected ItemSlot slot;
		
		private Item item = null;
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			bg = Chrome.get(chromeType());
			add( bg );
			
			slot = new ItemSlot() {
				@Override
				protected void onPointerDown() {
					bg.brightness( 1.2f );
					Sample.INSTANCE.play( Assets.Sounds.CLICK );
				}
				@Override
				protected void onPointerUp() {
					bg.resetColor();
				}
				@Override
				protected void onClick() {
					super.onClick();
					Item item = InputButton.this.item;
					if (item != null) {
						if (!item.collect()) {
							Dungeon.level.drop(item, Dungeon.hero.pos);
						}
						InputButton.this.item(null);
						updateState();
					}
					WndCrafting.this.addToFront(WndBag.getBag( itemSelector ));
				}

				@Override
				protected boolean onLongClick() {
					Item item = InputButton.this.item;
					if (item != null){
						WndCrafting.this.addToFront(new WndInfoItem(item));
						return true;
					}
					return false;
				}

				@Override
				//only the first empty button accepts key input
				public GameAction keyAction() {
					for (InputButton i : inputs){
						if (i.item == null || i.item instanceof WndBag.Placeholder) {
							if (i == InputButton.this) {
								return SPDAction.INVENTORY;
							} else {
								return super.keyAction();
							}
						}
					}
					return super.keyAction();
				}

				@Override
				protected String hoverText() {
					if (item == null || item instanceof WndBag.Placeholder){
						return emptyHoverText();
					}
					return super.hoverText();
				}

				@Override
				public GameAction secondaryTooltipAction() {
					return SPDAction.INVENTORY_SELECTOR;
				}
			};
			slot.enable(true);
			add( slot );
		}

		@Override
		protected void layout() {
			super.layout();
			
			bg.x = x;
			bg.y = y;
			bg.size( width, height );
			
			slot.setRect( x + 2, y + 2, width - 4, height - 4 );
		}

		public Item item(){
			return item;
		}

		public void item( Item item ) {
			if (item == null){
				this.item = null;
				slot.item(new WndBag.Placeholder(placeholderImage()));
			} else {
				slot.item(this.item = item);
			}
		}

		/** SPSEXPD: 空槽占位图——水格/火源格用各自的图标区分语义。 */
		protected IconEntry placeholderImage(){
			return SpecificPlaceHolderDict.SOMETHING_0;
		}

		/** SPSEXPD: 底框类型——水格/火源格用灰色框，与红色材料格区分。 */
		protected Chrome.Type chromeType(){
			return Chrome.Type.RED_BUTTON;
		}

		/** SPSEXPD: 空槽的悬浮提示文案。 */
		protected String emptyHoverText(){
			return Messages.get(WndCrafting.class, "add");
		}
	}

	//SPSEXPD: 快速添加按钮——图标按钮 + 点击动作
	private void addQuickButton(float x, float y, IconEntry icon, Runnable action) {
		IconButton button = new IconButton(new ItemSprite(icon)) {
			@Override
			protected void onClick() {
				icon().resetColor();
				action.run();
			}
		};
		button.setRect(x, y, QUICK_BTN, QUICK_BTN);
		add(button);
	}

	/** 在当前携带的物品（主背包 + 各包裹）里找第一件指定类型的物品。 */
	private Item findCarried(Class<? extends Item> type) {
		if (Dungeon.hero == null) return null;
		for (Item item : Dungeon.hero.belongings.backpack.items) {
			if (type.isInstance(item)) return item;
			if (item instanceof Bag) {
				for (Item in : ((Bag)item).items) {
					if (type.isInstance(in)) return in;
				}
			}
		}
		return null;
	}

	/** 第一个空闲的辅材槽，没有则 -1。 */
	private int firstEmptyAdjuvant() {
		for (int i = SLOT_ADJUVANT; i < activeInputCount; i++) {
			if (inputs[i] != null && inputs[i].item() == null) return i;
		}
		return -1;
	}

	/** 快速加料：把携带的指定材料放进第一个空闲辅材格（绝不进主材格）。 */
	private void quickAdd(Class<? extends Item> type) {
		if (Dungeon.hero == null) return;
		int slot = firstEmptyAdjuvant();
		if (slot < 0) {
			GLog.w(Messages.get(WndCrafting.class, "no_space"));
			return;
		}
		Item found = findCarried(type);
		if (found == null) {
			GLog.w(Messages.get(WndCrafting.class, "no_material"));
			return;
		}
		Item taken = found.detach(Dungeon.hero.belongings.backpack);
		if (taken == null) {
			GLog.w(Messages.get(WndCrafting.class, "no_material"));
			return;
		}
		inputs[slot].item(taken);
		updateState();
	}

	private void quickAddWater() {
		quickAdd(WaterItem.class);
	}

	private void quickAddFire() {
		if (Dungeon.hero == null) return;
		//火种优先，其次磷火
		quickAdd(findCarried(Tinder.class) != null ? Tinder.class : WillOWisp.class);
	}

	/** 快捷打开指定包裹（没有该包裹则提示）。 */
	private void openBag(Class<? extends Bag> type) {
		if (Dungeon.hero == null) return;
		Bag bag = Dungeon.hero.belongings.getItem(type);
		if (bag == null) {
			GLog.w(Messages.get(WndCrafting.class, "no_bag"));
			return;
		}
		GameScene.show(new WndBag(bag, itemSelector));
	}

	private class CombineButton extends Component {

		protected int slot;

		protected RedButton button;
		protected RenderedTextBlock costText;

		private CombineButton(int slot){
			super();

			this.slot = slot;
		}

		@Override
		protected void createChildren() {
			super.createChildren();

			button = new RedButton(""){
				@Override
				protected void onClick() {
					super.onClick();
					combine(slot);
				}

				@Override
				protected String hoverText() {
					return Messages.get(WndCrafting.class, "craft");
				}

				@Override
				public GameAction keyAction() {
					if (slot == 0 && !combines[1].active && !combines[2].active){
						return SPDAction.TAG_LOOT;
					}
					return super.keyAction();
				}
			};
			button.icon(Icons.get(Icons.ARROW));
			add(button);

			costText = PixelScene.renderTextBlock(6);
			add(costText);
		}

		@Override
		protected void layout() {
			super.layout();

			button.setRect(x, y, width(), height());

			costText.setPos(
					left() + (width() - costText.width())/2,
					top() - costText.height()
			);
		}

		public void enable( boolean enabled ){
			enable(enabled, 0);
		}

		public void enable( boolean enabled, int cost ){
			button.enable(enabled);
			if (enabled) {
				button.icon().tint(1, 1, 0, 1);
				button.alpha(1f);
				costText.hardlight(0x44CCFF);
			} else {
				button.icon().color(0, 0, 0);
				button.alpha(0.6f);
				costText.hardlight(0xFF0000);
			}

			if (cost > 0){
				costText.visible = true;
				costText.text(Messages.get(WndCrafting.class, "energy") + " " + cost);
			} else {
				costText.visible = false;
			}

			layout();
			active = enabled;
		}

	}

	private class OutputSlot extends Component {

		protected NinePatch bg;
		protected ItemSlot slot;

		@Override
		protected void createChildren() {

			bg = Chrome.get(Chrome.Type.TOAST_TR);
			add(bg);

			slot = new ItemSlot() {
				@Override
				protected void onClick() {
					super.onClick();
					if (visible && item != null && item.trueName() != null){
						WndCrafting.this.addToFront(new WndInfoItem(item));
					}
				}
			};
			slot.item(null);
			add( slot );
		}

		@Override
		protected void layout() {
			super.layout();

			bg.x = x;
			bg.y = y;
			bg.size(width(), height());

			slot.setRect(x+2, y+2, width()-4, height()-4);
		}

		public void item( Item item ) {
			slot.item(item);
		}
	}

	private static AlchemistsToolkit toolkit;

	private static AlchemistsToolkit activeToolkit() {
		if (toolkit != null) return toolkit;
		if (Dungeon.hero == null) return null;
		AlchemistsToolkit equipped = Dungeon.hero.belongings.getItem(AlchemistsToolkit.class);
		return equipped != null && equipped.isEquipped(Dungeon.hero) ? equipped : null;
	}

	public static int spsInputCapacity(AlchemistsToolkit toolkit) {
		//SPSEXPD: 炼金釜固定 5 格——1 主材 / 4 辅材；水与火种作为辅材之一（docs/alchemy-rework.md §1.2）
		return 5;
	}

	public static void assignToolkit( AlchemistsToolkit toolkit ){
		WndCrafting.toolkit = toolkit;
	}

	public static void clearToolkit(){
		WndCrafting.toolkit = null;
	}

}
