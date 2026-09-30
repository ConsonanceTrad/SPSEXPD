package pd.items.weapon.spammo;

import pd.Dungeon;
import pd.actors.Char;
import pd.sprites.ItemSprite;
import watabou.utils.Random;

public class GoldAmmo extends SpAmmo {
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		int cost = Math.max(0, Dungeon.gold / 100);
		defender.damage((int)(cost * Random.Float(0.25f, 2f)), attacker);
		Dungeon.gold = Math.max(0, Dungeon.gold - cost);
	}
}
