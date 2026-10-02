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

import pd.Badges;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.ArmorAbility;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.TalentButton;
import pd.ui.TalentsPane;
import render.noosa.Game;
import render.noosa.Image;
import render.noosa.ui.Component;
import render.utils.platform.DeviceCompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import pd.messages.InlineText;

public class WndHeroInfo extends WndTabbed {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndHeroInfo.class)
			.t("talents", "天赋")
			.t("talents_msg", "英雄在升级时会获得一点天赋点数。更高阶的天赋在击杀第二个Boss后才会解锁。")
			.t("subclasses", "专精")
			.t("subclasses_msg", "击杀第二个Boss后可以选择一种职业专精。")
			.t("abilities", "护甲技能")
			.t("abilities_msg", "击杀第四个Boss后可以选择一项护甲技能。");
	}


	private HeroInfoTab heroInfo;
	private TalentInfoTab talentInfo;
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

		talentInfo = new TalentInfoTab(cl);
		add(talentInfo);
		talentInfo.setSize(WIDTH, MIN_HEIGHT);
		finalHeight = (int)Math.max(finalHeight, talentInfo.height());

		add( new IconTab( Icons.get(Icons.TALENT) ){
			@Override
			protected void select(boolean value) {
				super.select(value);
				talentInfo.visible = talentInfo.active = value;
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
		talentInfo.layout();

		select(0);

	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		talentInfo.layout();
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

	private static class TalentInfoTab extends Component {

		private RenderedTextBlock title;
		private RenderedTextBlock message;
		private TalentsPane talentPane;

		public TalentInfoTab( HeroClass cls ){
			super();
			title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "talents")), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "talents_msg"), 6);
			add(message);

			ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>();
			Talent.initClassTalents(cls, talents);
			talents.get(2).clear(); //we show T3 talents with subclasses

			talentPane = new TalentsPane(TalentButton.Mode.INFO, talents);
			add(talentPane);
		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);
			message.maxWidth((int)width);
			message.setPos(0, title.bottom()+4*MARGIN);

			talentPane.setRect(0, message.bottom() + 3*MARGIN, width, 85);

			height = Math.max(height, talentPane.bottom());
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
