package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.VioletDewdrop;
import pd.mechanics.pathfind.PathFinder;
import render.noosa.Game;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class DewTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DewTrap.class)
			.t("name", "紫露陷阱")
			.t("desc", "触发后会在周围九格洒落紫色露珠。");
	}



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
