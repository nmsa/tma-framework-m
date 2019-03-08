# TMA-Monitor `Python` Client Library 

Class files to use during the development of `TMA Monitor` probes developed in `Python`.


## Prerequisites

To use this library, you need to install `requests` and `tmalibrary` module. To install these modules, you just need to execute the following commands:

```sh
pip install requests
pip install tmalibrary
```

There is also a `docker` image with these modules already installed. In this case, `docker` is mandatory. 

## Usage

To use this library in the development of your probe, you just need to include the following lines in your probe main file, using the code below.

```python
from tmalibrary import *
```

**Note:** As an alternative, you can take advantage of the library docker image present in this folder to ease your deployment.