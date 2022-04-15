# ***ES-OS-Log library***

Elasticsearch / Opensearch (esos) Java Logging library

# What is this for

a super simple minimal library with no transient dependencies to send data from java direct to ES/OS without having to
do either
> logs->filebeat->logstash->ES
>
>    or
>
>    logs -> custom log to ES

why use this.... you want simple and meaning full data to go into ES/OS. typically when you take your raw log files into
ES you end up with tons of useless data. just log what you want. this keeps ES lean and fast.

also has an inbuilt timer mechanism. declare an identifier, and a timer type, START/LOG/STOP and a key is tagged to the
object for how long since the start occurred. useful for performance tracking

**Elasticsearch versions tested**

- 7.17.2

**OpenSearch versions tested**
*NOTE* you may need to use the disableSslChecking parameter if OS in dev env

- 1.3.1

**usage**
Early on in your application declare.

        new EsLog(true,new EsProperty()
                .setEsBulkUrl("http://localhost:9200/_bulk")
                .setUsername("elastic") //leave null for no password required
                .setPassword("FXl7CjgKFDjC0r5S29au")
        );

Parameters are :

esBulkUrl           : normal bulk url

username            : the elasticsearch username (basic authentication)

    default: null

password            : the elasticsearch password

    default: null

indexName           : index to use which is merged with the indexDate pattern

    default: log

indexDatePattern    : the suffix of the index, recomend daily or weekly

    default: yyyy.MM.dd

indexType           : one type for all records in the single application, default: _doc

serverName          : server name for ease of identification, recommended to be per app or server

    default: myserver

indexTemplate       : the ES template that is created on startup to ensure mappings for @timestamp are present

    default: 

                         "{\n" +
                         "  \"index_patterns\": [\n" +
                         "    \""+getIndexName()+"*\"\n" +
                         "  ],\n" +
                         "  \"settings\": {\n" +
                         "    \"number_of_shards\": 5,\n" +
                         "    \"number_of_replicas\": 0\n" +
                         "  },\n" +
                         "  \"mappings\": {\n" +
                         "    \"properties\": {\n" +
                         "      \"@timestamp\": {\n" +
                         "        \"type\": \"date\",\n" +
                         "        \"format\": \"epoch_millis\"\n" +
                         "      }\n" +
                         "    }\n" +
                         "  }\n" +
                         "}"

processSizeLimit            : max number of in memory items that are stored in the outgoing queue before discarding
oldest first

    default: 5000

batchProcessSize            : max number of records per bulk request to ES

    default: 2000

timerAgeSeconds            : how long an item timer is kept before a "TIMEOUT_WAITING" is logged to ES

    default: 60

disableSslChecking         : turns off any ssl checking (usefull for OS when it starts with certs that are not fully
valid chains)
default: false

debug                      : will log info and warn messages for each post to the ES server, usefull for testing.
careful with this on production

## Response timer  - how to use

set the **identifer** once with a TimerType.START

subsequent calls, TimerType.LOG.

will result in a **rspTime** (in milliseconds) being added to the object finally ensure to call TimerType.END for normal
scenarios

should the END for an identifier not be called, after the "timerAgeSeconds" a TIMEOUT_WAITING is logged for the
identifier eg content

      "@timestamp": 1650041187645,
      "serverName": "myserver",
      "logType": "ERROR",
      "message": "TIMEOUT_WAITING",
      "identifier": "asdfasdf",
      "rspTime": "5982",
      "details": "NO RESPONSE FOR IDENTIFIER :asdfasdf"

## Logging Code samples

    EsLog.log(EsLog.LogType.INFO,"SYSTEM","In Business people");

*


    EsLog.log(EsLog.LogType.MSG,"INBOUND","asdfasdf" , EsLog.TimerType.START,"sd");
    Thread.sleep(1000);
    EsLog.log(EsLog.LogType.MSG,"PROCESSING","asdfasdf" , EsLog.TimerType.LOG,"sASDFSDF");
    Thread.sleep(1000);
    EsLog.log(EsLog.LogType.MSG,"OUTBOUND","asdfasdf" , EsLog.TimerType.END,"ASsdfsdf");


DATA IN ES

      {
      "_index": "log-2022.04.15",
      "_type": "_doc",
      "_id": "vpkmLoABEU-z_8Su5PAL",
      "_score": 1,
      "_source": {
      "@timestamp": 1650041741342,
      "serverName": "myserver",
      "logType": "MSG",
      "message": "INBOUND",
      "identifier": "asdfasdf",
      "details": "sd"
      }
      }
      ,
      {
      "_index": "log-2022.04.15",
      "_type": "_doc",
      "_id": "v5kmLoABEU-z_8Su5fAw",
      "_score": 1,
      "_source": {
      "@timestamp": 1650041742344,
      "serverName": "myserver",
      "logType": "MSG",
      "message": "PROCESSING",
      "identifier": "asdfasdf",
      "rspTime": "1002",
      "details": "sd"
      }
      }
      ,
      {
      "_index": "log-2022.04.15",
      "_type": "_doc",
      "_id": "wJkmLoABEU-z_8Su6fB_",
      "_score": 1,
      "_source": {
      "@timestamp": 1650041743645,
      "serverName": "myserver",
      "logType": "MSG",
      "message": "OUTBOUND",
      "identifier": "asdfasdf",
      "rspTime": "2303",
      "details": "sd"
      }
      }


*


      Map<String,Object> testMap = new HashMap<String, Object>();

      testMap.put("F2","123456789");
      testMap.put("F3","123456789");
      testMap.put("F4","123456789");
      testMap.put("F5","123456789");

      EsLog.log(EsLog.LogType.MSG,"MESSAGE_FROM_REMOTE",testMap);

DATA IN ES

    {
    "_index": "log-2022.04.15",
    "_type": "_doc",
    "_id": "sJkeLoABEU-z_8SuVvCJ",
    "_score": 1,
    "_source": {
    "@timestamp": 1650041181662,
    "serverName": "myserver",
    "logType": "MSG",
    "F2": "123456789",
    "F3": "123456789",
    "F4": "123456789",
    "F5": "123456789",
    "message": "MESSAGE_FROM_REMOTE"
    }
    }

#Performance Testing

Using the Test class where it puts 5000 logs per second, no noticeable lag or hit. (ES/OS) running on same machine.
I have run this for a few hours and remains stable.

Seeing as this library is for small to medium enterprise uses, I havnt seen a need to do any stress testing further

In order to stress this to natural breaking point would require 


#Licence 

Copyright [2022] [RealZimboGuy]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
