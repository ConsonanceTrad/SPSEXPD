/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.Dungeon;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.normalarmor.ClothArmor;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.utils.serialize.Reflection;

import java.util.ArrayList;

/**
 * SPSEXPD: 符文绑带 —— 决斗一档（面具 B 路线）的副护甲。
 *
 * <p>防护能力与普通的布制护手相当；但它可以_消耗一件炼金材料_，
 * 把指定的一种刻印直接写到自己身上。</p>
 */
public class RuneBanding extends ClothArmor implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.DUELIST_SHIELD_T1_2_RUNE;

	public static final String AC_INSCRIBE = "INSCRIBE";

	/** 可以指定的刻印。 */
	private static final String[] GLYPH_CLASSES = {
		"pd.items.equipment.armor.glyphs.Swiftness",
		"pd.items.equipment.armor.glyphs.Thorns",
		"pd.items.equipment.armor.glyphs.Stone",
		"pd.items.equipment.armor.glyphs.Viscosity",
		"pd.items.equipment.armor.glyphs.Brimstone",
		"pd.items.equipment.armor.glyphs.AntiMagic",
		"pd.items.equipment.armor.glyphs.Obfuscation",
		"pd.items.equipment.armor.glyphs.Flow",
	};

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(RuneBanding.class)
			.t("name", "符文绑带")
			.t("ac_inscribe", "指定刻印")
			.t("prompt_material", "选择要消耗的炼金材料")
			.t("choose_title", "选择要写上的刻印")
			.t("no_material", "你需要一件炼金材料才能这么做。")
			.t("bad_glyph", "这道刻印无法写入。")
			.t("inscribed", "符文顺着绑带爬开，%s被写了上去。")
			.t("desc", "一条绣满细密符文的绑带。\n\n它只能装备在_副护甲栏_，并像副手专用的盾牌一样_不会造成双持惩罚_。\n\n防护能力与普通布制护手相当，但它可以「指定刻印」——_消耗一件炼金材料_，把你想好的那一种刻印直接写到自己身上。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SPS_PH_ARMOR_SPARE;
		unique = true;
	}

	/** SPSEXPD: 副手专用护甲——不能装备到主护甲栏。 */
	@Override public boolean canEquipPrimary() { return false; }

	/** SPSEXPD: 不参与双甲规则（不涨力量需求、不降移速）。 */
	@Override public boolean countsForDualArmor() { return false; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_INSCRIBE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_INSCRIBE.equals(action)) {
			GameScene.selectItem(materialSelector);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 先挑一件炼金材料（非装备、非独特的物品）。 */
	private final WndBag.ItemSelector materialSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(RuneBanding.class, "prompt_material"); }
		@Override public boolean itemSelectable(Item item) {
			return item != RuneBanding.this
					&& !(item instanceof UniqueItem)
					&& !(item instanceof Armor)
					&& !(item instanceof pd.items.KindOfWeapon)
					&& !(item instanceof pd.items.equipment.artifacts.Artifact)
					&& !(item instanceof pd.items.equipment.rings.Ring);
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			if (item.quantity() > 1) {
				item.quantity(item.quantity() - 1);
			} else if (Dungeon.hero != null) {
				item.detach(Dungeon.hero.belongings.backpack);
			}
			chooseGlyph();
		}
	};

	/** SPSEXPD: 弹出刻印列表，选定后直接写入。 */
	protected void chooseGlyph() {
		GameScene.show(new WndOptions(name(), Messages.get(this, "choose_title"), glyphNames()) {
			@Override
			protected void onSelect(int index) {
				if (index < 0 || index >= GLYPH_CLASSES.length) return;
				try {
					Armor.Glyph glyph = (Armor.Glyph) Reflection.newInstance(Class.forName(GLYPH_CLASSES[index]));
					if (glyph == null) {
						GLog.w(Messages.get(RuneBanding.this, "bad_glyph"));
						return;
					}
					inscribe(glyph);
					GLog.p(Messages.get(RuneBanding.this, "inscribed", glyph.name()));
				} catch (Exception e) {
					GLog.w(Messages.get(RuneBanding.this, "bad_glyph"));
				}
			}
		});
	}

	private String[] glyphNames() {
		String[] names = new String[GLYPH_CLASSES.length];
		for (int i = 0; i < GLYPH_CLASSES.length; i++) {
			String full = GLYPH_CLASSES[i];
			names[i] = full.substring(full.lastIndexOf('.') + 1);
		}
		return names;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
