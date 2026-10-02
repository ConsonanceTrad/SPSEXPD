package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.Fire;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class FireBuffTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(FireBuffTrap.class)
			.t("name", "火焰场陷阱")
			.t("desc", "这个可见陷阱会释放短暂的SPS火焰场。");
	}

	{ color = ORANGE; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, Fire.class)); }
}
