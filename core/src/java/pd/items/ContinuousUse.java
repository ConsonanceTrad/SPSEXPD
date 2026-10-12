/*
 * 连续使用 —— 「使用后不关闭操作框与背包」的规则集中点。
 *
 * <p>玩家在设置里开启「连续使用」并勾选允许的物品类型（白名单，位掩码存
 * {@link pd.SPDSettings#continuousUseTypes()}）。只有「立即生效的使用型动作」
 * 才允许连用：需要选目标 / 投掷的动作一律不生效。</p>
 *
 * <p>判定入口是 {@link #allows(pd.items.Item, String)}，实际接入点在
 * {@code pd.windows.WndUseItem} 的动作按钮里。</p>
 */

package pd.items;

import pd.SPDSettings;
import pd.items.consum.brewed.Brewed;
import pd.items.consum.food.Food;
import pd.items.consum.medicine.MendingTonic;
import pd.items.consum.medicine.Pill;
import pd.items.consum.potions.Potion;
import pd.items.consum.scrolls.Scroll;
import pd.items.consum.spells.Spell;
import pd.items.consum.stones.InventoryStone;
import pd.plants.Plant;

public final class ContinuousUse {

	private ContinuousUse() {
	}

	//白名单类型（位掩码的位；顺序即设置窗口里的显示顺序）
	public static final int TYPE_SCROLL = 1;
	public static final int TYPE_POTION = 1 << 1;
	public static final int TYPE_FOOD   = 1 << 2;
	public static final int TYPE_BREWED = 1 << 3;
	public static final int TYPE_PILL   = 1 << 4;
	public static final int TYPE_DRINK  = 1 << 5;
	public static final int TYPE_SEED   = 1 << 6;
	public static final int TYPE_SPELL  = 1 << 7;
	public static final int TYPE_STONE  = 1 << 8;

	public static final int MAX_VALUE = (1 << 9) - 1;

	/** 默认白名单：卷轴 / 药水 / 食物 */
	public static final int DEFAULT_TYPES = TYPE_SCROLL | TYPE_POTION | TYPE_FOOD;

	/** 类型键（窗口文案：windows.WndUseWhitelist.&lt;key&gt;） */
	public static final String[] NAME_IDS = {
			"scroll", "potion", "food", "brewed", "pill", "drink", "seed", "spell", "stone"
	};

	public static final int[] MASKS = {
			TYPE_SCROLL, TYPE_POTION, TYPE_FOOD, TYPE_BREWED, TYPE_PILL,
			TYPE_DRINK, TYPE_SEED, TYPE_SPELL, TYPE_STONE
	};

	/** 该物品属于白名单的哪一位；不属于任何一位时返回 0 */
	public static int typeOf( Item item ) {
		if (item == null) return 0;
		//注意顺序：先判更具体的子类（种子不是食物，调制物/药丸也不是食物）
		if (item instanceof Plant.Seed) return TYPE_SEED;
		if (item instanceof Scroll) return TYPE_SCROLL;
		if (item instanceof Spell) return TYPE_SPELL;
		if (item instanceof InventoryStone) return TYPE_STONE;
		if (item instanceof Potion) return TYPE_POTION;
		if (item instanceof Food) return TYPE_FOOD;
		if (item instanceof Brewed) return TYPE_BREWED;
		if (item instanceof Pill) return TYPE_PILL;
		//SPSEXPD: 这几件饮料不是 Potion 的子类，容易漏
		if (item instanceof Waterskin || item instanceof CrystalVial || item instanceof MendingTonic) {
			return TYPE_DRINK;
		}
		return 0;
	}

	/** 该类型的「使用型」动作名；无则返回 null */
	private static String useActionOf( int type ) {
		switch (type) {
			case TYPE_SCROLL: return "READ";
			case TYPE_SPELL:  return "CAST";
			case TYPE_SEED:   return "PLANT";
			case TYPE_STONE:  return "USE";
			case TYPE_POTION:
			case TYPE_DRINK:  return "DRINK";
			case TYPE_FOOD:
			case TYPE_BREWED:
			case TYPE_PILL:   return "EAT";
			default:          return null;
		}
	}

	/** 该物品的该动作是否允许「用后不关窗」 */
	public static boolean allows( Item item, String action ) {
		if (!SPDSettings.continuousUse() || item == null || action == null) return false;
		//需要选目标（投掷等）的动作保持原行为：关窗后进入目标选择
		if (item.usesTargeting) return false;
		int type = typeOf( item );
		if (type == 0) return false;
		if ((SPDSettings.continuousUseTypes() & type) == 0) return false;
		return action.equals( useActionOf( type ) );
	}
}
