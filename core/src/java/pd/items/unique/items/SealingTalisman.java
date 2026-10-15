/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.weapon.Weapon;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/**
 * SPSEXPD: 封魔帖 —— 信徒一档（面具 B 路线）的道具。
 *
 * <p>像纹章一样贴附武器：先「吸收」一把武器的附魔（武器随之清空），
 * 再对另一把武器「写附魔」把它转移过去。带着它时，你对_非人形生物_的攻击更狠。</p>
 */
public class SealingTalisman extends Item implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T1_2_TALISMAN;

	public static final String AC_ABSORB = "ABSORB";
	public static final String AC_APPLY = "APPLY";

	private static final String SEALED = "sealed";

	/** 帖里封着的附魔（为空表示帖是空的）。 */
	protected Weapon.Enchantment sealed = null;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SealingTalisman.class)
			.t("name", "封魔帖")
			.t("ac_absorb", "吸收附魔")
			.t("ac_apply", "写附魔")
			.t("prompt_absorb", "选择要吸取附魔的武器")
			.t("prompt_apply", "选择要写上附魔的武器")
			.t("already_sealed", "帖里已经封着一道附魔了。")
			.t("empty", "帖里是空的，没有可写的附魔。")
			.t("no_weapon", "这不是一件武器。")
			.t("no_enchant", "这件武器上没有任何附魔。")
			.t("absorbed", "封魔帖把%s吸了进去。")
			.t("applied", "你把帖里的%s写到了%s上。")
			.t("desc", "一张永远揭不干净的帖，背面爬满了吃掉魔力的字。\n\n「吸收附魔」：从一件武器上把附魔_吸进帖里_（该武器随之清空）。\n\n「写附魔」：把帖里封着的附魔_写到另一件武器上_（帖随之清空）。\n\n带着它时，你对_非人形生物_的攻击会更狠（+%1$d）。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	@Override
	public String desc() {
		String base = Messages.get(this, "desc", bonusVsNonHumanoid());
		if (sealed != null) {
			base += "\n\n" + sealed.name("");
		}
		return base;
	}

	/** 对非人形生物的额外伤害。 */
	public int bonusVsNonHumanoid() {
		return 3;
	}

	/** SPSEXPD: 携带封魔帖者对非人形生物的攻击加成（供 Weapon.proc 调用）。 */
	public static int bonusFor(Char attacker, Char defender, int damage) {
		if (!(attacker instanceof Hero) || defender == null) return damage;
		SealingTalisman talisman = ((Hero) attacker).belongings.getItem(SealingTalisman.class);
		if (talisman == null) return damage;
		if (!isNonHumanoid(defender)) return damage;
		return damage + talisman.bonusVsNonHumanoid();
	}

	/** SPSEXPD: 这里把「非人形」定义为亡灵／恶魔／首领之外的反面——即这三类不算人形。 */
	public static boolean isNonHumanoid(Char ch) {
		return ch.properties().contains(Char.Property.UNDEAD)
				|| ch.properties().contains(Char.Property.DEMONIC)
				|| ch.properties().contains(Char.Property.BOSS);
	}

	public Weapon.Enchantment sealedEnchantment() {
		return sealed;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_ABSORB);
		actions.add(AC_APPLY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (AC_ABSORB.equals(action)) {
			if (sealed != null) {
				GLog.w(Messages.get(this, "already_sealed"));
				return;
			}
			GameScene.selectItem(absorbSelector);
		} else if (AC_APPLY.equals(action)) {
			if (sealed == null) {
				GLog.w(Messages.get(this, "empty"));
				return;
			}
			GameScene.selectItem(applySelector);
		}
	}

	private final WndBag.ItemSelector absorbSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(SealingTalisman.class, "prompt_absorb"); }
		@Override public boolean itemSelectable(Item item) {
			return item instanceof Weapon && ((Weapon) item).enchantment != null;
		}
		@Override public void onSelect(Item item) {
			if (!(item instanceof Weapon)) return;
			Weapon wep = (Weapon) item;
			if (wep.enchantment == null) {
				GLog.w(Messages.get(SealingTalisman.class, "no_enchant"));
				return;
			}
			sealed = wep.enchantment;
			wep.enchant(null);
			GLog.p(Messages.get(SealingTalisman.class, "absorbed", sealed.name("")));
		}
	};

	private final WndBag.ItemSelector applySelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(SealingTalisman.class, "prompt_apply"); }
		@Override public boolean itemSelectable(Item item) {
			return item instanceof Weapon;
		}
		@Override public void onSelect(Item item) {
			if (!(item instanceof Weapon)) {
				GLog.w(Messages.get(SealingTalisman.class, "no_weapon"));
				return;
			}
			if (sealed == null) {
				GLog.w(Messages.get(SealingTalisman.class, "empty"));
				return;
			}
			Weapon.Enchantment ench = sealed;
			sealed = null;
			((Weapon) item).enchant(ench);
			GLog.p(Messages.get(SealingTalisman.class, "applied", ench.name(""), item.name()));
		}
	};

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SEALED, sealed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		sealed = (Weapon.Enchantment) bundle.get(SEALED);
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
