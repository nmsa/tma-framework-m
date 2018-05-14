# Testing JSON format
 
Test cases for validator of JSON messages against the JSON Schema [`tma-m_schema_0_3`](../../../interface/atmosphere_tma-m_schema.json).

The oracle is based on the name of the file, thus correct files must have a name that starts with `"correct"`. Conversely, incorrect files must have a name that starts with `"fail"`.




## Usage
`$ sh testing-json-format.sh endpoint_path`


## Default configuration

The current script assumes that the default endpoint path as `"http://10.2.0.240:7001/monitor"`


 
