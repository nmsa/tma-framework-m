using System;
using System.Threading;
using System.Collections.Generic;
using Newtonsoft.Json;
using log4net;
using log4net.Repository.Hierarchy;
using log4net.Core;
using log4net.Appender;
using log4net.Layout;

namespace ATMOSPHERE.TMAF.Monitor.Client
{
	public class Logger
    {
        public static void Setup()
        {
            Hierarchy hierarchy = (Hierarchy)LogManager.GetRepository();

            PatternLayout patternLayout = new PatternLayout();
            patternLayout.ConversionPattern = "%date [%thread] %-5level %logger - %message%newline";
            patternLayout.ActivateOptions();

            RollingFileAppender roller = new RollingFileAppender();
            roller.AppendToFile = false;
            roller.File = @"EventLog.txt";
            roller.Layout = patternLayout;
            roller.MaxSizeRollBackups = 5;
            roller.MaximumFileSize = "1GB";
            roller.RollingStyle = RollingFileAppender.RollingMode.Size;
            roller.StaticLogFileName = true;            
            roller.ActivateOptions();
            hierarchy.Root.AddAppender(roller);

            ConsoleAppender roll = new ConsoleAppender();
            hierarchy.Root.AddAppender(roll);
            roll.Layout = patternLayout;

            MemoryAppender memory = new MemoryAppender();
            memory.ActivateOptions();
            hierarchy.Root.AddAppender(memory);

            hierarchy.Root.Level = Level.Info;
            hierarchy.Configured = true;
        }
    }
 
	public class main
	{
		private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
		public static string createMessage()
		{
			Int32 unixTimestamp = 0;
			// message to sent do the server API
			// folow the json schema
			// need to change the probeId, resourceId and messageId
			Message message = new Message(1,1,1,unixTimestamp);
			//the timestamp is the same for all metrics.
			unixTimestamp = (Int32)(DateTime.UtcNow.Subtract(new DateTime(1970, 1, 1))).TotalSeconds;
			// append measurement data to message
			Data dt = new Data("measurement", 1);
			Observation obs = new Observation(unixTimestamp, 20000.00001 + 1);
			dt.add_observation(obs);
			// append data to message
			message.add_data(dt);
			// append event data to message
			dt = new Data("event", 2);
			obs = new Observation(unixTimestamp,-1);
			dt.add_observation(obs);
			// append data to message
			message.add_data(dt);
			string json = JsonConvert.SerializeObject(message);
			// return message formatted in json
	  		return json;
		}
	    static int Main (string[] args)
	    {
	    	Logger.Setup();
	    	log.Info("Trust me! This is ATMOSPHERE");
	    	log.Info("Probe Started");
	    	// receive the server url as paramenters
	    	string link = args[0];
	    	log.Info("Probe" + link);
	    	log.Warn("Authentication is yet to be supported");
	        Communication communication = new Communication(link);
	        int i = 0;
	        while(true)
	        {
	        	string message = createMessage();
	        	log.Info("Message Sent++=" + i);
	        	log.Info("Post:" + message);
	        	var response = communication.send_message(message);
        		Thread.Sleep(1000);
        		i++;
        		log.Info("result:" + response);
	        }
	    }
	}
}
