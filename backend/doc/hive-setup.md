## Setup HIVE locally
Do not use brew install, simply download the tarball and unzip

1. Follow the steps to setup HDFS locally https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html
- Start the local dfs
- http://localhost:50070/explorer.html#/

2. Follow the steps to setup HIVE locally https://cwiki.apache.org/confluence/display/Hive/GettingStarted
- Setup JAVA_HOME, HADOOP_HOME, HIVE_HOME
- $HIVE_HOME/bin/schematool -dbType <db type> -initSchema
- $HIVE_HOME/bin/hive


## Read orc file
### Read data
```bash
hive --orcfiledump test.orc
```

### Read schema
If hive installed: ```hive --orcfiledump -j -p test.orc ```

If hive not installed:
1. Download orc-tools-X.Y.Z-uber.jar
Doc: https://orc.apache.org/docs/tools.html
Download the uber jar from http://central.maven.org/maven2/org/apache/orc/orc-tools/1.4.1/ 
You can "View All" files at https://mvnrepository.com/artifact/org.apache.orc/orc-tools/1.4.1

2. Command
```bash
java -jar orc-tools-1.4.1-uber.jar meta test.orc
```

## HQL
```sql
drop table test;
create external table test (
    a string -- it must be a??
)
STORED AS ORC
TBLPROPERTIES("orc.compress"="ZLIB")
;

LOAD DATA LOCAL INPATH '/Users/chguo/Downloads/test.orc' OVERWRITE INTO TABLE test;
select * from test;
```
