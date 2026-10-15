/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.Actor;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.equipment.artifacts.Artifact;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 炼金反应堆 —— 星兵二档（王冠 B 路线）的特殊神器。
 *
 * <p>独立神器：装配后随时间**产出炼金能量**（{@link Dungeon#energy}），
 * 每 {@link #TURNS_PER_ENERGY} 回合 +1 点，存到 {@link #ENERGY_CAP} 为止。</p>
 */
public class AlchemyReactor extends Artifact implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.SOLDIER_GUN_T2_2_REACTOR;

	/** 每多少回合产出 1 点炼金能量。 */
	public static final int TURNS_PER_ENERGY = 25;
	/** 产出上限：已有这么多能量时暂停工作。 */
	public static final int ENERGY_CAP = 30;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(AlchemyReactor.class)
			.t("name", "炼金反应堆")
			.t("desc", "一台巴掌大的反应堆，内部缓慢地熔铸着炼金能量。\n\n装配后，它每 _%1$d 回合_会为你产出 _1 点炼金能量_，直到你身上的能量达到 _%2$d 点_为止。");
	}

	private static final String PROGRESS = "progress";

	private int progress = 0;

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", TURNS_PER_ENERGY, ENERGY_CAP);
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new ReactorCore();
	}

	/** SPSEXPD: 每回合推进产能。 */
	public class ReactorCore extends ArtifactBuff {
		@Override
		public boolean act() {
			if (Dungeon.energy < ENERGY_CAP) {
				progress++;
				if (progress >= TURNS_PER_ENERGY) {
					progress = 0;
					Dungeon.energy++;
				}
			}
			spend(Actor.TICK);
			return true;
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PROGRESS, progress);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		progress = bundle.getInt(PROGRESS);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
