/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.dboe.tdb2.junit;

import org.apache.jena.arq.sparql.engine.optimizer.reorder.ReorderLib ;
import org.apache.jena.arq.sparql.engine.optimizer.reorder.ReorderTransformation ;
import org.apache.jena.dboe.tdb2.sys.SystemTDB;
import org.junit.AfterClass ;
import org.junit.BeforeClass ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base_TS
{
    //static Level level = null ;
    static ReorderTransformation rt = null ;
    static Logger log = LoggerFactory.getLogger(Base_TS.class) ;

    @BeforeClass static public void beforeClass()   
    {
        rt = SystemTDB.defaultReorderTransform ;
        //level = Logger.getLogger("org.apache.jena.tdb.info").getLevel() ;
        //Logger.getLogger("org.apache.jena.tdb.info").setLevel(Level.FATAL) ;
       // Logger.getLogger("org.apache.jena.dboe.tdb2.info").setLevel(Level.FATAL) ;
        SystemTDB.defaultReorderTransform = ReorderLib.identity() ;
        rt = SystemTDB.defaultReorderTransform ;
    }
    
    @AfterClass static public void afterClass()
    {
        SystemTDB.defaultReorderTransform = rt ;
    }
}
