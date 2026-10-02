package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.ShadowGas;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class DarkBuffTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DarkBuffTrap.class)
			.t("name", "暗影场陷阱")
			.t("desc", "这个可见陷阱会释放短暂的SPS暗影场。");
	}

	{ color = VIOLET; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, ShadowGas.class)); }
}
