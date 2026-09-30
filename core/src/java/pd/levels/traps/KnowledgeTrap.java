package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import render.noosa.Game;
import render.noosa.audio.Sample;

public class KnowledgeTrap extends Trap {
	{ color = RED; shape = STARS; }

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos] && Game.instance != null && Game.scene() != null) {
			CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
			Sample.INSTANCE.play(Assets.Sounds.LEVELUP);
		}
		if (Dungeon.hero != null && Dungeon.hero.belongings != null) Dungeon.hero.belongings.observe();
	}
}
