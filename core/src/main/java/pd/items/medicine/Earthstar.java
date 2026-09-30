package pd.items.medicine;

import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Earthstar extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_EARTHSTAR; }
	public Earthstar() { this(1); }
	public Earthstar(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			int min = mob.HP / 8;
			int max = mob.HP / 4;
			Buff.affect(mob, Bleeding.class).set(max > min ? Random.Int(min, max) : Math.max(1, max));
		}
		hero.damage(Math.max(1, hero.HP / 4), this);
		Buff.prolong(hero, Blindness.class, Random.IntRange(5, 7));
	}
}
