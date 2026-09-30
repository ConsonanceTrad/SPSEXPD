package pd.items.medicine;

import pd.actors.buffs.Buff;
import pd.actors.buffs.AttackDown;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.sprites.ItemSpriteSheet;

public class BlueMilk extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_BLUEMILK; }
	public BlueMilk() { this(1); }
	public BlueMilk(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, Slow.class, 50f);
			Buff.affect(mob, AttackDown.class, 50f).level(50);
		}
		Buff.affect(hero, HasteBuff.class, 10f);
		Buff.affect(hero, BerryRegeneration.class).level(hero.HP / 2);
	}
}
