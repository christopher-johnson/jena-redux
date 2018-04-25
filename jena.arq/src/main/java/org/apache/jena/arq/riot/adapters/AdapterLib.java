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

package org.apache.jena.arq.riot.adapters;

import org.apache.jena.base.atlas.lib.Lib;
import org.apache.jena.arq.atlas.web.TypedInputStream;
import org.apache.jena.arq.riot.RiotException;
import org.apache.jena.arq.riot.system.stream.*;

class AdapterLib {
    public static org.apache.jena.core.util.TypedStream convert(TypedInputStream in) {
        return new org.apache.jena.core.util.TypedStream(in, in.getContentType(), in.getCharset());
    }

    public static LocationMapper copyConvert(org.apache.jena.core.util.LocationMapper locMap) {
        if ( locMap == null )
            return null;
        LocationMapper lmap2 = new LocationMapper();
        locMap.listAltEntries().forEachRemaining(k->lmap2.addAltEntry(k, locMap.getAltEntry(k)));
        locMap.listAltPrefixes().forEachRemaining(k->lmap2.addAltPrefix(k, locMap.getAltPrefix(k)));
        return lmap2;
    }

    public static Locator convert(org.apache.jena.core.util.Locator oldloc) {
        if ( oldloc instanceof org.apache.jena.core.util.LocatorFile ) {
            org.apache.jena.core.util.LocatorFile lFile = (org.apache.jena.core.util.LocatorFile)oldloc;
            return new LocatorFile(lFile.getDir());
        }
        if ( oldloc instanceof org.apache.jena.core.util.LocatorClassLoader ) {
            org.apache.jena.core.util.LocatorClassLoader classLoc = (org.apache.jena.core.util.LocatorClassLoader)oldloc;
            return new LocatorClassLoader(classLoc.getClassLoader());
        }
        if ( oldloc instanceof org.apache.jena.core.util.LocatorURL )
            return new LocatorHTTP();
        if ( oldloc instanceof org.apache.jena.core.util.LocatorZip ) {
            org.apache.jena.core.util.LocatorZip zipLoc = (org.apache.jena.core.util.LocatorZip)oldloc;
            return new LocatorZip(zipLoc.getZipFileName());
        }

        throw new RiotException("Unrecognized Locator: " + Lib.className(oldloc));
    }
}
