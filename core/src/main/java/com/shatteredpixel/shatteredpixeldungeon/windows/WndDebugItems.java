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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.SpsCatalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

//SPS: 调试工具——获取物品弹窗（原创缺口：原版/SPS 均无，仅 INDEV 构建可用）
//两级导航：组选择页 → 物品网格页；点击获得 1 个，长按获得 10 个。
//物品全集 = SpsCatalog 七组（SPS 0.9.8 目录 371 条目）+ 破碎 Catalog 各组补集。
public class WndDebugItems extends Window {

	private static final int COLS       = 5;   //SPS: 窄窗适配移动端（调试器）
	private static final int CELL       = 18;
	private static final int WIDTH      = COLS * CELL + 4;
	private static final int PAGE_ROWS  = 4;   //物品页每页 4 行 = 20 格
	private static final int GROUP_ROWS = 4;   //组菜单每页 4 行 × 2 列 = 8 组
	private static final int GROUP_COLS = 2; //两列，保证组名放得下

	private static LinkedHashMap<String, ArrayList<Class<? extends Item>>> groups;

	public WndDebugItems() {
		super();
		if (DeviceCompat.isDebug()) {
			showGroups(0);
		} else {
			hide();
		}
	}

	//SPS: 调试器分组按物品 Java 包细分（武器/护甲/…/包裹袋 28 类），
	//类全集 = SpsCatalog ∪ 破碎 Catalog ∪ Generator 生成表 ∪ 手写补充（图鉴外物品）。
	private static LinkedHashMap<String, ArrayList<Class<? extends Item>>> groups() {
		if (groups != null) return groups;

		LinkedHashMap<Class<? extends Item>, Boolean> all = new LinkedHashMap<>();
		for (SpsCatalog cat : SpsCatalog.values()) {
			for (Class<? extends Item> t : cat.items()) all.put(t, true);
		}
		for (Catalog cat : Catalog.values()) {
			for (Class<?> t : cat.items()) {
				if (Item.class.isAssignableFrom(t)) all.put((Class<? extends Item>) t, true);
			}
		}
		for (Generator.Category gc : Generator.Category.values()) {
			if (gc.classes == null) continue;   //部分 Category 仅有 superClass，classes 惰性赋值
			for (Class<?> t : gc.classes) {
				if (Item.class.isAssignableFrom(t)) all.put((Class<? extends Item>) t, true);
			}
		}
		//图鉴/生成表外的手写补充：包裹袋全家（含种子包旧存档兼容）、钥匙环、弹药收集器
		all.put(com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch.class, true);
		all.put(com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder.class, true);
		all.put(com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier.class, true);
		all.put(com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster.class, true);
		all.put(com.shatteredpixel.shatteredpixeldungeon.items.bags.KeyRing.class, true);
		all.put(com.shatteredpixel.shatteredpixeldungeon.items.bags.ArrowCollecter.class, true);
		all.put(com.shatteredpixel.shatteredpixeldungeon.items.bags.ShoppingCart.class, true);
		all.put(com.shatteredpixel.shatteredpixeldungeon.items.bags.HeartOfScarecrow.class, true);

		//按包分组 + 中文标签（顺序即组菜单顺序）
		LinkedHashMap<String, String> labels = new LinkedHashMap<>();
		labels.put("weapon", "武器");
		labels.put("armor", "护甲");
		labels.put("wands", "法杖");
		labels.put("rings", "戒指");
		labels.put("artifacts", "神器");
		labels.put("trinkets", "饰品");
		labels.put("bags", "包裹袋");
		labels.put("potions", "药水");
		labels.put("brewed", "酿造");
		labels.put("scrolls", "卷轴");
		labels.put("stones", "符石");
		labels.put("nornstone", "诺恩石");
		labels.put("medicine", "药品");
		labels.put("food", "食物");
		labels.put("bombs", "炸弹");
		labels.put("spells", "法术");
		labels.put("skills", "技能书");
		labels.put("summon", "召唤物");
		labels.put("eggs", "宠物蛋");
		labels.put("keys", "钥匙");
		labels.put("quest", "任务物品");
		labels.put("remains", "遗物");
		labels.put("reward", "奖励");
		labels.put("sellitem", "出售品");
		labels.put("challengelists", "挑战书");
		labels.put("journalpages", "书页");
		labels.put("journal", "日志");
		labels.put("misc", "杂物");

		LinkedHashMap<String, ArrayList<Class<? extends Item>>> map = new LinkedHashMap<>();
		for (String key : labels.keySet()) map.put(labels.get(key), new ArrayList<>());
		ArrayList<Class<? extends Item>> unknown = new ArrayList<>();

		for (Class<? extends Item> t : all.keySet()) {
			//合并前的旧袋子不列出（用户裁决 2026-09-28：只出现合并后的绒布袋/魔法套筒）
			if (t == com.shatteredpixel.shatteredpixeldungeon.items.bags.SeedPouch.class
					|| t == com.shatteredpixel.shatteredpixeldungeon.items.bags.WandHolster.class) continue;
			String pkg = t.getPackage().getName();
			String key = pkg.substring(pkg.lastIndexOf('.') + 1);
			ArrayList<Class<? extends Item>> list = map.get(labels.get(key));
			if (list != null) {
				list.add(t);
			} else {
				unknown.add(t);
			}
		}
		if (!unknown.isEmpty()) map.put("其他", unknown);

		//剔除空组（如奖励/出售品暂无独立类时）
		map.values().removeIf(ArrayList::isEmpty);

		groups = map;
		return map;
	}

