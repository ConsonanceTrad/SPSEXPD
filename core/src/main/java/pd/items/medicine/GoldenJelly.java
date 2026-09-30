package pd.items.medicine;

import pd.actors.buffs.Buff;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.sprites.ItemSpriteSheet;

public class GoldenJelly extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_GOLDENJELLY; }
	public GoldenJelly() { this(1); }
	public GoldenJelly(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) Buff.affect(mob, GrowSeed.class).set(10f);
		Buff.affect(hero, Vertigo.class, 10f);
	}
}
