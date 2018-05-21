input_todo_schema ={
    "title": "tma-m_schema_0_3",

    "$id": "http://atmosphere-eubrazil.eu/tma-m_schema_v0.2.json",
    "$schema": "http://json-schema.org/draft-07/schema#",
    "type": "object",
    "properties": {
        "probeId": {
            "$id": "/properties/probeId",
            "type": "integer",
            "default": -1,
            "minimum": 0
        },
        "resourceId": {
            "$id": "/properties/resourceId",
            "type": "integer",
            "default": -1,
            "minimum": 0
        },
        "messageId": {
            "$id": "/properties/messageId",
            "type": "integer",
            "default": -1,
            "minimum": 0
        },
        "sentTime": {
            "$id": "/properties/sentTime",
            "type": "integer",
            "default": -1,
            "minimum": 0
        },
        "data": {
            "$id": "/properties/data",
            "type": "array",
            "minItems": 0,
            "items": {
                "$id": "/properties/data/items",
                "type": "object",
                "properties": {
                    "type": {
                        "$id": "/properties/data/items/properties/type",
                        "type": "string",
                        "default": "measurement",
                        "enum": [
                            "measurement",
                            "event"
                        ]
                    },
                    "descriptionId": {
                        "$id": "/properties/data/items/properties/descriptionId",
                        "type": "integer",
                        "default": -10,
                        "minimum": 0
                    },
                    "observations": {
                        "$id": "/properties/data/items/properties/observations",
                        "type": "array",
                        "items": {
                            "$id": "/properties/data/items/properties/observations/items",
                            "type": "object",
                            "properties": {
                                "time": {
                                    "$id": "/properties/data/items/properties/observations/items/properties/time",
                                    "type": "integer",
                                    "default": -1,
                                    "minimum": 0
                                },
                                "value": {
                                    "$id": "/properties/data/items/properties/observations/items/properties/value",
                                    "type": "number",
                                    "default": 0
                                }
                            },
                            "required": [
                                "time"
                            ]
                        }
                    }
                },
                "required": [
                    "type",
                    "descriptionId",
                    "observations"
                ]
            }
        }
    },
    "required": [
        "probeId",
        "resourceId",
        "messageId",
        "sentTime",
        "data"
    ]
}

