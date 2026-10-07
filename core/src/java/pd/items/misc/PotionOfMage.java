/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.EquipmentNonEquipDict;

import pd.Assets;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.effects.Enchanting;
import pd.effects.particles.PurpleParticle;
import pd.items.BrokenSeal;
import pd.items.Item;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.consum.scrolls.exotic.ScrollOfEnchantment;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class PotionOfMage extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(PotionOfMage.class)
			.t("name", "奇迹烧瓶")
			.t("ac_enchant", "灌注")
			.t("select", "选择要灌注的装备")
			.t("enchant_weapon", "烧瓶中的魔力涌入了%s。")
			.t("enchant_armor", "烧瓶中的魔力渗入了%s。")
			.t("charge", "质量%d / %d。")
			.t("desc", "法师多年研究成果之一，会吸收你获得经验的一半化为瓶中魔力（每次至少为你保留1点经验）。\n瓶中魔力蓄满时，可以将其灌注进一件装备，为其附加一个随机的强力附魔。");
	}



	public static final String AC_ENCHANT = "ENCHANT";
	/** SPSEXPD: 每次灌注消耗的魔力。 */
	public static final float TIME_TO_ENCHANT = 1f;
	private static final String CHARGE = "charge";
	public static final int FULL_CHARGE = 100;
	private int charge;

	{
		image = EquipmentNonEquipDict.MIRACLE_FLASK;
		defaultAction = AC_ENCHANT;
		unique = true;
	}

	public int charge() { return charge; }
	public void gainCharge() {
		if (charge < FULL_CHARGE) {
			charge++;
			updateQuickslot();
		}
	}

	/** SPSEXPD: 吸收获得的经验（Hero.earnExp 按 50% 抽取后转入）。 */
	public void addCharge( int amount ) {
		if (amount <= 0 || charge >= FULL_CHARGE) return;
		charge = Math.min(FULL_CHARGE, charge + amount);
		updateQuickslot();
	}

	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= FULL_CHARGE) actions.add(AC_ENCHANT);
		actions.remove(AC_THROW);
		actions.remove(AC_DROP);
		return actions;
	}

	@Override public void execute(Hero hero, String action) {
		if (AC_ENCHANT.equals(action)) {
			curUser = hero;
			if (charge < FULL_CHARGE) GLog.i(Messages.get(this, "charge", charge, FULL_CHARGE));
			else GameScene.selectItem(itemSelector);
			return;
		}
		super.execute(hero, action);
	}

	/** SPSEXPD: 武器取 uncommon/rare 池的强力附魔（排除现有同类）。 */
	private static Weapon.Enchantment strongEnchantment( Weapon weapon ) {
		Class<? extends Weapon.Enchantment> existing =
				weapon.enchantment != null ? weapon.enchantment.getClass() : null;
		return Random.Int(2) == 0
				? Weapon.Enchantment.randomUncommon( existing )
				: Weapon.Enchantment.randomRare( existing );
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(PotionOfMage.class, "select"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return charge >= FULL_CHARGE && ScrollOfEnchantment.enchantable(item);
		}
		@Override public void onSelect(Item item) {
			if (item == null || charge < FULL_CHARGE) return;
			if (item instanceof Weapon) {
				Weapon weapon = (Weapon)item;
				weapon.enchant( strongEnchantment(weapon) );
				finishEnchant(weapon, Messages.get(PotionOfMage.class, "enchant_weapon", weapon.name()));
			} else if (item instanceof Armor) {
				Armor armor = (Armor)item;
				armor.inscribe( Armor.Glyph.random( armor.glyph != null ? armor.glyph.getClass() : null ) );
				finishEnchant(armor, Messages.get(PotionOfMage.class, "enchant_armor", armor.name()));
			} else if (item instanceof BrokenSeal) {
				BrokenSeal seal = (BrokenSeal)item;
				seal.inscribe( Armor.Glyph.random(
						seal.getGlyph() != null ? seal.getGlyph().getClass() : null ) );
				finishEnchant(seal, Messages.get(PotionOfMage.class, "enchant_armor", seal.name()));
			}
		}
	};

	private void finishEnchant( Item item, String message ) {
		ScrollOfRemoveCurse.uncurse(curUser, item);
		item.identify();
		GLog.p(message);
		charge = 0;
		updateQuickslot();
		if (curUser.sprite != null) {
			curUser.sprite.operate(curUser.pos);
			curUser.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
			Enchanting.show(curUser, item);
		}
		Sample.INSTANCE.play(Assets.Sounds.MISS);
		curUser.spendAndNext(TIME_TO_ENCHANT);
	}

	@Override public String status() { return Integer.toString(charge); }
	@Override public String info() { return desc() + "\n\n" + Messages.get(this, "charge", charge, FULL_CHARGE); }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 30 * quantity; }

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}
	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE)));
	}
}
