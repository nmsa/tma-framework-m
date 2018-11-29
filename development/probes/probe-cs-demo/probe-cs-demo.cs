using System;
using System.Threading;
using System.Collections.Generic;
using log4net;
using ATMOSPHERE.TMAF.Monitor.Client;

namespace ATMOSPHERE.TMAF.Monitor.Probe
{
 
	public class main
	{
		private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
		public static Message addValues(Message msg)
		{
			Int32 unixTimestamp = 0;
			// message to sent do the server API
			// folow the json schema
			// need to change the probeId, resourceId and messageId
			//Message message = new Message(1,1,1,unixTimestamp);
			//the timestamp is the same for all metrics.
			unixTimestamp = (Int32)(DateTime.UtcNow.Subtract(new DateTime(1970, 1, 1))).TotalSeconds;
			// append measurement data to message
			Data dt = new Data("measurement", 1);
			Observation obs = new Observation(unixTimestamp, 20000.00001 + 1);
			dt.add_observation(obs);
			// append data to message
			msg.add_data(dt);
			// append event data to message
			dt = new Data("event", 2);
			obs = new Observation(unixTimestamp,-1);
			dt.add_observation(obs);
			// append data to message
			msg.add_data(dt);
	  		return msg;
		}
	    static int Main (string[] args)
	    {
	    	log.Info("Trust me ! This is ATMOSPHERE");
	    	// receive the server url as paramenters
	    	string link = args[0];
	    	SynchronousClient client = new SynchronousClient(link);
	    	log.Info("Client Start");
	        int i = 0;
	        while(true)
	        {
	        	log.Debug("i = " + i);
	        	Message message = client.createMessage();
	        	message.messageId = i;
	        	message = addValues(message);
	        	client.post(message);
	        	log.Info("Sent ++" + i);
        		Thread.Sleep(1000);
        		i++;
	        }
	    }
	}
}
