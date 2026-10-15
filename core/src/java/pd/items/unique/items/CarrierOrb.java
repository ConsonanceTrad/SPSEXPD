/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Assets;
import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.EquipableItem;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.items.unique.UniqueItem;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.audio.Sample;

import java.util.ArrayList;

/**
 * SPSEXPD: 宝珠 —— 可贴附到_武器或护甲_上的独特强化物（与护甲上的纹章槽并列，互不冲突）。
 *
 * <p>宝珠可以携带若干级升级，贴附时把升级转到装备上；但它_不能携带刻印_。
 * 具体减免多少双持惩罚由子类决定。</p>
 */
public abstract class CarrierOrb extends Item implements UniqueItem {

	public static final String AC_AFFIX = "AFFIX";

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(CarrierOrb.class)
			.t("ac_affix", "贴附")
			.t("prompt", "选择要贴附的武器或护甲。")
			.t("unknown_item", "你需要先鉴定那件装备。")
			.t("already_affixed", "这件装备上已经贴附了一枚宝珠。")
			.t("affix", "你把宝珠按进了装备里！");
	}

	/** SPSEXPD: 可携带的升级上限。 */
	public abstract int maxCarriedLevel();

	/** SPSEXPD: 双持力量惩罚减免的点数；返回 -1 表示完全免除。 */
	public abstract int dualWieldReduction();

	{
		image = pd.atlas.items.SpecificPlaceHolderDict.SOMETHING_0;

		cursedKnown = levelKnown = true;
		unique = true;
		bones = false;

		defaultAction = AC_AFFIX;
	}

	@Override public boolean isUpgradable() { return level() < maxCarriedLevel(); }
	@Override public boolean isIdentified() { return true; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_AFFIX);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_AFFIX.equals(action)) {
			curItem = this;
			GameScene.selectItem(orbSelector);
		}
	}

	protected static WndBag.ItemSelector orbSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(CarrierOrb.class, "prompt");
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof EquipableItem && ((EquipableItem) item).checkOrb() == null;
		}

		@Override
		public void onSelect(Item item) {
			if (!(item instanceof EquipableItem)) return;
			CarrierOrb orb = (CarrierOrb) curItem;
			orb.affixTo((EquipableItem) item);
		}
	};

	/** SPSEXPD: 把宝珠贴附到目标装备（武器或护甲）上。 */
	public void affixTo(EquipableItem target) {
		if (target == null) return;
		if (!target.cursedKnown) {
			GLog.w(Messages.get(CarrierOrb.class, "unknown_item"));
			return;
		}
		if (target.checkOrb() != null) {
			GLog.w(Messages.get(this, "already_affixed"));
			return;
		}

		detach(Dungeon.hero.belongings.backpack);
		target.affixOrb(this);

		if (Dungeon.hero.sprite != null) Dungeon.hero.sprite.operate(Dungeon.hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
		GLog.p(Messages.get(this, "affix"));
		Dungeon.hero.spendAndNext(1f);
	}
}
