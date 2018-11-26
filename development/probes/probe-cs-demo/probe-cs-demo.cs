using System;
using System.Threading;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace MonitorClient
{
 
	public class main
	{
		public static string createMessage()
		{
			Int32 unixTimestamp = 0;
			Message message = new Message(1,1,1,unixTimestamp);
			unixTimestamp = (Int32)(DateTime.UtcNow.Subtract(new DateTime(1970, 1, 1))).TotalSeconds;
			Data dt = new Data("measurement", 1);
			Observation obs = new Observation(unixTimestamp, 20000.00001 + 1);
			dt.add_observation(obs);
			message.add_data(dt);
			dt = new Data("event", 2);
			obs = new Observation(unixTimestamp,-1);
			dt.add_observation(obs);
			message.add_data(dt);
			string sJSONResponse = JsonConvert.SerializeObject(message);
	  		return sJSONResponse;
		}
	    static int Main (string[] args)
	    {
	    	string link = args[0];
	        Communication communication = new Communication(link);
	        while(true)
	        {
	        	string message = createMessage();
	        	Console.WriteLine(message);
	        	var response = communication.send_message(message);
        		Thread.Sleep(1000);
        		Console.WriteLine(response);
	        }
	    }
	}
}
