#!/usr/bin/env bash

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

mkdir -p /tmp/shenyu-e2e/mysql

wget -O /tmp/shenyu-e2e/mysql/mysql-connector.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.29/mysql-connector-java-8.0.29.jar
wget -O /tmp/shenyu-e2e/mysql/schema.sql https://raw.githubusercontent.com/apache/shenyu/master/db/init/mysql/schema.sql

echo "GRANT ALL PRIVILEGES ON shenyu.* TO 'shenyue2e'@'%';" >> /tmp/shenyu-e2e/mysql/schema.sql