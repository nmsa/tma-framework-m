INSERT INTO Probe(probeName, password, salt, token) VALUES ("probe-k8s-api", "n/a","n/a","n/a");

INSERT INTO Description(dataType, descriptionName, unit) VALUES ("cpu", "measurement", "m");
INSERT INTO Description(dataType, descriptionName, unit) VALUES ("memory", "measurement", "Ki");

INSERT INTO Resource(resourceName, resourceType, resourceAddress) VALUES ("kafka-0", "POD", "n/a");
INSERT INTO Resource(resourceName, resourceType, resourceAddress) VALUES ("wildfly-0", "POD", "n/a");
INSERT INTO Resource(resourceName, resourceType, resourceAddress) VALUES ("mysql-wsvd-0", "POD", "n/a");

INSERT INTO Resource(resourceName, resourceType, resourceAddress) VALUES ("virtmanagernode-standard-pc-i440fx-piix-1996", "VM", "n/a");
INSERT INTO Resource(resourceName, resourceType, resourceAddress) VALUES ("virtmanagermaster-standard-pc-i440fx-piix-1996", "VM", "n/a");
