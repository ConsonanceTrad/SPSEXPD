/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.scenes.CellSelector;
import pd.scenes.GameScene;
import pd.utils.GLog;
import pd.windows.WndBag;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/**
 * SPSEXPD: SPS 0.9.8 之外原创武器——投掷器。
 *
 * <p>内部可以装填一件投掷武器：装填后射程不再受限制，攻击超出本体射程的目标时改为射出内部的
 * 投掷武器（以其伤害与附魔结算，并消耗一件）。没有装填时它只能当短程武器使用。</p>
 */
public class Thrower extends Weapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Thrower.class)
			.t("name", "投掷器")
			.t("ac_shoot", "射击")
			.t("ac_load", "装填")
			.t("desc", "一支便携的投射装置。把投掷物塞进内仓后，它能把东西送到视野尽头；超出本体射程的目标会用内仓里的投掷物结算，每射出一件就少一件。")
			.t("prompt", "选择要装填的投掷物")
			.t("shoot_prompt", "选择射击的目标")
			.t("no_ammo", "内仓是空的。")
			.t("already_loaded", "内仓里已经有投掷物了。")
			.t("load_ok", "你把%1$s装进了内仓。")
			.t("empty", "内仓空了。")
			.t("out_of_range", "没有装填时射程只有%1$d格。")
			.t("no_target", "那里没有目标。")
			.t("ammo", "内仓：%1$s（剩余 %2$d）");
	}

	public static final String AC_SHOOT = "SHOOT";
	public static final String AC_LOAD = "LOAD";

	/** 没有装填时的射程上限。 */
	public static final int BASE_RANGE = 3;
	public static final float LOAD_TIME = 2f;

	private static final String LOADED = "loaded";

	private MissileWeapon loaded;

	{
		image = SpecificPlaceHolderDict.SPS_PH_WEAPON;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.1f;
		defaultAction = AC_SHOOT;
		usesTargeting = true;
	}

	public Thrower() {
		DLY = 1f;
		RCH = 1;
	}

	@Override public int min(int lvl) { return 4 + lvl; }
	@Override public int max(int lvl) { return 10 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 13; }
	@Override public boolean isUpgradable() { return true; }
	@Override public boolean isIdentified() { return true; }

	public boolean isLoaded() {
		return loaded != null;
	}

	public MissileWeapon loadedAmmo() {
		return loaded;
	}

	/** 射程：没有装填时受本体射程限制，装填后视为无限。 */
	public int rangeLimit() {
		return loaded == null ? BASE_RANGE : Integer.MAX_VALUE;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SHOOT);
		actions.add(AC_LOAD);
		return actions;
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (AC_SHOOT.equals(action)) return Messages.get(this, "ac_shoot");
		if (AC_LOAD.equals(action)) return Messages.get(this, "ac_load");
		return super.actionName(action, hero);
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_SHOOT.equals(action)) {
			curUser = hero;
			GameScene.selectCell(shooter);
		} else if (AC_LOAD.equals(action)) {
			if (loaded != null) {
				GLog.i(Messages.get(this, "already_loaded"));
			} else {
				curUser = hero;
				GameScene.selectItem(ammoSelector);
			}
		} else {
			super.execute(hero, action);
		}
	}

	public static boolean isValidAmmo(Item item) {
		return item instanceof MissileWeapon && !((MissileWeapon) item).spawnedForEffect;
	}

	public boolean loadAmmoFromBackpack(Hero owner, Item selected) {
		if (owner == null || loaded != null || !isValidAmmo(selected)) return false;
		Bag backpack = owner.belongings.backpack;
		if (!backpack.contains(selected)) return false;
		//SPSEXPD: 整叠装填，射空后内仓才清空
		Item detached = selected.detachAll(backpack);
		if (!(detached instanceof MissileWeapon)) return false;
		loaded = (MissileWeapon) detached;
		loaded.identify();
		loaded.cursed = false;
		updateQuickslot();
		return true;
	}

	/** 射出一件内仓投掷物；用完后内仓自动清空。 */
	public boolean consumeAmmo() {
		if (loaded == null) return false;
		if (loaded.quantity() > 1) {
			loaded.quantity(loaded.quantity() - 1);
		} else {
			loaded = null;
		}
		updateQuickslot();
		return true;
	}

	private final WndBag.ItemSelector ammoSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(Thrower.this, "prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return isValidAmmo(item); }
		@Override public void onSelect(Item item) {
			if (item == null || curUser == null) return;
			if (!loadAmmoFromBackpack(curUser, item)) return;
			if (curUser.sprite != null) curUser.sprite.operate(curUser.pos);
			Sample.INSTANCE.play(Assets.Sounds.EVOKE);
			GLog.i(Messages.get(Thrower.this, "load_ok", loaded != null ? loaded.name() : ""));
			curUser.spendAndNext(LOAD_TIME);
		}
	};

	private final CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect(Integer target) {
			if (target == null || curUser == null || Dungeon.level == null) return;
			Hero hero = curUser;
			if (!Dungeon.level.insideMap(target)) return;

			int distance = Dungeon.level.distance(hero.pos, target);
			boolean beyondReach = distance > BASE_RANGE;
			if (beyondReach && loaded == null) {
				GLog.i(Messages.get(Thrower.this, "out_of_range", BASE_RANGE));
				return;
			}

			Char enemy = Actor.findChar(target);
			if (enemy == null || enemy == hero) {
				GLog.i(Messages.get(Thrower.this, "no_target"));
				return;
			}

			//SPSEXPD: 超出本体射程时射出内仓的投掷武器，并用它自己的伤害与附魔结算
			MissileWeapon ammo = beyondReach ? loaded : null;
			int damage = ammo != null ? ammo.damageRoll(hero) : damageRoll(hero);
			damage = ammo != null ? ammo.proc(hero, enemy, damage) : proc(hero, enemy, damage);
			Object source = ammo != null ? ammo : this;
			enemy.damage(damage, source);
			if (ammo != null) {
				consumeAmmo();
				if (!isLoaded()) GLog.i(Messages.get(Thrower.this, "empty"));
			}

			if (hero.sprite != null) hero.sprite.zap(target);
			hero.spendAndNext(1f);
		}

		@Override
		public String prompt() {
			return Messages.get(Thrower.this, "shoot_prompt");
		}
	};

	@Override
	public String info() {
		String info = super.info();
		if (loaded != null) {
			info += "\n\n" + Messages.get(this, "ammo", loaded.name(), loaded.quantity());
		}
		return info;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (loaded != null) bundle.put(LOADED, loaded);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		Object restored = bundle.get(LOADED);
		loaded = restored instanceof MissileWeapon ? (MissileWeapon) restored : null;
	}
}
