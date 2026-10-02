package pd.windows;

import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Blacksmith2;
import pd.items.Item;
import pd.items.equipment.bags.Bag;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.ui.ItemButton;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

/** SPS adamant welding window. */
public class WndBlacksmith2 extends Window {

	private static final int WIDTH = 116;
	private static final int BTN_SIZE = 32;
	private static final int BTN_GAP = 8;
	private static final int GAP = 2;

	private ItemButton pressed;
	private ItemButton equipment;
	private ItemButton adamant;
	private RedButton weld;
	private final WeldSelector selector = new WeldSelector();

	public WndBlacksmith2(Blacksmith2 smith, Hero hero) {
		IconTitle title = new IconTitle();
		title.icon(smith.sprite());
		title.label(Messages.titleCase(smith.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "prompt"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + GAP);
		add(message);

		equipment = itemButton(Messages.get(this, "select1"));
		equipment.setRect((WIDTH - BTN_GAP) / 2f - BTN_SIZE,
				message.bottom() + BTN_GAP, BTN_SIZE, BTN_SIZE);
		add(equipment);

		adamant = itemButton(Messages.get(this, "select2"));
		adamant.setRect(equipment.right() + BTN_GAP, equipment.top(), BTN_SIZE, BTN_SIZE);
		add(adamant);

		weld = new RedButton(Messages.get(this, "reforge")) {
			@Override
			protected void onClick() {
				if (Blacksmith2.upgrade(equipment.item(), adamant.item())) hide();
			}
		};
		weld.enable(false);
		weld.setRect(0, equipment.bottom() + BTN_GAP, WIDTH, 20);
		add(weld);
		resize(WIDTH, (int)weld.bottom());
	}

	private ItemButton itemButton(final String prompt) {
		return new ItemButton() {
			@Override
			protected void onClick() {
				pressed = this;
				selector.prompt = prompt;
				GameScene.selectItem(selector);
			}
		};
	}

	private class WeldSelector extends WndBag.ItemSelector {
		private String prompt;
		@Override public String textPrompt() { return prompt; }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return true; }
		@Override
		public void onSelect(Item item) {
			if (item == null || pressed == null) return;
			pressed.item(item);
			if (equipment.item() == null || adamant.item() == null) {
				weld.enable(false);
				return;
			}
			String error = Blacksmith2.verify(equipment.item(), adamant.item());
			weld.enable(error == null);
			if (error != null) GameScene.show(new WndMessage(error));
		}
	}
}
