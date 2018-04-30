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

package org.apache.jena.arq.sparql.junit;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;

import junit.framework.Test;
import junit.framework.TestResult;

@DisplayName("SurpressedTest")
@ExtendWith(TestItemParameterResolver.class)
public class SurpressedTest extends EarlTestCase implements Test {
    public static boolean verbose = true ;
    String comment ;
    TestItem testItem ;

    public SurpressedTest(TestItem testItem)
    {
        this.testItem = testItem;
    }

    @Override
    public void runTestForReal(TestInfo testInfo, TestReporter testReporter)
    {
        super.notTested() ;
        if ( verbose )
        {
            System.out.print("** Surpressed: " + testInfo.getDisplayName()) ;
            if ( comment != null )
                System.out.print(" ("+comment+")") ;
            System.out.println() ;
        }
    }

    @Override
    public int countTestCases() {
        return 0;
    }

    @Override
    public void run(TestResult result) {

    }
}
