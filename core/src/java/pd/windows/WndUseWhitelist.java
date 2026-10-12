/*
 * 连续使用的「物品类型白名单」窗口 —— 勾选哪些类型的物品在用后不关闭操作框。
 *
 * <p>与 {@link WndChallenges} 同构：分页勾选、没有确定按钮，关闭窗口（返回键 / 点窗外）
 * 时把勾选结果合并成位掩码写回 {@link pd.SPDSettings#continuousUseTypes()}。</p>
 */

package pd.windows;

import pd.ShatteredPixelDungeon;
import pd.items.ContinuousUse;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.PixelScene;
import pd.ui.CheckBox;
import pd.ui.IconButton;
import pd.ui.Icons;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

import java.util.ArrayList;

public class WndUseWhitelist extends Window {

	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndUseWhitelist.class)
			.t("title", "连续使用：物品类型")
			.t("scroll", "卷轴")
			.t("scroll_desc", "所有卷轴，含异界卷轴。")
			.t("potion", "药水 / 药剂")
			.t("potion_desc", "药水、异界药水、调制药剂与灵液。")
			.t("food", "食物")
			.t("food_desc", "一切可食用的食物（口粮、果实、蔬菜、肉食等）。")
			.t("brewed", "调制物")
			.t("brewed_desc", "无味果一类调制物（它不是食物的子类，需要单独勾选）。")
			.t("pill", "药丸")
			.t("pill_desc", "药丸与蘑菇（它与食物也不共享类型）。")
			.t("drink", "饮料")
			.t("drink_desc", "露珠瓶、水晶瓶与修补补药。它们不是药水的子类，需要单独勾选。")
			.t("seed", "种子")
			.t("seed_desc", "各类植物的种子。")
			.t("spell", "法术")
			.t("spell_desc", "可从背包施放的法术结晶等。")
			.t("stone", "符文石")
			.t("stone_desc", "可从背包直接使用的符文石。");
	}

	private static final int WIDTH = 130;
	private static final int TTL_HEIGHT = 16;
	private static final int BTN_HEIGHT = 16;
	private static final int GAP = 1;

	//SPS: 一屏放不下就分页，每页 6 项（与挑战窗口一致）
	private static final int PAGE_SIZE = 6;

	//SPS: 全部项一次建成，翻页只切换可见性（重建窗口会让场景的窗口/输入状态错乱）
	private ArrayList<CheckBox> boxes;
	private ArrayList<IconButton> infos;

	private RedButton prev;
	private RedButton next;
	private RedButton counter;

	private int page;
	private int pages;

	public WndUseWhitelist( int checked ) {

		super();

		RenderedTextBlock title = PixelScene.renderTextBlock(
				Messages.titleCase(Messages.get(this, "title")), 9 );
		title.hardlight( TITLE_COLOR );
		title.setPos( (WIDTH - title.width()) / 2, (TTL_HEIGHT - title.height()) / 2 );
		PixelScene.align( title );
		add( title );

		boxes = new ArrayList<>();
		infos = new ArrayList<>();

		pages = (ContinuousUse.NAME_IDS.length + PAGE_SIZE - 1) / PAGE_SIZE;

		float pos = TTL_HEIGHT;
		for (int i = 0; i < ContinuousUse.NAME_IDS.length; i++) {

			final String key = ContinuousUse.NAME_IDS[i];

			CheckBox cb = new CheckBox( Messages.get(this, key) );
			cb.checked( (checked & ContinuousUse.MASKS[i]) != 0 );
			cb.setRect( 0, pos, WIDTH - 16, BTN_HEIGHT );

			add( cb );
			boxes.add( cb );

			IconButton info = new IconButton( Icons.get(Icons.INFO) ) {
				@Override
				protected void onClick() {
					super.onClick();
					//SPSEXPD: 本按钮会换窗，按下状态收不到 pointerUp，先清掉亮度残留
					icon().resetColor();
					ShatteredPixelDungeon.scene().addToFront(
							new WndMessage( Messages.get(WndUseWhitelist.class, key + "_desc") ) );
				}
			};
			info.setRect( cb.right(), pos, 16, BTN_HEIGHT );
			add( info );
			infos.add( info );

			pos = cb.bottom() + GAP;
		}

		if (pages > 1) {
			prev = new RedButton( "<" ) {
				@Override
				protected void onClick() {
					if (page > 0) {
						page--;
						applyPage();
					}
				}
			};
			prev.setRect( 0, pos + 1, 20, 15 );
			add( prev );

			counter = new RedButton( "" ) {
				@Override
				protected void onClick() {}
			};
			counter.textColor( TITLE_COLOR );
			counter.enable( false );
			counter.setRect( prev.right() + 2, pos + 1, WIDTH - 44, 15 );
			add( counter );

			next = new RedButton( ">" ) {
				@Override
				protected void onClick() {
					if (page < pages - 1) {
						page++;
						applyPage();
					}
				}
			};
			next.setRect( counter.right() + 2, pos + 1, 20, 15 );
			add( next );

			pos = next.bottom();
		}

		resize( WIDTH, (int)pos );

		applyPage();
	}

	/** 按当前页重排：不可见的项只置 visible=false，可见项紧凑排列 */
	private void applyPage() {

		int from = page * PAGE_SIZE;
		int to = Math.min( from + PAGE_SIZE, boxes.size() );

		float pos = TTL_HEIGHT;
		for (int i = 0; i < boxes.size(); i++) {

			boolean show = i >= from && i < to;

			CheckBox cb = boxes.get( i );
			cb.visible = show;
			cb.active = show;

			IconButton info = infos.get( i );
			info.visible = show;

			if (show) {
				cb.setRect( 0, pos, WIDTH - 16, BTN_HEIGHT );
				info.setRect( cb.right(), pos, 16, BTN_HEIGHT );
				pos = cb.bottom() + GAP;
			}
		}

		if (pages > 1) {
			counter.text( (page + 1) + "/" + pages );
			prev.enable( page > 0 );
			next.enable( page < pages - 1 );

			prev.setRect( 0, pos + 1, 20, 15 );
			counter.setRect( prev.right() + 2, pos + 1, WIDTH - 44, 15 );
			next.setRect( counter.right() + 2, pos + 1, 20, 15 );

			pos = next.bottom();
		}

		resize( WIDTH, (int)pos );
	}

	@Override
	public void onBackPressed() {

		int value = 0;
		for (int i = 0; i < boxes.size(); i++) {
			if (boxes.get( i ).checked()) {
				value |= ContinuousUse.MASKS[i];
			}
		}
		pd.SPDSettings.continuousUseTypes( value );

		super.onBackPressed();
	}
}
