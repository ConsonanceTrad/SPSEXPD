/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.windows;

import pd.Dungeon;
import pd.items.Item;
import pd.items.bags.Bag;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import pd.sprites.ItemSprite;
import pd.ui.ItemButton;
import pd.ui.RedButton;
import pd.ui.RenderedTextBlock;
import pd.ui.Window;

/** Shared three-input layout used by the two original town crafting residents. */
abstract class WndSpsRecipe extends Window {

	private static final int WIDTH = 120;
	private static final int SLOT = 28;
	private static final int GAP = 2;

	private final ItemButton[] inputs = new ItemButton[3];
	private final ItemButton output;
	private final RedButton combine;
	private ItemButton selected;
	private boolean returningItems;

	WndSpsRecipe(Item icon, int goldCost) {
		IconTitle title = new IconTitle();
		title.icon(new ItemSprite(icon.image(), null));
		title.label(Messages.get(getClass(), "title"));
		title.setRect(0, 0, WIDTH, 0);
		add(title);

		RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(getClass(), "text"), 6);
		text.maxWidth(WIDTH);
		text.setPos(0, title.bottom() + GAP);
		add(text);

		float top = text.bottom() + 3 * GAP;
		for (int i = 0; i < inputs.length; i++) {
			ItemButton button = new ItemButton() {
				@Override protected void onClick() { chooseFor(this); }
			};
			button.setRect(15, top + i * (SLOT + GAP), SLOT, SLOT);
			inputs[i] = button;
			add(button);
		}

		output = new ItemButton() {
			@Override protected void onClick() {
				if (item() != null) GameScene.show(new WndInfoItem(item()));
			}
		};
		output.setRect(WIDTH - SLOT - 15, inputs[1].top(), SLOT, SLOT);
		add(output);

		combine = new RedButton(Messages.get(getClass(), "combine")) {
			@Override protected void onClick() {
				if (goldCost > 0) Dungeon.gold -= goldCost;
				Item result = mix(inputItems());
				for (ItemButton input : inputs) input.clear();
				output.item(result);
				if (result != null && !result.collect(Dungeon.hero.belongings.backpack)) {
					Dungeon.level.drop(result, Dungeon.hero.pos).sprite.drop();
				}
				updateState(goldCost);
			}
		};
		combine.setRect(3, inputs[2].bottom() + 5, (WIDTH - 8) / 2f, 18);
		add(combine);

		RedButton cancel = new RedButton(Messages.get(getClass(), "cancel")) {
			@Override protected void onClick() { onBackPressed(); }
		};
		cancel.setRect(combine.right() + GAP, combine.top(), (WIDTH - 8) / 2f, 18);
		add(cancel);
		resize(WIDTH, (int) cancel.bottom());
		updateState(goldCost);
	}

	private void chooseFor(ItemButton button) {
		if (button.item() != null) {
			returnItem(button.item());
			button.clear();
		}
		selected = button;
		GameScene.selectItem(new WndBag.ItemSelector() {
			@Override public String textPrompt() { return Messages.get(WndSpsRecipe.this.getClass(), "select"); }
			@Override public Class<? extends Bag> preferredBag() { return null; }
			@Override public boolean itemSelectable(Item item) { return accepts(item); }
			@Override public void onSelect(Item item) {
				if (item != null && selected != null) selected.item(item.detach(Dungeon.hero.belongings.backpack));
				selected = null;
				updateState(goldCost());
			}
		});
	}

	private Item[] inputItems() {
		Item[] items = new Item[inputs.length];
		for (int i = 0; i < inputs.length; i++) items[i] = inputs[i].item();
		return items;
	}

	private void updateState(int cost) {
		boolean hasInput = false;
		for (ItemButton input : inputs) if (input.item() != null) hasInput = true;
		combine.enable(hasInput && Dungeon.gold >= cost);
	}

	private void returnItem(Item item) {
		if (item != null && !item.collect(Dungeon.hero.belongings.backpack)) {
			Dungeon.level.drop(item, Dungeon.hero.pos).sprite.drop();
		}
	}

	@Override public void onBackPressed() {
		if (!returningItems) {
			returningItems = true;
			for (ItemButton input : inputs) returnItem(input.item());
		}
		super.onBackPressed();
	}

	protected abstract boolean accepts(Item item);
	protected abstract Item mix(Item[] items);
	protected abstract int goldCost();
}
