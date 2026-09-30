/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.actors.damagetype.SpsMagicDamage;
import pd.actors.mobs.Mob;
import pd.items.*;
import pd.items.armor.Armor;
import pd.items.artifacts.Artifact;
import pd.items.bags.Bag;
import pd.items.bombs.DungeonBomb;
import pd.items.rings.Ring;
import pd.items.scrolls.ScrollOfTransmutation;
import pd.items.wands.Wand;
import pd.items.weapon.Weapon;
import pd.plants.Plant;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndBag;

/** The four performer class skills from SPS-PD 0.9.8. */
public class PerformerSkill extends ClassSkill {
	{ image = ItemSpriteSheet.ARTIFACT_HORN1; }

	@Override public void doSpecial() {
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (!visibleMob(mob, Integer.MAX_VALUE)) continue;
			Buff.affect(mob, Charm.class, 10f).object = curUser.id();
			Buff.prolong(mob, Amok.class, 10f);
			Buff.prolong(mob, HasteBuff.class, 5f);
			Buff.prolong(mob, ArmorBreak.class, 20f).level(50);
		}
		Buff.prolong(curUser, DefenceUp.class, 10f).level(25);
		Buff.prolong(curUser, AttackUp.class, 10f).level(25);
		Buff.prolong(curUser, HighVoice.class, 100f);
		addCooldown(curUser.lvl > 55 ? 10 : 20);
		finishSkillCast();
	}

	@Override public void doSpecial2() {
		Buff.prolong(curUser, HighVoice.class, 100f);
		if (curUser.lvl > 55) dropAtHero(new DungeonBomb());
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (!visibleMob(mob, Integer.MAX_VALUE)) continue;
			int damage = Math.max(1, Math.round(curUser.lvl * (1f + 0.1f * curUser.magicSkill())));
			mob.damage(damage, SpsMagicDamage.ENERGY);
			Item seed = Generator.random(Generator.Category.SEED);
			if (seed instanceof Plant.Seed && Dungeon.level.insideMap(mob.pos)) Dungeon.level.plant((Plant.Seed) seed, mob.pos);
			if (mob.isAlive()) {
				Buff.prolong(mob, Blindness.class, 10f);
				Buff.prolong(mob, Slow.class, 10f);
			}
		}
		addCooldown(10);
		finishSkillCast();
	}

	@Override public void doSpecial3() { GameScene.selectItem(transmutationSelector); }

	private final WndBag.ItemSelector transmutationSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return name(); }
		@Override public Class<? extends Bag> preferredBag() { return pd.actors.hero.Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) {
			return !item.isEquipped(Dungeon.hero) && (item instanceof Weapon || item instanceof Armor
					|| item instanceof Ring || item instanceof Wand || item instanceof Artifact);
		}
		@Override public void onSelect(Item item) {
			if (item == null) return;
			Item result = ScrollOfTransmutation.changeItem(item);
			if (result == null) return;
			item.detach(Dungeon.hero.belongings.backpack);
			if (!result.collect()) dropAtHero(result);
			Buff.prolong(curUser, HighVoice.class, 100f);
			if (curUser.lvl > 55) dropAtHero(new TransmutationBall());
			addCooldown(20);
			finishSkillCast();
		}
	};

	@Override public void doSpecial4() {
		Buff.prolong(curUser, HighVoice.class, 100f);
		Buff.affect(curUser, LearnSkill.class).set(50);
		addCooldown(20);
		finishSkillCast();
	}
}
