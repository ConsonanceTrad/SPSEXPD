package pd.items.medicine;

import pd.Assets;
import pd.Challenges;
import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.potions.PotionOfHealing;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.messages.Messages;
import watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class MendingTonic extends Item {

	public static final String AC_DRINK = "DRINK";

	{
		image = ItemSpriteSheet.POTION_CRIMSON;
		stackable = true;
		defaultAction = AC_DRINK;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_DRINK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (!AC_DRINK.equals(action)) return;
		detach(hero.belongings.backpack);
		PotionOfHealing.cure(hero);
		if (Dungeon.isChallenged(Challenges.NO_HEALING)) {
			PotionOfHealing.pharmacophobiaProc(hero);
		} else {
			int healing = 8 + hero.HT / 3;
			Buff.affect(hero, Healing.class).setHeal(healing, 0.2f, 0, true);
			GLog.p(Messages.get(this, "mend"));
		}
		Sample.INSTANCE.play(Assets.Sounds.DRINK);
		hero.sprite.operate(hero.pos);
		hero.spendAndNext(1f);
	}

	@Override public boolean isIdentified() { return true; }
	@Override public boolean isUpgradable() { return false; }
	@Override public int value() { return 20 * quantity; }
}
