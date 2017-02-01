# Linear Regresion with BFGS

Fit a linear regression of the form:

    y = 10 + (5 * x1) + (10 * x2)

Start the eventserver:
    pio eventserver

Create an app:
    pio app new BFGS

Update engine.json with app name BFGS

Load mock data:
    python data/import_eventserver.py --access_key=<ACCESS_KEY>

Check the data is persisted:
    curl -i -X GET http://localhost:7070/events.json?accessKey=<ACCESS_KEY>

Build, Train, Deploy:
    pio build; pio train; pio deploy

Make a prediction (yhat should be ~25):
     curl -H "Content-Type: application/json" \
-d '{ "x1":1, "x2":1 }' http://localhost:8000/queries.json


## Documentation

Please refer to http://predictionio.incubator.apache.org/templates/vanilla/quickstart/
