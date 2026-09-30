package pd.actors.mobs.npcs;

import pd.Badges;
import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.items.AdamantArmor;
import pd.items.AdamantRing;
import pd.items.AdamantWand;
import pd.items.AdamantWeapon;
import pd.items.Item;
import pd.items.armor.Armor;
import pd.items.quest.DarkGold;
import pd.items.rings.Ring;
import pd.items.sellitem.BrokenHammer;
import pd.items.wands.Wand;
import pd.items.weapon.guns.GunWeapon;
import pd.items.weapon.melee.MeleeWeapon;
import pd.items.weapon.ranges.RangeWeapon;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ElectricwelderSprite;
import pd.utils.GLog;
import pd.windows.WndBlacksmith2;
import pd.windows.WndQuest;
import render.noosa.Game;
import render.utils.Callback;

/** SPS troll welder, who combines equipment with matching adamant components. */
public class Blacksmith2 extends NPC {

	{
		spriteClass = ElectricwelderSprite.class;
		properties.add(Property.TROLL);
		properties.add(Property.IMMOVABLE);
	}

	@Override
	public Item SupercreateLoot() {
		return new BrokenHammer();
	}

	@Override
	public boolean interact(Char c) {
		sprite.turnTo(pos, c.pos);
		if (c != Dungeon.hero) return true;

		DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
		String message;
		if (!hasAdamant()) {
			message = Messages.get(this, "himself");
		} else if (gold == null || gold.quantity() < 50) {
			message = Messages.get(this, "adamantite");
		} else {
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndBlacksmith2(Blacksmith2.this, Dungeon.hero));
				}
			});
			return true;
		}

		final String text = message;
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show(new WndQuest(Blacksmith2.this, text));
			}
		});
		return true;
	}

	public static String verify(Item equipment, Item adamant) {
		if (equipment == adamant) return Messages.get(Blacksmith2.class, "same_item");
		if (!equipment.isIdentified()) return Messages.get(Blacksmith2.class, "un_ided");
		if (equipment.cursed) return Messages.get(Blacksmith2.class, "cursed");
		if (equipment.isReinforced()) return Messages.get(Blacksmith2.class, "already_reforge");
		if (equipment.level() < 0) return Messages.get(Blacksmith2.class, "degraded");
		if (!equipment.isUpgradable()) return Messages.get(Blacksmith2.class, "cant_reforge");

		if (equipment instanceof Armor && adamant instanceof AdamantArmor) return null;
		if ((equipment instanceof MeleeWeapon || equipment instanceof GunWeapon
				|| equipment instanceof RangeWeapon) && adamant instanceof AdamantWeapon) return null;
		if (equipment instanceof Wand && adamant instanceof AdamantWand) return null;
		if (equipment instanceof Ring && adamant instanceof AdamantRing) return null;
		return Messages.get(Blacksmith2.class, "cant_work");
	}

	public static boolean upgrade(Item equipment, Item adamant) {
		if (Dungeon.hero == null || verify(equipment, adamant) != null) return false;
		DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
		if (gold == null || gold.quantity() < 50) return false;

		equipment.reinforce();
		adamant.detach(Dungeon.hero.belongings.backpack);
		if (gold.quantity() == 50) {
			gold.detachAll(Dungeon.hero.belongings.backpack);
		} else {
			gold.quantity(gold.quantity() - 50);
			Item.updateQuickslot();
		}
		GLog.p(Messages.get(Blacksmith2.class, "looks_better", equipment.name()));
		Dungeon.hero.spendAndNext(2f);
		Badges.validateItemLevelAquired(equipment);
		return true;
	}

	public static boolean hasAdamant() {
		if (Dungeon.hero == null) return false;
		return Dungeon.hero.belongings.getItem(AdamantArmor.class) != null
				|| Dungeon.hero.belongings.getItem(AdamantWeapon.class) != null
				|| Dungeon.hero.belongings.getItem(AdamantRing.class) != null
				|| Dungeon.hero.belongings.getItem(AdamantWand.class) != null;
	}

	@Override public int defenseSkill(Char enemy) { return INFINITE_EVASION; }
	@Override public void damage(int dmg, Object src) { }
	@Override public boolean add(Buff buff) { return false; }
	@Override public boolean reset() { return true; }
}
