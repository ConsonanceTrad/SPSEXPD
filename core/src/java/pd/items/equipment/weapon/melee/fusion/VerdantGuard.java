/*
 * Content adapted from Magic Ling Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Assets;
import pd.actors.Char;
import pd.items.equipment.weapon.melee.Quarterstaff;
import pd.messages.InlineText;

public class VerdantGuard extends Quarterstaff implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(VerdantGuard.class)
			.t("name", "森之守御")
			.t("stats_desc", "这件防御武器至少可格挡_3点伤害_，升级后会缓慢增强格挡，但最大伤害较低。")
			.t("ability_name", "森之架势")
			.t("typical_ability_desc", "决斗家可立即进入_森之架势_，一般在_%d回合_内使闪避变为3倍。")
			.t("ability_desc", "决斗家可立即进入_森之架势_，在_%d回合_内使闪避变为3倍。")
			.t("upgrade_ability_stat_name", "武技持续时间")
			.t("desc", "由魔绫像素地牢的树木主题武器重新设计。它牺牲进攻，换取稳定防护。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.ROUND_SHIELD_0;
		hitSound = Assets.Sounds.HIT_CRUSH;
		tier = 3;
	}

	@Override
	public int max(int lvl) {
		return 14 + 4 * lvl;
	}

	@Override
	public int defenseFactor(Char owner) {
		return 3 + Math.max(0, buffedLvl()) / 2;
	}
}
