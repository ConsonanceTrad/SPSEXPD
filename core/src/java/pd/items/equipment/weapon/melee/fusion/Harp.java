/*
 * Content adapted from Special Surprise Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.weapon.melee.Scimitar;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class Harp extends Scimitar implements FusionWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Harp.class)
			.t("name", "战斗竖琴")
			.t("stats_desc", "这件高速武器每成功命中五次会恢复少量生命，但直接伤害大幅降低。")
			.t("ability_name", "战斗节拍")
			.t("typical_ability_desc", "决斗家可立即奏响_战斗节拍_，一般在_%d回合_内获得60%%攻速与50%%精准加成。")
			.t("ability_desc", "决斗家可立即奏响_战斗节拍_，在_%d回合_内获得60%%攻速与50%%精准加成。")
			.t("upgrade_ability_stat_name", "武技持续时间")
			.t("desc", "从特别惊喜像素地牢重新调音的演奏武器。它奖励持续战斗，而非爆发回复。");
	}




	private static final String HITS = "hits";
	private int hits;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.4f;
		tier = 5;
	}

	@Override
	public int max(int lvl) {
		return 18 + 6 * lvl;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		if (attacker instanceof Hero && ++hits >= 5) {
			hits = 0;
			attacker.HP = Math.min(attacker.HT, attacker.HP + 1 + Math.max(0, buffedLvl()) / 3);
			Item.updateQuickslot();
		}
		return result;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HITS, hits);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		hits = bundle.getInt(HITS);
	}
}
