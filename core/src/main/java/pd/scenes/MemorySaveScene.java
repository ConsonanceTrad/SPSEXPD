/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.scenes;

import pd.Chrome;
import pd.Dungeon;
import pd.GamesInProgress;
import pd.ShatteredPixelDungeon;
import pd.actors.hero.HeroSubClass;
import pd.messages.Messages;
import pd.ui.ActionIndicator;
import pd.ui.RenderedTextBlock;
import pd.ui.ScrollPane;
import pd.ui.StyledButton;
import pd.ui.TitleBackground;
import pd.ui.Window;
import pd.windows.WndOptions;
import watabou.noosa.Camera;
import watabou.noosa.Game;
import watabou.noosa.ui.Component;
import watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;

/** Legacy memory-fire interface for copying the active run into an empty slot. */
public class MemorySaveScene extends PixelScene {

	private static final int SLOT_WIDTH = 124;
	private static final int SLOT_HEIGHT = 22;

	@Override
	public void create() {
		super.create();
		uiCamera.visible = false;
		int width = Camera.main.width;
		int height = Camera.main.height;
		add(new TitleBackground(width, height));

		RenderedTextBlock title = renderTextBlock(Messages.get(this, "title"), 9);
		title.hardlight(Window.TITLE_COLOR);
		title.setPos((width - title.width()) / 2f, 8);
		align(title);
		add(title);

		Component content = new Component();
		ScrollPane list = new ScrollPane(content);
		list.setRect(0, title.bottom() + 6, width, height - title.bottom() - 12);
		add(list);

		ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
		float y = 0;
		for (GamesInProgress.Info info : games) {
			MemorySlotButton button = new MemorySlotButton(info.slot, label(info));
			button.setRect((width - SLOT_WIDTH) / 2f, y, SLOT_WIDTH, SLOT_HEIGHT);
			content.add(button);
			y += SLOT_HEIGHT + 4;
		}
		if (games.size() < GamesInProgress.MAX_SLOTS) {
			int slot = GamesInProgress.firstEmpty();
			MemorySlotButton button = new MemorySlotButton(slot, Messages.get(this, "new", slot));
			button.setRect((width - SLOT_WIDTH) / 2f, y, SLOT_WIDTH, SLOT_HEIGHT);
			content.add(button);
			y += SLOT_HEIGHT + 4;
		}
		content.setSize(width, y);
		list.scrollTo(0, 0);
		fadeIn();
	}

	private static String label(GamesInProgress.Info info) {
		String hero = info.subClass != HeroSubClass.NONE ? info.subClass.title() : info.heroClass.title();
		return Messages.get(MemorySaveScene.class, "slot", info.slot, hero, info.depth, info.level);
	}

	@Override
	protected void onBackPressed() {
		returnToActiveGame();
	}

	private static void returnToActiveGame() {
		Dungeon.hero = null;
		ActionIndicator.clearAction();
		InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
		Game.switchScene(InterlevelScene.class);
	}

	private static class MemorySlotButton extends StyledButton {
		private final int slot;

		MemorySlotButton(int slot, String label) {
			super(Chrome.Type.TOAST_TR, label, 7);
			this.slot = slot;
			multiline = true;
		}

		@Override
		protected void onClick() {
			super.onClick();
			GamesInProgress.Info info = GamesInProgress.check(slot);
			if (info == null) {
				try {
					Dungeon.saveNewSlot(slot);
					returnToActiveGame();
				} catch (IOException exception) {
					ShatteredPixelDungeon.reportException(exception);
					ShatteredPixelDungeon.scene().add(new WndOptions(
							Messages.get(MemorySaveScene.class, "error_title"),
							Messages.get(MemorySaveScene.class, "error"),
							Messages.get(MemorySaveScene.class, "cancel")));
				}
				return;
			}

			ShatteredPixelDungeon.scene().add(new WndOptions(
					Messages.get(MemorySaveScene.class, "existing_title", slot),
					Messages.get(MemorySaveScene.class, "existing_body"),
					Messages.get(MemorySaveScene.class, "load"),
					Messages.get(MemorySaveScene.class, "erase"),
					Messages.get(MemorySaveScene.class, "cancel")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						GamesInProgress.curSlot = slot;
						returnToActiveGame();
					} else if (index == 1) {
						FileUtils.deleteDir(GamesInProgress.gameFolder(slot));
						GamesInProgress.setUnknown(slot);
						ShatteredPixelDungeon.seamlessResetScene();
					}
				}
			});
		}
	}
}
