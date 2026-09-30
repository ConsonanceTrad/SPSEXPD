package pd.windows;

import pd.Dungeon;
import pd.actors.mobs.npcs.Tinkerer2;
import pd.items.Item;
import pd.items.quest.Mushroom;
import pd.items.summon.ActiveMrDestructo;
import pd.items.summon.FairyCard;
import pd.items.summon.Mobile;
import pd.messages.Messages;

public class WndTinkerer2 extends WndOptions {

	private final Tinkerer2 tinkerer;

	public WndTinkerer2(Tinkerer2 tinkerer) {
		super(tinkerer.sprite(), Messages.titleCase(tinkerer.name()),
				Messages.get(WndTinkerer2.class, "info"),
				Messages.get(WndTinkerer2.class, "mr"),
				Messages.get(WndTinkerer2.class, "call"),
				Messages.get(WndTinkerer2.class, "mob"));
		this.tinkerer = tinkerer;
	}

	@Override
	protected void onSelect(int index) {
		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		if (mushroom == null || index < 0 || index > 2) return;
		mushroom.detach(Dungeon.hero.belongings.backpack);

		Item reward;
		if (index == 0) reward = new ActiveMrDestructo();
		else if (index == 1) reward = new FairyCard();
		else reward = new Mobile();
		Dungeon.dewNorn = true;
		Dungeon.level.drop(reward, Dungeon.hero.pos).sprite.drop();
		tinkerer.yell(Messages.get(WndTinkerer2.class, "farewell", Dungeon.hero.name()));
		tinkerer.destroy();
		tinkerer.sprite.die();
	}
}
