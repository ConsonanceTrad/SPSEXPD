package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.Fire;
import pd.messages.InlineText;
public class FireBuff2Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(FireBuff2Trap.class)
			.t("name", "火种中陷阱")
			.t("desc", "会释放中等范围火焰场的陷阱。");
	}


 public FireBuff2Trap(){ super(ORANGE, WAVES, Fire.class, 1, 6, true); } }
