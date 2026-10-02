/*
 * Content adapted from Special Surprise Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.weapon.melee.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.weapon.melee.Scimitar;
import render.utils.serialize.Bundle;

public class Harp extends Scimitar implements FusionWeapon {

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
