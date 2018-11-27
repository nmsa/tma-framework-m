using System;
using System.Net;
using System.IO;

namespace MonitorClient
{

	public class Communication
	{
		private string url
		{
			get;
			set;
		}
		public Communication(string link)
		{
			this.url = link;
		}
		public dynamic send_message(string message_formated)
		{
			var httpWebRequest = (HttpWebRequest)WebRequest.Create(this.url);
			httpWebRequest.ContentType = "application/json";
			httpWebRequest.Method = "POST";

			using (var streamWriter = new StreamWriter(httpWebRequest.GetRequestStream()))
			{
				streamWriter.Write(message_formated);
				streamWriter.Flush();
				streamWriter.Close();
			}
			var httpResponse = (HttpWebResponse)httpWebRequest.GetResponse();
			using (var streamreader = new StreamReader(httpResponse.GetResponseStream()))
			{
				// return the response from Post request
				var result = streamreader.ReadToEnd();
				return result;
			} 

		}
	}
}
