using System;
using System.Net;
using System.IO;
using Newtonsoft.Json;
using log4net;

namespace ATMOSPHERE.TMAF.Monitor.Client
{

	public class SynchronousClient
	{
		private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
		public string url
		{
			get;
			set;
		}
		public SynchronousClient(string link)
		{
			log.Info(string.Format("MonitorClient {0} !",link));
			this.url = link;
		}
		public Message createMessage()
		{
			Message m = new Message();
			return m;
		}
		public dynamic post(Message message)
		{
			int unixTimestamp = (Int32)(DateTime.UtcNow.Subtract(new DateTime(1970, 1, 1))).TotalSeconds;
			message.sentTime = unixTimestamp;
			string json = JsonConvert.SerializeObject(message);
			log.Info(string.Format("Post:{0}",json));
			var httpWebRequest = (HttpWebRequest)WebRequest.Create(this.url);
			httpWebRequest.ContentType = "application/json";
			httpWebRequest.Method = "POST";

			using (var streamWriter = new StreamWriter(httpWebRequest.GetRequestStream()))
			{
				streamWriter.Write(json);
				streamWriter.Flush();
				streamWriter.Close();
			}
			var httpResponse = (HttpWebResponse)httpWebRequest.GetResponse();
			using (var streamreader = new StreamReader(httpResponse.GetResponseStream()))
			{
				// return the response from Post request
				var result = streamreader.ReadToEnd();
				log.Info(string.Format("Result: {0}",result));
				return result;
			} 

		}
	}
}
