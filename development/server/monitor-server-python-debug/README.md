

# TMA-Monitor Python Server Implementation 

## REST API description

This application has mainly three methods: process_message(), validate_schema(), and setup_logging():
- process_message() method is used to load the json file sent to this application and calls the validate_schema() method.
- validate_schema() method receives as argument the json file read in the previous method. It is verified in this method if the json received has the same structure of the json schema illustrated by the following image:
*![Monitor Schema](https://github.com/nmsa/tma-framework-m/blob/master/interface/atmosphere_tma-m_schema.png)Format of the data to be provided to the monitor component.*

If there are errors, the method returns the "Rejected" string concatenated with the description of the errors found.
If there are not errors, this method returns a string with "Accepted".
- setup_logging() method is used to load the logging configuration file.


## REST API usage

This REST API supports GET and POST methods. GET method only shows a page where user can test TMA Monitor writing a JSON file in this page.
To interact with this application, user needs to generate and send a HTTP POST with a json file to the endpoint of TMA_Monitor. In this case, it is the MASTER_IP:32025.
If json file sent to TMA_Monitor endpoint in in the expected format, the output of this application is a message with string "Accepted". If the data sent is not valid, a message with string "Rejected" is returned by the TMA_Monitor and also the description of errors occurred.



## Implementation Details
 To implement this web API, it was used Flask microframework (http://flask.pocoo.org/) and other python libraries, namely, json (https://docs.python.org/2/library/json.html) and logging (https://docs.python.org/2/library/logging.html).

