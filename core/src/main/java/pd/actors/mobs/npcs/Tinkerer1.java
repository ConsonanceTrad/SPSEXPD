package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.items.Waterskin;
import pd.items.quest.Mushroom;
import pd.items.sellitem.SellMushroom;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.TinkererSprite;
import pd.windows.WndQuest;
import pd.windows.WndTinkerer;
import render.noosa.Game;

public class Tinkerer1 extends NPC {

	{
		spriteClass = TinkererSprite.class;
		properties.add(Property.IMMOVABLE);
		properties.add(Property.HUMAN);
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 1000;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public boolean add(Buff buff) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public SellMushroom SupercreateLoot() {
		return new SellMushroom();
	}

	@Override
	public boolean interact(Char ch) {
		sprite.turnTo(pos, Dungeon.hero.pos);
		if (ch != Dungeon.hero) return true;

		Mushroom mushroom = Dungeon.hero.belongings.getItem(Mushroom.class);
		Waterskin waterskin = Dungeon.hero.belongings.getItem(Waterskin.class);
		Game.runOnRenderThread(() -> {
			if (mushroom != null && waterskin != null) {
				GameScene.show(new WndTinkerer(Tinkerer1.this));
			} else {
				GameScene.show(new WndQuest(Tinkerer1.this,
						Messages.get(Tinkerer1.this, waterskin == null ? "tell2" : "tell1")));
			}
		});
		return true;
	}
}
