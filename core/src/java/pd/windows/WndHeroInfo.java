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
import pd.ui.PerkSlot;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
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

	public WndHeroInfo( HeroClass cl ){

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

		heroInfo = new HeroInfoTab(cl);
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
		perkInfo.layout();

		select(0);

	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		perkInfo.layout();
	}

	private static class HeroInfoTab extends Component {

		private RenderedTextBlock title;
		private RenderedTextBlock[] info;
		private Image[] icons;

		public HeroInfoTab(HeroClass cls){
			super();
			title = PixelScene.renderTextBlock(Messages.titleCase(cls.title()), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			String[] desc_entries = cls.desc().split("\n\n");

			info = new RenderedTextBlock[desc_entries.length];

			for (int i = 0; i < desc_entries.length; i++){
				info[i] = PixelScene.renderTextBlock(desc_entries[i], 6);
				add(info[i]);
			}

			switch (cls){
				case WARRIOR: default:
					icons = new Image[]{ new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.WORN_SHORTSWORD_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case MAGE:
					icons = new Image[]{ new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case ROGUE:
					icons = new Image[]{ new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							Icons.get(Icons.STAIRS),
							new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.DAGGER_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case HUNTRESS:
					icons = new Image[]{ new ItemSprite(EquipmentEquipWeaponUniqueWeaponDict.SPIRIT_BOW_0),
							Icons.GRASS.get(),
							new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.GLOVES),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case DUELIST:
					icons = new Image[]{ new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.RAPIER_0),
							new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.WAR_HAMMER_0),
							new ItemSprite(ConsumThrowsDict.THROWING_SPIKE_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case CLERIC:
					icons = new Image[]{ new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							Icons.TALENT.get(),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case SPELLSWORD:
					icons = new Image[]{ new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case PERFORMER:
					icons = new Image[]{ new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0), Icons.BUFFS.get(),
							new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.DAGGER_0), new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case SOLDIER:
					icons = new Image[]{ new ItemSprite(ConsumThrowsDict.THROWING_SPIKE_0), Icons.TARGET.get(),
							new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.RAPIER_0), new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case FOLLOWER:
					icons = new Image[]{ new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0), Icons.GOLD.get(),
							new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0), new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
				case ASCETIC:
					icons = new Image[]{ new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.GLOVES), new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0),
							new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.GLOVES), new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0)};
					break;
			}
			for (Image im : icons) {
				add(im);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);

			float pos = title.bottom()+4*MARGIN;

			for (int i = 0; i < info.length; i++){
				info[i].maxWidth((int)width - 20);
				info[i].setPos(20, pos);

				icons[i].x = (20-icons[i].width())/2;
				icons[i].y = info[i].top() + (info[i].height() - icons[i].height())/2;
				PixelScene.align(icons[i]);

				pos = info[i].bottom() + 4*MARGIN;
			}

			height = Math.max(height, pos - 4*MARGIN);

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
			initialPane.setRect(0, pos, width, PANE_H);
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
