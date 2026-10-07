/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 特质选择窗：网格展示候选特质（每行居中），下方显示说明与确认按钮。
 * 子类可在确认按钮下方追加按钮（见 extraHeight()/layoutExtra()）。
 */

package pd.windows;

import java.util.ArrayList;

import pd.actors.hero.perks.Perk;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.messages.Span;
import pd.scenes.PixelScene;
import pd.ui.PerkSlot;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.Window;
import render.noosa.ui.Component;

public abstract class WndSelectPerk extends Window {

	static {
		InlineText.of(WndSelectPerk.class)
				.t("confirm", "确认")
				.t("upgrade_to", "（升级至 %d 级）");
	}

	protected static final int WIDTH = 130;
	protected static final int BTN_H = 16;
	private static final int SLOT = PerkSlot.BTN;
	private static final int COLS = 4;
	private static final int GAP = 2;
	/** 描述区固定展示的行数（更长的内容在视口内滚动） */
	private static final int DESC_LINES = 6;
	/** 右侧滚动条占位宽度 */
	private static final int SCROLLBAR = 3;

	/** 说明文字视口高度：按当前字号实测 DESC_LINES 行的高度（懒算并按缩放缓存） */
	private static int descViewHeight = 0;
	private static float descViewZoom = -1f;

	private final ArrayList<Perk> perks;
	private final ArrayList<PerkSlot> slots = new ArrayList<>();

	private RenderedTextBlock titleBlock;
	private RenderedTextBlock descBlock;
	private ScrollPane descPane;
	protected RedButton confirm;

	private int selected = -1;
	/** 描述内容变化后需要把视口滚回顶部 */
	private boolean resetScroll;

	public WndSelectPerk(String title, ArrayList<Perk> perks) {
		this(title, perks, 0);
	}

	public WndSelectPerk(String title, ArrayList<Perk> perks, int initialSelection) {
		super();

		this.perks = perks;

		titleBlock = PixelScene.renderTextBlock(title, 9);
		titleBlock.hardlight(Window.TITLE_COLOR);
		add(titleBlock);

		for (int i = 0; i < perks.size(); i++) {
			final int index = i;
			PerkSlot slot = new PerkSlot(perks.get(i)) {
				@Override
				protected void onClick() {
					select(index);
				}
			};
			slots.add(slot);
			add(slot);
		}

		//说明文字放进固定高度的滚动视口：过长时内部滚动，按钮位置不再跳动
		descBlock = PixelScene.renderTextBlock("", 6);
		descBlock.maxWidth(descWidth());
		descPane = new ScrollPane(new Component());
		descPane.content().add(descBlock);
		add(descPane);

		confirm = new RedButton(Messages.get(WndSelectPerk.class, "confirm")) {
			@Override
			protected void onClick() {
				super.onClick();
				if (selected >= 0 && selected < WndSelectPerk.this.perks.size()) {
					onPerkSelected(WndSelectPerk.this.perks.get(selected));
				}
			}
		};
		add(confirm);

		if (!perks.isEmpty()) {
			select(Math.max(0, Math.min(initialSelection, perks.size() - 1)));
		} else {
			relayout();
		}
	}

	/** 当前选中的候选下标（无选中时为 -1），供子类做「只重随选中格」 */
	protected int selectedIndex() {
		return selected;
	}

	/** 候选列表（只读用途） */
	protected ArrayList<Perk> perkList() {
		return perks;
	}

	private int rows() {
		return Math.max(1, (slots.size() + COLS - 1) / COLS);
	}

	/** 子类覆写：确认按钮下方追加区域的高度（默认 0） */
	protected float extraHeight() {
		return 0f;
	}

	/** 子类覆写：摆放追加按钮，top 为其起始 y */
	protected void layoutExtra(float top) {
	}

	/** 重新计算整窗布局（含子类追加的按钮），子类追加按钮后需再调一次 */
	protected void relayout() {
		titleBlock.setPos(GAP, 0);

		float top = titleBlock.height() + GAP;

		//候选格子：每行独立居中（避免数量不足一行时偏向左侧）
		for (int r = 0; r < rows(); r++) {
			int countInRow = Math.min(COLS, slots.size() - r * COLS);
			float rowWidth = countInRow * SLOT + Math.max(0, countInRow - 1) * GAP;
			float left = (WIDTH - rowWidth) / 2f;
			for (int c = 0; c < countInRow; c++) {
				slots.get(r * COLS + c).setRect(
						left + c * (SLOT + GAP),
						top + r * (SLOT + GAP),
						SLOT, SLOT);
			}
		}

		//说明文字：固定高度的视口（过长时内部滚动）。先更新内容尺寸再布局，
		//否则 ScrollPane 的滚动条可见性会基于旧的 content 高度
		float descTop = top + rows() * (SLOT + GAP) + GAP;
		float viewH = descViewHeight();
		descBlock.setPos(0, 0);
		descPane.content().setSize(WIDTH - GAP * 2, Math.max(viewH, descBlock.height()));
		descPane.setRect(GAP, descTop, WIDTH - GAP * 2, viewH);
		if (resetScroll) {
			descPane.scrollTo(0, 0);
			resetScroll = false;
		}

		//确认按钮：位置只取决于固定视口，不随描述长度变化
		confirm.setRect(0, descTop + viewH + GAP, WIDTH, BTN_H);

		//子类追加按钮
		float extraTop = confirm.bottom() + GAP;
		layoutExtra(extraTop);

		resize(WIDTH, (int) (extraTop + extraHeight() + 2));
	}

	private void select(int index) {
		selected = index;
		for (int i = 0; i < slots.size(); i++) {
			slots.get(i).showHighlight(i == index);
		}
		if (index >= 0 && index < perks.size()) {
			showDescription(perks.get(index));
		}
		relayout();
	}

	/**
	 * 标题 + 富文本描述：升级后会变化的数值为绿字，未达等级才解锁的效果为灰字。
	 * 候选实例的等级 > 1 表示这是「已拥有特质的升级预览」，标题里标出升级后的等级。
	 */
	private void showDescription(Perk p) {
		ArrayList<Span> spans = new ArrayList<>();
		String title = p.title();
		if (p.level() > 1) {
			title += " " + Messages.get(WndSelectPerk.class, "upgrade_to", p.level());
		}
		spans.add(new Span(title + "\n", Span.DEFAULT));
		spans.addAll(p.describeRich());
		descBlock.spans(spans, descWidth());
		resetScroll = true;
	}

	/** 描述文本可用宽度（视口宽减去滚动条占位） */
	private static int descWidth() {
		return WIDTH - GAP * 2 - SCROLLBAR;
	}

	/** 视口高度：实测 DESC_LINES 行文字的高度，缩放变化时重算 */
	private static int descViewHeight() {
		float zoom = PixelScene.defaultZoom;
		if (descViewHeight <= 0 || zoom != descViewZoom) {
			StringBuilder probe = new StringBuilder("字");
			for (int i = 1; i < DESC_LINES; i++) probe.append("\n字");
			descViewHeight = Math.max(1,
					Math.round(PixelScene.renderTextBlock(probe.toString(), 6).height()));
			descViewZoom = zoom;
		}
		return descViewHeight;
	}

	/** 子类实现选中后的处理 */
	protected abstract void onPerkSelected(Perk perk);
}
