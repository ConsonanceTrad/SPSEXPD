/*
 * Content adapted from Special Surprise Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.equipment.weapon.melee.Mace;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;

public class WarDrum extends Mace implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WarDrum.class)
			.t("name", "战鼓")
			.t("stats_desc", "每次命中会对目标身边的敌对角色造成最终伤害四分之一的波及伤害。")
			.t("ability_name", "雷鸣重击")
			.t("typical_ability_desc", "决斗家可以发动_雷鸣重击_。这次偷袭必定命中，造成_%d-%d点伤害_并击晕目标。")
			.t("ability_desc", "决斗家可以发动_雷鸣重击_。这次偷袭必定命中，造成_%d-%d点伤害_并击晕目标。")
			.t("upgrade_ability_stat_name", "武技伤害")
			.t("desc", "从特别惊喜像素地牢重做的武器，冲击会滚过成群敌人。其直接伤害刻意低于其他四阶武器。");
	}


	{
		image = EquipmentEquipWeaponBasicWeaponDict.WAR_HAMMER_0;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 0.8f;
		tier = 4;
		ACC = 1f;
	}

	@Override
	public int max(int lvl) {
		return 20 + 5 * lvl;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char target = Actor.findChar(defender.pos + offset);
			if (target != null && target != attacker && target != defender
					&& target.alignment != attacker.alignment) {
				target.damage(Math.max(1, result / 4), this);
			}
		}
		return result;
	}
}
