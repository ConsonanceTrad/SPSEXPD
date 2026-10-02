package pd.levels.traps;

import pd.Dungeon;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ParalyticGas;
import pd.scenes.GameScene;
import pd.messages.InlineText;

public class ParalyticTrap extends Trap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(ParalyticTrap.class)
			.t("name", "麻痹陷阱")
			.t("desc", "触发这个陷阱将在附近释放出一片麻痹气体。");
	}



	{
		color = YELLOW;
		shape = DIAMOND;
	}

	@Override
	public void activate() {
		GameScene.add(Blob.seed(pos, 80 + 5 * Dungeon.legacyDepth(), ParalyticGas.class));
	}
}
