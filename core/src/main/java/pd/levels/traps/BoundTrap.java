package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Heap;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

public class BoundTrap extends Trap {
	{ color = ORANGE; shape = GRILL; }

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos] && Game.instance != null && Game.scene() != null) {
			CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
			Sample.INSTANCE.play(Assets.Sounds.GOLD);
		}
		Heap heap = Dungeon.level.drop(Generator.random(), pos);
		if (heap.sprite != null) heap.sprite.drop();
	}
}
