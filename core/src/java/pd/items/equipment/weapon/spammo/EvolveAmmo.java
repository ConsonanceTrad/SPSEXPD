/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.spammo;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.actors.mobs.NormalCell;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

public class EvolveAmmo extends SpAmmo {
	private static final ItemSprite.Glowing DEEP_GREEN = new ItemSprite.Glowing(0x006633);
	@Override public ItemSprite.Glowing glowing() { return DEEP_GREEN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(10) == 3) {
			if (!transform(attacker, defender)) defender.damage((int)(0.10f * damage), attacker);
		} else {
			defender.damage((int)(0.10f * damage), attacker);
		}
	}

	static boolean transform(Char attacker, Char defender) {
		if (!(defender instanceof Mob) || defender == attacker || Dungeon.level == null
				|| !defender.isAlive() || !Dungeon.level.insideMap(defender.pos)
				|| Char.hasProp(defender, Char.Property.BOSS)
				|| Char.hasProp(defender, Char.Property.MINIBOSS)) return false;
		int pos = defender.pos;
		NormalCell cell = new NormalCell();
		cell.HT = cell.HP = Math.max(1, defender.HP);
		cell.pos = pos;
		Actor.remove(defender);
		Dungeon.level.mobs().remove(defender);
		if (defender.sprite != null) defender.sprite.killAndErase();
		GameScene.add(cell);
		Dungeon.level.occupyCell(cell);
		if (defender.sprite != null) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 4);
		return true;
	}
}
