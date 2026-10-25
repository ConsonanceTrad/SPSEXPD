/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.items.consum.scrolls.ScrollOfUpgrade;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.Assets;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.effects.Enchanting;
import pd.effects.particles.PurpleParticle;
import pd.items.BrokenSeal;
import pd.items.Item;
import pd.items.consum.scrolls.ScrollOfRemoveCurse;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.items.unique.UniqueItem;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import java.util.Collections;

/**
 * SPSEXPD: 秘银手杖 —— 法师一档（面具 B 路线）的近战特殊武器。
 *
 * <p>_无法施法_，只是一根近战武器：每次近战命中会为_随机 2 支_法杖少量补充能量，
 * 并且带上你_上次施放的法术_的完整命中效果。</p>
 *
 * <p>它还_继承奇迹烧瓶的附魔功能_：吸收所得经验的一半化为魔力，蓄满后可以把魔力
 * 灌注进一件装备，为其附加随机强力附魔。</p>
 */
public class MythrilStaff extends MagesStaff implements UniqueItem {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.MAGE_STAFF_T1_2_MITHRIL;

	public static final String AC_ENCHANT = "ENCHANT";

	/** 近战一次为多少支法杖回能。 */
	public static final int RECHARGE_COUNT = 2;
	/** 每支法杖补充的充能点数。 */
	public static final float RECHARGE_AMOUNT = 0.34f;
	/** SPSEXPD: 灌注所需的魔力上限（与奇迹烧瓶一致）。 */
	public static final int FULL_CHARGE = 100;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MythrilStaff.class)
			.t("name", "秘银手杖")
			.t("ac_enchant", "灌注")
			.t("select", "选择要灌注的装备")
			.t("enchant_weapon", "手杖中的魔力涌入了%s。")
			.t("enchant_armor", "手杖中的魔力渗入了%s。")
			.t("charge", "魔力%d / %d。")
			.t("desc", "一根封了银的实心手杖——它_没法施法_，只能拿来打人。\n\n每次近战命中，它会为_随机 %1$d 支_法杖各补上一点能量；同时，你_上次施放的法术_会顺着杖身涌出，让这一击带上那道法术的完整命中效果。\n\n它也会像奇迹烧瓶那样吸收你获得的_一半经验_化为魔力（每次至少为你保留 1 点经验）；魔力_蓄满_时，可以把魔力_灌注_进一件装备，为其附加一个随机的强力附魔。");
	}

	/** SPSEXPD: 手杖自己攒的魔力（0 ~ FULL_CHARGE）。 */
	protected int charge;

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", RECHARGE_COUNT);
	}

	public int charge() {
		return charge;
	}

	/** SPSEXPD: 吸收经验转化来的魔力。 */
	public void addCharge(int amount) {
		if (amount <= 0 || charge >= FULL_CHARGE) return;
		charge = Math.min(FULL_CHARGE, charge + amount);
		updateQuickslot();
	}

	/** SPSEXPD: 无法施法——移除「释放」与「灌注」动作；魔力满了才加上「灌注附魔」。 */
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_ZAP);
		actions.remove(AC_IMBUE);
		actions.remove(AC_ENCHANT);
		if (charge >= FULL_CHARGE) actions.add(AC_ENCHANT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		//SPSEXPD: 没法施法——这两个动作直接忽略
		if (AC_ZAP.equals(action) || AC_IMBUE.equals(action)) return;
		if (AC_ENCHANT.equals(action)) {
			//SPSEXPD: 灌注附魔（继承奇迹烧瓶）
			curUser = hero;
			if (charge < FULL_CHARGE) GLog.i(Messages.get(this, "charge", charge, FULL_CHARGE));
			else GameScene.selectItem(itemSelector);
			return;
		}
		super.execute(hero, action);
	}

	/** 本次近战为多少支法杖回能（精金手杖覆写为「全部」）。 */
	protected int rechargeCount(int total) {
		return Math.min(RECHARGE_COUNT, total);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (attacker instanceof Hero) {
			onMeleeRecharge((Hero) attacker, defender, damage);
		}
		return super.proc(attacker, defender, damage);
	}

	/** SPSEXPD: 近战命中 → 给若干支法杖回能 + 重放上次施法的完整命中效果。 */
	protected void onMeleeRecharge(Hero hero, Char defender, int damage) {
		ArrayList<Wand> wands = new ArrayList<>();
		for (Item it : hero.belongings) {
			if (it instanceof Wand) wands.add((Wand) it);
		}
		if (!wands.isEmpty()) {
			int n = rechargeCount(wands.size());
			if (n < wands.size()) Collections.shuffle(wands);
			for (int i = 0; i < n; i++) {
				wands.get(i).gainCharge(RECHARGE_AMOUNT);
			}
		}

		Class<? extends Wand> lastSpell = SpsSpellMemory.last();
		if (lastSpell != null && defender != null) {
			Wand echo = render.utils.serialize.Reflection.newInstance(lastSpell);
			if (echo != null) {
				echo.onHit(this, hero, defender, damage);
			}
		}
	}

	// ---------- SPSEXPD: 灌注附魔（继承奇迹烧瓶） ----------

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(MythrilStaff.class, "select"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return charge >= FULL_CHARGE && ScrollOfUpgrade.enchantable(item);
		}
		@Override public void onSelect(Item item) {
			if (item == null || charge < FULL_CHARGE) return;
			if (item instanceof Weapon) {
				Weapon weapon = (Weapon)item;
				weapon.enchant( pd.items.misc.PotionOfMage.strongEnchantment(weapon) );
				finishEnchant(weapon, Messages.get(MythrilStaff.class, "enchant_weapon", weapon.name()));
			} else if (item instanceof Armor) {
				Armor armor = (Armor)item;
				armor.inscribe( Armor.Glyph.random( armor.glyph != null ? armor.glyph.getClass() : null ) );
				finishEnchant(armor, Messages.get(MythrilStaff.class, "enchant_armor", armor.name()));
			} else if (item instanceof BrokenSeal) {
				BrokenSeal seal = (BrokenSeal)item;
				seal.inscribeRandom();
				finishEnchant(seal, Messages.get(MythrilStaff.class, "enchant_armor", seal.name()));
			}
		}
	};

	/** SPSEXPD: 灌注完成——解咒 + 鉴定 + 特效，并清空魔力。 */
	private void finishEnchant(Item item, String message) {
		ScrollOfRemoveCurse.uncurse(curUser, item);
		item.identify();
		GLog.p(message);
		charge = 0;
		updateQuickslot();
		if (curUser != null && curUser.sprite != null) {
			curUser.sprite.operate(curUser.pos);
			curUser.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
			pd.effects.Enchanting.show(curUser, item);
		}
		Sample.INSTANCE.play(Assets.Sounds.MISS);
		if (curUser != null) curUser.spendAndNext(1f);
	}

	@Override
	public String info() {
		return super.info() + "\n\n" + Messages.get(MythrilStaff.class, "charge", charge, FULL_CHARGE);
	}

	private static final String CHARGE = "charge";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, Math.min(FULL_CHARGE, bundle.getInt(CHARGE)));
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}
