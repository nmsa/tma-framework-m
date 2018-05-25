from flask import Flask
from flask import request
import json
from jsonschema import Draft4Validator
import logging
import logging.config
import os
from kafka import SimpleProducer, KafkaClient

app = Flask(__name__)

logger = logging.getLogger(__name__)
logger.info('Starting Monitor Server Python')

# load json schema
with open('schemas/atmosphere_tma-m_schema.json') as f:
    tma_m_schema = json.load(f)
logger.debug('Schema loaded %s', tma_m_schema)

validator = Draft4Validator(tma_m_schema)
logger.info('Validator initialized %s', validator)

#Connect Kafka client to Kafka pod
kafka = KafkaClient('kafka-0.kafka-hs.default.svc.cluster.local:9093')

# Incialize producer structure to send messages
producer = SimpleProducer(kafka)


@app.route('/monitor', methods=['GET', 'POST'])
def process_message():
    # reject GET
    if request.method == 'GET':
        return "Method GET is not supported!"

    # load json file
    input = request.get_json(force=True)
    logger.trace('Processing Request %s', input)

    # todo, validate schema should return number of errors.
    # If 0, proceed with processing
    return validate_schema(input)


# Should return the number of errors, or -1 if exception
def validate_schema(input_msg):
    # check if there are errors in json file and return the result
    errors = [error.message for error in validator.iter_errors(input_msg)]
    if errors:
        response = "Rejected" + "\n" + str(errors) + "\n"
        return response
    else:
	# Convert dict into string. Kafka only accept messages at bytes or string format
        jd = json.dumps(input_msg)
        # Sending message
        producer.send_messages('kafka-mysql', jd)
        return "Accepted" + "\n"


# load logging configuration file
def setup_logging(default_path='logging.json', env_key='LOG_CFG'):
    path = default_path
    value = os.getenv(env_key, None)
    if value:
        path = value
    if os.path.exists(path):
        with open(path, 'rt') as f:
            config = json.load(f)
        logging.config.dictConfig(config)
    else:
        logging.basicConfig(level=logging.DEBUG)


if __name__ == '__main__':
    setup_logging()
    logger = logging.getLogger(__name__)
    logger.info('Initializing  Monitor Server Python')
    app.run(debug='True', host='0.0.0.0', port=5000)
