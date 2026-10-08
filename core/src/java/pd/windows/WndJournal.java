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

import pd.atlas.IconEntry;

import pd.atlas.items.EquipmentEquipArmorBasicArmorDict;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;
import pd.atlas.items.SpecificPagesDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.ConsumPotionSeedBasicPotionDict;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.SPDAction;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.mobs.CrystalSpire;
import pd.actors.mobs.Mimic;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Pylon;
import pd.items.EnergyCrystal;
import pd.items.Gold;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.ClassArmor;
import pd.items.equipment.artifacts.Artifact;
import pd.items.consum.potions.Potion;
import pd.items.consum.potions.exotic.ExoticPotion;
import pd.items.equipment.rings.Ring;
import pd.items.consum.scrolls.Scroll;
import pd.items.equipment.trinkets.Trinket;
import pd.items.equipment.wands.WandOfRegrowth;
import pd.items.equipment.wands.WandOfWarding;
import pd.items.equipment.weapon.SpiritBow;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.journal.Bestiary;
import pd.journal.Catalog;
import pd.journal.Document;
import pd.journal.Notes;
import pd.journal.SpsCatalog;
import pd.journal.SpsMobCatalog;
import pd.levels.traps.Trap;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.CharSprite;
import pd.sprites.ItemSprite;
import pd.sprites.ItemIconSheet;
import pd.tiles.TerrainFeaturesTilemap;
import pd.ui.BadgesGrid;
import pd.ui.BadgesList;
import pd.ui.CustomNoteButton;
import pd.ui.Icons;
import pd.ui.QuickRecipe;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.ScrollingGridPane;
import pd.ui.ScrollingListPane;
import pd.ui.Window;
import render.input.KeyBindings;
import render.input.KeyEvent;
import render.noosa.BitmapText;
import render.noosa.ColorBlock;
import render.noosa.Image;
import render.noosa.Visual;
import render.noosa.ui.Component;
import render.utils.geom.RectF;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Collection;
import pd.messages.InlineText;

