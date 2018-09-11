#!/bin/bash

if [ -z "$1" ]
then
    API_ENDPOINT="https://10.100.166.233:5000/monitor"
else
    API_ENDPOINT="$1"
fi

RESULTSDIR="results"

rm -r $RESULTSDIR
mkdir $RESULTSDIR

for filename in *.json; do
    OUTPUT_FILE="$RESULTSDIR/$filename.example.out"
    curl -ss -X POST $API_ENDPOINT -d @"$filename" -o $OUTPUT_FILE --cacert cert.pem >> "$RESULTSDIR/curl.log.out"
    if [[ $filename = "correct"* ]];
    then   
        if grep -q "0" "$OUTPUT_FILE"
        then  
            echo "Accepted  (correct):  $filename"
        else
            echo "Rejected  (wrong):    $filename"
        fi

    elif [[ $filename = "fail"* ]];
    then
        line=$(head -n 1 $OUTPUT_FILE)
        if [[ $line = "0" ]];
        then  
            echo "Accepted  (wrong):    $filename"
        else
            echo "Rejected  (correct):  $filename"
        fi

    else
        echo "Incorrect filename:   $filename"
    fi
done
