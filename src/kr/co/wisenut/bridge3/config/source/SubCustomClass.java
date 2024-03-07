package kr.co.wisenut.bridge3.config.source;

public class SubCustomClass {
	private String order = null;
	private String useIdx = null;
	private int[] useIdxes;
	private String className = null;

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getUseIdx() {
		return useIdx;
	}

	public void setUseIdx(String useIdx) {
		this.useIdx = useIdx;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int[] getUseIdxes() {
		if (useIdxes == null) {
			String[] idxStr = useIdx.split(",");
			useIdxes = new int[idxStr.length];

			for (int i = 0; i < idxStr.length; i++) {
				useIdxes[i] = Integer.parseInt(idxStr[i]);
			}
		}
		return useIdxes;
	}

}
