# TMA-Monitor `Python` Client Library 

Class files to use during the development of `TMA Monitor` probes developed in `Python`.


## Prerequisites

To use this library, you need to import in probe `Python` file the files presented in this directory and install `requests` module. To install `requests` module, you just need to execute the following command:

```sh
pip install requests
```

There is also a `docker` image that contains all files and dependencies needed. In this case, `docker` is mandatory. 

## Usage

To use this library in the development of your probe, you just need to include the following lines in your probe main file, using the code below.

```python
from data import Data
from message import Message
from message import ComplexEncoder
from observation import Observation
from communication import Communication
```

**Note:** As an alternative, you can take advantage of the library docker image present in this folder to ease your deployment.