package pd.items.equipment.bags;

import java.util.ArrayList;

import pd.Dungeon;
import pd.atlas.items.EquipmentBagsDict;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.plants.Plant;
import pd.plants.PlantHarvest;
import pd.utils.GLog;

/**
 * SPSEXPD: 绿色小瓶——绒布包的强化组件。
 * 直接把种子 / 迷你种子转化成人「人工种植」档位的产物（对应数量的蔬菜与果实），默认处理整组。
 */
public class GreenVial extends BagUpgrade {

	static {
		InlineText.of(GreenVial.class)
				.t("name", "绿色小瓶")
				.t("desc", "一只灌满淡绿色液体的细颈瓶。把种子丢进去，它会在瓶里替你把整季的收成直接长出来。")
				.t("ac_convert", "转化种子")
				.t("prompt", "选择要转化的种子")
				.t("none", "这件东西放不进小瓶。");
	}

	{
		image = EquipmentBagsDict.POUCH;
	}

	@Override
	public Class<? extends Bag> targetBag() {
		return VelvetPouch.class;
	}

	@Override
	public int componentOrder() {
		return 30;
	}

	@Override
	public ArrayList<String> actions(pd.actors.hero.Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (active()) actions.add("CONVERT");
		return actions;
	}

	@Override
	public void execute(pd.actors.hero.Hero hero, String action) {
		super.execute(hero, action);
		if (!"CONVERT".equals(action)) return;
		if (hero == null || Dungeon.hero == null) return;
		pd.scenes.GameScene.selectItem(new pd.windows.WndBag.ItemSelector() {
			@Override
			public String textPrompt() {
				return Messages.get(GreenVial.class, "prompt");
			}

			@Override
			public boolean itemSelectable(pd.items.Item item) {
				return item instanceof Plant.Seed;
			}

			@Override
			public void onSelect(pd.items.Item item) {
				if (!(item instanceof Plant.Seed)) return;
				convert((Plant.Seed) item);
			}
		});
	}

	/** 把整组种子直接转化成人「人工种植」档的产物：对应数量的蔬菜 + 果实。 */
	private void convert(Plant.Seed seed) {
		if (Dungeon.hero == null || seed == null) return;
		int count = Math.max(1, seed.quantity());

		//植物类由 PlantHarvest 按种子类解析（Seed.plantClass 是 protected，外部拿不到）
		Class<? extends pd.items.Item> veg = PlantHarvest.vegetableForSeed(seed.getClass());
		Class<? extends pd.items.Item> fruit = PlantHarvest.fruitForVariant(seed.getClass());

		ArrayList<pd.items.Item> yields = new ArrayList<>();
		if (veg != null) {
			pd.items.Item v = render.utils.serialize.Reflection.newInstance(veg);
			if (v != null) { v.quantity(count); yields.add(v); }
		}
		if (fruit != null) {
			pd.items.Item f = render.utils.serialize.Reflection.newInstance(fruit);
			if (f != null) { f.quantity(count); yields.add(f); }
		}

		if (yields.isEmpty()) {
			GLog.w(Messages.get(this, "none"));
			return;
		}

		seed.detach(Dungeon.hero.belongings.backpack);
		for (pd.items.Item out : yields) {
			if (!out.collect(Dungeon.hero.belongings.backpack)) {
				Dungeon.level.drop(out, Dungeon.hero.pos);
			}
		}
		pd.items.Item.updateQuickslot();
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}
}