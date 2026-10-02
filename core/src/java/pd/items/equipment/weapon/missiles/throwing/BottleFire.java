/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.FireFollower;
import pd.actors.hero.Hero;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.messages.InlineText;

/** Lery's bottled flame, which leaves a thirty-turn trail of delayed fire. */
public class BottleFire extends TossWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BottleFire.class)
			.t("name", "瓶装火焰")
			.t("desc", "奇怪的火焰，装在瓶子里面。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 1;
		baseUses = 1;
		bones = false;
	}

	@Override public int min(int lvl) { return 1; }
	@Override public int max(int lvl) { return 1; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override
	protected void onThrow(int cell) {
		Char enemy = Actor.findChar(cell);
		if (enemy == null) igniteArea(curUser, cell);
		else super.onThrow(cell);
	}

	void igniteArea(Hero owner, int center) {
		if (Dungeon.level == null) return;
		for (int offset : PathFinder.NEIGHBOURS9) {
			int cell = center + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			if (Dungeon.level.flamable[cell] || Actor.findChar(cell) != null
					|| Dungeon.level.heaps.get(cell) != null) {
				GameScene.add(Blob.seed(cell, 5, Fire.class));
			}
		}
		if (owner != null) Buff.affect(owner, FireFollower.class).set(FireFollower.DURATION);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Burning.class).reignite(defender, 10f);
		Buff.affect(attacker, FireFollower.class).set(FireFollower.DURATION);
		return super.proc(attacker, defender, damage);
	}

	@Override public int value() { return 0; }
}
