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
import pd.items.ArmorKit;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.bags.BambooBasket;
import pd.items.equipment.bags.ArrowCollecter;
import pd.items.equipment.bags.HeartOfScarecrow;
import pd.items.equipment.bags.KeyRing;
import pd.items.equipment.bags.MagicalHolster;
import pd.items.equipment.bags.PotionBandolier;
import pd.items.equipment.bags.ScrollHolder;
import pd.items.equipment.bags.ShoppingCart;
import pd.items.equipment.bags.VelvetPouch;
import pd.items.consum.food.Blandfruit;
import pd.items.consum.food.fruit.Durian;
import pd.items.consum.food.processed.*;
import pd.items.consum.food.vegetable.*;
import pd.items.consum.potions.elixirs.WishPotion;
import pd.items.equipment.weapon.missiles.arrows.*;
import pd.items.misc.Ankhshield;
import pd.items.misc.DewBadge;
import pd.journal.Catalog;
import pd.plants.*;
import pd.journal.SpsCatalog;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.ui.Button;
import pd.ui.RedButton;
import pd.ui.Window;
import pd.utils.GLog;
import render.noosa.Game;
import render.utils.platform.DeviceCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import pd.messages.InlineText;

//SPS: 调试工具——获取物品弹窗（原创缺口：原版/SPS 均无，仅 INDEV 构建可用）
//两级导航：组选择页 → 物品网格页；点击获得 1 个，长按获得 10 个。
//物品全集 = SpsCatalog 七组（SPS 0.9.8 目录 371 条目）+ 破碎 Catalog 各组补集。
public class WndDebugItems extends Window {
	//SPSEXPD: inline Chinese text (generated from messages/windows/zh)
	static {
		InlineText.of(WndDebugItems.class)
			.t("title", "调试器")
			.t("back", "返回")
			.t("received", "获得 %1$s ×%2$d");
	}




	private static final int COLS       = 5;   //SPS: 窄窗适配移动端（调试器）
	private static final int CELL       = 18;
	private static final int WIDTH      = COLS * CELL + 4;
	private static final int PAGE_ROWS  = 4;   //物品页每页 4 行 = 20 格
	private static final int GROUP_ROWS = 4;   //组菜单每页 4 行 × 2 列 = 8 组
	private static final int GROUP_COLS = 2; //两列，保证组名放得下

	private static LinkedHashMap<String, ArrayList<Class<? extends Item>>> groups;

	public WndDebugItems() {
		super();
		//SPS: 与构建类型无关，窗口只由暂停菜单的「测试时间」挑战入口控制
		showGroups(0);
	}

	//SPS: 调试器分组按功能域划分（武器/护甲/…/包裹袋 28 组，见 GROUP_LABELS），
	//类全集 = SpsCatalog ∪ 破碎 Catalog ∪ Generator 生成表 ∪ 手写补充（图鉴外物品）。
	//功能域 → 组标签（键 = 功能域包名段，与根包名无关；顺序即组菜单顺序）
	private static final LinkedHashMap<String, String> GROUP_LABELS = new LinkedHashMap<>();
	static {
		GROUP_LABELS.put("consum",    "消耗品");
		GROUP_LABELS.put("equipment", "装备");
		GROUP_LABELS.put("ground",    "地面");
		GROUP_LABELS.put("specific",  "特殊");
	}

	//SPSXPD: 分组键 = pd/items 下的大类段（consum/equipment/ground/specific），
	//与图集目录结构一致；未分组的（misc/summon/顶层类）归入「其他」。
	private static String groupKeyOf(Class<?> type) {
		Package pkg = type.getPackage();
		if (pkg == null) return null;
		String name = pkg.getName();
		if (!name.startsWith("pd.items.")) return null;
		String rest = name.substring("pd.items.".length());
		int dot = rest.indexOf('.');
		String segment = dot < 0 ? rest : rest.substring(0, dot);
		return GROUP_LABELS.containsKey(segment) ? segment : null;
	}

	//SPSXPD: 「作物相关」独立组——顺序严格按参照图（seed.png 行优先：种子 → 蔬菜 → 二次产物 → 普通果实 → 大型果实）
	private static final String CROP_LABEL = "作物相关";

