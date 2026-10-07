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
import pd.ui.Window;

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

	private final ArrayList<Perk> perks;
	private final ArrayList<PerkSlot> slots = new ArrayList<>();

	private RenderedTextBlock titleBlock;
	private RenderedTextBlock descBlock;
	protected RedButton confirm;

	private int selected = -1;

	public WndSelectPerk(String title, ArrayList<Perk> perks) {
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

		descBlock = PixelScene.renderTextBlock("", 6);
		descBlock.maxWidth(WIDTH - GAP * 2);
		add(descBlock);

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
			select(0);
		} else {
			relayout();
		}
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

		//说明文字
		descBlock.maxWidth(WIDTH - GAP * 2);
		descBlock.setPos(GAP, top + rows() * (SLOT + GAP) + GAP);

		//确认按钮
		confirm.setRect(0, descBlock.bottom() + GAP, WIDTH, BTN_H);

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
		descBlock.spans(spans, WIDTH - GAP * 2);
	}

	/** 子类实现选中后的处理 */
	protected abstract void onPerkSelected(Perk perk);
}
