package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.VioletDewdrop;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class DewTrap extends Trap {
	{ color = RED; shape = CROSSHAIR; }

	@Override
	public void activate() {
		if (Dungeon.level.heroFOV[pos] && Game.instance != null && Game.scene() != null) {
			CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
			Sample.INSTANCE.play(Assets.Sounds.BLAST, 2f);
		}
		for (int offset : PathFinder.NEIGHBOURS9) {
			int cell = pos + offset;
			if (!Dungeon.level.insideMap(cell)) cell = pos;
			Heap heap = Dungeon.level.drop(new VioletDewdrop(), cell);
			if (heap.sprite != null) heap.sprite.drop(pos);
		}
	}
}
