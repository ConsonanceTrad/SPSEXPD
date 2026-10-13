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
import pd.SPDAction;
import pd.ShatteredPixelDungeon;
import pd.Statistics;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.HeroSprite;
import pd.ui.BuffIcon;
import pd.ui.BuffIndicator;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.StatusPane;
import pd.ui.TalentButton;
import pd.ui.TalentsPane;
import pd.ui.PerkSlot;
import pd.ui.Window;
import pd.utils.DungeonSeed;
import render.input.KeyBindings;
import render.input.KeyEvent;
import render.noosa.Gizmo;
import render.noosa.Image;
import render.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Locale;
import pd.messages.InlineText;

public class WndHero extends WndTabbed {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndHero.class)
			.t("stats", "属性")
			.t("talents", "特质")
			.t("buffs", "状态")
			.t("$statstab.title", "%1$d级%2$s")
			.t("$statstab.exp", "经验")
			.t("$statstab.satiation", "饱食度")
			.t("$statstab.accuracy", "命中")
			.t("$statstab.evasion", "闪避")
			.t("$statstab.spell_power", "法术强度")
			.t("$statstab.magic_resist", "魔法抗性")
			.t("$statstab.critical_chance", "暴击几率")
			.t("$statstab.critical_multiplier", "暴击倍率")
			.t("$statstab.physical_bonus", "物理伤害加成")
			.t("$statstab.element_bonus", "元素伤害加成")
			.t("$statstab.pure_bonus", "纯粹伤害加成")
			.t("$statstab.element_resist", "元素抗性")
			.t("$statstab.regen_bonus", "回血速度加成")
			.t("$statstab.el_energy", "能")
			.t("$statstab.el_fire", "火")
			.t("$statstab.el_ice", "冰")
			.t("$statstab.el_earth", "地")
			.t("$statstab.el_shock", "雷")
			.t("$statstab.el_light", "光")
			.t("$statstab.el_dark", "暗")
			.t("$statstab.el_wind", "风")
			.t("$statstab.el_chaos", "混乱")
			.t("$statstab.perks", "特质")
			.t("$statstab.str", "力量")
			.t("$statstab.health", "生命")
			.t("$statstab.gold", "金币收集数")
			.t("$statstab.move_speed", "移速")
			.t("$statstab.dungeon_seed", "地牢种子")
			.t("$statstab.custom_seed", "_自定义种子_")
			.t("$statstab.daily_for", "_日常挑战于_")
			.t("$statstab.replay_for", "_重玩于_");
	}



	
	private static final int WIDTH		= 120;
	//SPSEXPD: 属性页高度改为按内容动态计算（含屏幕高度上限，超出可滚动），
	//原写死 210，每加一行属性都要手改常量
	private static final int MIN_HEIGHT	= 210;

	private StatsTab stats;
	private TalentsTab talents;
	private BuffsTab buffs;

	public static int lastIdx = 0;

	public WndHero() {
		
		super();

		stats = new StatsTab();
		add( stats );

		//SPSEXPD: 高度 = 属性页内容高度，上限改为界面高度的 80%（原为 uiCamera.height - 20），
		//下限保持 MIN_HEIGHT——但屏幕矮时 80% 上限优先，保证窗口不超出界面
		int maxHeight = PixelScene.uiCamera == null
				? MIN_HEIGHT
				: Math.round( PixelScene.uiCamera.height * 0.8f );
		int height = Math.min( Math.round( stats.height() ), maxHeight );
		height = Math.max( height, Math.min( MIN_HEIGHT, maxHeight ) );

		resize( WIDTH, height );

		//SPSEXPD: 属性页必须在 resize 之后再定位。
		//它内部用 ScrollPane，ScrollPane 会在 layout 时按所属窗口的相机定位内容；
		//若在 resize 前定位，内容会被绑到窗口的初始（未 resize，居中）相机上，
		//整页就会从画面中心开始向右下渲染。
		stats.setRect( 0, 0, WIDTH, height );

		talents = new TalentsTab();
		add(talents);
		talents.setRect(0, 0, WIDTH, height);

		buffs = new BuffsTab();
		add( buffs );
		buffs.setRect(0, 0, WIDTH, height);
		buffs.setupList();
		
		add( new IconTab( Icons.get(Icons.RANKINGS) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) {
					lastIdx = 0;
					if (!stats.visible) {
						stats.initialize();
					}
				}
				stats.visible = stats.active = selected;
			}
		} );
		add( new IconTab( new render.noosa.Image( pd.Assets.Interfaces.SPECIFIC_POINT ) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) lastIdx = 1;
				talents.visible = talents.active = selected;
			}
		} );
		add( new IconTab( Icons.get(Icons.BUFFS) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) lastIdx = 2;
				buffs.visible = buffs.active = selected;
			}
		} );

		layoutTabs();

		talents.setRect(0, 0, WIDTH, height);
		talents.pane.scrollTo(0, talents.pane.content().height() - talents.pane.height());
		talents.layout();

		select( lastIdx );
	}

	@Override
	public boolean onSignal(KeyEvent event) {
		if (event.pressed && KeyBindings.getActionForKey( event ) == SPDAction.HERO_INFO) {
			onBackPressed();
			return true;
		} else {
			return super.onSignal(event);
		}
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		talents.layout();
		buffs.layout();
	}

	private class StatsTab extends Component {
		
		private static final int GAP = 5;
		
		private float pos;
		private ScrollPane pane;
		private StatsContent content;

		//SPSEXPD: Group.members 是 protected，清理逻辑须放在子类里才能访问
		private class StatsContent extends Component {
			void clearAll() {
				for (Gizmo g : members) {
					if (g != null) g.destroy();
				}
				clear();
			}
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			//SPSEXPD: 属性行放进滚动窗格，行数再多也不会顶出屏幕
			//（createChildren 在构造期被调用，字段必须在此初始化，不能用声明处初始化）
			content = new StatsContent();
			pane = new ScrollPane( content );
			//SPSEXPD: 属性页不显示滚动条（拖动/滚轮滚动保留）
			pane.scrollBarVisible = false;
			add( pane );
		}

		@Override
		protected void layout() {
			super.layout();
			//SPSEXPD: 构造期（本组件还没挂到窗口上）时 camera() 为 null，不能布局——
			//ScrollPane.layout() 需要用窗口相机定位其内容，此时会 NPE（实测崩溃）。
			//挂上窗口后，WndHero 构造里 add + resize 之后的 stats.setRect(...) 会再走一次这里。
			if (pane != null && camera() != null) pane.setRect( 0, 0, width, height );
		}
		
		public StatsTab() {
			initialize();
		}

		public void initialize(){

			content.clearAll();
			
			Hero hero = Dungeon.hero;

			IconTitle title = new IconTitle();
			title.icon( HeroSprite.avatar(hero) );
			if (hero.name().equals(hero.className()))
				title.label( Messages.get(this, "title", hero.lvl, hero.className() ).toUpperCase( Locale.ENGLISH ) );
			else
				title.label((hero.name() + "\n" + Messages.get(this, "title", hero.lvl, hero.className())).toUpperCase(Locale.ENGLISH));
			title.color(Window.TITLE_COLOR);
			title.setRect( 0, 0, WIDTH-16, 0 );
			content.add(title);

			IconButton infoButton = new IconButton(Icons.get(Icons.INFO)){
				@Override
				protected void onClick() {
					super.onClick();
					if (ShatteredPixelDungeon.scene() instanceof GameScene){
						GameScene.show(new WndHeroInfo(hero.heroClass, hero.skin));
					} else {
						ShatteredPixelDungeon.scene().addToFront(new WndHeroInfo(hero.heroClass, hero.skin));
					}
				}

				@Override
				protected String hoverText() {
					return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info"));
				}

			};
			infoButton.setRect(title.right(), 0, 16, 16);
			content.add(infoButton);

			pos = title.bottom() + 2*GAP;

			int strBonus = hero.STR() - hero.STR;
			String strText;
			if (strBonus > 0)           strText = hero.STR + " + " + strBonus;
			else if (strBonus < 0)      strText = hero.STR + " - " + -strBonus;
			else                        strText = Integer.toString( hero.STR() );

			String healthText = hero.shielding() > 0
					? hero.HP + "+" + hero.shielding() + "/" + hero.HT
					: hero.HP + "/" + hero.HT;

			//SPSXPD: 饱食度（当前饥饿值 / 上限；上限受「坚忍肠胃」特质影响）
			pd.actors.buffs.Hunger hungerBuff = hero.buff(pd.actors.buffs.Hunger.class);
			int hunger = hungerBuff == null ? 0 : hungerBuff.hunger();
			int hungerCap = (int)(pd.actors.buffs.Hunger.STARVING
					+ pd.actors.hero.perks.HardenedStomach.capBonus(hero));

			String perkText = String.valueOf(hero.heroPerk == null ? 0 : hero.heroPerk.getPerks().size());
			if (hero.reservedPerks > 0) {
				perkText += " +" + hero.reservedPerks;
			}

			//SPSEXPD: 属性改双列显示 —— 每行两项（左列 / 右列），值右对齐到本列右缘
			statRow( Messages.get(this, "str"), strText,
					Messages.get(this, "health"), healthText );
			statRow( Messages.get(this, "exp"), hero.exp + "/" + hero.maxExp(),
					Messages.get(this, "satiation"), hunger + "/" + hungerCap );
			statRow( Messages.get(this, "accuracy"), Integer.toString( hero.attackSkill(hero) ),
					Messages.get(this, "evasion"), Integer.toString( hero.defenseSkill(hero) ) );
			statRow( Messages.get(this, "spell_power"), Integer.toString( hero.magicSkill() ),
					Messages.get(this, "magic_resist"),
					Math.round(hero.magicalResistance() * 100) + "%" );

			//SPSXPD: 特质体系在属性页的显示 —— 暴击几率；SPSEXPD: 新增移速（100% 基准，更快绿更慢红）
			statRow( Messages.get(this, "critical_chance"),
					Math.round(pd.actors.hero.Critical.chance(hero) * 100) + "%",
					Messages.get(this, "move_speed"), speedText(hero), speedColor(hero) );

			//SPSEXPD: 战斗属性（统一属性层 HeroStats）——暴击倍率/物理/元素/纯粹加成/元素抗性/回血
			statRow( Messages.get(this, "critical_multiplier"),
					Math.round(pd.actors.hero.HeroStats.critMultiplier(hero) * 100) + "%",
					Messages.get(this, "regen_bonus"),
					bonusText( pd.actors.hero.HeroStats.regenBonus(hero) ) );
			statRow( Messages.get(this, "physical_bonus"),
					bonusText( pd.actors.hero.HeroStats.physicalDamageBonus(hero) ),
					Messages.get(this, "pure_bonus"),
					bonusText( pd.actors.hero.HeroStats.pureDamageBonus(hero) ) );
			statRow( Messages.get(this, "element_bonus"), elementText( hero, true ),
					Messages.get(this, "element_resist"), elementText( hero, false ) );
			statRow( Messages.get(this, "perks"), perkText,
					Messages.get(this, "gold"), Integer.toString( Statistics.goldCollected ) );

			pos += GAP;

			//SPSEXPD: 种子/每日挑战文本可能较长，仍走整行
			if (Dungeon.daily){
				if (!Dungeon.dailyReplay) {
					statSlot(Messages.get(this, "daily_for"), "_" + Dungeon.customSeedText + "_");
				} else {
					statSlot(Messages.get(this, "replay_for"), "_" + Dungeon.customSeedText + "_");
				}
			} else if (!Dungeon.customSeedText.isEmpty()){
				statSlot( Messages.get(this, "custom_seed"), "_" + Dungeon.customSeedText + "_" );
			} else {
				statSlot( Messages.get(this, "dungeon_seed"), DungeonSeed.convertToCode(Dungeon.seed) );
			}

			pos += GAP;
			content.setSize( WIDTH, pos );
			//SPSEXPD: 内容尺寸变了，滚动视口要跟着重新布局（视口裁剪与滚动上限）
			layout();
		}

		//SPSEXPD: 百分比加成格式化（0 显示 "0%"，正数带 +）
		private String bonusText( float ratio ) {
			int pct = Math.round( ratio * 100 );
			return (pct > 0 ? "+" : "") + pct + "%";
		}

		//SPSEXPD: 七系元素加成/抗性摘要 —— 只列非零项（如「火+10% 冰+5%」），全零显示 "0%"
		private String elementText( Hero hero, boolean offensive ) {
			StringBuilder sb = new StringBuilder();
			for (pd.actors.damagetype.Element e : pd.actors.damagetype.Element.values()) {
				float v = offensive
						? pd.actors.hero.HeroStats.elementBonus( hero, e )
						: pd.actors.hero.HeroStats.elementResistance( hero, e );
				int pct = Math.round( v * 100 );
				if (pct == 0) continue;
				if (sb.length() > 0) sb.append( ' ' );
				sb.append( Messages.get( this, "el_" + e.name().toLowerCase( Locale.ENGLISH ) ) )
						.append( pct > 0 ? "+" : "" ).append( pct ).append( '%' );
			}
			return sb.length() == 0 ? "0%" : sb.toString();
		}

		//SPSEXPD: ---- 双列属性行 ------------------------------------------------
		//每行两项：左列在前、右列在后。每列 = 左标签 + 右值（值右对齐到本列右缘），
		//行高取两列较大者（长文本会被 maxWidth 换行，不会横跨到邻列）。
		private static final int COL_GAP = 4;

		private void statRow( String lLabel, String lValue, String rLabel, String rValue ) {
			statRow( lLabel, lValue, rLabel, rValue, 0 );
		}

		/** rValueColor != 0 时给右列的值上色（0 = 默认色） */
		private void statRow( String lLabel, String lValue, String rLabel, String rValue, int rValueColor ) {
			float colW = (WIDTH - COL_GAP) / 2f;
			float h = 0;
			if (lLabel != null) h = Math.max( h, statCell( 0, colW, lLabel, lValue, 0 ) );
			if (rLabel != null) h = Math.max( h, statCell( 1, colW, rLabel, rValue, rValueColor ) );
			pos += GAP + h;
		}

		/** 渲染一列（标签左、值右对齐到列右缘），返回该列占用高度 */
		private float statCell( int col, float colW, String label, String value, int valueColor ) {
			float x0 = col * (colW + COL_GAP);

			int size = 7;
			RenderedTextBlock lab;
			do {
				lab = PixelScene.renderTextBlock( label, size );
				size--;
			} while (size >= 5 && lab.width() >= colW * 0.55f);
			lab.maxWidth( Math.max(8, (int)(colW * 0.6f)) );
			lab.setPos( x0, pos + (6 - lab.height())/2f );
			PixelScene.align( lab );
			content.add( lab );

			size = 7;
			RenderedTextBlock val;
			do {
				val = PixelScene.renderTextBlock( value, size );
				size--;
			} while (size >= 5 && val.width() >= colW * 0.45f);
			val.maxWidth( Math.max(8, (int)(colW * 0.45f)) );
			val.setPos( x0 + colW - val.width(), pos + (6 - val.height())/2f );
			if (valueColor != 0) val.hardlight( valueColor );
			PixelScene.align( val );
			content.add( val );

			return Math.max( lab.height(), val.height() );
		}

		/** SPSEXPD: 整行项（文本可能较长，如种子/每日挑战）——保持原有的满宽单列 */
		private void statSlot( String label, String value ) {

			int size = 8;
			RenderedTextBlock txt;
			do {
				txt = PixelScene.renderTextBlock( label, size );
				size--;
			} while (size >= 5 && txt.width() >= WIDTH * 0.55f);
			txt.setPos(0, pos + (6 - txt.height())/2);
			PixelScene.align(txt);
			content.add( txt );

			size = 8;
			do {
				txt = PixelScene.renderTextBlock( value, size );
				size--;
			} while (size >= 5 && txt.width() >= WIDTH * 0.45f);
			txt.setPos(WIDTH * 0.55f, pos + (6 - txt.height())/2);
			PixelScene.align(txt);
			content.add( txt );
			
			pos += GAP + txt.height();
		}

		//SPSEXPD: 移速格式化（100% 为基准）与着色（更快绿、更慢红）
		private String speedText( Hero hero ) {
			return Math.round( hero.speed() * 100 ) + "%";
		}

		private int speedColor( Hero hero ) {
			float spd = hero.speed();
			if (spd > 1.001f) return 0x44FF44;
			if (spd < 0.999f) return 0xFF6666;
			return 0;
		}
		
		public float height() {
			return pos;
		}
	}

	public class TalentsTab extends Component {

		private static final int GAP = 2;
		private static final int COLS = 5;

		private ScrollPane pane;
		private ArrayList<PerkSlot> slots;

		@Override
		protected void createChildren() {
			super.createChildren();
			//SPSXPD: 特质页 —— 展示已获得的特质（取代原天赋页）
			//注意：createChildren() 会在构造期被 Component 调用，
			//所以字段必须在这里初始化，不能在声明处初始化（否则 NPE）
			slots = new ArrayList<>();
			pane = new ScrollPane(new Component());
			//SPSEXPD: 特质页不显示滚动条（拖动/滚轮滚动保留）
			pane.scrollBarVisible = false;
			add(pane);

			if (pd.Dungeon.hero != null && pd.Dungeon.hero.heroPerk != null) {
				for (pd.actors.hero.perks.Perk perk : pd.Dungeon.hero.heroPerk.getPerks()) {
					PerkSlot slot = new PerkSlot(perk);
					slots.add(slot);
					pane.content().add(slot);
				}
			}
		}

		@Override
		protected void layout() {
			super.layout();
			if (pane == null || slots == null) return;
			pane.setRect(x, y, width, height);

			for (int i = 0; i < slots.size(); i++) {
				int r = i / COLS;
				int c = i % COLS;
				slots.get(i).setRect(
						GAP + c * (PerkSlot.BTN + GAP),
						GAP + r * (PerkSlot.BTN + GAP),
						PerkSlot.BTN, PerkSlot.BTN);
			}
			int rows = Math.max(1, (slots.size() + COLS - 1) / COLS);
			pane.content().setSize(width, Math.max(height, GAP + rows * (PerkSlot.BTN + GAP)));
		}

	}
	
	private class BuffsTab extends Component {
		
		private static final int GAP = 2;
		
		private float pos;
		private ScrollPane buffList;
		private ArrayList<BuffSlot> slots = new ArrayList<>();

		@Override
		protected void createChildren() {

			super.createChildren();

			buffList = new ScrollPane( new Component() ){
				@Override
				public void onClick( float x, float y ) {
					int size = slots.size();
					for (int i=0; i < size; i++) {
						if (slots.get( i ).onClick( x, y )) {
							break;
						}
					}
				}
			};
			add(buffList);
		}
		
		@Override
		protected void layout() {
			super.layout();
			buffList.setRect(0, 0, width, height);
		}
		
		private void setupList() {
			Component content = buffList.content();
			for (Buff buff : Dungeon.hero.buffs()) {
				if (buff.icon() != BuffIndicator.NONE) {
					BuffSlot slot = new BuffSlot(buff);
					slot.setRect(0, pos, WIDTH, slot.icon.height());
					content.add(slot);
					slots.add(slot);
					pos += GAP + slot.height();
				}
			}
			content.setSize(buffList.width(), pos);
			buffList.setSize(buffList.width(), buffList.height());
		}

		private class BuffSlot extends Component {

			private Buff buff;

			Image icon;
			RenderedTextBlock txt;

			public BuffSlot( Buff buff ){
				super();
				this.buff = buff;

				icon = new BuffIcon(buff, true);
				icon.y = this.y;
				add( icon );

				txt = PixelScene.renderTextBlock( Messages.titleCase(buff.name()), 8 );
				txt.setPos(
						icon.width + GAP,
						this.y + (icon.height - txt.height()) / 2
				);
				PixelScene.align(txt);
				add( txt );

			}

			@Override
			protected void layout() {
				super.layout();
				icon.y = this.y;
				txt.maxWidth((int)(width - icon.width()));
				txt.setPos(
						icon.width + GAP,
						this.y + (icon.height - txt.height()) / 2
				);
				PixelScene.align(txt);
			}
			
			protected boolean onClick ( float x, float y ) {
				if (inside( x, y )) {
					GameScene.show(new WndInfoBuff(buff));
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
