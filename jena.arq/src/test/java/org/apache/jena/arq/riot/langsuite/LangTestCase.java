/**
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

package org.apache.jena.arq.riot.langsuite;

import org.apache.jena.arq.atlas.legacy.BaseTest2 ;
import org.apache.jena.arq.riot.SysRIOT ;
import org.apache.jena.arq.riot.system.ErrorHandlerFactory ;
import org.apache.jena.arq.sparql.junit.EarlReport ;
import org.apache.jena.arq.sparql.junit.EarlTestCase ;
import org.junit.After;
import org.junit.Before;

public abstract class LangTestCase extends EarlTestCase
{
    public LangTestCase()
    {  }

    public abstract void _setUp() ;
    public abstract void _tearDown() ;

    public boolean sysRIOT_strictMode ;

    @Before
    final public void setUpTest()
    {
        // The W3C Turtle and TriG test suites contain IRIs that generate warnings.
        // They are bad NFC for the version of UTF-8 that Java6 understands.
        BaseTest2.setTestLogging(ErrorHandlerFactory.errorHandlerNoWarnings) ;

        // If the test suite is sloppy, with IRIs that are not good practice, you may need
        // to run with warnings as not-errors ....
        //BaseTest.setTestLogging(ErrorHandlerFactory.errorHandlerStd) ;
        sysRIOT_strictMode = SysRIOT.isStrictMode() ;
        SysRIOT.setStrictMode(true) ;
        _setUp() ;
    }

    @After
    final public void tearDownTest()
    {
        _tearDown() ;
        SysRIOT.setStrictMode(sysRIOT_strictMode) ;
        BaseTest2.unsetTestLogging() ;
    }

}

