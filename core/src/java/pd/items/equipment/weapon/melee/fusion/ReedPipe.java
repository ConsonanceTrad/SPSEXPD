/*
 * Content adapted from Magic Ling Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Daze;
import pd.items.equipment.weapon.melee.Whip;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

public class ReedPipe extends Whip implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ReedPipe.class)
			.t("name", "牧笛")
			.t("stats_desc", "这件武器攻击距离很远、直接伤害较低，并有1/7概率使目标眩晕。")
			.t("ability_name", "共鸣横扫")
			.t("typical_ability_desc", "决斗家可以发动_共鸣横扫_，对攻击范围内所有敌人造成_%d-%d点伤害_。")
			.t("ability_desc", "决斗家可以发动_共鸣横扫_，对攻击范围内所有敌人造成_%d-%d点伤害_。")
			.t("upgrade_ability_stat_name", "武技伤害")
			.t("desc", "一支被改作柔性武器的牧笛。扰乱心神的音色比物理打击传得更远。");
	}




	{
		image = EquipmentEquipWeaponBasicWeaponDict.FLUTE;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.35f;
		tier = 2;
	}

	@Override
	public int min(int lvl) {
		return 2 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 10 + 3 * lvl;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(7) == 0) {
			Buff.prolong(defender, Daze.class, 2f);
		}
		return super.proc(attacker, defender, damage);
	}
}
