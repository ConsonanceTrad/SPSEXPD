/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.particles.EarthParticle;
import pd.messages.Messages;
import pd.ui.BuffIndicator;

public class EarthImbue extends FlavourBuff {
	{
		type = buffType.POSITIVE;
		announced = true;
		immunities.add(Paralysis.class);
		immunities.add(Roots.class);
		immunities.add(Slow.class);
	}
	public void proc(Char enemy) {
		Buff.prolong(enemy, Roots.class, 2f);
		if (enemy.sprite != null) CellEmitter.bottom(enemy.pos).start(EarthParticle.FACTORY, 0.05f, 8);
	}
	@Override public int icon() { return BuffIndicator.IMBUE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}
