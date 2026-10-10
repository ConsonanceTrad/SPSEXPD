package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Heap;
import pd.items.RedDewdrop;
import pd.mechanics.pathfind.PathFinder;
import render.noosa.Game;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class DewTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DewTrap.class)
			.t("name", "露珠陷阱")
			.t("desc", "触发后会在周围九格洒落红色露珠，而且只会落在可以站立的格子上。");
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
			//SPSEXPD: 只洒在可以站立的格子上——墙、上锁的门、装饰等不可抵达的格子都不生成
			//（原来越界格会回退到陷阱自身位置，现在直接跳过）
			if (!Dungeon.level.insideMap(cell) || !Dungeon.level.passable[cell]) {
				continue;
			}
			Heap heap = Dungeon.level.drop(new RedDewdrop(), cell);
			if (heap.sprite != null) heap.sprite.drop(pos);
		}
	}
}
