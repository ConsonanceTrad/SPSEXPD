/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.melee.special;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.equipment.weapon.melee.normalweapon.NormalMeleeWeapon;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The tester loadout weapon, which awards experiment points on high damage rolls. */
public class TestWeapon extends NormalMeleeWeapon {
	{
		image = SpecificPlaceHolderDict.SPS_PH_WEAPON_TEST;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TestWeapon.class)
			.t("name", "测试武器")
			.t("desc", "测试用的武器。造成较高伤害时会获得试验点数。");
	}




	public TestWeapon() {
		//SPS: 测试武器把攻击力拉满（原 10-10），方便测试时间挑战里秒杀验证
		super(1, 1f, 1f, 1, 9999, 9999, SpecificPlaceHolderDict.SOMETHING_0);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (damage > Random.Int(8, 20)) {
			Hero hero = attacker instanceof Hero ? (Hero) attacker : Dungeon.hero;
			if (hero != null) hero.spp++;
		}
		return super.proc(attacker, defender, damage);
	}
}
