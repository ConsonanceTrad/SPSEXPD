/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.artifacts.Artifact;

import java.util.ArrayList;

/** Compatibility base for SPS-PD's three-slot equippable utility items. */
public abstract class MiscEquippable extends Artifact {

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}

	@Override protected final ArtifactBuff passiveBuff() { return createBuff(); }
	protected abstract MiscBuff createBuff();

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

	public class MiscBuff extends ArtifactBuff {
		@Override public boolean act() { spend(Buff.TICK); return true; }
	}
}
