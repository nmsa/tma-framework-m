# Monitor Component @ TMA Framework

According to the [architecture of the TMA Framework](https://github.com/nmsa/tma-framework/blob/master/architecture/diagrams/TMA-Platform_Architecture.jpg)
the `TMA_Monitor` component provides a generic interface that can be used by the probes installed in the different layers of the system to send data about the collected measurements and events. 
These data are then stored in the `TMA_Knowledge` component, where it will be available to be used in by the `TMA_Analyze`, `TMA_Planning`, and `TMA_Execute` components.


The monitoring interface receives data regarding a **broad range of components in a generic fashion**.
To use this interface, the protocol defined [here](https://github.com/nmsa/tma-framework/blob/master/architecture/diagrams/TMA-M/TMA-M_sequence.jpg) must be followed, to assure the basic security properties in the data transmission (integrity and confidentiality). 

*![Monitor Usage Sequence Diagram](https://github.com/nmsa/tma-framework/blob/master/architecture/diagrams/TMA-M/TMA-M_sequence.jpg) Sequence diagram for the usage of the monitor component.*

Each probe starts its activity with an authentication synchronous message, in which it will receive is `probeId` and the token to use in the ensuing messages. 
All communication is performed over SSL sockets.
Each of the post observations interactions is an asynchronous message, which includes the obtained token in the headers. 
The messages format allows to aggregate multiple metrics and observations before sending, to avoid the communication overhead.


## TMA Monitor Message Format
The messages to be submitted to the `TMA_Monitor` follow the `JSON` schema specified in [tma-m_schema](interface/atmosphere_tma-m_schema.json), which is currently in the version `0.3`.

The [figure below](interface/atmosphere_tma-m_schema.png)  presents a representation of this schema, which is also explained below. 

*![Monitor Schema](interface/atmosphere_tma-m_schema.png)Format of the data to be provided to the monitor component.*

Each message includes:

* `probeId` -- obtained during authentication
* `resourceId` -- identifies the resource that is the subject of the attached data
* `messageId` -- control information 
* `sentTime` -- control information 
* `data` -- an array of objects, each one including: 
	* `type` -- event or measurement
	* `descriptionId` -- identifies the event or measurement, and an array of 
	* `observations` -- each one is a pair of 
		* `time` -- at which the specific observation was performed
		* `value` -- the observerd value which can be 0 in the case of `event`.



## TMA Monitor Server

**Under development**

*a server component will be developed*




## TMA Monitor Client libraries

**Under development**

*a set of libraries will be developed to provide an easier implementation of the probes*






