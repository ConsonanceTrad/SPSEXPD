/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import java.util.ArrayList;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.actors.hero.perks.Perk;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.RedButton;
import pd.ui.Window;
import render.utils.serialize.Reflection;

/**
 * SPSXPD: 调试器 —— 直接给英雄授予任意特质（含权重 0、正常途径拿不到的那些），便于在游戏内测试。
 * 点一下即授予；同一种再点一次会升级它。
 */
public class WndDebugPerks extends Window {

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(WndDebugPerks.class)
			.t("title", "调试特质")
			.t("hint", "点击即授予（再点一次升级）")
			.t("prev", "上一页")
			.t("next", "下一页")
			.t("back", "返回");
	}

	private static final int WIDTH = 120;
	private static final int COLS = 2;
	private static final int ROWS = 4;
	private static final int PER_PAGE = COLS * ROWS;
	private static final int BTN_H = 16;
	private static final int GAP = 2;

	private final int page;
	private final int pages;

	public WndDebugPerks() {
		this(0);
	}

	private WndDebugPerks(int page) {
		super();

		ArrayList<Class<? extends Perk>> all = Perk.Companion.allClasses();
		this.pages = Math.max(1, (all.size() + PER_PAGE - 1) / PER_PAGE);
		this.page = Math.max(0, Math.min(page, this.pages - 1));

		float pos = 0;

		RedButton head = new RedButton(Messages.get(this, "title")) {
			@Override
			protected void onClick() {
			}
		};
		head.enable(false);
		head.setRect(0, pos, WIDTH, BTN_H);
		add(head);
		pos = head.bottom() + GAP;

		RedButton hint = new RedButton(Messages.get(this, "hint")) {
			@Override
			protected void onClick() {
			}
		};
		hint.enable(false);
		hint.setRect(0, pos, WIDTH, BTN_H);
		add(hint);
		pos = hint.bottom() + GAP;

		int from = this.page * PER_PAGE;
		int to = Math.min(all.size(), from + PER_PAGE);
		float btnW = (WIDTH - (COLS - 1) * GAP) / (float) COLS;
		for (int i = from; i < to; i++) {
			final Class<? extends Perk> cls = all.get(i);
			int slot = i - from;
			RedButton btn = new RedButton(labelOf(cls)) {
				@Override
				protected void onClick() {
					grant(cls);
				}
			};
			btn.textColor(0xCCCCCC);
			btn.setRect((slot % COLS) * (btnW + GAP), pos + (slot / COLS) * (BTN_H + GAP), btnW, BTN_H);
			add(btn);
		}
		pos += ROWS * (BTN_H + GAP);

		if (this.pages > 1) {
			RedButton prev = new RedButton(Messages.get(this, "prev")) {
				@Override
				protected void onClick() {
					if (WndDebugPerks.this.page > 0) {
						hide();
						GameScene.show(new WndDebugPerks(WndDebugPerks.this.page - 1));
					}
				}
			};
			prev.enable(this.page > 0);
			prev.setRect(0, pos, (WIDTH - GAP) / 2f, BTN_H);
			add(prev);

			RedButton next = new RedButton(Messages.get(this, "next")) {
				@Override
				protected void onClick() {
					if (WndDebugPerks.this.page < WndDebugPerks.this.pages - 1) {
						hide();
						GameScene.show(new WndDebugPerks(WndDebugPerks.this.page + 1));
					}
				}
			};
			next.enable(this.page < this.pages - 1);
			next.setRect(WIDTH / 2f + GAP / 2f, pos, (WIDTH - GAP) / 2f, BTN_H);
			add(next);
			pos += BTN_H + GAP;
		}

		RedButton back = new RedButton(Messages.get(this, "back")) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		back.setRect(0, pos, WIDTH, BTN_H);
		add(back);
		pos += BTN_H;

		resize(WIDTH, (int) pos);
	}

	/** 特质名（实例化一次取 title） */
	private static String labelOf(Class<? extends Perk> cls) {
		Perk sample = Reflection.newInstance(cls);
		return sample == null ? cls.getSimpleName() : sample.title();
	}

	/** 授予：已拥有则升级，未拥有则加入并提示 */
	private void grant(Class<? extends Perk> cls) {
		Hero hero = Dungeon.hero;
		if (hero == null) return;
		Perk perk = Reflection.newInstance(cls);
		if (perk == null) return;
		hero.heroPerk.grantIfMissing(perk, hero);
	}
}