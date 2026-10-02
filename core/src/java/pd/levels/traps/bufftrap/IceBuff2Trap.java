package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.FrostCloud;
import pd.messages.InlineText;
public class IceBuff2Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(IceBuff2Trap.class)
			.t("name", "冰种中陷阱")
			.t("desc", "会释放中等范围寒冰场的陷阱。");
	}


 public IceBuff2Trap(){ super(TEAL, WAVES, FrostCloud.class, 1, 6, false); } }
