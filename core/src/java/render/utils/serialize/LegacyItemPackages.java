/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 */

package render.utils.serialize;

/**
 * SPDX: pd/items 按图集四大类（consum/equipment/ground/specific）重分组后，
 * 旧存档里记录的仍然是重分组前的全限定类名。此表把旧包名映射到新包名，
 * 使老存档仍能反序列化。
 *
 * 只做包前缀替换（pd.items.<旧类> -> pd.items.<新类>.<旧类>），
 * 未参与重分组的包（misc/summon/quest/skills/nornstone 及顶层类）不在表内。
 */
public final class LegacyItemPackages {

	private static final String[] OLD_PREFIXES = {
			"pd.items.armor.",
			"pd.items.artifacts.",
			"pd.items.bags.",
			"pd.items.bombs.",
			"pd.items.rings.",
			"pd.items.trinkets.",
			"pd.items.wands.",
			"pd.items.weapon.",
			"pd.items.food.",
			"pd.items.potions.",
			"pd.items.scrolls.",
			"pd.items.stones.",
			"pd.items.spells.",
			"pd.items.brewed.",
			"pd.items.medicine.",
			"pd.items.eggs.",
			"pd.items.keys.",
			"pd.items.journalpages.",
			"pd.items.journal.",
			"pd.items.challengelists.",
			"pd.items.reward.",
			"pd.items.sellitem.",
			"pd.items.remains.",
	};

	private static final String[] NEW_PREFIXES = {
			"pd.items.equipment.armor.",
			"pd.items.equipment.artifacts.",
			"pd.items.equipment.bags.",
			"pd.items.equipment.bombs.",
			"pd.items.equipment.rings.",
			"pd.items.equipment.trinkets.",
			"pd.items.equipment.wands.",
			"pd.items.equipment.weapon.",
			"pd.items.consum.food.",
			"pd.items.consum.potions.",
			"pd.items.consum.scrolls.",
			"pd.items.consum.stones.",
			"pd.items.consum.spells.",
			"pd.items.consum.brewed.",
			"pd.items.consum.medicine.",
			"pd.items.consum.eggs.",
			"pd.items.specific.keys.",
			"pd.items.specific.journalpages.",
			"pd.items.specific.journal.",
			"pd.items.specific.challengelists.",
			"pd.items.specific.reward.",
			"pd.items.specific.sellitem.",
			"pd.items.ground.remains.",
	};

	private LegacyItemPackages() {
	}

	/**
	 * @return 迁移后的类名；若无需迁移（或不是物品类）返回 null。
	 */
	public static String migrate( String className ) {
		if (className == null || !className.startsWith("pd.items.")) {
			return null;
		}
		for (int i = 0; i < OLD_PREFIXES.length; i++) {
			if (className.startsWith(OLD_PREFIXES[i])) {
				return NEW_PREFIXES[i] + className.substring(OLD_PREFIXES[i].length());
			}
		}
		return null;
	}
}