public class WndJournal extends WndTabbed {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndJournal.class)
			.t("$guidetab.title", "地牢指南")
			.t("$alchemytab.title", "炼金指南")
			.t("$alchemytab.missing", "缺页")
			.t("$alchemytab.section.basics", "入门配方")
			.t("$alchemytab.section.advanced", "进阶与强化")
			.t("$alchemytab.section.rarities", "珍稀产物")
			.t("$guidetab.missing", "缺页")
			.t("$notestab.title", "探险手册")
			.t("$notestab.desc", "随着你在地牢中的旅途逐渐推进，这里会自动为你记录下重要信息。")
			.t("$notestab.custom_notes", "自定义")
			.t("$notestab.floor_header", "第%d层")
			.t("$catalogtab.title", "图鉴")
			.t("$catalogtab.title_equipment", "装备")
			.t("$catalogtab.title_consumables", "消耗品")
			.t("$catalogtab.title_bestiary", "单位图鉴")
			.t("$catalogtab.title_lore", "背景故事")
			.t("$catalogtab.not_seen_item", "你还没在任何一局游戏中发现并鉴定过这种物品。")
			.t("$catalogtab.not_seen_enchantment", "你还没在任何一局游戏中发现并鉴定过带有这种附魔的武器。")
			.t("$catalogtab.not_seen_glyph", "你还没在任何一局游戏中发现并鉴定过带有这种刻印的护甲。")
			.t("$catalogtab.not_seen_enemy", "你还没在任何一局游戏中击败过这种敌人。")
			.t("$catalogtab.not_seen_ally", "你还没在任何一局游戏中遇见过这个角色。")
			.t("$catalogtab.not_seen_trap", "你还没在任何一局游戏中检查或触发过这种陷阱。")
			.t("$catalogtab.not_seen_plant", "你还没在任何一局游戏中检查或践踏过这种植物。")
			.t("$catalogtab.upgrade_count", "你已经升级了这种物品共计_%,d_次。")
			.t("$catalogtab.trinket_count", "你已经合成或升级了这种物品共计_%,d_次。")
			.t("$catalogtab.use_count", "你已经使用了这种物品共计_%,d_次。")
			.t("$catalogtab.gold_count", "你已经花费了共计_%,d_枚金币。")
			.t("$catalogtab.energy_count", "你已经使用了共计_%,d_个能量晶体。")
			.t("$catalogtab.enemy_count", "你已经击败了这种敌人共计_%,d_次。")
			.t("$catalogtab.trap_count", "你已经触发了这种陷阱共计_%,d_次。")
			.t("$catalogtab.plant_count", "你已经践踏了这种植物共计_%,d_次。")
			.t("$catalogtab.not_seen_lore", "你还没在任何一局游戏中发现过这段背景故事。")
			.t("$badgestab.title", "徽章")
			.t("$badgestab.title_main_menu", "你的徽章")
			.t("$badgestab.this_run", "本局")
			.t("$badgestab.overall", "全局");
	}



	
	public static final int WIDTH_P     = 126;
	public static final int HEIGHT_P    = 180;
	
	public static final int WIDTH_L     = 216;
	public static final int HEIGHT_L    = 130;
	
	private static final int ITEM_HEIGHT	= 18;
	
	private GuideTab guideTab;
	private AlchemyTab alchemyTab;
	private NotesTab notesTab;
	private CatalogTab catalogTab;
	private BadgesTab badgesTab;
	
	public static int last_index = 0;

	private static WndJournal INSTANCE = null;
	
	public WndJournal(){

		if (INSTANCE != null){
			INSTANCE.hide();
		}
		
		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
		int height = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;
		
		resize(width, height);
		
		guideTab = new GuideTab();
		add(guideTab);
		guideTab.setRect(0, 0, width, height);
		guideTab.updateList();
		
		alchemyTab = new AlchemyTab();
		add(alchemyTab);
		alchemyTab.setRect(0, 0, width, height);
		
		notesTab = new NotesTab();
		add(notesTab);
		notesTab.setRect(0, 0, width, height);
		notesTab.updateList();
		
		catalogTab = new CatalogTab();
		add(catalogTab);
		catalogTab.setRect(0, 0, width, height);
		catalogTab.updateList();

		badgesTab = new BadgesTab();
		add(badgesTab);
		badgesTab.setRect(0, 0, width, height);
		badgesTab.updateList();
		
		Tab[] tabs = {
				new IconTab( Icons.JOURNAL.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						notesTab.active = notesTab.visible = value;
						if (value) last_index = 0;
					}

					@Override
					protected String hoverText() {
						return Messages.get(notesTab, "title");
					}
				},
				new IconTab( new ItemSprite(ConsumUsefulProcessEnhanceDict.MASTERY_0, null) ) {
					protected void select( boolean value ) {
						super.select( value );
						guideTab.active = guideTab.visible = value;
						if (value) last_index = 1;
					}

					@Override
					protected String hoverText() {
						return Messages.get(guideTab, "title");
					}
				},
				new IconTab( Icons.ALCHEMY.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						alchemyTab.active = alchemyTab.visible = value;
						if (value) last_index = 2;
					}

					@Override
					protected String hoverText() {
						return Messages.get(alchemyTab, "title");
					}
				},
				new IconTab( Icons.CATALOG.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						catalogTab.active = catalogTab.visible = value;
						if (value) last_index = 3;
					}

					@Override
					protected String hoverText() {
						return Messages.get(catalogTab, "title");
					}
				},
				new IconTab( Icons.BADGES.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						badgesTab.active = badgesTab.visible = value;
						if (value) last_index = 4;
					}

					@Override
					protected String hoverText() {
						return Messages.get(badgesTab, "title");
					}
				}
		};

		for (Tab tab : tabs) {
			add( tab );
		}
		
		layoutTabs();
		
		select(last_index);

		INSTANCE = this;
	}

	@Override
	public boolean onSignal(KeyEvent event) {
		if (event.pressed && KeyBindings.getActionForKey( event ) == SPDAction.JOURNAL) {
			onBackPressed();
			return true;
		} else {
			return super.onSignal(event);
		}
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		guideTab.layout();
		alchemyTab.layout();
		notesTab.layout();
		catalogTab.layout();
	}
	
	public static class GuideTab extends Component {

		private ScrollingListPane list;
		
		@Override
		protected void createChildren() {
			list = new ScrollingListPane();
			add( list );
		}
		
		@Override
		protected void layout() {
			super.layout();
			list.setRect( x, y, width, height);
		}
		
		public void updateList(){
			addDocument(Document.STORY_GUIDE);
			addDocument(Document.ADVENTURERS_GUIDE);

			list.setRect(x, y, width, height);
		}

		private void addDocument(Document document) {
			list.addTitle(document.title());

			for (String page : document.pageNames()){
				boolean found = document.isPageFound(page);
				ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(
						document.pageSprite(page),
						null,
						found ? Messages.titleCase(document.pageTitle(page)) : Messages.titleCase(Messages.get( this, "missing" ))
				){
					@Override
					public boolean onClick(float x, float y) {
						if (inside( x, y ) && found) {
							ShatteredPixelDungeon.scene().addToFront( new WndStory( document.pageSprite(page),
									document.pageTitle(page),
									document.pageBody(page) ));
							document.readPage(page);
							return true;
						} else {
							return false;
						}
					}
				};
				if (!found){
					item.hardlight(0x999999);
					item.hardlightIcon(0x999999);
				}
				list.addItem(item);
			}
		}

	}
	
	public static class AlchemyTab extends Component {
		
		//SPSEXPD: 页签本体只是一份可滚动的文字目录，每一页的内容改由 WndAlchemyPage 弹窗显示。
		//这样以后新增配方组时只是目录多一行，不必再往顶部挤图标按钮（原来固定 9 个图标按钮）。
		public static int currentPageIdx   = -1;
		
		private ScrollingListPane list;
		
		@Override
		protected void createChildren() {
			list = new ScrollingListPane();
			add(list);
		}
		
		@Override
		protected void layout() {
			super.layout();
			list.setRect(x, y, width, height);
			updateList();
		}
		
		/**
		 * SPSEXPD: 炼金指南目录的分组——每行是「组标题文案后缀 + 该组收录的页 id」
		 * （页 id 就是 {@code Document.ALCHEMY_GUIDE.pagesStates} 的键）。
		 *
		 * <p>新增配方组：在这里加一行；往已有组里加页：把页 id 填进对应行。
		 * 组标题文案是 windows 里的 {@code $alchemytab.section.<后缀>}。
		 * 没写进本表的页不会丢，会追加在最后一组之后（新增页时不同步这里也能看到），
		 * 写错/不存在的页 id 会被直接跳过而不是抛异常。</p>
		 */
		private static final String[][] SECTIONS = {
				{"basics",    "Potions", "Stones", "Energy_Food"},
				{"advanced",  "Exotic_Potions", "Exotic_Scrolls", "Bombs", "Weapons"},
				{"rarities",  "Brews_Elixirs", "Spells"},
		};

		public void updateList() {
			list.clear();

			String missing = Messages.titleCase(Messages.get(this, "missing"));

			ArrayList<String> placed = new ArrayList<>();
			for (String[] section : SECTIONS){
				list.addTitle(Messages.get(this, "section." + section[0]));
				for (int i = 1; i < section.length; i++){
					addPage(section[i], missing);
					placed.add(section[i]);
				}
			}

			//分组表没覆盖到的页仍然列出来，新增页时不会凭空消失
			for (String page : Document.ALCHEMY_GUIDE.pageNames()){
				if (!placed.contains(page)){
					addPage(page, missing);
				}
			}
		}

		private void addPage(String page, String missing) {
			final int idx = Document.ALCHEMY_GUIDE.pageIdx(page);
			if (idx < 0){
				return;
			}
			final boolean found = Document.ALCHEMY_GUIDE.isPageFound(page);
			ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(
					Document.ALCHEMY_GUIDE.pageSprite(page), null,
					found ? Messages.titleCase(Document.ALCHEMY_GUIDE.pageTitle(page)) : missing) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y) && found) {
						currentPageIdx = idx;
						ShatteredPixelDungeon.scene().addToFront(new WndAlchemyPage(idx));
						Document.ALCHEMY_GUIDE.readPage(idx);
						return true;
					} else {
						return false;
					}
				}
			};
			if (!found){
				item.hardlight(0x999999);
				item.hardlightIcon(0x999999);
			}
			list.addItem(item);
		}
	}
	
	private static class NotesTab extends Component {
		
		private ScrollingGridPane grid;
		private CustomNoteButton custom;
		
		@Override
		protected void createChildren() {
			grid = new ScrollingGridPane();
			add(grid);
		}
		
		@Override
		protected void layout() {
			super.layout();
			grid.setRect( x, y, width, height);
		}
		
		private void updateList(){

			grid.addHeader("_" + Messages.get(this, "title") + "_", 9, true);

			grid.addHeader(Messages.get(this, "desc"), 6, true);

			ArrayList<Notes.CustomRecord> customRecs = Notes.getRecords(Notes.CustomRecord.class);

			if (!customRecs.isEmpty()){
				grid.addHeader("_" + Messages.get(this, "custom_notes") + "_ (" + customRecs.size() + "/" + Notes.customRecordLimit() + ")");

				for (Notes.CustomRecord rec : customRecs){
					ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(rec.icon()){
						@Override
						public boolean onClick(float x, float y) {
							if (inside(x, y)) {
								GameScene.show(new CustomNoteButton.CustomNoteWindow(rec, WndJournal.INSTANCE));
								return true;
							} else {
								return false;
							}
						}
					};

					Visual secondIcon = rec.secondIcon();
					if (secondIcon != null){
						gridItem.addSecondIcon( secondIcon );
					}

					grid.addItem(gridItem);
				}
			}

			for (int i = Statistics.deepestFloor; i > 0; i--){

				ArrayList<Notes.Record> recs = Notes.getRecords(i);

				if (i == Dungeon.depth) {
					grid.addHeader("_" + Messages.get(this, "floor_header", i) + "_");
				} else {
					grid.addHeader(Messages.get(this, "floor_header", i));
				}
				for( Notes.Record rec : recs){

					ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(rec.icon()){
						@Override
						public boolean onClick(float x, float y) {
							if (inside(x, y)) {
								GameScene.show(new WndJournalItem(rec.icon(),
										Messages.titleCase(rec.title()),
										rec.desc()));
								return true;
							} else {
								return false;
							}
						}
					};

					Visual secondIcon = rec.secondIcon();
					if (secondIcon != null){
						gridItem.addSecondIcon( secondIcon );
					}

					grid.addItem(gridItem);
				}
			}

			custom = new CustomNoteButton();
			grid.content().add(custom);
			custom.setPos(width-custom.width()-1, 0);

			grid.setRect(x, y, width, height);

		}
		
	}
	
	public static class CatalogTab extends Component{
		
		private RedButton[] itemButtons;
		private static final int NUM_BUTTONS = 4;

		public static int currentItemIdx   = 0;
		private static float[] scrollPositions = new float[NUM_BUTTONS];
		
		//sprite locations
		private static final int EQUIP_IDX = 0;
		private static final int CONSUM_IDX = 1;
		private static final int BESTIARY_IDX = 2;
		private static final int LORE_IDX = 3;

		private ScrollingGridPane grid;
		
		@Override
		protected void createChildren() {
			itemButtons = new RedButton[NUM_BUTTONS];
			for (int i = 0; i < NUM_BUTTONS; i++){
				final int idx = i;
				itemButtons[i] = new RedButton( "" ){
					@Override
					protected void onClick() {
						currentItemIdx = idx;
						updateList();
					}
				};
				add( itemButtons[i] );
			}
			itemButtons[EQUIP_IDX].icon(new ItemSprite(SpecificPlaceHolderDict.SOMETHING_0));
			itemButtons[CONSUM_IDX].icon(new ItemSprite(SpecificPlaceHolderDict.POTION_HOLDER_0));
			itemButtons[BESTIARY_IDX].icon(new ItemSprite(SpecificPlaceHolderDict.MOB_HOLDER_0));
			itemButtons[LORE_IDX].icon(new ItemSprite(SpecificPlaceHolderDict.DOCUMENT_HOLDER_0));

			grid = new ScrollingGridPane(){
				@Override
				public synchronized void update() {
					super.update();
					scrollPositions[currentItemIdx] = content.camera.scroll.y;
				}
			};
			add( grid );
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			int perRow = NUM_BUTTONS;
			float buttonWidth = width()/perRow;
			
			for (int i = 0; i < NUM_BUTTONS; i++) {
				itemButtons[i].setRect(x +(i%perRow) * (buttonWidth),
						y + (i/perRow) * (ITEM_HEIGHT ),
						buttonWidth, ITEM_HEIGHT);
				PixelScene.align(itemButtons[i]);
			}
			
			grid.setRect(x,
					itemButtons[NUM_BUTTONS-1].bottom() + 1,
					width,
					height - itemButtons[NUM_BUTTONS-1].height() - 1);
		}
		
		public void updateList() {
			
			grid.clear();
			
			for (int i = 0; i < NUM_BUTTONS; i++){
				if (i == currentItemIdx){
					itemButtons[i].icon().color(TITLE_COLOR);
				} else {
					itemButtons[i].icon().resetColor();
				}
			}
			
			grid.scrollTo( 0, 0 );

			if (currentItemIdx == EQUIP_IDX) {
				int totalItems = 0;
				int totalSeen = 0;
				for (SpsCatalog catalog : SpsCatalog.EQUIPMENT){
					totalItems += catalog.totalItems();
					totalSeen += catalog.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_equipment") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (SpsCatalog catalog : SpsCatalog.EQUIPMENT){
					grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalItems() + "):");
					addGridItems(grid, catalog.items());
				}

			} else if (currentItemIdx == CONSUM_IDX){
				int totalItems = 0;
				int totalSeen = 0;
				for (SpsCatalog catalog : SpsCatalog.CONSUMABLES){
					totalItems += catalog.totalItems();
					totalSeen += catalog.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_consumables") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (SpsCatalog catalog : SpsCatalog.CONSUMABLES){
					grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalItems() + "):");
					addGridItems(grid, catalog.items());
				}

			} else if (currentItemIdx == BESTIARY_IDX){
				int totalItems = 0;
				int totalSeen = 0;
				for (SpsMobCatalog catalog : SpsMobCatalog.values()){
					totalItems += catalog.totalMobs();
					totalSeen += catalog.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_bestiary") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (SpsMobCatalog catalog : SpsMobCatalog.values()){
					grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalMobs() + "):");
					addGridEntities(grid, catalog.mobs());
				}

			} else {
				int totalItems = 0;
				int totalSeen = 0;
				for (Document doc : Document.values()){
					if (!doc.isLoreDoc()){
						continue;
					}
					for (String page : doc.pageNames()){
						totalItems++;
						if (doc.isPageFound(page)){
							totalSeen++;
						}
					}
				}
				grid.addHeader("_" + Messages.get(this, "title_lore") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (Document doc : Document.values()){
					if (!doc.isLoreDoc()){
						continue;
					}

					for (String page : doc.pageNames()){
						totalItems++;
						if (doc.isPageFound(page)){
							totalSeen++;
						}
					}
				}
				for (Document doc : Document.values()){
					if (!doc.isLoreDoc()){
						continue;
					}
					totalItems = totalSeen = 0;
					for (String page : doc.pageNames()){
						totalItems++;
						if (doc.isPageFound(page)){
							totalSeen++;
						}
					}
					if (!doc.anyPagesFound()){
						grid.addHeader("_???_ (" + totalSeen + "/" + totalItems + "):");
					} else {
						grid.addHeader("_" + Messages.titleCase(doc.title()) + "_ (" + totalSeen + "/" + totalItems + "):");
					}
					addGridDocuments(grid, doc);
				}
			}

			grid.setRect(x, itemButtons[NUM_BUTTONS-1].bottom() + 1, width,
					height - itemButtons[NUM_BUTTONS-1].height() - 1);

			grid.scrollTo(0, scrollPositions[currentItemIdx]);
		}
		
	}

	//also includes item-like things such as enchantments, glyphs, curses.
	private static void addGridItems( ScrollingGridPane grid, Collection<? extends Class<?>> classes) {
		for (Class<?> itemClass : classes) {

			boolean seen = SpsCatalog.contains(itemClass) || Catalog.isSeen(itemClass);
			ItemSprite sprite = null;
			Image secondIcon = null;
			String title = "";
			String desc = "";

			if (Item.class.isAssignableFrom(itemClass)) {

				Item item = (Item) Reflection.newInstance(itemClass);

				if (seen) {
					if (item instanceof Ring) {
						((Ring) item).anonymize();
					} else if (item instanceof Potion) {
						((Potion) item).anonymize();
					} else if (item instanceof Scroll) {
						((Scroll) item).anonymize();
					}
				}

				sprite = new ItemSprite(item.image(), seen ? item.glowing() : null);
				if (!seen)  {
					if (item instanceof ExoticPotion){
						sprite.frame(ConsumPotionSeedBasicPotionDict.POTION_CRIMSON_0);
					}
					sprite.lightness(0);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_item");
					desc += "\n\n" + Messages.get(item, "discover_hint");
				} else {
					title = Messages.titleCase( item.name() );
					//some items don't include direct stats, generally when they're not applicable
					if (item instanceof ClassArmor || item instanceof SpiritBow){
						desc += item.desc();
					} else {
						desc += item.info();
					}

					if (Catalog.useCount(itemClass) > 1) {
						if (item.isUpgradable() || item instanceof Artifact) {
							desc += "\n\n" + Messages.get(CatalogTab.class, "upgrade_count", Catalog.useCount(itemClass));
						} else if (item instanceof Trinket) {
							desc += "\n\n" + Messages.get(CatalogTab.class, "trinket_count", Catalog.useCount(itemClass));
						} else if (item instanceof Gold) {
							desc += "\n\n" + Messages.get(CatalogTab.class, "gold_count", Catalog.useCount(itemClass));
						} else if (item instanceof EnergyCrystal) {
							desc += "\n\n" + Messages.get(CatalogTab.class, "energy_count", Catalog.useCount(itemClass));
						} else {
							desc += "\n\n" + Messages.get(CatalogTab.class, "use_count", Catalog.useCount(itemClass));
						}
					}

					//mage's staff normally has 2 pixels extra at the top for particle effects, we chop that off here
					if (item instanceof MagesStaff){
						RectF frame = sprite.frame();
						frame.top += frame.height()/8f;
						sprite.frame(frame);
					}

					if (item.icon != -1) {
						secondIcon = new Image(Assets.Sprites.ITEM_ICONS);
						secondIcon.frame(ItemIconSheet.film.get(item.icon));
					}
				}

			} else if (Weapon.Enchantment.class.isAssignableFrom(itemClass)){

				Weapon.Enchantment ench = (Weapon.Enchantment) Reflection.newInstance(itemClass);

				if (seen){
					sprite = new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.WORN_SHORTSWORD_0, ench.glowing());
					title = Messages.titleCase(ench.name());
					desc = ench.desc();
				} else {
					sprite = new ItemSprite(EquipmentEquipWeaponBasicWeaponDict.WORN_SHORTSWORD_0);
					sprite.lightness(0f);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_enchantment");
					desc += "\n\n" + Messages.get(ench, "discover_hint");
				}

			} else if (Armor.Glyph.class.isAssignableFrom(itemClass)){

				Armor.Glyph glyph = (Armor.Glyph) Reflection.newInstance(itemClass);

				if (seen){
					sprite = new ItemSprite(EquipmentEquipArmorBasicArmorDict.ARMOR_CLOTH_0, glyph.glowing());
					title = Messages.titleCase(glyph.name());
					desc = glyph.desc();
				} else {
					sprite = new ItemSprite(EquipmentEquipArmorBasicArmorDict.ARMOR_CLOTH_0);
					sprite.lightness(0f);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_glyph");
					desc += "\n\n" + Messages.get(glyph, "discover_hint");
				}

			}

			String finalTitle = title;
			String finalDesc = desc;
			ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(sprite) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)) {
						Image sprite = new ItemSprite();
						sprite.copy(icon);
						if (ShatteredPixelDungeon.scene() instanceof GameScene){
							GameScene.show(new WndJournalItem(sprite, finalTitle, finalDesc));
						} else {
							ShatteredPixelDungeon.scene().addToFront(new WndJournalItem(sprite, finalTitle, finalDesc));
						}
						return true;
					} else {
						return false;
					}
				}
			};
			if (secondIcon != null){
				gridItem.addSecondIcon(secondIcon);
			}
			if (!seen) {
				gridItem.hardLightBG(2f, 1f, 2f);
			}
			grid.addItem(gridItem);
		}
	}

	private static void addGridEntities(ScrollingGridPane grid, Collection<? extends Class<?>> classes) {
		for (Class<?> entityCls : classes){

			boolean seen = SpsMobCatalog.contains(entityCls) || Bestiary.isSeen(entityCls);
			Mob mob = null;
			Image icon = null;
			String title = null;
			String desc = null;

			if (Mob.class.isAssignableFrom(entityCls)) {

				mob = (Mob) Reflection.newInstance(entityCls);

				if (mob instanceof Mimic || mob instanceof Pylon || mob instanceof CrystalSpire) {
					mob.alignment = Char.Alignment.ENEMY;
				}
				if (mob instanceof WandOfWarding.Ward){
					if (mob instanceof WandOfWarding.Ward.WardSentry){
						((WandOfWarding.Ward) mob).upgrade(3);
						((WandOfWarding.Ward) mob).upgrade(3);
						((WandOfWarding.Ward) mob).upgrade(3);
						((WandOfWarding.Ward) mob).upgrade(3);
					} else {
						((WandOfWarding.Ward) mob).upgrade(0);
					}
				}

				CharSprite sprite = mob.sprite();
				if (sprite == null) {
					//健壮性：spriteClass 未设置的实体（占位类/旧存档壳类）跳过，避免整个怪物页崩溃
					continue;
				}
				sprite.idle();

				icon = new Image(sprite);
				if (seen) {
					title = Messages.titleCase(mob.name());
					desc = mob.description();
					if (Bestiary.encounterCount(entityCls) > 1){
						desc += "\n\n" + Messages.get(CatalogTab.class, "enemy_count", Bestiary.encounterCount(entityCls));
					}
				} else {
					icon.lightness(0f);
					title = "???";
					if (mob instanceof WandOfRegrowth.Lotus){
						desc = Messages.get(CatalogTab.class, "not_seen_plant");
					} else if (mob.alignment == Char.Alignment.ENEMY){
						desc = Messages.get(CatalogTab.class, "not_seen_enemy");
					} else {
						desc = Messages.get(CatalogTab.class, "not_seen_ally");
					}
					desc += "\n\n" + Messages.get(mob, "discover_hint");
				}

				//we have to clip the bounds of the sprite if it's too large
				if (icon.width() >= 17 || icon.height() >= 17) {
					RectF frame = icon.frame();

					float wShrink = frame.width() * (1f - 17f / icon.width());
					if (wShrink > 0) {
						frame.left += wShrink / 2f;
						frame.right -= wShrink / 2f;
					}
					float hShrink = frame.height() * (1f - 17f / icon.height());
					if (hShrink > 0) {
						frame.top += hShrink / 2f;
						frame.bottom -= hShrink / 2f;
					}
					icon.frame(frame);
				}
			} else if (Trap.class.isAssignableFrom(entityCls)){

				Trap trap = (Trap) Reflection.newInstance(entityCls);
				icon = TerrainFeaturesTilemap.getTrapVisual(trap);

				if (seen) {
					title = Messages.titleCase(trap.name());
					desc = trap.desc();
					if (Bestiary.encounterCount(entityCls) > 1){
						desc += "\n\n" + Messages.get(CatalogTab.class, "trap_count", Bestiary.encounterCount(entityCls));
					}
				} else {
					icon.lightness(0f);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_trap");
					desc += "\n\n" + Messages.get(trap, "discover_hint");
				}

			} else if (Plant.class.isAssignableFrom(entityCls)){

				Plant plant = (Plant) Reflection.newInstance(entityCls);
				icon = TerrainFeaturesTilemap.getPlantVisual(plant);

				if (seen) {
					title = Messages.titleCase(plant.name());
					desc = plant.desc();
					if (Bestiary.encounterCount(entityCls) > 1){
						desc += "\n\n" + Messages.get(CatalogTab.class, "plant_count", Bestiary.encounterCount(entityCls));
					}
				} else {
					icon.lightness(0f);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_plant");
					desc += "\n\n" + Messages.get(plant, "discover_hint");
				}

			}

			Mob finalMob = mob;
			String finalTitle = title;
			String finalDesc = desc;
			ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(icon) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)) {
						Image image;
						if (seen && finalMob != null){
							image = finalMob.sprite();
						} else {
							image = new Image(icon);
						}

						if (ShatteredPixelDungeon.scene() instanceof GameScene){
							GameScene.show(new WndJournalItem(image, finalTitle, finalDesc));
						} else {
							ShatteredPixelDungeon.scene().addToFront(new WndJournalItem(image, finalTitle, finalDesc));
						}

						return true;
					} else {
						return false;
					}
				}
			};
			if (!seen) {
				gridItem.hardLightBG(2f, 1f, 2f);
			}
			grid.addItem(gridItem);
		}
	};

	private static void addGridDocuments( ScrollingGridPane grid, Document doc ){
		for (String page : doc.pageNames()){

			Image sprite = doc.pageSprite(page);

			boolean seen = doc.isPageFound(page);
			boolean read = doc.isPageRead(page);

			if (!seen){
				sprite.lightness(0f);
			}

			ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(sprite) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)) {
						Image sprite = new Image(icon);
						if (seen) {
							if (ShatteredPixelDungeon.scene() instanceof GameScene){
								GameScene.show(new WndStory(sprite, doc.pageTitle(page), doc.pageBody(page)));
							} else {
								ShatteredPixelDungeon.scene().addToFront(new WndStory(sprite, doc.pageTitle(page), doc.pageBody(page)));
							}

							doc.readPage(page);
							hardLightBG(1, 1, 1);
						} else {
							if (ShatteredPixelDungeon.scene() instanceof GameScene){
								GameScene.show(new WndJournalItem(sprite, "???",
										Messages.get(CatalogTab.class, "not_seen_lore") + "\n\n" + doc.discoverHint()));
							} else {
								ShatteredPixelDungeon.scene().addToFront(new WndJournalItem(sprite, "???",
										Messages.get(CatalogTab.class, "not_seen_lore") + "\n\n" + doc.discoverHint()));
							}

						}
						return true;
					} else {
						return false;
					}
				}
			};

			if (seen){
				BitmapText text = new BitmapText(Integer.toString(doc.pageIdx(page)+1), PixelScene.pixelFont);
				text.measure();
				gridItem.addSecondIcon( text );
				if (!read) {
					gridItem.hardLightBG(0.6f, 1f, 2f);
				}
			} else {
				gridItem.hardLightBG(2.2f, 1f, 2.2f);
			}
			grid.addItem(gridItem);
		}
	}

	public static class BadgesTab extends Component {

		private RedButton btnLocal;
		private RedButton btnGlobal;

		private RenderedTextBlock title;

		private Component badgesLocal;
		private Component badgesGlobal;

		public static boolean global = false;

		@Override
		protected void createChildren() {

			if (Dungeon.hero != null) {
				btnLocal = new RedButton(Messages.get(this, "this_run")) {
					@Override
					protected void onClick() {
						super.onClick();
						global = false;
						updateList();
					}
				};
				btnLocal.icon(Icons.BADGES.get());
				add(btnLocal);

				btnGlobal = new RedButton(Messages.get(this, "overall")) {
					@Override
					protected void onClick() {
						super.onClick();
						global = true;
						updateList();
					}
				};
				btnGlobal.icon(Icons.BADGES.get());
				add(btnGlobal);

				if (Badges.filterReplacedBadges(false).size() <= 8){
					badgesLocal = new BadgesList(false);
				} else {
					badgesLocal = new BadgesGrid(false);
				}
				add( badgesLocal );
			} else {
				title = PixelScene.renderTextBlock(Messages.get(this, "title_main_menu"), 9);
				title.hardlight(Window.TITLE_COLOR);
				add(title);
			}

			badgesGlobal = new BadgesGrid(true);
			add( badgesGlobal );
		}

		@Override
		protected void layout() {
			super.layout();

			if (btnLocal != null) {
				btnLocal.setRect(x, y, width / 2, 18);
				btnGlobal.setRect(x + width / 2, y, width / 2, 18);

				badgesLocal.setRect(x, y + 20, width, height-20);
				badgesGlobal.setRect( x, y + 20, width, height-20);
			} else {
				title.setPos( x + (width - title.width())/2, y + (12-title.height())/2);

				badgesGlobal.setRect( x, y + 14, width, height-14);
			}
		}

		private void updateList(){
			if (btnLocal != null) {
				badgesLocal.visible = badgesLocal.active = !global;
				badgesGlobal.visible = badgesGlobal.active = global;

				btnLocal.textColor(global ? Window.WHITE : Window.TITLE_COLOR);
				btnGlobal.textColor(global ? Window.TITLE_COLOR : Window.WHITE);
			} else {
				badgesGlobal.visible = badgesGlobal.active = true;
			}
		}

	}
	
}
