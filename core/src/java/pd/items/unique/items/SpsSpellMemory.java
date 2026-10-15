/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.items.equipment.wands.Wand;

/**
 * SPSEXPD: 「上次施放的法术」记录 —— 供秘银手杖 / 精金手杖的近战特效使用。
 *
 * <p>每次法杖成功施放时（{@link Wand#zap} 扣充能处）登记一次，之后手杖近战命中
 * 就会重放对应法术的完整命中效果。</p>
 */
public final class SpsSpellMemory {

	private SpsSpellMemory() { }

	private static Class<? extends Wand> last;

	public static void record(Class<? extends Wand> wandClass) {
		if (wandClass != null) last = wandClass;
	}

	public static Class<? extends Wand> last() {
		return last;
	}
}
