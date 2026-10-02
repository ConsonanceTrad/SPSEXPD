/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.rings.fusion;

import pd.actors.Char;
import pd.items.equipment.rings.Ring;
import pd.messages.Messages;
import pd.sprites.ItemIconSheet;
import pd.messages.InlineText;

public class RingOfMagic extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RingOfMagic.class)
			.t("name", "奥术戒指")
			.t("stats", "佩戴这枚戒指时，你的法强值会提升_%d_点。")
			.t("upgrade_stat_name_1", "法强加成")
			.t("desc", "你的法杖在这枚戒指散布的奥术力场中会变得更加强大。在30级时这枚戒指效果达到上限。");
	}


	{
		icon = ItemIconSheet.RING_ARCANA;
		buffClass = RingMagic.class;
	}

	@Override
	public String statsInfo() {
		return isIdentified() ? Messages.get(this, "stats", Math.min(30, level())) : "???";
	}

	@Override
	public String upgradeStat1(int level) {
		return Integer.toString(Math.min(30, level));
	}

	@Override
	protected RingBuff buff() {
		return new RingMagic();
	}

	public static int magicSkillBonus(Char target) {
		int bonus = 0;
		for (RingMagic buff : target.buffs(RingMagic.class)) {
			bonus += Math.min(30, buff.level());
		}
		return bonus;
	}

	public class RingMagic extends RingBuff {
		@Override public int level() { return RingOfMagic.this.level(); }
		@Override public int buffedLvl() { return level(); }
	}
}
