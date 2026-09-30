/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.Badges;
import pd.Dungeon;
import pd.actors.buffs.*;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.NPC;
import pd.items.Generator;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.windows.WndBag;
import com.watabou.utils.Random;

/** The four follower class skills from SPS-PD 0.9.8. */
public class FollowerSkill extends ClassSkill {
	{ image = ItemSpriteSheet.ARTIFACT_CLOAK; }

	@Override public void doSpecial() {
		Buff.affect(curUser, ParyAttack.class);
		addCooldown(15);
		finishSkillCast();
	}

	@Override public void doSpecial2() {
		int people = 0;
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			mob.beckon(curUser.pos);
			if (!(mob instanceof NPC)) people++;
		}
		if (curUser.lvl > 55) Dungeon.gold += people * (curUser.lvl / 10 + 100);
		for (int i = 0; i < people; i++) if (Random.Int(4) == 0) dropAtHero(Generator.random());
		addCooldown(20);
		finishSkillCast();
	}

	@Override public void doSpecial3() {
		if (curUser.spendPermanentHT(40)) {
			curUser.STR++;
			curUser.improveAttackSkill(1);
			curUser.improveDefenseSkill(1);
			curUser.improveMagicSkill(1);
			Buff.affect(curUser, Blasphemy.class).level(curUser.lvl > 55 ? 2 : 1);
		} else {
			Buff.prolong(curUser, AttackUp.class, 50f).level(50);
			Buff.prolong(curUser, ArmorBreak.class, 50f).level(50);
		}
		addCooldown(15);
		finishSkillCast();
	}

	@Override public void doSpecial4() { GameScene.selectItem(upgradeSelector); }

	private final WndBag.ItemSelector upgradeSelector = new WndBag.ItemSelector() {
		@Override public String textPrompt() { return name(); }
		@Override public Class<? extends Bag> preferredBag() { return pd.actors.hero.Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item.isUpgradable(); }
		@Override public void onSelect(Item item) {
			if (item == null) return;
			item.upgrade();
			if (curUser.lvl > 55) item.uncurse();
			Badges.validateItemLevelAquired(item);
			addCooldown(40);
			finishSkillCast();
		}
	};
}
