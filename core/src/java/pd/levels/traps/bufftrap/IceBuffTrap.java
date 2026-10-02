package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.FrostCloud;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class IceBuffTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(IceBuffTrap.class)
			.t("name", "寒冰场陷阱")
			.t("desc", "这个可见陷阱会释放短暂的SPS寒冰场。");
	}



	{ color = TEAL; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, FrostCloud.class)); }
}
