package pd.items.equipment.bags;

import java.util.ArrayList;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.atlas.items.EquipmentBagsDict;
import pd.items.Item;
import pd.items.consum.food.Food;
import pd.items.consum.food.staplefood.NormalRation;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

/**
 * SPSEXPD: 丰饶号角——购物车的强化组件。
 *
 * <p>不再是神器、没有等级：吞噬食物的效率恒定取「原先 5 级」的数值，
 * 充能攒够就自动凝成干粮，直接进背包（满了就掉在脚下）。
 */
public class PlentyHorn extends BagUpgrade {

	static {
		InlineText.of(PlentyHorn.class)
				.t("name", "丰饶号角")
				.t("desc", "一只永远不会空的号角。把食物丢进去，它会把味道换成另一种更耐放的东西还给你。")
				.t("swallow", "你把%1$s丢了进去，号角里涨起了 %2$d 点暖意。")
				.t("ration", "号角吐出一包干粮。")
				.t("ration_many", "号角吐出 %d 包干粮。");
	}

	/** 每这么多点充能自动凝成 1 包干粮（沿用原值）。 */
	public static final int RATION_COST = 6;
	/** 恒定按「原先 5 级」的转化效率结算（原公式 0.75 + 0.5 * level/10）。 */
	public static final float EFFICIENCY = 0.75f + 0.5f * (5f / 10f);

	public static final String AC_SWALLOW = "SWALLOW";

	protected float hornCharge = 0f;

	private static final String CHARGE = "horn_charge";

	{
		image = EquipmentBagsDict.SHOPPING_CART_0;
		defaultAction = AC_SWALLOW;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return ShoppingCart.class;
	}

	@Override
	public int componentOrder() {
		return 20;
	}

	public float conversionEfficiency() {
		return EFFICIENCY;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (active()) actions.add(AC_SWALLOW);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_SWALLOW.equals(action)) return;
		pd.scenes.GameScene.selectItem(new pd.windows.WndBag.ItemSelector() {
			@Override
			public String textPrompt() {
				return Messages.get(PlentyHorn.class, "desc");
			}

			@Override
			public boolean itemSelectable(Item item) {
				return item instanceof Food && ((Food) item).hornValue > 0;
			}

			@Override
			public void onSelect(Item item) {
				if (item instanceof Food) swallow(Dungeon.hero, (Food) item);
			}
		});
	}

	/** 整组吞噬：hornValue × 数量 × 恒定效率 → 充能，攒够自动凝成干粮。 */
	public float swallow(Hero hero, Food food) {
		if (hero == null || food == null) return 0f;
		int count = Math.max(1, food.quantity());
		float gained = Math.max(0, food.hornValue) * count * conversionEfficiency();
		hornCharge += gained;
		String label = count > 1 ? food.name() + " ×" + count : food.name();
		GLog.p(Messages.get(this, "swallow", label, (int) gained));
		food.detach(hero.belongings.backpack);
		convertRations(hero);
		Item.updateQuickslot();
		return gained;
	}

	private void convertRations(Hero hero) {
		int produced = 0;
		while (hornCharge >= RATION_COST) {
			hornCharge -= RATION_COST;
			NormalRation ration = new NormalRation();
			boolean collected = hero != null && ration.collect(hero.belongings.backpack);
			if (!collected && Dungeon.level != null && hero != null) {
				Dungeon.level.drop(ration, hero.pos);
			}
			produced++;
		}
		if (produced <= 0) return;
		Sample.INSTANCE.play(Assets.Sounds.EAT);
		GLog.p(produced == 1
				? Messages.get(this, "ration")
				: Messages.get(this, "ration_many", produced));
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, hornCharge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		hornCharge = bundle.getFloat(CHARGE);
	}
}