package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Tinkerer2;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Mushroom;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.ActiveMrDestructo;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.FairyCard;
import com.shatteredpixel.shatteredpixeldungeon.items.summon.Mobile;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

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
