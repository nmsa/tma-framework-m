using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace ATMOSPHERE.TMAF.Monitor.Client
{

	public class Data
	{
		[JsonProperty(Required = Required.Always)]
		public string type
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		public int descriptionId
		{
			get;
			set;
		}
		[JsonProperty(Required = Required.Always)]
		private List<Observation> observations = new List<Observation>();
		public Data (string type = "measurement", int descriptionID = -10)
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
