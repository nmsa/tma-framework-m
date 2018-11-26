using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace MonitorClient
{

	public class Data
	{
		[JsonProperty(Required = Required.Always)]
		private string type
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		private int descriptionId
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		private List<Observation> observations = new List<Observation>();
		public Data (string type = "measurement", int descriptionID = -10, List<Observation> obs = null)
		{
			this.type = type;
			this.descriptionId = descriptionID;
		}
		public void add_observation (Observation obs)
		{
			this.observations.Add(obs);
		}
	}
}