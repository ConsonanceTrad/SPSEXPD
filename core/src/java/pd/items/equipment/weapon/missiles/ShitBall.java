/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Tar;
import pd.actors.mobs.Mob;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class ShitBall extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShitBall.class)
			.t("name", "污团")
			.t("desc", "这个东西装满了污物。扔到空地上会发出巨响，使视野内附近的怪物失明并将其引来；直接命中还会造成衰老与焦油效果。");
	}



	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 1;
		baseUses = 1;
		DLY = 0.5f;
		levelKnown = true;
	}

	public ShitBall() { quantity(2); }
	@Override public int min(int lvl) { return 1; }
	@Override public int max(int lvl) { return 1; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override protected void onThrow(int cell) {
		Char target = Actor.findChar(cell);
		if (target == null) {
			for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
				if (Dungeon.level.heroFOV[mob.pos] && Dungeon.level.distance(cell, mob.pos) <= 5) {
					Buff.affect(mob, Blindness.class, 5f);
					mob.beckon(cell);
				}
			}
		} else {
			super.onThrow(cell);
		}
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, BeOld.class).set(20f);
		Buff.affect(defender, Tar.class);
		return super.proc(attacker, defender, damage);
	}

	@Override public ShitBall random() { quantity(Random.IntRange(1, 2)); return this; }
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 5 * quantity; }
}
