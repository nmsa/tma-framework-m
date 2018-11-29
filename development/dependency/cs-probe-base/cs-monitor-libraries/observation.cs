using System;
using Newtonsoft.Json;

namespace ATMOSPHERE.TMAF.Monitor.Client
{
	public class Observation
	{
		[JsonProperty(Required = Required.Always)]
		public int time
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		public double value
		{
			get;
			set;
		}
		public Observation (int timestamp = -1, double val = 0)
		{
			this.time = timestamp;
			this.value = val;
		}
	}
}
