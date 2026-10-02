package pd.levels.traps.bufftrap;

import pd.actors.blobs.Blob;
import pd.actors.blobs.effectblobs.AcidWater;
import pd.levels.traps.Trap;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class EarthBuffTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(EarthBuffTrap.class)
			.t("name", "酸蚀场陷阱")
			.t("desc", "这个可见陷阱会释放短暂的SPS酸蚀场。");
	}



	{ color = GREEN; shape = DOTS; canBeHidden = false; }
	@Override public void activate() { GameScene.add(Blob.seed(pos, 3, AcidWater.class)); }
}
