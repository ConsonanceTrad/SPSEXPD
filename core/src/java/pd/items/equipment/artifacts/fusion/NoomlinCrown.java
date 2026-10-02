/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.equipment.artifacts.Artifact;
import pd.messages.InlineText;

/** SPS-PD 0.9.8's powerless Noomlin keepsake. */
public class NoomlinCrown extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(NoomlinCrown.class)
			.t("name", "诺姆林王冠")
			.t("desc", "来自宫殿深处，冰雪山峰上的王冠。冰寒之力早已消散，只留下他的宠物和这个破旧的王冠。\n即便这东西没有力量，但它依然被视为神器。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		levelCap = 1;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Crown();
	}

	public class Crown extends ArtifactBuff {
	}

	@Override
	public int value() {
		return 100 * quantity();
	}
}
