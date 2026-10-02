package pd.levels.traps;

import pd.Assets;
import pd.Dungeon;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.Generator;
import pd.items.Heap;
import render.noosa.Game;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class BoundTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(BoundTrap.class)
			.t("name", "随机奖励陷阱")
			.t("desc", "触发后会释放一件随机物品。");
	}

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
