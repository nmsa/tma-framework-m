using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace ATMOSPHERE.TMAF.Monitor.Client
{

	public class Message
	{
		[JsonProperty(Required = Required.Always)]
		public int probeId
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		public int resourceId
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		public int messageId
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		public int sentTime
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		private List<Data> data = new List<Data>();
		public Message (int probeID = 1, int resourceID = 1, int messageID = 1, int timestamp = -1)
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
	}
}
