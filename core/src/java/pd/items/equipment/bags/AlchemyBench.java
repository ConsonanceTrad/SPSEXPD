package pd.items.equipment.bags;

import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;

/**
 * SPSEXPD: 炼药工具台——药水箱的强化组件。
 * 不再是神器、没有等级：随时间自行积累炼金能量，上限 20 点，供炼金釜使用。
 */
public class AlchemyBench extends BagUpgrade {

	static {
		InlineText.of(AlchemyBench.class)
				.t("name", "炼药工具台")
				.t("desc", "一套折叠式的炼药器具，装进药水箱后能自己慢慢积攒可供炼金釜使用的能量（上限 %d 点）。")
				.t("ac_stoke", "催动")
				.t("stoked", "你把积攒的能量推进了炼金釜。");
	}

	public static final int MAX_ENERGY = 20;
	/** 每积攒 1 点能量需要的回合数。 */
	public static final int TURNS_PER_CHARGE = 12;

	private int energy = 0;
	private float progress = 0;

	private static final String ENERGY = "energy";
	private static final String PROGRESS = "progress";

	{
		image = EquipmentBagsDict.BANDOLIER;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return PotionBandolier.class;
	}

	@Override
	public int componentOrder() {
		return 10;
	}

	/** 炼金釜可以额外用多少能量。 */
	public int availableEnergy() {
		return active() ? energy : 0;
	}

	/** 消耗能量：返回扣掉本工具能覆盖的部分后，还需要英雄自己出的量。 */
	public int consumeEnergy(int amount) {
		if (!active() || amount <= 0) return amount;
		int used = Math.min(energy, amount);
		energy -= used;
		return amount - used;
	}

	/** 每回合由英雄推进（Hero 的回合钩子里调用）。 */
	public void onHeroTurn() {
		if (!active() || energy >= MAX_ENERGY) return;
		progress += 1f / TURNS_PER_CHARGE;
		while (progress >= 1f && energy < MAX_ENERGY) {
			progress -= 1f;
			energy++;
		}
		if (energy >= MAX_ENERGY) progress = 0f;
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", MAX_ENERGY) + "\n\n" + super.desc();
	}

	@Override
	public void storeInBundle(render.utils.serialize.Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ENERGY, energy);
		bundle.put(PROGRESS, progress);
	}

	@Override
	public void restoreFromBundle(render.utils.serialize.Bundle bundle) {
		super.restoreFromBundle(bundle);
		energy = bundle.getInt(ENERGY);
		progress = bundle.getFloat(PROGRESS);
	}
}