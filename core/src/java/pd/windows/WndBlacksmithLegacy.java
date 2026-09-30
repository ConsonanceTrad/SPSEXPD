package pd.windows;

import pd.actors.hero.Belongings;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.Blacksmith;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.ui.ItemButton;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

/** Single-use SPS 0.9.8 blacksmith reforge window. */
public class WndBlacksmithLegacy extends Window {

	private static final int WIDTH = 116;
	private static final int BTN_SIZE = 32;
	private static final int BTN_GAP = 8;
	private static final int GAP = 2;

	private ItemButton pressed;
	private ItemButton first;
	private ItemButton second;
	private RedButton reforge;

	public WndBlacksmithLegacy(Blacksmith smith, Hero hero) {
		IconTitle title = new IconTitle();
		title.icon(smith.sprite());
		title.label(Messages.titleCase(smith.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "prompt"), 6);
		message.maxWidth(WIDTH);
		message.setPos(0, title.bottom() + GAP);
		add(message);

		first = itemButton(Messages.get(this, "select1"));
		first.setRect((WIDTH - BTN_GAP) / 2f - BTN_SIZE,
				message.bottom() + BTN_GAP, BTN_SIZE, BTN_SIZE);
		add(first);

		second = itemButton(Messages.get(this, "select2"));
		second.setRect(first.right() + BTN_GAP, first.top(), BTN_SIZE, BTN_SIZE);
		add(second);

		reforge = new RedButton(Messages.get(this, "reforge")) {
			@Override
			protected void onClick() {
				if (Blacksmith.upgradeLegacy(first.item(), second.item())) hide();
			}
		};
		reforge.enable(false);
		reforge.setRect(0, first.bottom() + BTN_GAP, WIDTH, 20);
		add(reforge);
		resize(WIDTH, (int)reforge.bottom());
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

	private final LegacySelector selector = new LegacySelector();

	private class LegacySelector extends WndBag.ItemSelector {
		private String prompt;
		@Override public String textPrompt() { return prompt; }
		@Override public Class<? extends Bag> preferredBag() { return Belongings.Backpack.class; }
		@Override public boolean itemSelectable(Item item) { return item.isUpgradable(); }
		@Override
		public void onSelect(Item item) {
			if (item == null || pressed == null) return;
			pressed.item(item);
			if (first.item() == null || second.item() == null) {
				reforge.enable(false);
				return;
			}
			String error = Blacksmith.verifyLegacy(first.item(), second.item());
			reforge.enable(error == null);
			if (error != null) GameScene.show(new WndMessage(error));
		}
	}
}
