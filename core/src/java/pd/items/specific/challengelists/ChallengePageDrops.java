package pd.items.specific.challengelists;

import pd.Dungeon;
import pd.items.Heap;
import pd.items.quest.ChallengeJournal;

public final class ChallengePageDrops {
	private ChallengePageDrops() { }

	public static void offer(ChallengeList page, int pos) {
		if (Dungeon.hero == null || Dungeon.level == null) return;
		ChallengeJournal journal = Dungeon.hero.belongings.getItem(ChallengeJournal.class);
		if (journal != null && journal.isUnlocked(page.challenge())) return;
		if (Dungeon.hero.belongings.getItem(page.getClass()) != null) return;
		for (Heap existing : Dungeon.level.heaps.values()) {
			for (pd.items.Item item : existing.items) {
				if (page.getClass().isInstance(item)) return;
			}
		}
		Heap heap = Dungeon.level.drop(page, pos);
		if (heap.sprite != null) heap.sprite.drop();
	}
}
