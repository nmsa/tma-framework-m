from flask import Flask
from flask import abort
from flask import jsonify
from flask import request
from functools import wraps
import json
from jsonschema import Draft4Validator
from schemas.atmosphere_tma_m_schema import m_schema_0_3


def validate_schema(schema):
    validator = Draft4Validator(schema)

    def wrapper(fn):
        @wraps(fn)
        def wrapped(*args, ** kwargs):
            input = request.get_json(force=True)
            errors = [error.message for error in validator.iter_errors(input)]
            if errors:
                response = "Rejected" + "\n" + str(errors) + "\n"
                return response
            else:
                return "Accepted" + "\n"
        return wrapped
    return wrapper 


app = Flask(__name__)
@app.route('/monitor', methods=['GET', 'POST'])
@validate_schema (m_schema_0_3)
def processjson():
    pass


if __name__ == '__main__':
    app.run(debug = 'True',host='0.0.0.0')

