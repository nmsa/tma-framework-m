using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace MonitorClient
{

	public class Message
	{
		[JsonProperty(Required = Required.Always)]
		private int probeId
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		private int resourceId
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		private int messageId
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		private int sentTime
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		private List<Data> data = new List<Data>();
		public Message (int probeID = -1, int resourceID = -1, int messageID = -1, int timestamp = -1)
		{
			this.probeId = probeID;
			this.resourceId = resourceID;
			this.messageId = messageID;
			this.sentTime = timestamp;
		}
		public void add_data (Data dt)
		{
			this.data.Add(dt);
		}
		public List<Data> get_data()
		{
			return this.data;
		}
	}
}