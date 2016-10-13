package models;

import java.util.List;

public class FlowJsonClickModel {
	public String status;
	public FlowJsonClickData data;

	public class FlowJsonClickData {
		public String total;
		public List<FlowJsonClicks> clicks;
	}

	public class FlowJsonClicks {
		public String kid;
		public String date;
		public String clicks;
	}

}
