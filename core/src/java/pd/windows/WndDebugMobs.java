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
import pd.actors.Actor;
import pd.actors.mobs.Mob;
import pd.actors.mobs.MobSpawner;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import pd.ui.Button;
import pd.ui.RedButton;
import pd.ui.Window;
import pd.utils.GLog;
import render.noosa.Game;
import render.utils.serialize.Reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

//SPS: 调试工具——召唤怪物弹窗（与 WndDebugItems 同构：组选择页 → 怪物网格页）
//点击在英雄身旁召唤 1 只，长按召唤 3 只。分组 = MobSpawner 每层轮转表（1-26 层）+「全部」。
//同上：窗口只由暂停菜单的「测试时间」挑战入口控制，与构建类型无关。
public class WndDebugMobs extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndDebugMobs.class)
			.t("title", "调试怪物")
			.t("back", "返回")
			.t("all", "全部")
			.t("floor", "第 %1$d 层")
			.t("spawned", "召唤 %1$s")
			.t("failed", "附近没有空位");
	}

	private static final int COLS       = 5;   //SPS: 窄窗适配移动端（调试器）
	private static final int CELL       = 18;
	private static final int WIDTH      = COLS * CELL + 4;
	private static final int PAGE_ROWS  = 4;   //怪物页每页 4 行 = 20 格
	private static final int GROUP_ROWS = 4;   //组菜单每页 4 行 × 2 列 = 8 组
	private static final int GROUP_COLS = 2;

	private static final int MAX_FLOOR = 26;

	private static LinkedHashMap<String, ArrayList<Class<? extends Mob>>> groups;

	public WndDebugMobs() {
		super();
		showGroups(0);
	}

	//SPS: 探测/召唤的怪物类全集来自 MobSpawner 的每层轮转表，
	//这样测试时能直接重现该层真实会出现的怪，而不是另列一份清单。
	private static LinkedHashMap<String, ArrayList<Class<? extends Mob>>> groups() {
		if (groups != null) return groups;

		LinkedHashMap<String, ArrayList<Class<? extends Mob>>> map = new LinkedHashMap<>();
		LinkedHashSet<Class<? extends Mob>> all = new LinkedHashSet<>();

		for (int depth = 1; depth <= MAX_FLOOR; depth++) {
			ArrayList<Class<? extends Mob>> list = new ArrayList<>();
			try {
				for (Class<? extends Mob> t : MobSpawner.getMobRotation(depth)) {
					if (!list.contains(t)) list.add(t);
				}
			} catch (Exception e) {
				//单层轮转异常不影响其它层
			}
			if (list.isEmpty()) continue;
			map.put(Messages.get(WndDebugMobs.class, "floor", depth), list);
			all.addAll(list);
		}
		if (!all.isEmpty()) map.put(Messages.get(WndDebugMobs.class, "all"), new ArrayList<>(all));

		groups = map;
		return map;
	}

	//---- 组选择页 ----

	private WndDebugMobs(int groupPage) {
		super();
		showGroups(groupPage);
	}

	private void showGroups(int page) {
		LinkedHashMap<String, ArrayList<Class<? extends Mob>>> map = groups();

		float pos = 0;

		RedButton backButton = new RedButton(Messages.get(this, "title")) {
			@Override
			protected void onClick() {}
		};
		backButton.enable(false);
		backButton.setRect(0, pos, WIDTH, 16);
		add(backButton);
		pos = backButton.bottom() + 2;

		final ArrayList<String> keys = new ArrayList<>(map.keySet());
		final int pageSize = GROUP_ROWS * GROUP_COLS;
		int pageCount = (keys.size() + pageSize - 1) / pageSize;
		if (pageCount == 0) pageCount = 1;
		final int pages = pageCount;
		final int cur = Math.max(0, Math.min(page, pages - 1));

		float btnW = (WIDTH - 2 * (GROUP_COLS - 1)) / (float) GROUP_COLS;
		float x = 0;
		float rowY = pos;

		int from = cur * pageSize;
		int to = Math.min(keys.size(), from + pageSize);
		for (int i = from; i < to; i++) {
			final String title = keys.get(i);
			RedButton btn = new RedButton(title) {
				@Override
				protected void onClick() {
					hide();
					GameScene.show(new WndDebugMobs(title, groups().get(title), 0));
				}
			};
			btn.textColor(0xCCCCCC);
			btn.setRect(x, rowY, btnW, 16);
			add(btn);

			x += btnW + 2;
			if (x + btnW > WIDTH) {
				x = 0;
				rowY += 16 + 2;
			}
		}
		if (x > 0) rowY += 18;

		//翻页栏
		if (pages > 1) {
			RedButton prev = new RedButton("<") {
				@Override
				protected void onClick() {
					hide();
					GameScene.show(new WndDebugMobs(cur - 1));
				}
			};
			prev.enable(cur > 0);
			prev.setRect(0, rowY + 2, 20, 15);
			add(prev);

			RedButton counter = new RedButton((cur + 1) + "/" + pages) {
				@Override
				protected void onClick() {}
			};
			counter.textColor(Window.TITLE_COLOR);
			counter.enable(false);
			counter.setRect(prev.right() + 2, rowY + 2, WIDTH - 44, 15);
			add(counter);

			RedButton next = new RedButton(">") {
				@Override
				protected void onClick() {
					hide();
					GameScene.show(new WndDebugMobs(cur + 1));
				}
			};
			next.enable(cur < pages - 1);
			next.setRect(counter.right() + 2, rowY + 2, 20, 15);
			add(next);

			rowY = next.bottom();
		}

		resize(WIDTH, (int) rowY + 2);
	}

	//---- 怪物网格页 ----

	private WndDebugMobs(final String title, Collection<Class<? extends Mob>> mobs, int page) {
		super();

		float pos = 0;

		RedButton backButton = new RedButton(Messages.get(this, "back")) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show(new WndDebugMobs());
			}
		};
		backButton.setRect(0, pos, WIDTH / 2 - 1, 15);
		add(backButton);

		RedButton titleButton = new RedButton(title) {
			@Override
			protected void onClick() {}
		};
		titleButton.textColor(Window.TITLE_COLOR);
		titleButton.enable(false);
		titleButton.setRect(backButton.right() + 2, pos, WIDTH / 2 - 1, 15);
		add(titleButton);
		pos = titleButton.bottom() + 2;

		final ArrayList<Class<? extends Mob>> list = new ArrayList<>(mobs);
		final int pageSize = COLS * PAGE_ROWS;
		int pageCount = (list.size() + pageSize - 1) / pageSize;
		if (pageCount == 0) pageCount = 1;
		final int pages = pageCount;
		final int cur = Math.max(0, Math.min(page, pages - 1));

		float x = 0;
		float rowY = pos;
		int from = cur * pageSize;
		int to = Math.min(list.size(), from + pageSize);
		for (int i = from; i < to; i++) {
			MobBtn btn = new MobBtn(list.get(i));
			btn.setRect(x, rowY, CELL - 2, CELL - 2);
			add(btn);

			x += CELL;
			if (x + CELL - 2 > WIDTH) {
				x = 0;
				rowY += CELL;
			}
		}
		if (x > 0) rowY += CELL;

		//翻页栏
		if (pages > 1) {
			RedButton prev = new RedButton("<") {
				@Override
				protected void onClick() {
					hide();
					GameScene.show(new WndDebugMobs(title, list, cur - 1));
				}
			};
			prev.enable(cur > 0);
			prev.setRect(0, rowY + 2, 20, 15);
			add(prev);

			RedButton counter = new RedButton((cur + 1) + "/" + pages) {
				@Override
				protected void onClick() {}
			};
			counter.textColor(Window.TITLE_COLOR);
			counter.enable(false);
			counter.setRect(prev.right() + 2, rowY + 2, WIDTH - 44, 15);
			add(counter);

			RedButton next = new RedButton(">") {
				@Override
				protected void onClick() {
					hide();
					GameScene.show(new WndDebugMobs(title, list, cur + 1));
				}
			};
			next.enable(cur < pages - 1);
			next.setRect(counter.right() + 2, rowY + 2, 20, 15);
			add(next);

			rowY = next.bottom();
		}

		resize(WIDTH, (int) rowY + 2);
	}

	//SPS: 在英雄周围找一格可站立的空位（含相邻 8 格 → 半径 2），找不到返回 -1
	private static int findSpawnCell() {
		int hero = Dungeon.hero.pos;
		int width = Dungeon.level.width();

		ArrayList<Integer> ring1 = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int c = hero + offset;
			if (c < 0 || c >= Dungeon.level.length()) continue;
			if (!Dungeon.level.passable[c] || Actor.findChar(c) != null) continue;
			ring1.add(c);
		}
		if (!ring1.isEmpty()) return ring1.get(0);

		//半径 2 兜底
		for (int y = -2; y <= 2; y++) {
			for (int x = -2; x <= 2; x++) {
				if (Math.abs(x) < 2 && Math.abs(y) < 2) continue;
				int c = hero + x + y * width;
				if (c < 0 || c >= Dungeon.level.length()) continue;
				if (!Dungeon.level.passable[c] || Actor.findChar(c) != null) continue;
				return c;
			}
		}
		return -1;
	}

	//怪物格子：点击召唤 1 只，长按召唤 3 只
	private class MobBtn extends Button {

		private Class<? extends Mob> type;
		private CharSprite sprite;
		private String mobName;

		MobBtn(Class<? extends Mob> type) {
			this.type = type;
			try {
				Mob mob = type.getDeclaredConstructor().newInstance();
				mobName = mob.name();
				try {
					sprite = Reflection.newInstance(mob.spriteClass);
				} catch (Exception ignored) {
					sprite = null;
				}
			} catch (Exception e) {
				mobName = type.getSimpleName();
				sprite = null;
			}
			if (sprite == null) sprite = new CharSprite();
			add(sprite);
		}

		@Override
		protected void layout() {
			super.layout();
			sprite.x = x + (width - sprite.width()) / 2f;
			sprite.y = y + (height - sprite.height()) / 2f;
		}

		@Override
		protected void onClick() {
			spawn(1);
		}

		@Override
		protected boolean onLongClick() {
			spawn(3);
			return true;
		}

		private void spawn(int qty) {
			try {
				int cell = findSpawnCell();
				if (cell < 0) {
					GLog.w(Messages.get(WndDebugMobs.class, "failed"));
					return;
				}
				for (int i = 0; i < qty; i++) {
					Mob mob = type.getDeclaredConstructor().newInstance();
					mob.pos = cell;
					GameScene.add(mob);
					//下一只另找空位（同格会重叠）
					cell = findSpawnCell();
					if (cell < 0) break;
				}
				GLog.h(Messages.get(WndDebugMobs.class, "spawned", mobName));
			} catch (Exception e) {
				Game.reportException(e);
			}
		}

		@Override
		protected String hoverText() {
			return mobName;
		}
	}
}
