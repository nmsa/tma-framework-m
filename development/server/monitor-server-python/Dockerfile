FROM    tma-monitor/python:0.1

EXPOSE  5000

WORKDIR /monitor-api-python
COPY    monitor-api-python /monitor-api-python

CMD ["gunicorn", "--certfile", "cert.pem", "--keyfile", "key.pem", "-b", "0.0.0.0:5000", "wsgi:app"]