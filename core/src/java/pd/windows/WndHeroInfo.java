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

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.ConsumThrowsDict;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.Assets;
import pd.Badges;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.abilities.ArmorAbility;
import pd.actors.hero.perks.Perk;
import pd.actors.hero.perks.PerkGrants;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.ItemSlotButton;
import pd.ui.PerkSlot;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import render.noosa.ColorBlock;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.ui.Component;
import render.utils.platform.DeviceCompat;

import java.util.ArrayList;
import pd.messages.InlineText;

public class WndHeroInfo extends WndTabbed {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndHeroInfo.class)
			.t("perks", "特质")
			.t("perks_msg", "特质是英雄开局拥有或途中获得的被动能力：初始特质开局即有；职业专属特质需要达到对应等级或满足条件才会获得。")
			.t("perks_initial", "初始特质")
			.t("perks_exclusive", "职业专属")
			.t("perks_none", "（无）")
			.t("perks_cond_class_level", "达到 %d 级时获得")
			.t("items_title", "初始物品")
			.t("perks_group_equip", "装备套装")
			.t("perks_group_special", "特殊装备")
			.t("perks_group_bag", "包裹袋")
			.t("perks_group_food", "食品")
			.t("perks_group_potion", "药水")
			.t("perks_group_scroll", "卷轴")
			.t("perks_group_wand", "法杖")
			.t("perks_group_missile", "投掷与弹药")
			.t("perks_group_bomb", "炸弹")
			.t("perks_group_other", "其它")
			.t("items_none", "（无）")
			.t("subclasses", "专精")
			.t("subclasses_msg", "击杀第二个Boss后可以选择一种职业专精。")
			.t("abilities", "护甲技能")
			.t("abilities_msg", "击杀第四个Boss后可以选择一项护甲技能。");
	}




	private HeroInfoTab heroInfo;
	private PerkInfoTab perkInfo;
	private SubclassInfoTab subclassInfo;
	private ArmorAbilityInfoTab abilityInfo;

	private static int WIDTH = 120;
	private static int MIN_HEIGHT = 125;
	private static int MARGIN = 2;

	//SPSEXPD: 当前主题（选角界面传 GamesInProgress.selectedSkin，游戏内传 Dungeon.hero.skin）
	private final int themeSkin;

	public WndHeroInfo( HeroClass cl ){
		this(cl, 0);
	}

	public WndHeroInfo( HeroClass cl, int skin ){
		this.themeSkin = skin;

		Image tabIcon;
		switch (cl){
			case WARRIOR: default:
				tabIcon = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0, null);
				break;
			case MAGE:
				tabIcon = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0, null);
				break;
			case ROGUE:
				tabIcon = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0, null);
				break;
			case HUNTRESS:
				tabIcon = new ItemSprite(EquipmentEquipWeaponUniqueWeaponDict.SPIRIT_BOW_0, null);
				break;
			case DUELIST:
				tabIcon = new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.RAPIER_0, null);
				break;
			case CLERIC:
				tabIcon = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0, null);
				break;
			case SPELLSWORD:
				tabIcon = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0, null);
				break;
			case PERFORMER:
				tabIcon = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0, null);
				break;
			case SOLDIER:
				tabIcon = new ItemSprite(ConsumThrowsDict.THROWING_SPIKE_0, null);
				break;
			case FOLLOWER:
				tabIcon = new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0, null);
				break;
			case ASCETIC:
				tabIcon = new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.GLOVES, null);
				break;
		}

		int finalHeight = MIN_HEIGHT;

		heroInfo = new HeroInfoTab(cl, themeSkin);
		add(heroInfo);
		heroInfo.setSize(WIDTH, MIN_HEIGHT);
		finalHeight = (int)Math.max(finalHeight, heroInfo.height());

		add( new IconTab( tabIcon ){
			@Override
			protected void select(boolean value) {
				super.select(value);
				heroInfo.visible = heroInfo.active = value;
			}
		});

		perkInfo = new PerkInfoTab(cl);
		add(perkInfo);
		perkInfo.setSize(WIDTH, MIN_HEIGHT);
		finalHeight = (int)Math.max(finalHeight, perkInfo.height());

		//SPSEXPD: 图标与局内快捷栏的「特质加点」按钮一致
		add( new IconTab( new Image(Assets.Interfaces.SPECIFIC_POINT) ){
			@Override
			protected void select(boolean value) {
				super.select(value);
				perkInfo.visible = perkInfo.active = value;
			}
		});

		if (Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_2) || DeviceCompat.isDebug()) {
			subclassInfo = new SubclassInfoTab(cl);
			add(subclassInfo);
			subclassInfo.setSize(WIDTH, MIN_HEIGHT);
			finalHeight = (int)Math.max(finalHeight, subclassInfo.height());

			add(new IconTab(new ItemSprite(ConsumUsefulProcessEnhanceDict.MASK_0, null)) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					subclassInfo.visible = subclassInfo.active = value;
				}
			});
		}

		if (Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_4) || DeviceCompat.isDebug()) {
			abilityInfo = new ArmorAbilityInfoTab(cl);
			add(abilityInfo);
			abilityInfo.setSize(WIDTH, MIN_HEIGHT);
			finalHeight = (int)Math.max(finalHeight, abilityInfo.height());

			add(new IconTab(new ItemSprite(ConsumUsefulProcessEnhanceDict.CROWN_0, null)) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					abilityInfo.visible = abilityInfo.active = value;
				}
			});
		}

		resize(WIDTH, finalHeight);

		layoutTabs();
		//SPSEXPD: 构造期 setSize() 已经触发过一次首页 layout()，那时窗口还没 resize、
		//ScrollPane 会按初始相机定位（内容整块偏下）；resize 之后再排一次拿正确的相机。
		heroInfo.layout();
		perkInfo.layout();

		select(0);

	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		//SPSEXPD: 窗口整体位移后，ScrollPane 的内部相机要跟着重排（否则内容偏位）
		heroInfo.layout();
		perkInfo.layout();
	}

	private static class HeroInfoTab extends Component {

		private static final int GAP = 2;
		private static final int COLS = 6;
		private static final int CELL = ItemSlotButton.BTN + GAP;
		/** SPSEXPD: 清单区最大高度（超出由 ScrollPane 滚动） */
		private static final int MAX_LIST_H = 120;
		/** SPSEXPD: 分组之间的分隔线颜色（半透明白，深色底上可见） */
		private static final int DIVIDER_COLOR = 0x33FFFFFF;

		private RenderedTextBlock title;
		private ScrollPane pane;
		private ArrayList<Group> groups;
		private ArrayList<ColorBlock> dividers;

		/** 一个分组：小标题 + 该组物品的格子 */
		private static class Group {
			RenderedTextBlock label;
			ArrayList<ItemSlotButton> buttons = new ArrayList<>();
		}

		/**
		 * SPSEXPD: 第一栏 = 开局携带的物品，按用途分组、以图标网格展示（含数量角标，
		 * 点击图标看物品详情），跟随当前主题。数据来自
		 * HeroClass.baseStartingItems() + startingItems(cls, skin)（单一真源）。
		 */
		public HeroInfoTab(HeroClass cls, int skin){
			super();

			title = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "items_title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			groups = new ArrayList<>();
			dividers = new ArrayList<>();

			pane = new ScrollPane(new Component());
			pane.scrollBarVisible = false;
			add(pane);

			ArrayList<pd.items.Item> items = new ArrayList<>();
			items.addAll(HeroClass.baseStartingItems().preview());
			items.addAll(HeroClass.startingItems(cls, skin).preview());
			//SPSEXPD: 不做鉴定 —— 开局未鉴定的物品，在这里也如实保持未鉴定（点开详情能看到）

			//按用途分组：组序按枚举声明顺序，组内保持发放顺序
			for (HeroClass.ItemGroup g : HeroClass.ItemGroup.values()){
				Group group = new Group();
				for (pd.items.Item item : items){
					if (item == null || HeroClass.groupOf(item) != g) continue;
					ItemSlotButton btn = new ItemSlotButton(item);
					group.buttons.add(btn);
					pane.content().add(btn);
				}
				if (group.buttons.isEmpty()) continue;

				//SPSEXPD: 分组之间的分隔线（首组之前不画）
				if (!groups.isEmpty()){
					ColorBlock divider = new ColorBlock(1, 1, DIVIDER_COLOR);
					pane.content().add(divider);
					dividers.add(divider);
				}

				group.label = PixelScene.renderTextBlock(
						Messages.get(WndHeroInfo.class, "perks_group_" + g.name().toLowerCase()), 7);
				group.label.hardlight(TITLE_COLOR);
				pane.content().add(group.label);

				groups.add(group);
			}
		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);
			float top = title.bottom() + 3*MARGIN;

			//逐组排：小标题一行，下面是图标网格；组之间画一条分隔线
			float y = 1;
			int di = 0;
			for (Group g : groups){
				if (di < dividers.size()){
					ColorBlock divider = dividers.get(di++);
					divider.x = 0;
					divider.y = y;
					divider.size(width, 1);
					y += 4;
				}
				g.label.setPos(0, y);
				y = g.label.bottom() + 1;

				for (int i = 0; i < g.buttons.size(); i++){
					int r = i / COLS;
					int c = i % COLS;
					g.buttons.get(i).setRect(
							GAP + c * CELL,
							y + r * CELL,
							ItemSlotButton.BTN, ItemSlotButton.BTN);
				}
				int rows = Math.max(1, (g.buttons.size() + COLS - 1) / COLS);
				y += rows * CELL + 2;
			}

			float listH = Math.max(24, Math.min(y, MAX_LIST_H));
			pane.setRect(0, top, width, listH);
			pane.content().setSize(width, Math.max(listH, y));

			height = Math.max(height, top + listH);
		}
	}

	/**
	 * SPSEXPD: 特质页（取代原天赋页）—— 两栏展示：
	 * 「初始特质」= 该职业开局就有的特质；「职业专属」= 按职业/等级或条件授予的后续特质。
	 * 两种数据都来自 PerkGrants 的静态查询（只有 HeroClass，没有存档英雄）。
	 */
	private static class PerkInfoTab extends Component {

		private static final int GAP = 2;
		private static final int COLS = 5;
		/** SPSEXPD: 初始特质一般只有数个（不到 5 个），一行就够 */
		private static final int INIT_H = PerkSlot.BTN + 2*GAP;
		/** SPSEXPD: 职业专属栏高度 —— 条件文案可能占两行以上，超出由 ScrollPane 滚动 */
		private static final int PANE_H = 42;

		private RenderedTextBlock title;
		private RenderedTextBlock message;
		private RenderedTextBlock initialLabel;
		private RenderedTextBlock exclusiveLabel;
		private RenderedTextBlock initialNone;
		private RenderedTextBlock exclusiveNone;
		private ScrollPane initialPane;
		private ScrollPane exclusivePane;
		private ArrayList<PerkSlot> initialSlots;
		private ArrayList<PerkSlot> exclusiveSlots;
		private ArrayList<RenderedTextBlock> exclusiveConds;

		public PerkInfoTab( HeroClass cls ){
			super();

			title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "perks")), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "perks_msg"), 6);
			add(message);

			initialLabel = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "perks_initial")), 7);
			initialLabel.hardlight(TITLE_COLOR);
			add(initialLabel);

			exclusiveLabel = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "perks_exclusive")), 7);
			exclusiveLabel.hardlight(TITLE_COLOR);
			add(exclusiveLabel);

			initialSlots = new ArrayList<>();
			initialPane = new ScrollPane(new Component());
			initialPane.scrollBarVisible = false;
			add(initialPane);
			for (Perk p : PerkGrants.initialPerksFor(cls)){
				PerkSlot slot = new PerkSlot(p);
				initialSlots.add(slot);
				initialPane.content().add(slot);
			}

			exclusiveSlots = new ArrayList<>();
			exclusiveConds = new ArrayList<>();
			exclusivePane = new ScrollPane(new Component());
			exclusivePane.scrollBarVisible = false;
			add(exclusivePane);
			//SPSEXPD: 职业专属每行一个 —— 左半行是图标，后半行是获取条件（该行内可容纳多行文本）
			for (PerkGrants.Exclusive ex : PerkGrants.exclusivePerksFor(cls)){
				PerkSlot slot = new PerkSlot(ex.perk);
				exclusiveSlots.add(slot);
				exclusivePane.content().add(slot);

				RenderedTextBlock cond = PixelScene.renderTextBlock(
						Messages.get(WndHeroInfo.class, ex.conditionKey, ex.conditionArgs), 6);
				exclusiveConds.add(cond);
				exclusivePane.content().add(cond);
			}

			initialNone = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "perks_none"), 6);
			initialNone.visible = initialSlots.isEmpty();
			add(initialNone);

			exclusiveNone = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "perks_none"), 6);
			exclusiveNone.visible = exclusiveSlots.isEmpty();
			add(exclusiveNone);
		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);
			message.maxWidth((int)width);
			message.setPos(0, title.bottom()+4*MARGIN);

			float pos = message.bottom() + 3*MARGIN;

			initialLabel.setPos(0, pos);
			pos = initialLabel.bottom() + 1;
			initialPane.setRect(0, pos, width, INIT_H);
			initialNone.setPos(1, pos + 1);
			layoutSlots(initialSlots, initialPane);

			pos = initialPane.bottom() + 3*MARGIN;
			exclusiveLabel.setPos(0, pos);
			pos = exclusiveLabel.bottom() + 1;
			exclusivePane.setRect(0, pos, width, PANE_H);
			exclusiveNone.setPos(1, pos + 1);
			layoutExclusive(exclusivePane);

			height = Math.max(height, exclusivePane.bottom());
		}

		private void layoutSlots(ArrayList<PerkSlot> slots, ScrollPane pane){
			for (int i = 0; i < slots.size(); i++){
				int r = i / COLS;
				int c = i % COLS;
				slots.get(i).setRect(
						GAP + c * (PerkSlot.BTN + GAP),
						GAP + r * (PerkSlot.BTN + GAP),
						PerkSlot.BTN, PerkSlot.BTN);
			}
			int rows = Math.max(1, (slots.size() + COLS - 1) / COLS);
			pane.content().setSize(width, Math.max(pane.height(), GAP + rows * (PerkSlot.BTN + GAP)));
		}

		/** SPSEXPD: 职业专属栏 —— 每行一个特质：左半行图标，后半行是获取条件（可多行，行高自适应） */
		private void layoutExclusive(ScrollPane pane){
			float y = GAP;
			int textW = Math.max(20, (int)width - PerkSlot.BTN - 3*GAP);

			for (int i = 0; i < exclusiveSlots.size(); i++){
				PerkSlot slot = exclusiveSlots.get(i);
				RenderedTextBlock cond = exclusiveConds.get(i);
				cond.maxWidth(textW);

				float rowH = Math.max(PerkSlot.BTN, cond.height());
				slot.setRect(GAP, y + (rowH - PerkSlot.BTN)/2f, PerkSlot.BTN, PerkSlot.BTN);
				cond.setPos(GAP + PerkSlot.BTN + GAP, y + (rowH - cond.height())/2f);

				y += rowH + GAP;
			}

			pane.content().setSize(width, Math.max(pane.height(), y));
		}
	}

	private static class SubclassInfoTab extends Component {

		private RenderedTextBlock title;
		private RenderedTextBlock message;
		private RenderedTextBlock[] subClsDescs;
		private IconButton[] subClsInfos;

		public SubclassInfoTab( HeroClass cls ){
			super();
			title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "subclasses")), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "subclasses_msg"), 6);
			add(message);

			HeroSubClass[] subClasses = cls.subClasses();

			subClsDescs = new RenderedTextBlock[subClasses.length];
			subClsInfos = new IconButton[subClasses.length];

			for (int i = 0; i < subClasses.length; i++){
				subClsDescs[i] = PixelScene.renderTextBlock(subClasses[i].shortDesc(), 6);
				int finalI = i;
				subClsInfos[i] = new IconButton( Icons.get(Icons.INFO) ){
					@Override
					protected void onClick() {
						Game.scene().addToFront(new WndInfoSubclass(cls, subClasses[finalI]));
					}
				};
				add(subClsDescs[i]);
				add(subClsInfos[i]);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);
			message.maxWidth((int)width);
			message.setPos(0, title.bottom()+4*MARGIN);

			float pos = message.bottom()+4*MARGIN;

			for (int i = 0; i < subClsDescs.length; i++){
				subClsDescs[i].maxWidth((int)width - 20);
				subClsDescs[i].setPos(0, pos);

				subClsInfos[i].setRect(width-20, subClsDescs[i].top() + (subClsDescs[i].height()-20)/2, 20, 20);

				pos = subClsDescs[i].bottom() + 4*MARGIN;
			}

			height = Math.max(height, pos - 4*MARGIN);

		}
	}

	private static class ArmorAbilityInfoTab extends Component {

		private RenderedTextBlock title;
		private RenderedTextBlock message;
		private RenderedTextBlock[] abilityDescs;
		private IconButton[] abilityInfos;

		public ArmorAbilityInfoTab(HeroClass cls){
			super();
			title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "abilities")), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "abilities_msg"), 6);
			add(message);

			ArmorAbility[] abilities = cls.armorAbilities();

			abilityDescs = new RenderedTextBlock[abilities.length];
			abilityInfos = new IconButton[abilities.length];

			for (int i = 0; i < abilities.length; i++){
				abilityDescs[i] = PixelScene.renderTextBlock(abilities[i].shortDesc(), 6);
				int finalI = i;
				abilityInfos[i] = new IconButton( Icons.get(Icons.INFO) ){
					@Override
					protected void onClick() {
						Game.scene().addToFront(new WndInfoArmorAbility(cls, abilities[finalI]));
					}
				};
				add(abilityDescs[i]);
				add(abilityInfos[i]);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);
			message.maxWidth((int)width);
			message.setPos(0, title.bottom()+4*MARGIN);

			float pos = message.bottom()+4*MARGIN;

			for (int i = 0; i < abilityDescs.length; i++){
				abilityDescs[i].maxWidth((int)width - 20);
				abilityDescs[i].setPos(0, pos);

				abilityInfos[i].setRect(width-20, abilityDescs[i].top() + (abilityDescs[i].height()-20)/2, 20, 20);

				pos = abilityDescs[i].bottom() + 4*MARGIN;
			}

			height = Math.max(height, pos - 4*MARGIN);

		}
	}

}
