/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.Magazine;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.weapon.guns.GunWeapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: 原创神器——无限弹药袋。
 *
 * <p>装备后按回合缓慢积攒「充能点数」，上限随等级成长（4 → 10 点）。点数足够时可以
 * 「补充弹药」：消耗 4 点，把主手/副手装备的<b>常规枪械</b>弹匣一次性补满。</p>
 *
 * <p>用通用弹匣「喂食」可以让它升级——每 {@link #MAGAZINES_PER_LEVEL} 个弹匣升 1 级，
 * 等级越高充能上限越高、充能也越快（30 回合/点 → 12 回合/点）。</p>
 */
public class EndlessAmmoBag extends Artifact implements pd.items.unique.UniqueItem {

	/** SPSEXPD: 特殊物品编号（见 UniqueItemId）。 */
	public static final pd.items.unique.UniqueItemId UNIQUE_ID = pd.items.unique.UniqueItemId.SOLDIER_GUN_BASE_B;

	@Override public pd.items.unique.UniqueItemId uniqueId() { return UNIQUE_ID; }

	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EndlessAmmoBag.class)
			.t("name", "无限弹药袋")
			.t("ac_activate", "补充弹药")
			.t("ac_feed", "喂食弹匣")
			.t("activate", "弹药袋把装备的枪械弹匣补满了。")
			.t("no_gun", "你没有装备需要弹药的常规枪械。")
			.t("no_charge", "充能不足，无法补充弹药。")
			.t("fed", "你往弹药袋里塞入了弹匣，它似乎更充实了一点。")
			.t("level_up", "弹药袋升级了！充能更快、上限更高。")
			.t("level_max", "弹药袋已经达到最高等级。")
			.t("feed_prompt", "选择要喂给弹药袋的弹匣")
			.t("desc", "一个内里远比外表大的弹药袋。装备后它会缓慢积攒充能；充能足够时可以消耗_4 点_，把装备的常规枪械弹匣一次性补满。\n\n用通用弹匣喂食可以让它升级（每 2 个弹匣 1 级）。等级越高，充能上限越高（4~10 点）、积攒也越快（30 回合/点 → 12 回合/点）。")
			.t("desc_worn", "袋子正在缓慢充能，充能足够时可以补充弹药。");
	}

	public static final String AC_ACTIVATE = "ACTIVATE";
	public static final String AC_FEED = "FEED";

	/** SPSEXPD: 「补充弹药」消耗的充能点数。 */
	public static final int AMMO_COST = 4;
	/** SPSEXPD: 最高等级。 */
	public static final int MAX_LEVEL = 10;
	/** SPSEXPD: 每多少个弹匣升 1 级。 */
	public static final int MAGAZINES_PER_LEVEL = 2;
	/** SPSEXPD: 0 级时每点充能所需回合数。 */
	public static final float TURNS_PER_POINT_MIN_LEVEL = 30f;
	/** SPSEXPD: 满级时每点充能所需回合数。 */
	public static final float TURNS_PER_POINT_MAX_LEVEL = 12f;

	{
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
		levelCap = MAX_LEVEL;
		charge = 0;
		partialCharge = 0;
		chargeCap = chargeCapForLevel(0);
		defaultAction = AC_ACTIVATE;
	}

	/** SPSEXPD: 充能上限随等级成长：4（0 级）→ 10（10 级）。 */
	public static int chargeCapForLevel(int level) {
		int lvl = Math.max(0, Math.min(MAX_LEVEL, level));
		return 4 + lvl / 2 + (lvl >= MAX_LEVEL ? 1 : 0);
	}

	/** SPSEXPD: 当前等级每积攒 1 点充能所需的回合数（30 → 12）。 */
	public float turnsPerPoint() {
		int lvl = Math.max(0, Math.min(MAX_LEVEL, level()));
		float t = TURNS_PER_POINT_MIN_LEVEL
				- (TURNS_PER_POINT_MIN_LEVEL - TURNS_PER_POINT_MAX_LEVEL) * lvl / (float) MAX_LEVEL;
		return Math.max(1f, t);
	}

	public int charge() {
		return charge;
	}

	public int chargeCap() {
		//SPSEXPD: 上限跟随等级动态计算（Item.upgrade(int) 是 final，无法在升级时拦截同步）
		return chargeCapForLevel(level());
	}

	/** SPSEXPD: 把字段上限同步成当前等级对应的值，并把充能夹进范围内（供 Artifact 内部逻辑用）。 */
	private void syncChargeCap() {
		chargeCap = chargeCapForLevel(level());
		if (charge > chargeCap) charge = chargeCap;
		if (charge < 0) charge = 0;
	}

	/** 直接增减充能（受 0..chargeCap 限制）。 */
	public void addCharge(int amount) {
		charge = Math.max(0, Math.min(chargeCap(), charge + amount));
		updateQuickslot();
	}

	@Override
	public String status() {
		return charge + "/" + chargeCap();
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		syncChargeCap();
		updateQuickslot();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)) {
			if (charge >= AMMO_COST) actions.add(AC_ACTIVATE);
			if (hero.belongings.getItem(Magazine.class) != null) actions.add(AC_FEED);
		}
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);

		if (AC_ACTIVATE.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else if (charge < AMMO_COST) {
				GLog.i(Messages.get(this, "no_charge"));
			} else if (!reloadEquippedGuns(hero)) {
				GLog.i(Messages.get(this, "no_gun"));
			} else {
				addCharge(-AMMO_COST);
				GLog.p(Messages.get(this, "activate"));
				if (com.badlogic.gdx.Gdx.audio != null) Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				//SPSEXPD: 必须用 spendAndNext 结束回合——只 spend 不 next 会让回合卡住（计数器一直转）
				hero.busy();
				hero.spendAndNext(1f);
			}
		} else if (AC_FEED.equals(action)) {
			if (!isEquipped(hero)) {
				GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			} else {
				curUser = hero;
				GameScene.selectItem(feedSelector);
			}
		}
	}

	/** SPSEXPD: 给主手/副手装备的常规枪械补满弹匣；返回是否补到至少一把。 */
	public boolean reloadEquippedGuns(Hero hero) {
		boolean any = fillGun(hero, hero.belongings.weapon());
		any |= fillGun(hero, hero.belongings.secondWep());
		return any;
	}

	private boolean fillGun(Hero hero, KindOfWeapon weapon) {
		if (!(weapon instanceof GunWeapon)) return false;
		GunWeapon gun = (GunWeapon) weapon;
		if (!gun.isEquipped(hero) || !gun.usesStandardAmmo()) return false;
		if (gun.charge() >= gun.fullCharge()) return false;
		gun.fillAmmo();
		return true;
	}

	/** SPSEXPD: 喂食弹匣——每 {@link #MAGAZINES_PER_LEVEL} 个升 1 级。 */
	public boolean feedMagazines(Hero hero, int count) {
		if (hero == null || count <= 0) return false;
		if (level() >= levelCap) {
			GLog.i(Messages.get(this, "level_max"));
			return false;
		}
		exp += count;
		boolean leveled = false;
		while (level() < levelCap && exp >= MAGAZINES_PER_LEVEL) {
			exp -= MAGAZINES_PER_LEVEL;
			upgrade(1);
			leveled = true;
		}
		syncChargeCap();
		updateQuickslot();
		if (leveled) GLog.p(Messages.get(this, "level_up"));
		else GLog.i(Messages.get(this, "fed"));
		return true;
	}

	/** SPSEXPD: 喂食入口——只允许选通用弹匣，整堆喂入。 */
	private final WndBag.ItemSelector feedSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(EndlessAmmoBag.this, "feed_prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item instanceof Magazine; }
		@Override public void onSelect(Item item) {
			if (item == null || !(item instanceof Magazine)) return;
			if (curUser == null || curUser.belongings == null) return;
			if (!curUser.belongings.backpack.contains(item)) return;
			int amount = Math.max(1, item.quantity());
			Item detached = item.detachAll(curUser.belongings.backpack);
			if (detached == null) return;
			feedMagazines(curUser, amount);
			//SPSEXPD: 喂食也消耗 1 回合并推进（否则回合会卡住）
			if (curUser.sprite != null) curUser.sprite.operate(curUser.pos);
			curUser.busy();
			curUser.spendAndNext(1f);
		}
	};

	@Override
	public String desc() {
		String desc = super.desc();
		//SPSEXPD: 选角界面等无 Dungeon.hero 的场景要守卫
		if (Dungeon.hero != null && isEquipped(Dungeon.hero)) desc += "\n\n" + Messages.get(this, "desc_worn");
		return desc;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		//SPSEXPD: 等级恢复后重算上限（旧档存的是旧上限的充能值，会被夹紧）
		syncChargeCap();
		updateQuickslot();
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new Feeding();
	}

	/** 装备期间自行充能：0 级约每 30 回合 1 点，满级约每 12 回合 1 点。 */
	public class Feeding extends ArtifactBuff {
		@Override
		public boolean act() {
			syncChargeCap();
			if (charge < chargeCap) {
				partialCharge += 1f / turnsPerPoint();
				while (partialCharge >= 1f && charge < chargeCap) {
					partialCharge -= 1f;
					charge++;
				}
				if (charge >= chargeCap) partialCharge = 0f;
			} else {
				partialCharge = 0f;
			}
			updateQuickslot();
			spend(TICK);
			return true;
		}
	}
}
