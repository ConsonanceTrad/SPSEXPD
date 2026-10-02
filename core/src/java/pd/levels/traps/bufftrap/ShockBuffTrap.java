package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class ShockBuffTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(ShockBuffTrap.class)
			.t("name", "雷电场陷阱")
			.t("desc", "这个可见陷阱会释放短暂的SPS雷电场。");
	}



	{ color = YELLOW; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, ElectriShock.class)); }
}
