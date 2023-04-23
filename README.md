# README

This document describes the approach used for handling create and update requests for policies.

### Approach

In this example, synchronous REST processing is utilized for simpler local development and testing. However, the service
can be effortlessly adapted to support asynchronous interactions.

Data from create and update requests is saved in a table in the database with the following structure:

- _id
- _class
- first_name
- last_name
- person_id
- policy_id
- policy_version
- premium
- start_date

### Create Request

A valid policy create request in which the data of two persons is given will result in two table entries:

```json
[
  {
    "_id": { "$oid": "6444d01e41cdc43004084fc1" },
    "_class": "policy-person",
    "first_name": "Jane",
    "last_name": "Johnson",
    "person_id": 1,
    "policy_id": "ZKAHHGD3W",
    "policy_version": 1,
    "premium": "12.90",
    "start_date": { "$date": "2023-07-14T22:00:00.000Z" }
  },
  {
    "_id": { "$oid": "6444d01e41cdc43004084fc2" },
    "_class": "policy-person",
    "first_name": "Jack",
    "last_name": "Doe",
    "person_id": 2,
    "policy_id": "ZKAHHGD3W",
    "policy_version": 1,
    "premium": "15.90",
    "start_date": { "$date": "2023-07-14T22:00:00.000Z" }
  }
]
```

Policy Id and person Id are generated in service before being saved to the database.
On creation of policy all the entries will have the field policy_version = 1

### Update request.

The approach for the Update request is similar to the Create request, with some differences:

- Use the policyId provided in the request.
- Generate an ID for people who do not have an ID.
- A new policy version is created, i.e., the new rows saved in the database will have policy_version set to 2. The idea
  is that nothing is edited in the database; only new entries are added so that all versions/changes are accessible.
- Before a new ID is generated for people without an ID, the application checks all IDs used on the same policy in its
  different versions and assigns the next available ID. This allows generating a report with all people on the policy
  over time.

- After an update, the policy data in the database may look like this:

```json
[
{
"_id": { "$oid": "6444d09441cdc43004084fc5"},
{ "_class":", "policy-person",
"first_name": "Jane",
"last_name": "Johnson",
"person_id": 1,
"policy_id": "ZKAHHGD3W",
"policy_version": 3,
"premium": "16",
"start_date": {"$date": "2023-11-14T23:00:00.000Z"}
},
{
"_id": { "$oid": "6444d09441cdc43004084fc6"},
{ "_class":", "policy-person",
"first_name": "Jack",
"last_name": "Doe",
"person_id": 2,
"policy_id": "ZKAHHGD3W",
"policy_version": 3,
"premium": "65",
"start_date": {"$date": "2023-11-14T23:00:00.000Z"}
},
{
"_id": { "$oid": "6444d09441cdc43004084fc7"},
{ "_class":", "policy-person",
"first_name": "Jennifer",
"last_name": "Lopez",
"person_id": 3,
"policy_id": "ZKAHHGD3W",
"policy_version": 3,
"premium": "65",
"start_date": {"$date": "2023-11-14T23:00:00.000Z"}
}
]
```

New people with new id available have been added, policy version has been changed.

### Get policy info request:

Based on the date given in the request, the application extracts/generates the policy with the closest previous date
from the database.

### Response Policy Generation

Policy generation for the response is done programmatically based on the database entries for the relevant policy
version. In the same process the totalPremium is calculated.

### Technologies used:

- Java 17
- Spring Boot
- Spring Data MongoDB
- MongoDB

For local testing, a local instance of Mongo db should be provisioned using the docker-compose file, included in the code
main directory:

```code
docker-compose up
```