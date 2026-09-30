package pd.items.medicine;

import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Drowsy;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.effects.Speck;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class PixieParasol extends Pill {
	{ image = ItemSpriteSheet.MUSHROOM_PIXIEPARASOL; }
	public PixieParasol() { this(1); }
	public PixieParasol(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, Drowsy.class);
			Buff.prolong(mob, Paralysis.class, Random.IntRange(10, 16));
			Buff.affect(mob, ArmorBreak.class, 50f).level(30);
			if (mob.sprite != null) mob.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
		}
		Buff.affect(hero, Bless.class, 20f);
	}
}
