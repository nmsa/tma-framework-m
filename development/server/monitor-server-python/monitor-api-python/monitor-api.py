from flask import Flask
from flask import abort
from flask import jsonify
from flask import request
from functools import wraps
import json
from jsonschema import Draft4Validator
from pprint import pprint
from time import sleep
from datetime import datetime
# from schemas.atmosphere_tma_m_schema import m_schema_0_3
# from schemas.input_todo_schema import input_todo_schema


# todo later get add warning
app = Flask(__name__)

print "\n\n", datetime.now().strftime('%Y-%m-%d %H:%M:%S'), " - Starting Monitor Server Python\n",

with open('schemas/atmosphere_tma-m_schema.json') as f:
    message_schema = json.load(f)

validator = Draft4Validator(message_schema)

print datetime.now().strftime('%Y-%m-%d %H:%M:%S'), "message_schema: ", len(message_schema)

print datetime.now().strftime('%Y-%m-%d %H:%M:%S'), "validator: ", validator



@app.route('/monitor', methods=['POST'])
def processjson():
    input = request.get_json(force=True)
    print datetime.now().strftime('%Y-%m-%d %H:%M:%S'), "\n##Nuno Here. Why", request.method , "###\n"
    pprint(input)
    return validate_schema (input)




def validate_schema(input):

    def wrapper(fn):
        @wraps(fn)
        def wrapped(*args, ** kwargs):
            #input = request.get_json(force=True)
            errors = [error.message for error in validator.iter_errors(input)]
            if errors:
                response = "Rejected" + "\n" + str(errors) + "\n"
                return response
            else:
                return "Accepted" + "\n"
        return wrapped
    return wrapper 






if __name__ == '__main__':

    print "[MAIN]   Starting Monitor Server Python\n"

    app.run(debug = 'False',host='0.0.0.0')

