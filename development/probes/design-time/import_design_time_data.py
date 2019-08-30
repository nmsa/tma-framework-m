import csv

filename = "functions.csv"
initial_resource_id = 30
initial_description_id = 60
partner = "30"
timestamp = 1567178716


def write_file(filename, rows):
    with open(filename, 'w') as output_file:
        for row in rows:
            output_file.write(row + '\n')

# (1) Insert Descriptions
# The file insert_description_metrics.sql contains the data

# (2) Insert Resources
def insert_resources(filename):
    current_resource_id = initial_resource_id
    resources = []

    with open(filename) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            resources.append("({}{},\"{}\",\"Java_Function\")".format(partner, str(current_resource_id).zfill(3), row[0]))
            current_resource_id = current_resource_id + 1

    with open("insert_resources.sql", 'w') as output_file:
        output_file.write("INSERT INTO RESOURCE(resourceId, resourceName, resourceType) VALUES \n")
        output_file.write(",\n".join(resources))
        output_file.write(";")


# (3) Generate the design time data (CSV file)
def generate_data(filename):
    data_items = []
    current_resource_id = initial_resource_id

    with open(filename) as csv_file:
        csv_reader = csv.reader(csv_file, delimiter=',')
        for row in csv_reader:
            index = 1
            resource_id = "{}{}".format(partner, str(current_resource_id).zfill(3))
            current_description_id = initial_description_id

            # resourceId,descriptionId,time,value
            while (index < len(row)):
                description_id = "{}{}".format(partner, str(current_description_id).zfill(3))
                data_items.append(
                    "{},{},{},{}".format(resource_id, description_id, timestamp, row[index]))
                index = index + 1
                current_description_id = current_description_id + 1

            current_resource_id = current_resource_id + 1

    with open("tabular_data_design_time.csv", 'w') as output_file:
        output_file.write("\n".join(data_items))


insert_resources(filename)
generate_data(filename)