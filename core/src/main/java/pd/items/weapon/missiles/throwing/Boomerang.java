/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.throwing;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.items.weapon.missiles.MissileWeapon;
import pd.items.weapon.spammo.SpAmmo;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;
import pd.sprites.MissileSprite;
import pd.windows.WndBag;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.utils.serialize.Bundle;

import java.util.ArrayList;

/** The SPS-PD boomerang, which returns immediately and never loses durability. */
public class Boomerang extends MissileWeapon {
	public static final String AC_AMMO = "AMMO";
	private static final String SP_AMMO = "sp_ammo";

	private SpAmmo ammo;

	{
		image = ItemSpriteSheet.LEGACY_BOOMERANG;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1f;
		tier = 1;
		stackable = false;
		unique = true;
		reinforced = true;
		sticky = false;
	}

	@Override public int defaultQuantity() { return 1; }
	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 6 + 2 * lvl; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public float durabilityPerUse(int level) { return 0f; }
	@Override public boolean isUpgradable() { return true; }

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_AMMO);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_AMMO.equals(action)) {
			curUser = hero;
			GameScene.selectItem(ammoSelector);
		} else {
			super.execute(hero, action);
		}
	}

	private final WndBag.ItemSelector ammoSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return Messages.get(Boomerang.class, "prompt"); }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item instanceof SpAmmo; }
		@Override public void onSelect(Item item) {
			if (!(item instanceof SpAmmo) || curUser == null) return;
			SpAmmo selected = (SpAmmo)item;
			if (ammo == null) {
				consumeAndLoad(selected);
			} else {
				GameScene.show(new WndOptions(new ItemSprite(selected), Messages.titleCase(selected.name()),
						Messages.get(Boomerang.class, "replace", ammo.name(), selected.name()),
						Messages.get(Boomerang.class, "yes"), Messages.get(Boomerang.class, "no")) {
					@Override protected void onSelect(int index) {
						if (index == 0) consumeAndLoad(selected);
					}
				});
			}
		}
	};

	private void consumeAndLoad(SpAmmo selected) {
		if (!loadAmmoFromBackpack(curUser, selected)) return;
		if (curUser.sprite != null) curUser.sprite.operate(curUser.pos);
		Sample.INSTANCE.play(Assets.Sounds.EVOKE);
		curUser.spendAndNext(2f);
	}

	public boolean loadAmmoFromBackpack(Hero owner, SpAmmo selected) {
		if (owner == null || selected == null || !owner.belongings.backpack.contains(selected)) return false;
		Item consumed = selected.detach(owner.belongings.backpack);
		if (!(consumed instanceof SpAmmo)) return false;
		loadAmmo((SpAmmo)consumed);
		return true;
	}

	public void loadAmmo(SpAmmo ammo) {
		this.ammo = ammo;
		if (ammo != null) {
			ammo.identify();
			ammo.cursed = false;
		}
		updateQuickslot();
	}

	public SpAmmo loadedAmmo() {
		return ammo;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (ammo != null) ammo.onHit(attacker, defender, damage);
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (ammo != null) desc += "\n\n" + Messages.get(this, "ammo", ammo.name());
		return desc;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (ammo != null) bundle.put(SP_AMMO, ammo);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		Object restored = bundle.get(SP_AMMO);
		ammo = restored instanceof SpAmmo ? (SpAmmo)restored : null;
	}

	@Override
	protected void rangedHit(Char enemy, int cell) {
		parent = null;
		returnToOwner(cell, curUser);
	}

	@Override
	protected void rangedMiss(int cell) {
		parent = null;
		returnToOwner(cell, curUser);
	}

	protected void returnToOwner(int from, Hero owner) {
		if (owner == null) return;
		if (owner.sprite != null && owner.sprite.parent != null) {
			((MissileSprite)owner.sprite.parent.recycle(MissileSprite.class))
					.reset(from, owner.pos, this, null);
		}
		if (spawnedForEffect || collect(owner.belongings.backpack)) return;
		if (Dungeon.level != null && Dungeon.level.insideMap(owner.pos)) {
			Dungeon.level.drop(this, owner.pos).sprite.drop();
		}
	}
}
