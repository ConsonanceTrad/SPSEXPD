/*
 * Content adapted from Magic Ling Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Assets;
import pd.items.equipment.weapon.melee.Sword;
import pd.messages.InlineText;

public class RitualBlade extends Sword implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RitualBlade.class)
			.t("name", "仪式剑")
			.t("stats_desc", "这件武器的精准提高10%%，但最大伤害略低。")
			.t("ability_name", "仪式顺劈")
			.t("typical_ability_desc", "决斗家可以发动_仪式顺劈_，必定命中并通常造成_%d-%d点伤害_。若这一击击杀目标，5回合内可免费再次发动。")
			.t("ability_desc", "决斗家可以发动_仪式顺劈_，必定命中并造成_%d-%d点伤害_。若这一击击杀目标，5回合内可免费再次发动。")
			.t("upgrade_ability_stat_name", "武技伤害")
			.t("desc", "由魔绫像素地牢的仪式武器重新收束而来。宽阔的剑势重视稳定命中，而非夸张伤害。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.SICKLE_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.15f;
		tier = 2;
		ACC = 1.1f;
	}

	@Override
	public int min(int lvl) {
		return 3 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 13 + 3 * lvl;
	}
}