	//---- 组选择页 ----

	//组页翻页入口（调试器菜单分页，避免移动端窗口过高）
	private WndDebugItems(int groupPage) {
		super();
		showGroups(groupPage);
	}

	private void showGroups(int page) {
		LinkedHashMap<String, ArrayList<Class<? extends Item>>> map = groups();

		float pos = 0;

		RedButton backButton = new RedButton(Messages.get(this, "title")) {
			@Override
			protected void onClick() {}
		};
		backButton.enable(false);
		backButton.setRect(0, pos, WIDTH, 16);
		add(backButton);
		pos = backButton.bottom() + 2;

		//SPS: 组菜单分页（每页 GROUP_ROWS 行 × 2 列），窄窗适配移动端
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
					GameScene.show(new WndDebugItems(title, groups().get(title), 0));
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
					GameScene.show(new WndDebugItems(cur - 1));
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
					GameScene.show(new WndDebugItems(cur + 1));
				}
			};
			next.enable(cur < pages - 1);
			next.setRect(counter.right() + 2, rowY + 2, 20, 15);
			add(next);

			rowY = next.bottom();
		}

		resize(WIDTH, (int) rowY + 2);
	}

	//---- 物品网格页 ----

	private WndDebugItems(final String title, Collection<Class<? extends Item>> items, int page) {
		super();

		float pos = 0;

		RedButton backButton = new RedButton(Messages.get(this, "back")) {
			@Override
			protected void onClick() {
				hide();
				GameScene.show(new WndDebugItems());
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

		//SPS: 分页显示（每页 COLS×PAGE_ROWS 格），窄窗适配移动端调试
		final ArrayList<Class<? extends Item>> list = new ArrayList<>(items);
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
			ItemBtn btn = new ItemBtn(list.get(i));
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
					GameScene.show(new WndDebugItems(title, list, cur - 1));
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
					GameScene.show(new WndDebugItems(title, list, cur + 1));
				}
			};
			next.enable(cur < pages - 1);
			next.setRect(counter.right() + 2, rowY + 2, 20, 15);
			add(next);

			rowY = next.bottom();
		}

		resize(WIDTH, (int) rowY + 2);
	}

	//物品格子：点击得 1 个，长按得 10 个
	private class ItemBtn extends Button {

		private Class<? extends Item> type;
		private ItemSprite sprite;
		private String itemName;

		ItemBtn(Class<? extends Item> type) {
			this.type = type;
			try {
				Item item = type.getDeclaredConstructor().newInstance();
				itemName = item.name();
				sprite = new ItemSprite(item);
				add(sprite);
			} catch (Exception e) {
				itemName = type.getSimpleName();
				sprite = new ItemSprite();
				add(sprite);
			}
		}

		@Override
		protected void layout() {
			super.layout();
			sprite.x = x + (width - sprite.width()) / 2f;
			sprite.y = y + (height - sprite.height()) / 2f;
		}

		@Override
		protected void onClick() {
			give(1);
		}

		@Override
		protected boolean onLongClick() {
			give(10);
			return true;
		}

		private void give(int qty) {
			try {
				for (int i = 0; i < qty; i++) {
					Item item = type.getDeclaredConstructor().newInstance();
					item.identify(true);
					if (!item.collect(Dungeon.hero.belongings.backpack)) {
						//背包满时直接丢脚下
						Dungeon.level.drop(item, Dungeon.hero.pos);
					}
				}
				GLog.h(Messages.get(WndDebugItems.class, "received", itemName, qty));
			} catch (Exception e) {
				Game.reportException(e);
			}
		}

		@Override
		protected String hoverText() {
			return itemName;
		}
	}
}
