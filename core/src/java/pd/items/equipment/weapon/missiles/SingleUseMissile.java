/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles;

import pd.items.Item;

/**
 * SPSEXPD: 「一次性投掷武器」的通用行为集合（果实、魔术手、砖头、地形塑造块等）。
 *
 * 为什么是接口而不是抽象基类：这些物品的直接父类各不相同
 * （SpsFruit 系走 MissileWeapon、MagicHand 走 Arrows、Brick 走 MeleeThrowWeapon），
 * Java 单继承下没法共享基类；而这里要统一的行为都是不依赖 super 的覆写，接口默认方法刚好够用。
 *
 * 统一语义（以果实为模板提取）：
 *  · 不在描述里显示耐久度 —— durabilityPerUse 为 0，MissileWeapon 的 uses_left 段就不会拼出来
 *  · 默认为一次性消耗品 —— 可堆叠、按数量消耗，不参与投掷武器的「每组 3 个」耐久机制
 *  · 无法升级
 *
 * 后续要再指定某个投掷武器为一次性，让它 implements 本接口即可。
 */
public interface SingleUseMissile {

	/** 每次投掷不消耗耐久（返回 0 = 投掷武器里的「不磨损」）。 */
	default float durabilityPerUse(int level) {
		return 0f;
	}

	/** 一次性投掷武器不可升级。 */
	default boolean isUpgradable() {
		return false;
	}

	/** 按种类堆叠合并——不走投掷武器的 setID（「同一组」）判定。 */
	default boolean isSimilar(Item item) {
		return item != null && getClass() == item.getClass();
	}

	/** 堆叠上限：一次性投掷武器不做「每组 3 个」的截断。 */
	default int defaultQuantity() {
		return 999;
	}
}
