# Design-Time Probe
This probe was developed to send design-time data to TMA_Monitor endpoint. To execute this probe, you need to create two files:

- CSV file - File that has the values of design-time metrics to be loaded to TMA_Monitor;
- JSON file - File that has the description of all fields of CSV file.

There is one example of each of these files in this folder.

## Prerequisites
To use this probe, you need to initialize the Kubernetes cluster and deploy on it all components of [`server`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server) folder of this repository following the instructions present in [`README`](https://github.com/eubr-atmosphere/tma-framework-m/tree/master/development/server/README.md)  file of that folder.

To execute this probe, you need also to install csv and tmalibrary packages. To do that, you need to execute the following commands:

```
pip install csv
pip install tmalibrary
```

## Execution

To execute this probe, you need to execute the following command:

```
python3 probe_design_time.py [JSON FILE NAME] [CSV FILE NAME] [TMA_MONITOR ENDPOINT]
```

## Testing

For testing purposes, you should create an Apache Kafka consumer that receives messages from `topic-monitor` topic. To do that you should execute the following command:

```
kubectl exec -ti kafka-0 -- kafka-console-consumer.sh --topic topic-monitor --bootstrap-server localhost:9093
```

After running the previous command, you will see the data collected by this probe.

## Sample Data

To demonstrate the use of the design time probe, software metrics were exported to a `CSV` file to be sent to TMA. The software metrics can be of different levels, such as File, Function or Class levels. The file `tabular_data_design_time.csv` contains metrics of Function level of a Java application, and they were obtained from the following [dataset](https://eden.dei.uc.pt/~nmsa/metrics-dataset/).

1. Export the data from the dataset to a CSV file (check the [generate_data.sql](data/generate_data.sql) as an example);
2. Generate the script to insert the resources in the database (this can be done using the [format\_design\_time\_data.py](utils/format_design_time_data.py) script);
3. Generate the tabular data (using the [format\_design\_time\_data.py](utils/format_design_time_data.py) script);
4. Insert the resources in the database using the query created in the step 2;
5. Run the prbe using the tabular data generated in the step 3.