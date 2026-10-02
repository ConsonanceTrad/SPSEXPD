package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.HolyLight;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class LightBuffTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(LightBuffTrap.class)
			.t("name", "圣光场陷阱")
			.t("desc", "这个可见陷阱会释放短暂的SPS圣光场。");
	}

	{ color = WHITE; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, HolyLight.class)); }
}
