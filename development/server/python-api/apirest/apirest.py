from functools import wraps
from flask import Flask, request, jsonify, abort
from jsonschema import Draft4Validator
from schemas.input_todo_schema import input_todo_schema
import json

def validate_schema(schema): 
 validator = Draft4Validator(schema)


 def wrapper(fn):
  @wraps(fn)
  def wrapped(*args, **kwargs):
   input = request.get_json(force=True)
   errors = [error.message for error in validator.iter_errors(input)]
   if errors:
    response = "Rejected" + "\n" + str(errors) +"\n"
    return response
   else:
    return "Accepted" + "\n"
  return wrapped
 return wrapper 

app = Flask(__name__)
@app.route('/', methods=['GET', 'POST'])
@validate_schema (input_todo_schema)
def foo():
 pass
if __name__== '__main__':
  app.run(debug = 'True',host='0.0.0.0')

