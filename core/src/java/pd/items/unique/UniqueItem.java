/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique;

/**
 * SPSEXPD: 特殊物品标记接口。
 *
 * <p>凡是实现本接口的物品，都带一个稳定的 {@link UniqueItemId} 编号；据此可以快速判断
 * 「它是不是特殊物品」以及「升级时要换成哪一件」，不再依赖外部按类名硬编码的映射表。</p>
 */
public interface UniqueItem {

	/** 该物品的编号（每件特殊物品一个稳定常量）。 */
	UniqueItemId uniqueId();

	default boolean isUniqueItem() {
		return uniqueId() != null;
	}
}
