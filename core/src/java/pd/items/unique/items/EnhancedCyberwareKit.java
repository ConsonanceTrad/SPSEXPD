/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndOptions;

/**
 * SPSEXPD: 强化义体工具箱 —— 苦修者一档（面具 B 路线）的背包道具（同样不是神器）。
 *
 * <p>造出来的义体_电量更强_，而且会一次给出 _3 种_让你自己挑。</p>
 */
public class EnhancedCyberwareKit extends CyberwareKit {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T1_2_KIT;

	/** 电量加成倍率。 */
	public static final float CAPACITY_BONUS = 1.5f;
	/** 一次给出几种候选。 */
	public static final int CHOICES = 3;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(EnhancedCyberwareKit.class)
			.t("name", "强化义体工具箱")
			.t("ac_build", "制造义体")
			.t("no_material", "你没有足够的义体材料（需要 %d 份）。")
			.t("built", "工具箱咔哒一声，弹出了一台%s。")
			.t("full", "背包塞不下了。")
			.t("choose_title", "选择要制造的义体")
			.t("desc", "一只加固过的工具箱，外壳上还留着上一任主人的刻痕。\n\n「制造义体」：消耗 %1$d 份_义体材料_，从 _%2$d 种_候选里挑一台造出来。\n\n它造出的义体_电量更强_（+%3$d%%）。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", materialCost(), CHOICES, Math.round((CAPACITY_BONUS - 1f) * 100));
	}

	/** SPSEXPD: 先扣材料，再让你从 3 种里挑。 */
	@Override
	protected void build(Hero hero) {
		pd.items.consum.materials.CyberMaterial material =
				hero.belongings.getItem(pd.items.consum.materials.CyberMaterial.class);
		int have = material == null ? 0 : material.quantity();
		if (have < materialCost()) {
			GLog.w(Messages.get(this, "no_material", materialCost()));
			return;
		}
		if (have > materialCost()) {
			material.quantity(have - materialCost());
		} else {
			material.detach(hero.belongings.backpack);
		}

		final Cyberware[] candidates = new Cyberware[CHOICES];
		String[] names = new String[CHOICES];
		for (int i = 0; i < CHOICES; i++) {
			candidates[i] = Cyberwares.randomCyberware();
			names[i] = candidates[i].name();
		}

		GameScene.show(new WndOptions(name(), Messages.get(this, "choose_title"), names) {
			@Override
			protected void onSelect(int index) {
				if (index < 0 || index >= candidates.length) return;
				deliver(hero, candidates[index]);
			}
		});
	}

	/** SPSEXPD: 交付义体（电量加强）。 */
	private void deliver(Hero hero, Cyberware ware) {
		if (ware == null) return;
		ware.boostCapacity(CAPACITY_BONUS);
		if (ware.collect()) {
			GLog.p(Messages.get(this, "built", ware.name()));
		} else {
			GLog.w(Messages.get(this, "full"));
			pd.Dungeon.level.drop(ware, hero.pos);
		}
		hero.spendAndNext(1f);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
