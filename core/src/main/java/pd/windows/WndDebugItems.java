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
import pd.items.Item;
import pd.items.bags.ArrowCollecter;
import pd.items.bags.HeartOfScarecrow;
import pd.items.bags.KeyRing;
import pd.items.bags.MagicalHolster;
import pd.items.bags.PotionBandolier;
import pd.items.bags.ScrollHolder;
import pd.items.bags.SeedPouch;
import pd.items.bags.ShoppingCart;
import pd.items.bags.VelvetPouch;
import pd.items.bags.WandHolster;
import pd.items.Generator;
import pd.journal.Catalog;
import pd.journal.SpsCatalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.ui.Button;
import pd.ui.RedButton;
import pd.ui.Window;
import pd.utils.GLog;
import watabou.noosa.Game;
import watabou.utils.DeviceCompat;

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

	//SPS: 调试器分组按功能域划分（武器/护甲/…/包裹袋 28 组，见 GROUP_LABELS），
	//类全集 = SpsCatalog ∪ 破碎 Catalog ∪ Generator 生成表 ∪ 手写补充（图鉴外物品）。
	//功能域 → 组标签（键 = 功能域包名段，与根包名无关；顺序即组菜单顺序）
	private static final LinkedHashMap<String, String> GROUP_LABELS = new LinkedHashMap<>();
	static {
		GROUP_LABELS.put("weapon", "武器");
		GROUP_LABELS.put("armor", "护甲");
		GROUP_LABELS.put("wands", "法杖");
		GROUP_LABELS.put("rings", "戒指");
		GROUP_LABELS.put("artifacts", "神器");
		GROUP_LABELS.put("trinkets", "饰品");
		GROUP_LABELS.put("bags", "包裹袋");
		GROUP_LABELS.put("potions", "药水");
		GROUP_LABELS.put("brewed", "酿造");
		GROUP_LABELS.put("scrolls", "卷轴");
		GROUP_LABELS.put("stones", "符石");
		GROUP_LABELS.put("nornstone", "诺恩石");
		GROUP_LABELS.put("medicine", "药品");
		GROUP_LABELS.put("food", "食物");
		GROUP_LABELS.put("bombs", "炸弹");
		GROUP_LABELS.put("spells", "法术");
		GROUP_LABELS.put("skills", "技能书");
		GROUP_LABELS.put("summon", "召唤物");
		GROUP_LABELS.put("eggs", "宠物蛋");
		GROUP_LABELS.put("keys", "钥匙");
		GROUP_LABELS.put("quest", "任务物品");
		GROUP_LABELS.put("remains", "遗物");
		GROUP_LABELS.put("reward", "奖励");
		GROUP_LABELS.put("sellitem", "出售品");
		GROUP_LABELS.put("challengelists", "挑战书");
		GROUP_LABELS.put("journalpages", "书页");
		GROUP_LABELS.put("journal", "日志");
		GROUP_LABELS.put("misc", "杂物");
	}

	//分组键 = 类所在包中自内向外第一个命中的功能域段（如 items.weapon.melee.fusion.Flute → weapon）
	private static String groupKeyOf(Class<?> type) {
		Package pkg = type.getPackage();
		if (pkg == null) return null;
		String name = pkg.getName();
		int end = name.length();
		while (end > 0) {
			int dot = name.lastIndexOf('.', end - 1);
			String segment = dot < 0 ? name.substring(0, end) : name.substring(dot + 1, end);
			if (GROUP_LABELS.containsKey(segment)) return segment;
			if (dot < 0) break;
			end = dot;
		}
		return null;
	}

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
		all.put(VelvetPouch.class, true);
		all.put(ScrollHolder.class, true);
		all.put(PotionBandolier.class, true);
		all.put(MagicalHolster.class, true);
		all.put(KeyRing.class, true);
		all.put(ArrowCollecter.class, true);
		all.put(ShoppingCart.class, true);
		all.put(HeartOfScarecrow.class, true);

		//按功能域分组（GROUP_LABELS 的顺序即组菜单顺序）
		LinkedHashMap<String, ArrayList<Class<? extends Item>>> map = new LinkedHashMap<>();
		for (String label : GROUP_LABELS.values()) map.put(label, new ArrayList<>());
		ArrayList<Class<? extends Item>> unknown = new ArrayList<>();

		for (Class<? extends Item> t : all.keySet()) {
			//合并前的旧袋子不列出（用户裁决 2026-09-28：只出现合并后的绒布袋/魔法套筒）
			if (t == SeedPouch.class || t == WandHolster.class) continue;
			String label = GROUP_LABELS.get(groupKeyOf(t));
			ArrayList<Class<? extends Item>> list = label == null ? null : map.get(label);
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