	private static final Class<?>[] CROP_ORDER = {
			// row 0：种子（鲜莓与腐莓共用第一格）
			Freshberry.Seed.class, Rotberry.Seed.class, Firebloom.Seed.class, Blindweed.Seed.class,
			Sungrass.Seed.class, Icecap.Seed.class, Stormvine.Seed.class, Sorrowmoss.Seed.class,
			Dreamfoil.Seed.class, Earthroot.Seed.class, Fadeleaf.Seed.class, BlandfruitBush.Seed.class,
			Starflower.Seed.class, NutPlant.Seed.class, StarEater.Seed.class, ReNepenth.Seed.class,
			SiOtwoFlower.Seed.class, Dewcatcher.Seed.class, Seedpod.Seed.class, Swiftthistle.Seed.class,
			// row 2：蔬菜
			Durian.class, Chili.class, Marigold.class, HealGrass.class, IceMint.class, Tulip.class,
			ToxicEggplant.class, DreamLeaf.class, Radish.class, Sunflower.class, Blandfruit.class,
			BattleFlower.class, NutVegetable.class, StarEaterFlower.class, TransmuteCage.class,
			QuartzFlower.class, DewSpore.class, RainbowPansy.class, Sorrel.class,
			// row 3：二次加工产物
			Adhesive.class, Capsaicin.class, TransmutePowder.class, HealingSalve.class, CoolingOil.class,
			Perfume.class, ToxicExtract.class, WakeTea.class, NutrientSolution.class, SunflowerSeed.class,
			FruitThread.class, Sedative.class, RedRose.class, DigestiveFluid.class, AetherLiquid.class,
			CrystalShard.class, HighEnergySpore.class, WishPetal.class, HormoneSolution.class,
			// row 4：普通果实
			FreshFruit.class, RotFruit.class, FireFruit.class, BlindFruit.class, HealFruit.class,
			IceFruit.class, ShockFruit.class, ToxicFruit.class, CharmFruit.class, RootFruit.class,
			SmokeFruit.class, FlavorlessFruit.class, StarFruit.class, NutFruit.class, StarEaterFruit.class,
			TransmuteFruit.class, GlassFruit.class, DewFruit.class, SeedFruit.class, SwiftFruit.class,
			// row 5：大型果实
			LargeFreshFruit.class, LargeRotFruit.class, LargeFireFruit.class, LargeBlindFruit.class,
			LargeHealFruit.class, LargeIceFruit.class, LargeShockFruit.class, LargeToxicFruit.class,
			LargeCharmFruit.class, LargeRootFruit.class, LargeSmokeFruit.class, LargeFlavorlessFruit.class,
			LargeStarFruit.class, LargeNutFruit.class, LargeStarEaterFruit.class, LargeTransmuteFruit.class,
			LargeGlassFruit.class, LargeDewFruit.class, LargeSeedFruit.class, LargeSwiftFruit.class
	};

	private static final LinkedHashSet<Class<?>> CROP_SET = new LinkedHashSet<>();

	static {
		Collections.addAll(CROP_SET, CROP_ORDER);
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
		all.put(BambooBasket.class, true);   //SPSEXPD: 竹背篓（只装果实）

		//SPSEXPD: 许愿魔药（图鉴已登记；这里显式补入，保证调试器始终可取）
		all.put(WishPotion.class, true);

		//SPSEXPD: 魔法集露袋（移动时自动收露珠）不在图鉴/生成表里，手动补入调试器
		all.put(DewBadge.class, true);

		//SPSEXPD: 护甲配件包（换皮工具）与神圣护盾不在图鉴/生成表里，手动补入调试器
		all.put(ArmorKit.class, true);
		all.put(Ankhshield.class, true);

		//SPSEXPD: 干粮碾制机（购物车强化组件）不在图鉴/生成表里，手动补入调试器
		all.put(pd.items.RationGrinder.class, true);

		//SPSEXPD: 作物相关物品（种子/蔬菜/二次产物/果实）登记进调试器全集
		for (Class<?> t : CROP_ORDER) all.put((Class<? extends Item>) t, true);

		//按功能域分组（「作物相关」排在最前且按参照图顺序，其后是 GROUP_LABELS 的顺序）
		ArrayList<Class<? extends Item>> crops = new ArrayList<>();
		for (Class<?> t : CROP_ORDER) crops.add((Class<? extends Item>) t);
		LinkedHashMap<String, ArrayList<Class<? extends Item>>> map = new LinkedHashMap<>();
		map.put(CROP_LABEL, crops);
		for (String label : GROUP_LABELS.values()) map.put(label, new ArrayList<>());
		ArrayList<Class<? extends Item>> unknown = new ArrayList<>();

		for (Class<? extends Item> t : all.keySet()) {
			//SPSEXPD: 种子包已并入绒布袋、法杖套已并入魔法筒袋，这两个类已删除
			if (CROP_SET.contains(t)) continue;   //已按参照图顺序归入「作物相关」
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
